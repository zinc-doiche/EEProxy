package zinc.doiche.util

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import zinc.doiche.EEProxy.Companion.proxy
import java.io.File

object FileUtils {
    internal val gson = GsonBuilder().disableHtmlEscaping().create()
    internal val writerGson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

    fun getConfig(path: String) = File(proxy.dataDirectory.toFile(), path).apply {
        if(!exists()) {
            parentFile.mkdirs()
            createNewFile()
        }
    }

    fun <T> read(path: String, clazz: Class<T>): T {
        return getConfig(path).reader().use {
            gson.fromJson(it, clazz)
        }
    }

    fun write(path: String, obj: Any) {
        getConfig(path).writer().use {
            writerGson.toJson(obj, it)
        }
    }
}

internal fun File.write(obj: Any) {
    writer().use {
        FileUtils.writerGson.toJson(obj, it)
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <K, V> File.toMapOf(
    keyClass: Class<K>, valueClass: Class<V>
): Map<K, V> = reader().use {
    val typeToken = TypeToken.getParameterized(Map::class.java, keyClass, valueClass)
    FileUtils.gson.fromJson(it, typeToken)
} as? Map<K, V> ?: mutableMapOf()