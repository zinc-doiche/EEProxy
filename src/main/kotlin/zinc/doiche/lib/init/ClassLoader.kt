package zinc.doiche.lib.init

import org.reflections.Reflections

class ClassLoader {
    private val processors = ArrayList<Processor<*>>()

    fun <T> add(processorFactory: (ProcessorFactory<T>) -> ProcessorFactory<T>): ClassLoader {
        val factory = processorFactory.invoke(ProcessorFactory.factory())
        return add(factory.create())
    }

    fun add(processor: Processor<*>): ClassLoader {
        processors.add(processor)
        return this
    }

    fun process() {
        val objectList = mutableListOf<Any?>()
        val reflections = Reflections("zinc.doiche")

        processors.forEach {
            val preObject = it.preProcess()
            objectList.add(preObject)
        }

        processors.forEachIndexed { index, processor ->
            val preObject = objectList[index]
            processor.process(reflections, preObject)
        }
        processors.forEachIndexed { index, processor ->
            val preObject = objectList[index]
            processor.postProcess(preObject)
        }
    }
}
