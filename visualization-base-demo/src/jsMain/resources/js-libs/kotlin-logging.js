(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlin-logging'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlin-logging'.");
    }
    root['kotlin-logging'] = factory(typeof this['kotlin-logging'] === 'undefined' ? {} : this['kotlin-logging'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var $$importsForInline$$ = _.$$importsForInline$$ || (_.$$importsForInline$$ = {});
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var toString = Kotlin.toString;
  var equals = Kotlin.equals;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var get_js = Kotlin.kotlin.js.get_js_1yb8b7$;
  var Enum = Kotlin.kotlin.Enum;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var throwISE = Kotlin.throwISE;
  var Unit = Kotlin.kotlin.Unit;
  var getCallableRef = Kotlin.getCallableRef;
  KotlinLoggingLevel.prototype = Object.create(Enum.prototype);
  KotlinLoggingLevel.prototype.constructor = KotlinLoggingLevel;
  var toStringSafe = defineInlineFunction('kotlin-logging.mu.internal.toStringSafe_qhgloa$', wrapFunction(function () {
    var toString = Kotlin.toString;
    var Exception = Kotlin.kotlin.Exception;
    return function ($receiver) {
      var tmp$;
      try {
        tmp$ = toString($receiver());
      }
       catch (e) {
        if (Kotlin.isType(e, Exception)) {
          tmp$ = 'Log message invocation failed: ' + e;
        }
         else
          throw e;
      }
      return tmp$;
    };
  }));
  function ConsoleOutputPipes() {
    ConsoleOutputPipes_instance = this;
  }
  ConsoleOutputPipes.prototype.trace_s8jyv4$ = function (message) {
    console.log(message);
  };
  ConsoleOutputPipes.prototype.debug_s8jyv4$ = function (message) {
    console.log(message);
  };
  ConsoleOutputPipes.prototype.info_s8jyv4$ = function (message) {
    console.info(message);
  };
  ConsoleOutputPipes.prototype.warn_s8jyv4$ = function (message) {
    console.warn(message);
  };
  ConsoleOutputPipes.prototype.error_s8jyv4$ = function (message) {
    console.error(message);
  };
  ConsoleOutputPipes.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ConsoleOutputPipes',
    interfaces: [OutputPipes]
  };
  var ConsoleOutputPipes_instance = null;
  function ConsoleOutputPipes_getInstance() {
    if (ConsoleOutputPipes_instance === null) {
      new ConsoleOutputPipes();
    }
    return ConsoleOutputPipes_instance;
  }
  function DefaultMessageFormatter() {
    DefaultMessageFormatter_instance = this;
  }
  var Exception = Kotlin.kotlin.Exception;
  DefaultMessageFormatter.prototype.formatMessage_pijeg6$ = function (level, loggerName, msg) {
    var tmp$ = level.name + ': [' + loggerName + '] ';
    var tmp$_0;
    try {
      tmp$_0 = toString(msg());
    }
     catch (e) {
      if (Kotlin.isType(e, Exception)) {
        tmp$_0 = 'Log message invocation failed: ' + e;
      }
       else
        throw e;
    }
    return tmp$ + tmp$_0;
  };
  DefaultMessageFormatter.prototype.formatMessage_hqgb2y$ = function (level, loggerName, t, msg) {
    var tmp$ = level.name + ': [' + loggerName + '] ';
    var tmp$_0;
    try {
      tmp$_0 = toString(msg());
    }
     catch (e) {
      if (Kotlin.isType(e, Exception)) {
        tmp$_0 = 'Log message invocation failed: ' + e;
      }
       else
        throw e;
    }
    return tmp$ + tmp$_0 + this.throwableToString_0(t);
  };
  DefaultMessageFormatter.prototype.formatMessage_i9qi47$ = function (level, loggerName, marker, msg) {
    var tmp$ = level.name + ': [' + loggerName + '] ' + toString(marker != null ? marker.getName() : null) + ' ';
    var tmp$_0;
    try {
      tmp$_0 = toString(msg());
    }
     catch (e) {
      if (Kotlin.isType(e, Exception)) {
        tmp$_0 = 'Log message invocation failed: ' + e;
      }
       else
        throw e;
    }
    return tmp$ + tmp$_0;
  };
  DefaultMessageFormatter.prototype.formatMessage_fud0c7$ = function (level, loggerName, marker, t, msg) {
    var tmp$ = level.name + ': [' + loggerName + '] ' + toString(marker != null ? marker.getName() : null) + ' ';
    var tmp$_0;
    try {
      tmp$_0 = toString(msg());
    }
     catch (e) {
      if (Kotlin.isType(e, Exception)) {
        tmp$_0 = 'Log message invocation failed: ' + e;
      }
       else
        throw e;
    }
    return tmp$ + tmp$_0 + this.throwableToString_0(t);
  };
  DefaultMessageFormatter.prototype.throwableToString_0 = function ($receiver) {
    if ($receiver == null) {
      return '';
    }
    var msg = '';
    var current = $receiver;
    while (current != null && !equals(current.cause, current)) {
      msg += ", Caused by: '" + toString(current.message) + "'";
      current = current.cause;
    }
    return msg;
  };
  DefaultMessageFormatter.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'DefaultMessageFormatter',
    interfaces: [MessageFormatter]
  };
  var DefaultMessageFormatter_instance = null;
  function DefaultMessageFormatter_getInstance() {
    if (DefaultMessageFormatter_instance === null) {
      new DefaultMessageFormatter();
    }
    return DefaultMessageFormatter_instance;
  }
  function KLogger() {
  }
  KLogger.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'KLogger',
    interfaces: []
  };
  function KMarkerFactory() {
    KMarkerFactory_instance = this;
  }
  KMarkerFactory.prototype.getMarker_61zpoe$ = function (name) {
    return new MarkerJS(name);
  };
  KMarkerFactory.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'KMarkerFactory',
    interfaces: []
  };
  var KMarkerFactory_instance = null;
  function KMarkerFactory_getInstance() {
    if (KMarkerFactory_instance === null) {
      new KMarkerFactory();
    }
    return KMarkerFactory_instance;
  }
  function KotlinLogging() {
    KotlinLogging_instance = this;
  }
  KotlinLogging.prototype.logger_o14v8n$ = function (func) {
    return new KLoggerJS(get_js(Kotlin.getKClassFromExpression(func)).name);
  };
  KotlinLogging.prototype.logger_61zpoe$ = function (name) {
    return new KLoggerJS(name);
  };
  KotlinLogging.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'KotlinLogging',
    interfaces: []
  };
  var KotlinLogging_instance = null;
  function KotlinLogging_getInstance() {
    if (KotlinLogging_instance === null) {
      new KotlinLogging();
    }
    return KotlinLogging_instance;
  }
  var LOG_LEVEL;
  function KotlinLoggingLevel(name, ordinal) {
    Enum.call(this);
    this.name$ = name;
    this.ordinal$ = ordinal;
  }
  function KotlinLoggingLevel_initFields() {
    KotlinLoggingLevel_initFields = function () {
    };
    KotlinLoggingLevel$TRACE_instance = new KotlinLoggingLevel('TRACE', 0);
    KotlinLoggingLevel$DEBUG_instance = new KotlinLoggingLevel('DEBUG', 1);
    KotlinLoggingLevel$INFO_instance = new KotlinLoggingLevel('INFO', 2);
    KotlinLoggingLevel$WARN_instance = new KotlinLoggingLevel('WARN', 3);
    KotlinLoggingLevel$ERROR_instance = new KotlinLoggingLevel('ERROR', 4);
  }
  var KotlinLoggingLevel$TRACE_instance;
  function KotlinLoggingLevel$TRACE_getInstance() {
    KotlinLoggingLevel_initFields();
    return KotlinLoggingLevel$TRACE_instance;
  }
  var KotlinLoggingLevel$DEBUG_instance;
  function KotlinLoggingLevel$DEBUG_getInstance() {
    KotlinLoggingLevel_initFields();
    return KotlinLoggingLevel$DEBUG_instance;
  }
  var KotlinLoggingLevel$INFO_instance;
  function KotlinLoggingLevel$INFO_getInstance() {
    KotlinLoggingLevel_initFields();
    return KotlinLoggingLevel$INFO_instance;
  }
  var KotlinLoggingLevel$WARN_instance;
  function KotlinLoggingLevel$WARN_getInstance() {
    KotlinLoggingLevel_initFields();
    return KotlinLoggingLevel$WARN_instance;
  }
  var KotlinLoggingLevel$ERROR_instance;
  function KotlinLoggingLevel$ERROR_getInstance() {
    KotlinLoggingLevel_initFields();
    return KotlinLoggingLevel$ERROR_instance;
  }
  KotlinLoggingLevel.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KotlinLoggingLevel',
    interfaces: [Enum]
  };
  function KotlinLoggingLevel$values() {
    return [KotlinLoggingLevel$TRACE_getInstance(), KotlinLoggingLevel$DEBUG_getInstance(), KotlinLoggingLevel$INFO_getInstance(), KotlinLoggingLevel$WARN_getInstance(), KotlinLoggingLevel$ERROR_getInstance()];
  }
  KotlinLoggingLevel.values = KotlinLoggingLevel$values;
  function KotlinLoggingLevel$valueOf(name) {
    switch (name) {
      case 'TRACE':
        return KotlinLoggingLevel$TRACE_getInstance();
      case 'DEBUG':
        return KotlinLoggingLevel$DEBUG_getInstance();
      case 'INFO':
        return KotlinLoggingLevel$INFO_getInstance();
      case 'WARN':
        return KotlinLoggingLevel$WARN_getInstance();
      case 'ERROR':
        return KotlinLoggingLevel$ERROR_getInstance();
      default:throwISE('No enum constant mu.KotlinLoggingLevel.' + name);
    }
  }
  KotlinLoggingLevel.valueOf_61zpoe$ = KotlinLoggingLevel$valueOf;
  function isLoggingEnabled($receiver) {
    return $receiver.ordinal >= LOG_LEVEL.ordinal;
  }
  var outputPipes;
  var messageFormatter;
  function Marker() {
  }
  Marker.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Marker',
    interfaces: []
  };
  function MessageFormatter() {
  }
  MessageFormatter.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'MessageFormatter',
    interfaces: []
  };
  function OutputPipes() {
  }
  OutputPipes.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'OutputPipes',
    interfaces: []
  };
  function KLoggerJS(loggerName, pipes, formatter) {
    if (pipes === void 0)
      pipes = outputPipes;
    if (formatter === void 0)
      formatter = messageFormatter;
    this.loggerName_0 = loggerName;
    this.pipes_0 = pipes;
    this.formatter_0 = formatter;
  }
  KLoggerJS.prototype.trace_nq59yw$ = function (msg) {
    this.logIfEnabled_0(KotlinLoggingLevel$TRACE_getInstance(), msg, getCallableRef('trace', function ($receiver, message) {
      return $receiver.trace_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.debug_nq59yw$ = function (msg) {
    this.logIfEnabled_0(KotlinLoggingLevel$DEBUG_getInstance(), msg, getCallableRef('debug', function ($receiver, message) {
      return $receiver.debug_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.info_nq59yw$ = function (msg) {
    this.logIfEnabled_0(KotlinLoggingLevel$INFO_getInstance(), msg, getCallableRef('info', function ($receiver, message) {
      return $receiver.info_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.warn_nq59yw$ = function (msg) {
    this.logIfEnabled_0(KotlinLoggingLevel$WARN_getInstance(), msg, getCallableRef('warn', function ($receiver, message) {
      return $receiver.warn_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.error_nq59yw$ = function (msg) {
    this.logIfEnabled_0(KotlinLoggingLevel$ERROR_getInstance(), msg, getCallableRef('error', function ($receiver, message) {
      return $receiver.error_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.trace_ca4k3s$ = function (t, msg) {
    this.logIfEnabled_1(KotlinLoggingLevel$TRACE_getInstance(), msg, t, getCallableRef('trace', function ($receiver, message) {
      return $receiver.trace_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.debug_ca4k3s$ = function (t, msg) {
    this.logIfEnabled_1(KotlinLoggingLevel$DEBUG_getInstance(), msg, t, getCallableRef('debug', function ($receiver, message) {
      return $receiver.debug_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.info_ca4k3s$ = function (t, msg) {
    this.logIfEnabled_1(KotlinLoggingLevel$INFO_getInstance(), msg, t, getCallableRef('info', function ($receiver, message) {
      return $receiver.info_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.warn_ca4k3s$ = function (t, msg) {
    this.logIfEnabled_1(KotlinLoggingLevel$WARN_getInstance(), msg, t, getCallableRef('warn', function ($receiver, message) {
      return $receiver.warn_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.error_ca4k3s$ = function (t, msg) {
    this.logIfEnabled_1(KotlinLoggingLevel$ERROR_getInstance(), msg, t, getCallableRef('error', function ($receiver, message) {
      return $receiver.error_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.trace_8jakm3$ = function (marker, msg) {
    this.logIfEnabled_2(KotlinLoggingLevel$TRACE_getInstance(), marker, msg, getCallableRef('trace', function ($receiver, message) {
      return $receiver.trace_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.debug_8jakm3$ = function (marker, msg) {
    this.logIfEnabled_2(KotlinLoggingLevel$DEBUG_getInstance(), marker, msg, getCallableRef('debug', function ($receiver, message) {
      return $receiver.debug_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.info_8jakm3$ = function (marker, msg) {
    this.logIfEnabled_2(KotlinLoggingLevel$INFO_getInstance(), marker, msg, getCallableRef('info', function ($receiver, message) {
      return $receiver.info_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.warn_8jakm3$ = function (marker, msg) {
    this.logIfEnabled_2(KotlinLoggingLevel$WARN_getInstance(), marker, msg, getCallableRef('warn', function ($receiver, message) {
      return $receiver.warn_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.error_8jakm3$ = function (marker, msg) {
    this.logIfEnabled_2(KotlinLoggingLevel$ERROR_getInstance(), marker, msg, getCallableRef('error', function ($receiver, message) {
      return $receiver.error_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.trace_o4svvp$ = function (marker, t, msg) {
    this.logIfEnabled_3(KotlinLoggingLevel$TRACE_getInstance(), marker, msg, t, getCallableRef('trace', function ($receiver, message) {
      return $receiver.trace_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.debug_o4svvp$ = function (marker, t, msg) {
    this.logIfEnabled_3(KotlinLoggingLevel$DEBUG_getInstance(), marker, msg, t, getCallableRef('debug', function ($receiver, message) {
      return $receiver.debug_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.info_o4svvp$ = function (marker, t, msg) {
    this.logIfEnabled_3(KotlinLoggingLevel$INFO_getInstance(), marker, msg, t, getCallableRef('info', function ($receiver, message) {
      return $receiver.info_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.warn_o4svvp$ = function (marker, t, msg) {
    this.logIfEnabled_3(KotlinLoggingLevel$WARN_getInstance(), marker, msg, t, getCallableRef('warn', function ($receiver, message) {
      return $receiver.warn_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.error_o4svvp$ = function (marker, t, msg) {
    this.logIfEnabled_3(KotlinLoggingLevel$ERROR_getInstance(), marker, msg, t, getCallableRef('error', function ($receiver, message) {
      return $receiver.error_s8jyv4$(message), Unit;
    }.bind(null, this.pipes_0)));
  };
  KLoggerJS.prototype.logIfEnabled_0 = function ($receiver, msg, logFunction) {
    if (isLoggingEnabled($receiver)) {
      logFunction(this.formatMessage_pijeg6$($receiver, this.loggerName_0, msg));
    }
  };
  KLoggerJS.prototype.logIfEnabled_1 = function ($receiver, msg, t, logFunction) {
    if (isLoggingEnabled($receiver)) {
      logFunction(this.formatMessage_hqgb2y$($receiver, this.loggerName_0, t, msg));
    }
  };
  KLoggerJS.prototype.logIfEnabled_2 = function ($receiver, marker, msg, logFunction) {
    if (isLoggingEnabled($receiver)) {
      logFunction(this.formatMessage_i9qi47$($receiver, this.loggerName_0, marker, msg));
    }
  };
  KLoggerJS.prototype.logIfEnabled_3 = function ($receiver, marker, msg, t, logFunction) {
    if (isLoggingEnabled($receiver)) {
      logFunction(this.formatMessage_fud0c7$($receiver, this.loggerName_0, marker, t, msg));
    }
  };
  KLoggerJS.prototype.formatMessage_pijeg6$ = function (level, loggerName, msg) {
    return this.formatter_0.formatMessage_pijeg6$(level, loggerName, msg);
  };
  KLoggerJS.prototype.formatMessage_hqgb2y$ = function (level, loggerName, t, msg) {
    return this.formatter_0.formatMessage_hqgb2y$(level, loggerName, t, msg);
  };
  KLoggerJS.prototype.formatMessage_i9qi47$ = function (level, loggerName, marker, msg) {
    return this.formatter_0.formatMessage_i9qi47$(level, loggerName, marker, msg);
  };
  KLoggerJS.prototype.formatMessage_fud0c7$ = function (level, loggerName, marker, t, msg) {
    return this.formatter_0.formatMessage_fud0c7$(level, loggerName, marker, t, msg);
  };
  KLoggerJS.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KLoggerJS',
    interfaces: [MessageFormatter, KLogger]
  };
  function MarkerJS(name) {
    this.name_0 = name;
  }
  MarkerJS.prototype.getName = function () {
    return this.name_0;
  };
  MarkerJS.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'MarkerJS',
    interfaces: [Marker]
  };
  var package$mu = _.mu || (_.mu = {});
  var package$internal = package$mu.internal || (package$mu.internal = {});
  package$internal.toStringSafe_qhgloa$ = toStringSafe;
  Object.defineProperty(package$mu, 'ConsoleOutputPipes', {
    get: ConsoleOutputPipes_getInstance
  });
  $$importsForInline$$['kotlin-logging'] = _;
  Object.defineProperty(package$mu, 'DefaultMessageFormatter', {
    get: DefaultMessageFormatter_getInstance
  });
  package$mu.KLogger = KLogger;
  Object.defineProperty(package$mu, 'KMarkerFactory', {
    get: KMarkerFactory_getInstance
  });
  Object.defineProperty(package$mu, 'KotlinLogging', {
    get: KotlinLogging_getInstance
  });
  Object.defineProperty(package$mu, 'LOG_LEVEL', {
    get: function () {
      return LOG_LEVEL;
    },
    set: function (value) {
      LOG_LEVEL = value;
    }
  });
  Object.defineProperty(KotlinLoggingLevel, 'TRACE', {
    get: KotlinLoggingLevel$TRACE_getInstance
  });
  Object.defineProperty(KotlinLoggingLevel, 'DEBUG', {
    get: KotlinLoggingLevel$DEBUG_getInstance
  });
  Object.defineProperty(KotlinLoggingLevel, 'INFO', {
    get: KotlinLoggingLevel$INFO_getInstance
  });
  Object.defineProperty(KotlinLoggingLevel, 'WARN', {
    get: KotlinLoggingLevel$WARN_getInstance
  });
  Object.defineProperty(KotlinLoggingLevel, 'ERROR', {
    get: KotlinLoggingLevel$ERROR_getInstance
  });
  package$mu.KotlinLoggingLevel = KotlinLoggingLevel;
  package$mu.isLoggingEnabled_pm19j7$ = isLoggingEnabled;
  Object.defineProperty(package$mu, 'outputPipes', {
    get: function () {
      return outputPipes;
    },
    set: function (value) {
      outputPipes = value;
    }
  });
  Object.defineProperty(package$mu, 'messageFormatter', {
    get: function () {
      return messageFormatter;
    },
    set: function (value) {
      messageFormatter = value;
    }
  });
  package$mu.Marker = Marker;
  package$mu.MessageFormatter = MessageFormatter;
  package$mu.OutputPipes = OutputPipes;
  package$internal.KLoggerJS = KLoggerJS;
  package$internal.MarkerJS = MarkerJS;
  LOG_LEVEL = KotlinLoggingLevel$INFO_getInstance();
  outputPipes = ConsoleOutputPipes_getInstance();
  messageFormatter = DefaultMessageFormatter_getInstance();
  Kotlin.defineModule('kotlin-logging', _);
  return _;
}));

//# sourceMappingURL=kotlin-logging.js.map
