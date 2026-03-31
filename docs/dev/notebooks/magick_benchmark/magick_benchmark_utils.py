import contextlib
import ctypes
import io
import multiprocessing
import os
import queue
import re
import subprocess
import sys
import tempfile
import time
from functools import partial
from lets_plot import *
from typing import List, Callable, Any, Dict

# --- C-Level Flush Hack ---
try:
    _libc = ctypes.CDLL(None)
    _fflush_all = lambda: _libc.fflush(None)
except Exception:
    _fflush_all = lambda: None

# --- GLOBAL HELPER FOR WARMUP ---
# Must be defined at top level so it can be pickled
def _noop_task():
    pass

def capture_c_output(fn, *args, **kwargs) -> str:
    """
    Run fn(*args, **kwargs) and capture anything written to C stdout/stderr.
    """
    sys.stdout.flush(); sys.stderr.flush(); _fflush_all()
    old_out_fd = os.dup(1)
    old_err_fd = os.dup(2)

    with tempfile.TemporaryFile() as tmp_out, tempfile.TemporaryFile() as tmp_err:
        try:
            os.dup2(tmp_out.fileno(), 1)
            os.dup2(tmp_err.fileno(), 2)
            fn(*args, **kwargs)
            sys.stdout.flush(); sys.stderr.flush(); _fflush_all()
        finally:
            os.dup2(old_out_fd, 1)
            os.dup2(old_err_fd, 2)
            os.close(old_out_fd)
            os.close(old_err_fd)

        tmp_out.seek(0); tmp_err.seek(0)
        out = tmp_out.read().decode("utf-8", errors="replace")
        err = tmp_err.read().decode("utf-8", errors="replace")

    return out + err

def _persistent_worker_loop(in_q, out_q):
    """
    The loop running inside the persistent process.
    Waits for tasks, executes them, and returns results.
    """
    while True:
        task = in_q.get()
        if task is None: # Sentinel to exit
            break

        func, args, kwargs = task
        try:
            result = func(*args, **kwargs)
            out_q.put(("success", result))
        except Exception as e:
            out_q.put(("error", e))

class PersistentSafetyNet:
    """
    Manages a background worker process. 
    Restarts it only if it crashes or times out.
    """
    def __init__(self):
        self.process = None
        self.in_q = None
        self.out_q = None

    def _start_worker(self):
        """Starts the worker process and warms it up."""
        self.in_q = multiprocessing.Queue()
        self.out_q = multiprocessing.Queue()
        
        self.process = multiprocessing.Process(
            target=_persistent_worker_loop, 
            args=(self.in_q, self.out_q)
        )
        self.process.start()
        
        # --- WARMUP ---
        # FIX: Use the global _noop_task function instead of lambda
        self.in_q.put((_noop_task, (), {}))
        self.out_q.get() # Wait for warmup to finish

    def run(self, func, args=(), kwargs=None, timeout=2.0):
        if kwargs is None: kwargs = {}

        # 1. Ensure worker is running
        if self.process is None or not self.process.is_alive():
            self._start_worker()

        # 2. Send Task
        self.in_q.put((func, args, kwargs))

        # 3. Wait for result with Timeout
        try:
            status, payload = self.out_q.get(timeout=timeout)
            
            if status == "error":
                raise payload
            return payload

        except queue.Empty:
            # --- TIMEOUT HANDLER ---
            # Try to dump stack trace
            subprocess.run(["py-spy", "dump", "--native", "--pid", str(self.process.pid)], check=False)
            
            # Kill the stuck process
            self.process.terminate()
            self.process.join()
            
            # Reset state so next call starts a fresh one
            self.process = None
            
            raise RuntimeError(f"Task timed out after {timeout}s")

    def stop(self):
        """Gracefully stops the worker."""
        if self.process and self.process.is_alive():
            self.in_q.put(None) # Sentinel
            self.process.join(timeout=0.5)
            if self.process.is_alive():
                self.process.terminate()

def _parse_output_to_dict(output_text):
    """Parses C++ logs into a dictionary of timings."""
    def to_sec(t_str):
        if not t_str: return None
        t = t_str.strip()
        if 'ms' in t: return float(t.replace('ms','')) / 1000
        if 'us' in t: return float(t.replace('us','')) / 1e6
        if 'ns' in t: return float(t.replace('ns','')) / 1e9
        if 'm' in t:
            parts = t.split('m')
            secs = float(parts[0]) * 60
            if len(parts) > 1 and parts[1].strip():
                secs += float(parts[1].replace('s',''))
            return secs
        if 's' in t: return float(t.replace('s',''))
        try: return float(t)
        except: return None

    patterns = {
        "mapping": r"(.+?): exportMvg\(\)\: plot mapped to canvas",
        "painting": r"(.+?): exportMvg\(\)\: plot painted",
        "snapshot": r"(.+?): exportMvg\(\)\: snapshot taken"
    }
    
    times = {}
    for key, pat in patterns.items():
        match = re.search(pat, output_text)
        times[key] = to_sec(match.group(1)) if match else None

    if times["snapshot"] and times["painting"] and times["mapping"]:
        return {
            "time_map": times["mapping"],
            "time_paint": times["painting"] - times["mapping"],
            "time_snapshot": times["snapshot"] - times["painting"],
            "time_total": times["snapshot"]
        }
    return {"time_total": times.get("snapshot")}

def benchmark_mvg_save(ns: List[int], 
                       plot_factory: Callable[[int], Any], 
                       file_prefix: str, 
                       scale: float = 1.0, 
                       timeout: float = 15.0) -> List[Dict[str, Any]]:
    """
    Runs benchmarks using a persistent worker process to minimize overhead.
    """
    
    batch_results = []
    total_steps = len(ns)
    skip_remaining = False  

    # Initialize the persistent runner
    runner = PersistentSafetyNet()

    print(f"{file_prefix}@{scale}x", flush=True)
    
    try:
        for i, n in enumerate(ns):
            step_num = i + 1
            file_name = f"{file_prefix}_{n}@{scale}x.mvg"
            
            # --- CASE 1: PREVIOUSLY TIMED OUT ---
            if skip_remaining:
                print(f"[{step_num}/{total_steps}] n={n} (SKIPPED - previous timeout)", flush=True)
                batch_results.append({
                    "n": n,
                    "prefix": file_prefix,
                    "scale": scale,
                    "error": "skipped",
                    "time_total": None
                })
                continue

            # --- CASE 2: ATTEMPT EXECUTION ---
            try:
                # 1. Factory (Fast, Main Process)
                p = plot_factory(n)

                ggsave_action = partial(ggsave, scale=scale)
                
                # 2. Execution (Slow, Worker Process)
                logs = runner.run(
                    capture_c_output, 
                    args=(ggsave_action, p, file_name), 
                    timeout=timeout
                )
                
                metrics = _parse_output_to_dict(logs)
                
                # 3. Log Success
                time_val = metrics.get("time_total")
                time_str = f"{time_val:.4f}s" if time_val else "N/A"
                print(f"[{step_num}/{total_steps}] n={n} ({time_str})", flush=True)

                record = {
                    "n": n,
                    "prefix": file_prefix,
                    "scale": scale,
                    **metrics 
                }
                batch_results.append(record)

            except Exception as e:
                # --- CASE 3: FAILURE / TIMEOUT ---
                is_timeout = isinstance(e, RuntimeError)
                error_tag = "timeout" if is_timeout else "error"
                
                print(f"[{step_num}/{total_steps}] n={n} ({error_tag.upper()})", flush=True)
                
                batch_results.append({
                    "n": n,
                    "prefix": file_prefix,
                    "scale": scale,
                    "error": error_tag,
                    "time_total": None
                })
                
                if is_timeout:
                    skip_remaining = True

    finally:
        # Clean up the worker process when finished
        runner.stop()

    return batch_results