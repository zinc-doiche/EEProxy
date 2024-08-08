package zinc.doiche.lib.init

import org.reflections.Reflections

class Processor<T>(
    val preProcess: () -> T?,
    private val process: (Reflections, T?) -> Unit,
    private val postProcess: (T?) -> Unit
) {
    fun process(reflections: Reflections, preObject: Any?) {
        process.invoke(reflections, preObject(preObject))
    }

    @Suppress("UNCHECKED_CAST")
    private fun preObject(any: Any?): T? = any.runCatching {
        this as T?
    }.getOrNull()

    fun postProcess(preObject: Any?) {
        postProcess.invoke(preObject(preObject))
    }
}