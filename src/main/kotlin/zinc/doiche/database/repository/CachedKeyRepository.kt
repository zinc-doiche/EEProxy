package zinc.doiche.database.repository

import redis.clients.jedis.JedisPooled
import zinc.doiche.EEProxy.Companion.proxy
import zinc.doiche.database.CachePoolFactory

abstract class CachedKeyRepository<K, E>: Repository<E>() {
    protected abstract val prefix: String

    protected val jedisPooled: JedisPooled by lazy {
        try {
            proxy.jedisPooled
        } catch (e: Exception) {
            CachePoolFactory().create() ?: throw IllegalStateException("jedis pooled is null")
        }
    }

    fun saveId(identifier: K, id: Long) {
        jedisPooled.set("$prefix:$identifier", id.toString())
    }

    fun removeId(identifier: K) {
        jedisPooled.del("$prefix:$identifier")
    }

    fun getId(identifier: K): Long? {
        return jedisPooled.get("$prefix:$identifier")?.toLong()
    }
}