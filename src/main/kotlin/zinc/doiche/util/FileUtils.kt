package zinc.doiche.util

import com.google.gson.GsonBuilder
import zinc.doiche.EEProxy.Companion.proxy
import java.io.File

object FileUtils {
    private val gson = GsonBuilder().disableHtmlEscaping().create()
    private val writerGson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

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