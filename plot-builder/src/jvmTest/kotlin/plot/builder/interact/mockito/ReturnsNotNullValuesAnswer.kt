package jetbrains.datalore.plot.builder.interact.mockito

import jetbrains.datalore.base.values.Color
import org.mockito.internal.stubbing.defaultanswers.ReturnsMoreEmptyValues
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer

class ReturnsNotNullValuesAnswer : Answer<Any?> {
    private val delegate: Answer<Any> = ReturnsMoreEmptyValues()

    override fun answer(invocation: InvocationOnMock): Any? {
        val value = delegate.answer(invocation)
        if (value != null) {
            return value
        }

        @Suppress("MoveVariableDeclarationIntoWhen")
        val valueType = invocation.method.returnType
        return when (valueType) {
            Color::class.java ->
                Color.BLACK
//            else -> throw IllegalStateException("No default value configured for type: $valueType")
            else -> null
        }
    }
}