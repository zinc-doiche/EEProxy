package zinc.doiche.lib.init

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import com.mojang.brigadier.tree.LiteralCommandNode
import com.velocitypowered.api.command.BrigadierCommand
import com.velocitypowered.api.command.CommandManager
import com.velocitypowered.api.event.EventManager
import com.velocitypowered.api.proxy.ProxyServer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.reflections.Reflections
import zinc.doiche.EEProxy
import zinc.doiche.EEProxy.Companion.proxy
import zinc.doiche.lib.*
import zinc.doiche.service.Service
import zinc.doiche.util.*
import java.io.File
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Comparator

interface ProcessorFactory<T> {
    fun preProcess(preProcess: () -> T?): ProcessorFactory<T>
    fun process(processor: (Reflections, T?) -> Unit): ProcessorFactory<T>
    fun postProcess(postProcess: (T?) -> Unit): ProcessorFactory<T>
    fun create(): Processor<T>

    companion object {
        fun <T> factory(): ProcessorFactory<T> {
            return ProcessorFactoryImpl()
        }

        fun listener(proxy: EEProxy, eventManager: EventManager): Processor<Nothing> = factory<Nothing>()
            .process { reflections, _ ->
                reflections.getTypesAnnotatedWith(ListenerRegistry::class.java).forEach { clazz ->
                    val listener = clazz.getDeclaredConstructor().newInstance()

                    eventManager.register(proxy, listener)
                }
            }
            .create()

        fun command(commandManager: CommandManager): Processor<Nothing> = factory<Nothing>()
            .process { reflections, list ->
                reflections.getTypesAnnotatedWith(CommandRegistry::class.java).forEach { clazz ->
                    val commandRegistry = clazz.getDeclaredConstructor().newInstance()
//                    val commandMap = Bukkit.getCommandMap()
                    for (method in clazz.declaredMethods) {
                        if(method.isAnnotationPresent(CommandFactory::class.java)) {
                            val factory = method.getAnnotation(CommandFactory::class.java)
                            val aliases = factory.aliases

                            @Suppress("UNCHECKED_CAST")
                            val brigadierCommand = method.invoke(commandRegistry) as BrigadierCommand

                            CommandHolder(brigadierCommand.node.name, brigadierCommand, aliases).let {
                                logCommand(it)
                                it.register(commandManager)
                            }
                        }
                    }
                }
            }
            .create()

        fun translatable(replacer: (String) -> String) = factory<Array<File>>()
            .preProcess {
                FileUtils.getConfig("translation").listFiles()
            }
            .process { reflections, files ->
                val miniMessage = MiniMessage.miniMessage()

                reflections.getTypesAnnotatedWith(TranslationRegistry::class.java).forEach { clazz ->
                    // find ?: create
                    val file = files!!.find {
                        it.name.contains(clazz.simpleName)
                    } ?: run {
                        val path = "/translation/" + if(clazz.packageName.contains("service")) {
                            clazz.packageName.substringAfterLast("service.")
                                .replace('.', '/') + "/"
                        } else {
                            ""
                        } + clazz.simpleName

                        File(proxy.dataDirectory.toFile(), "$path.json").apply {
                            if(!exists()) {
                                if(parentFile?.exists() != true) {
                                    parentFile?.mkdirs() ?: File(proxy.dataDirectory.toFile(), path).mkdirs()
                                }

                                clazz.declaredFields.filter {
                                    it.isAnnotationPresent(Translatable::class.java)
                                }.associate {
                                    it.getAnnotation(Translatable::class.java).let { translatable ->
                                        val value = translatable.defaultValues.takeIf { array ->
                                            array.isNotEmpty()
                                        } ?: translatable.defaultValue

                                        translatable.key to value
                                    }
                                }.let {
                                    LoggerProvider.logger.info("[ Translation ] Creating new translation file: $path.json")
                                    createNewFile()
                                    write(it)
                                }
                            }
                        }
                    }

                    val map = file.toMapOf(String::class.java, Any::class.java)
                        .toMutableMap()// as MutableMap<String, Any>
                    val newKeys = mutableSetOf<String>()
                    val keys = mutableSetOf<String>()

                    // read
                    clazz.declaredFields.forEach { field ->
                        if (field.isAnnotationPresent(Translatable::class.java)) {
                            val type = field.type
                            val translatable = field.getAnnotation(Translatable::class.java)
                            val key = translatable.key

                            if(key in map) {
                                keys.add(key)
                            } else {
                                map[key] = translatable.defaultValues.takeIf { array ->
                                    array.isNotEmpty()
                                } ?: translatable.defaultValue
                                newKeys.add(key)
                            }

                            field.isAccessible = true

                            map[key]?.let {
                                when(it) {
                                    is String -> replacer(it).let { string ->
                                            when(type) {
                                                String::class.java -> {
                                                    field.set(null, string)
                                                }
                                                Component::class.java -> {
                                                    val component = miniMessage.deserialize(string)
                                                    field.set(null, component)
                                                }
                                                else -> null
                                            }
                                        }
                                    else -> (it.takeIf { array ->
                                            array.javaClass == String::class.java.arrayType()
                                        } as? Array<*>)
                                        ?.map { element -> replacer(element as String) }
                                        ?.let { array ->
                                            when(type) {
                                                String::class.java.arrayType() -> {
                                                    field.set(null, it)
                                                }
                                                Component::class.java.arrayType() -> {
                                                    val components = array.map(miniMessage::deserialize)
                                                    field.set(null, components.toTypedArray())
                                                }
                                                else -> null
                                            }
                                        }
                                }
                            } ?: throw IllegalStateException("Invalid translation value: ${FileUtils.gson.toJson(map[key])} in $map")
                        }
                    }

                    //write & update
                    val keyUnion = keys.union(newKeys)
                    if(map.all { keys.contains(it.key) } && newKeys.isEmpty()) {
                        return@forEach
                    }
                    file.write(map.filter { keyUnion.contains(it.key) })
                }
            }
            .create()

        fun service(): Processor<Multimap<Int, Service>> = factory<Multimap<Int, Service>>()
            .preProcess {
                Multimaps.newListMultimap(mutableMapOf(), ::mutableListOf)
            }
            .process { reflections, preObject ->
                reflections.getSubTypesOf(Service::class.java).forEach { clazz ->
                    val service = clazz.getDeclaredConstructor().newInstance() as Service
                    val priority = if(clazz.isAnnotationPresent(Priority::class.java))
                        clazz.getAnnotation(Priority::class.java).value
                    else
                        0
                    preObject?.let { multiMap ->
                        multiMap[priority].add(service)
                    }
                }
            }
            .postProcess { preObject ->
                preObject?.let { multiMap ->
                    multiMap.asMap().toSortedMap().values.let {
                        proxy.register(*it.flatten().toTypedArray())
                    }
                }
            }
            .create()

        private fun logCommand(commandHolder: CommandHolder) = LoggerProvider.logger
            .info("[ Command ] Registering '${commandHolder.name}' ~${commandHolder.aliases}")
    }
}