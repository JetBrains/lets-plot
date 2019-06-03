(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlin-test'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlin-test'.");
    }
    root['kotlin-test'] = factory(typeof this['kotlin-test'] === 'undefined' ? {} : this['kotlin-test'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var ensureNotNull = Kotlin.ensureNotNull;
  var Throwable = Error;
  var defineInlineFunction = Kotlin.defineInlineFunction;
  var wrapFunction = Kotlin.wrapFunction;
  var toString = Kotlin.toString;
  var equals = Kotlin.equals;
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var AssertionError_init = Kotlin.kotlin.AssertionError_init;
  var AssertionError_init_0 = Kotlin.kotlin.AssertionError_init_pdl1vj$;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var Annotation = Kotlin.kotlin.Annotation;
  var Unit = Kotlin.kotlin.Unit;
  var println = Kotlin.kotlin.io.println_s8jyv4$;
  var throwCCE = Kotlin.throwCCE;
  var IllegalArgumentException_init = Kotlin.kotlin.IllegalArgumentException_init_pdl1vj$;
  var getCallableRef = Kotlin.getCallableRef;
  var to = Kotlin.kotlin.to_ujzrz7$;
  var mapOf = Kotlin.kotlin.collections.mapOf_qfcya0$;
  function get_asserter() {
    return _asserter != null ? _asserter : lookupAsserter();
  }
  var _asserter;
  function assertTrue(message, block) {
    if (message === void 0)
      message = null;
    assertTrue_0(block(), message);
  }
  function assertTrue_0(actual, message) {
    if (message === void 0)
      message = null;
    return get_asserter().assertTrue_4mavae$(message != null ? message : 'Expected value to be true.', actual);
  }
  function assertFalse(message, block) {
    if (message === void 0)
      message = null;
    assertFalse_0(block(), message);
  }
  function assertFalse_0(actual, message) {
    if (message === void 0)
      message = null;
    return get_asserter().assertTrue_4mavae$(message != null ? message : 'Expected value to be false.', !actual);
  }
  function assertEquals(expected, actual, message) {
    if (message === void 0)
      message = null;
    get_asserter().assertEquals_lzc6tz$(message, expected, actual);
  }
  function assertNotEquals(illegal, actual, message) {
    if (message === void 0)
      message = null;
    get_asserter().assertNotEquals_lzc6tz$(message, illegal, actual);
  }
  function assertSame(expected, actual, message) {
    if (message === void 0)
      message = null;
    get_asserter().assertSame_lzc6tz$(message, expected, actual);
  }
  function assertNotSame(illegal, actual, message) {
    if (message === void 0)
      message = null;
    get_asserter().assertNotSame_lzc6tz$(message, illegal, actual);
  }
  function assertNotNull(actual, message) {
    if (message === void 0)
      message = null;
    get_asserter().assertNotNull_67rc9h$(message, actual);
    return ensureNotNull(actual);
  }
  function assertNotNull_0(actual, message, block) {
    if (message === void 0)
      message = null;
    get_asserter().assertNotNull_67rc9h$(message, actual);
    if (actual != null) {
      block(actual);
    }
  }
  function assertNull(actual, message) {
    if (message === void 0)
      message = null;
    get_asserter().assertNull_67rc9h$(message, actual);
  }
  function fail(message) {
    if (message === void 0)
      message = null;
    get_asserter().fail_pdl1vj$(message);
  }
  function expect(expected, block) {
    assertEquals(expected, block());
  }
  function expect_0(expected, message, block) {
    assertEquals(expected, block(), message);
  }
  function assertFails(block) {
    return assertFails_0(null, block);
  }
  function assertFails_0(message, block) {
    try {
      block();
    }
     catch (e) {
      if (Kotlin.isType(e, Throwable)) {
        assertEquals(e.message, e.message);
        return e;
      }
       else
        throw e;
    }
    get_asserter().fail_pdl1vj$(messagePrefix(message) + 'Expected an exception to be thrown, but was completed successfully.');
  }
  var assertFailsWith = defineInlineFunction('kotlin-test.kotlin.test.assertFailsWith_cnau6l$', wrapFunction(function () {
    var getKClass = Kotlin.getKClass;
    var assertFailsWith = _.kotlin.test.assertFailsWith_l9oqa2$;
    return function (T_0, isT, message, block) {
      if (message === void 0)
        message = null;
      return assertFailsWith(getKClass(T_0), message, block);
    };
  }));
  function assertFailsWith_0(exceptionClass, block) {
    return assertFailsWith_1(exceptionClass, null, block);
  }
  function Asserter() {
  }
  Asserter.prototype.assertTrue_o10pc4$ = function (lazyMessage, actual) {
    if (!actual) {
      this.fail_pdl1vj$(lazyMessage());
    }
  };
  function Asserter$assertTrue$lambda(closure$message) {
    return function () {
      return closure$message;
    };
  }
  Asserter.prototype.assertTrue_4mavae$ = function (message, actual) {
    this.assertTrue_o10pc4$(Asserter$assertTrue$lambda(message), actual);
  };
  function Asserter$assertEquals$lambda(closure$message, closure$expected, closure$actual) {
    return function () {
      return messagePrefix(closure$message) + ('Expected <' + toString(closure$expected) + '>, actual <' + toString(closure$actual) + '>.');
    };
  }
  Asserter.prototype.assertEquals_lzc6tz$ = function (message, expected, actual) {
    this.assertTrue_o10pc4$(Asserter$assertEquals$lambda(message, expected, actual), equals(actual, expected));
  };
  function Asserter$assertNotEquals$lambda(closure$message, closure$actual) {
    return function () {
      return messagePrefix(closure$message) + ('Illegal value: <' + toString(closure$actual) + '>.');
    };
  }
  Asserter.prototype.assertNotEquals_lzc6tz$ = function (message, illegal, actual) {
    this.assertTrue_o10pc4$(Asserter$assertNotEquals$lambda(message, actual), !equals(actual, illegal));
  };
  function Asserter$assertSame$lambda(closure$message, closure$expected, closure$actual) {
    return function () {
      return messagePrefix(closure$message) + ('Expected <' + toString(closure$expected) + '>, actual <' + toString(closure$actual) + '> is not same.');
    };
  }
  Asserter.prototype.assertSame_lzc6tz$ = function (message, expected, actual) {
    this.assertTrue_o10pc4$(Asserter$assertSame$lambda(message, expected, actual), actual === expected);
  };
  function Asserter$assertNotSame$lambda(closure$message, closure$actual) {
    return function () {
      return messagePrefix(closure$message) + ('Expected not same as <' + toString(closure$actual) + '>.');
    };
  }
  Asserter.prototype.assertNotSame_lzc6tz$ = function (message, illegal, actual) {
    this.assertTrue_o10pc4$(Asserter$assertNotSame$lambda(message, actual), actual !== illegal);
  };
  function Asserter$assertNull$lambda(closure$message, closure$actual) {
    return function () {
      return messagePrefix(closure$message) + ('Expected value to be null, but was: <' + toString(closure$actual) + '>.');
    };
  }
  Asserter.prototype.assertNull_67rc9h$ = function (message, actual) {
    this.assertTrue_o10pc4$(Asserter$assertNull$lambda(message, actual), actual == null);
  };
  function Asserter$assertNotNull$lambda(closure$message) {
    return function () {
      return messagePrefix(closure$message) + 'Expected value to be not null.';
    };
  }
  Asserter.prototype.assertNotNull_67rc9h$ = function (message, actual) {
    this.assertTrue_o10pc4$(Asserter$assertNotNull$lambda(message), actual != null);
  };
  Asserter.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Asserter',
    interfaces: []
  };
  function AsserterContributor() {
  }
  AsserterContributor.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'AsserterContributor',
    interfaces: []
  };
  function DefaultAsserter() {
    DefaultAsserter_instance = this;
  }
  DefaultAsserter.prototype.fail_pdl1vj$ = function (message) {
    if (message == null)
      throw AssertionError_init();
    else
      throw AssertionError_init_0(message);
  };
  DefaultAsserter.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'DefaultAsserter',
    interfaces: [Asserter]
  };
  var DefaultAsserter_instance = null;
  function DefaultAsserter_getInstance() {
    if (DefaultAsserter_instance === null) {
      new DefaultAsserter();
    }
    return DefaultAsserter_instance;
  }
  function DefaultAsserter_0() {
    return DefaultAsserter_getInstance();
  }
  function messagePrefix(message) {
    return message == null ? '' : toString(message) + '. ';
  }
  function overrideAsserter(value) {
    var $receiver = _asserter;
    _asserter = value;
    return $receiver;
  }
  function setAdapter(adapter) {
    setAdapter_0(adapter);
  }
  function Test() {
  }
  Test.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Test',
    interfaces: [Annotation]
  };
  function Ignore() {
  }
  Ignore.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Ignore',
    interfaces: [Annotation]
  };
  function BeforeTest() {
  }
  BeforeTest.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BeforeTest',
    interfaces: [Annotation]
  };
  function AfterTest() {
  }
  AfterTest.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AfterTest',
    interfaces: [Annotation]
  };
  function assertHook$lambda(f) {
    return Unit;
  }
  var assertHook;
  function DefaultJsAsserter() {
    DefaultJsAsserter_instance = this;
    this.e_0 = undefined;
    this.a_0 = undefined;
  }
  DefaultJsAsserter.prototype.assertEquals_lzc6tz$ = function (message, expected, actual) {
    this.e_0 = expected;
    this.a_0 = actual;
    Asserter.prototype.assertEquals_lzc6tz$.call(this, message, expected, actual);
  };
  DefaultJsAsserter.prototype.assertNotEquals_lzc6tz$ = function (message, illegal, actual) {
    this.e_0 = illegal;
    this.a_0 = actual;
    Asserter.prototype.assertNotEquals_lzc6tz$.call(this, message, illegal, actual);
  };
  DefaultJsAsserter.prototype.assertSame_lzc6tz$ = function (message, expected, actual) {
    this.e_0 = expected;
    this.a_0 = actual;
    Asserter.prototype.assertSame_lzc6tz$.call(this, message, expected, actual);
  };
  DefaultJsAsserter.prototype.assertNotSame_lzc6tz$ = function (message, illegal, actual) {
    this.e_0 = illegal;
    this.a_0 = actual;
    Asserter.prototype.assertNotSame_lzc6tz$.call(this, message, illegal, actual);
  };
  DefaultJsAsserter.prototype.assertNull_67rc9h$ = function (message, actual) {
    this.a_0 = actual;
    Asserter.prototype.assertNull_67rc9h$.call(this, message, actual);
  };
  DefaultJsAsserter.prototype.assertNotNull_67rc9h$ = function (message, actual) {
    this.a_0 = actual;
    Asserter.prototype.assertNotNull_67rc9h$.call(this, message, actual);
  };
  DefaultJsAsserter.prototype.assertTrue_o10pc4$ = function (lazyMessage, actual) {
    if (!actual) {
      this.failWithMessage_0(lazyMessage);
    }
     else {
      this.invokeHook_0(true, lazyMessage);
    }
  };
  function DefaultJsAsserter$assertTrue$lambda(closure$message) {
    return function () {
      return closure$message;
    };
  }
  DefaultJsAsserter.prototype.assertTrue_4mavae$ = function (message, actual) {
    this.assertTrue_o10pc4$(DefaultJsAsserter$assertTrue$lambda(message), actual);
  };
  function DefaultJsAsserter$fail$lambda(closure$message) {
    return function () {
      return closure$message;
    };
  }
  DefaultJsAsserter.prototype.fail_pdl1vj$ = function (message) {
    this.failWithMessage_0(DefaultJsAsserter$fail$lambda(message));
  };
  function DefaultJsAsserter$failWithMessage$lambda(closure$message) {
    return function () {
      return closure$message;
    };
  }
  DefaultJsAsserter.prototype.failWithMessage_0 = function (lazyMessage) {
    var message = lazyMessage();
    this.invokeHook_0(false, DefaultJsAsserter$failWithMessage$lambda(message));
    if (message == null)
      throw AssertionError_init();
    else
      throw AssertionError_init_0(message);
  };
  function DefaultJsAsserter$invokeHook$ObjectLiteral(closure$result, closure$lazyMessage) {
    this.result_13foyd$_0 = closure$result;
    this.expected_q67qvk$_0 = DefaultJsAsserter_getInstance().e_0;
    this.actual_wkq0m2$_0 = DefaultJsAsserter_getInstance().a_0;
    this.lazyMessage_wfmiv$_0 = closure$lazyMessage;
  }
  Object.defineProperty(DefaultJsAsserter$invokeHook$ObjectLiteral.prototype, 'result', {
    get: function () {
      return this.result_13foyd$_0;
    }
  });
  Object.defineProperty(DefaultJsAsserter$invokeHook$ObjectLiteral.prototype, 'expected', {
    get: function () {
      return this.expected_q67qvk$_0;
    }
  });
  Object.defineProperty(DefaultJsAsserter$invokeHook$ObjectLiteral.prototype, 'actual', {
    get: function () {
      return this.actual_wkq0m2$_0;
    }
  });
  Object.defineProperty(DefaultJsAsserter$invokeHook$ObjectLiteral.prototype, 'lazyMessage', {
    get: function () {
      return this.lazyMessage_wfmiv$_0;
    }
  });
  DefaultJsAsserter$invokeHook$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: []
  };
  DefaultJsAsserter.prototype.invokeHook_0 = function (result, lazyMessage) {
    try {
      assertHook(new DefaultJsAsserter$invokeHook$ObjectLiteral(result, lazyMessage));
    }
    finally {
      this.e_0 = undefined;
      this.a_0 = undefined;
    }
  };
  DefaultJsAsserter.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'DefaultJsAsserter',
    interfaces: [Asserter]
  };
  var DefaultJsAsserter_instance = null;
  function DefaultJsAsserter_getInstance() {
    if (DefaultJsAsserter_instance === null) {
      new DefaultJsAsserter();
    }
    return DefaultJsAsserter_instance;
  }
  function todo(block) {
    println('TODO at ' + toString(block));
  }
  function assertFailsWith_1(exceptionClass, message, block) {
    var tmp$;
    var exception = assertFails_0(message, block);
    assertTrue_0(exceptionClass.isInstance_s8jyv4$(exception), messagePrefix(message) + ('Expected an exception of ' + exceptionClass + ' to be thrown, but was ' + exception));
    return Kotlin.isType(tmp$ = exception, Throwable) ? tmp$ : throwCCE();
  }
  function lookupAsserter() {
    return DefaultJsAsserter_getInstance();
  }
  function setAdapter_0(adapter) {
    var tmp$;
    if (typeof adapter === 'string') {
      var tmp$_0;
      if ((tmp$ = NAME_TO_ADAPTER.get_11rb$(adapter)) != null) {
        setAdapter_0(tmp$());
        tmp$_0 = Unit;
      }
       else
        tmp$_0 = null;
      if (tmp$_0 == null)
        throw IllegalArgumentException_init("Unsupported test framework adapter: '" + adapter.toString() + "'");
    }
     else {
      currentAdapter = adapter;
    }
  }
  function setAssertHook(hook) {
    assertHook = hook;
  }
  function suite(name, ignored, suiteFn) {
    adapter().suite(name, ignored, suiteFn);
  }
  function test(name, ignored, testFn) {
    adapter().test(name, ignored, testFn);
  }
  var currentAdapter;
  function adapter() {
    var result = currentAdapter != null ? currentAdapter : detectAdapter();
    currentAdapter = result;
    return result;
  }
  function detectAdapter() {
    if (isQUnit())
      return new QUnitAdapter();
    else if (isJasmine())
      return new JasmineLikeAdapter();
    else
      return new BareAdapter();
  }
  var NAME_TO_ADAPTER;
  function BareAdapter() {
  }
  BareAdapter.prototype.suite = function (name, ignored, suiteFn) {
    if (!ignored) {
      suiteFn();
    }
  };
  BareAdapter.prototype.test = function (name, ignored, testFn) {
    if (!ignored) {
      testFn();
    }
  };
  BareAdapter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'BareAdapter',
    interfaces: []
  };
  function isQUnit() {
    return typeof QUnit !== 'undefined';
  }
  function isJasmine() {
    return typeof describe === 'function' && typeof it === 'function';
  }
  function JasmineLikeAdapter() {
  }
  JasmineLikeAdapter.prototype.suite = function (name, ignored, suiteFn) {
    if (ignored) {
      xdescribe(name, suiteFn);
    }
     else {
      describe(name, suiteFn);
    }
  };
  JasmineLikeAdapter.prototype.test = function (name, ignored, testFn) {
    if (ignored) {
      xit(name, testFn);
    }
     else {
      it(name, testFn);
    }
  };
  JasmineLikeAdapter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'JasmineLikeAdapter',
    interfaces: []
  };
  function QUnitAdapter() {
    this.ignoredSuite = false;
  }
  QUnitAdapter.prototype.suite = function (name, ignored, suiteFn) {
    var prevIgnore = this.ignoredSuite;
    this.ignoredSuite = this.ignoredSuite | ignored;
    QUnit.module(name, suiteFn);
    this.ignoredSuite = prevIgnore;
  };
  QUnitAdapter.prototype.test = function (name, ignored, testFn) {
    if (ignored | this.ignoredSuite) {
      QUnit.skip(name, this.wrapTest_0(testFn));
    }
     else {
      QUnit.test(name, this.wrapTest_0(testFn));
    }
  };
  function QUnitAdapter$wrapTest$lambda$lambda(closure$assertionsHappened, closure$assert) {
    return function (testResult) {
      closure$assertionsHappened.v = true;
      closure$assert.ok(testResult.result, testResult.lazyMessage());
      return Unit;
    };
  }
  function QUnitAdapter$wrapTest$lambda(closure$testFn) {
    return function (assert) {
      var assertionsHappened = {v: false};
      assertHook = QUnitAdapter$wrapTest$lambda$lambda(assertionsHappened, assert);
      closure$testFn();
      if (!assertionsHappened.v) {
        assertTrue_0(true, 'A test with no assertions is considered successful');
      }
      return Unit;
    };
  }
  QUnitAdapter.prototype.wrapTest_0 = function (testFn) {
    return QUnitAdapter$wrapTest$lambda(testFn);
  };
  QUnitAdapter.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'QUnitAdapter',
    interfaces: []
  };
  var package$kotlin = _.kotlin || (_.kotlin = {});
  var package$test = package$kotlin.test || (package$kotlin.test = {});
  Object.defineProperty(package$test, 'asserter', {
    get: get_asserter
  });
  Object.defineProperty(package$test, '_asserter_8be2vx$', {
    get: function () {
      return _asserter;
    },
    set: function (value) {
      _asserter = value;
    }
  });
  package$test.assertTrue_i7pyzi$ = assertTrue;
  package$test.assertTrue_ifx8ge$ = assertTrue_0;
  package$test.assertFalse_i7pyzi$ = assertFalse;
  package$test.assertFalse_ifx8ge$ = assertFalse_0;
  package$test.assertEquals_3m0tl5$ = assertEquals;
  package$test.assertNotEquals_3m0tl5$ = assertNotEquals;
  package$test.assertSame_3m0tl5$ = assertSame;
  package$test.assertNotSame_3m0tl5$ = assertNotSame;
  package$test.assertNotNull_tkjle6$ = assertNotNull;
  package$test.assertNotNull_k6pbc4$ = assertNotNull_0;
  package$test.assertNull_dzvdf1$ = assertNull;
  package$test.fail_pdl1vj$ = fail;
  package$test.expect_e96eyq$ = expect;
  package$test.expect_rr7wld$ = expect_0;
  package$test.assertFails_o14v8n$ = assertFails;
  package$test.assertFails_9bodf6$ = assertFails_0;
  package$test.assertFailsWith_l9oqa2$ = assertFailsWith_1;
  package$test.assertFailsWith_jbbixx$ = assertFailsWith_0;
  package$test.Asserter = Asserter;
  package$test.AsserterContributor = AsserterContributor;
  Object.defineProperty(package$test, 'DefaultAsserter', {
    get: DefaultAsserter_getInstance
  });
  package$test.DefaultAsserterConstructor = DefaultAsserter_0;
  package$test.messagePrefix_7efafy$ = messagePrefix;
  package$test.overrideAsserter_wbnzx$ = overrideAsserter;
  _.setAdapter = setAdapter;
  package$test.Test = Test;
  package$test.Ignore = Ignore;
  package$test.BeforeTest = BeforeTest;
  package$test.AfterTest = AfterTest;
  Object.defineProperty(package$test, 'assertHook_8be2vx$', {
    get: function () {
      return assertHook;
    },
    set: function (value) {
      assertHook = value;
    }
  });
  Object.defineProperty(package$test, 'DefaultJsAsserter', {
    get: DefaultJsAsserter_getInstance
  });
  package$test.todo_o14v8n$ = todo;
  package$test.lookupAsserter_8be2vx$ = lookupAsserter;
  package$test.setAdapter_kcmwxo$ = setAdapter_0;
  package$test.setAssertHook_4duqou$ = setAssertHook;
  package$test.suite = suite;
  package$test.test = test;
  Object.defineProperty(package$test, 'currentAdapter_8be2vx$', {
    get: function () {
      return currentAdapter;
    },
    set: function (value) {
      currentAdapter = value;
    }
  });
  package$test.adapter_8be2vx$ = adapter;
  package$test.detectAdapter_8be2vx$ = detectAdapter;
  Object.defineProperty(package$test, 'NAME_TO_ADAPTER_8be2vx$', {
    get: function () {
      return NAME_TO_ADAPTER;
    }
  });
  var package$adapters = package$test.adapters || (package$test.adapters = {});
  package$adapters.BareAdapter = BareAdapter;
  package$adapters.isQUnit_8be2vx$ = isQUnit;
  package$adapters.isJasmine_8be2vx$ = isJasmine;
  package$adapters.JasmineLikeAdapter = JasmineLikeAdapter;
  package$adapters.QUnitAdapter = QUnitAdapter;
  DefaultAsserter.prototype.assertTrue_o10pc4$ = Asserter.prototype.assertTrue_o10pc4$;
  DefaultAsserter.prototype.assertTrue_4mavae$ = Asserter.prototype.assertTrue_4mavae$;
  DefaultAsserter.prototype.assertEquals_lzc6tz$ = Asserter.prototype.assertEquals_lzc6tz$;
  DefaultAsserter.prototype.assertNotEquals_lzc6tz$ = Asserter.prototype.assertNotEquals_lzc6tz$;
  DefaultAsserter.prototype.assertSame_lzc6tz$ = Asserter.prototype.assertSame_lzc6tz$;
  DefaultAsserter.prototype.assertNotSame_lzc6tz$ = Asserter.prototype.assertNotSame_lzc6tz$;
  DefaultAsserter.prototype.assertNull_67rc9h$ = Asserter.prototype.assertNull_67rc9h$;
  DefaultAsserter.prototype.assertNotNull_67rc9h$ = Asserter.prototype.assertNotNull_67rc9h$;
  _asserter = null;
  assertHook = assertHook$lambda;
  currentAdapter = null;
  NAME_TO_ADAPTER = mapOf([to('qunit', getCallableRef('QUnitAdapter', function () {
    return new QUnitAdapter();
  })), to('jasmine', getCallableRef('JasmineLikeAdapter', function () {
    return new JasmineLikeAdapter();
  })), to('mocha', getCallableRef('JasmineLikeAdapter', function () {
    return new JasmineLikeAdapter();
  })), to('jest', getCallableRef('JasmineLikeAdapter', function () {
    return new JasmineLikeAdapter();
  })), to('auto', getCallableRef('detectAdapter', function () {
    return detectAdapter();
  }))]);
  Kotlin.defineModule('kotlin-test', _);
  return _;
}));

//# sourceMappingURL=kotlin-test.js.map
