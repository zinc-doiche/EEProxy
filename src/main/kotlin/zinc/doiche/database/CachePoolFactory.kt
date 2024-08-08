package zinc.doiche.database

import redis.clients.jedis.ConnectionPoolConfig
import redis.clients.jedis.DefaultJedisClientConfig
import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisPooled
import zinc.doiche.util.FileUtils
import java.time.Duration

class CachePoolFactory(
) {
    companion object {
        private const val REDIS_CONFIG_PATH = "database/redis.json"
        private var config: CacheConfig? = null

        val isInit: Boolean
            get() = config != null

        fun initConfig(config: CacheConfig) {
            if(this.config == null) {
                this.config = config
                return
            }

            throw IllegalStateException("config is already initialized")
        }
    }

    fun create(): JedisPooled? {
        return config?.let {
            jedisPooled()
        } ?: FileUtils.read(REDIS_CONFIG_PATH, CacheConfig::class.java).let {
            initConfig(it)
            jedisPooled()
        }
    }

    private fun poolConfig(config: CacheConfig) = ConnectionPoolConfig().apply {
        // maximum active connections in the pool,
        // tune this according to your needs and application type
        // default is 8
        maxTotal = config.maxTotal
        // maximum idle connections in the pool, default is 8
        maxIdle = config.maxIdle
        // minimum idle connections in the pool, default 0
        minIdle = config.minIdle
        // Enables waiting for a connection to become available.
        blockWhenExhausted = config.blockWhenExhausted
        // The maximum number of seconds to wait for a connection to become available
        setMaxWait(Duration.ofSeconds(config.maxWaitSeconds))
        // Enables sending a PING command periodically while the connection is idle.
        testWhileIdle = config.testWhileIdle
        // controls the period between checks for idle connections in the pool
        timeBetweenEvictionRuns = Duration.ofSeconds(config.timeBetweenEvictionRunsSeconds)
    }

    private fun jedisPooled(): JedisPooled? {
        return JedisPooled(
            config?.hostAndPort ?: return null,
            DefaultJedisClientConfig.builder()
                .password(config?.password ?: return null)
                .socketTimeoutMillis(5000)  // set timeout to 5 seconds
                .connectionTimeoutMillis(5000) // set connection timeout to 5 seconds
                .build()
            , poolConfig(config ?: return null)
        )
    }

    data class CacheConfig(
        val host: String,
        val port: Int,
        val password: String,
        val maxTotal: Int,
        val maxIdle: Int,
        val minIdle: Int,
        val blockWhenExhausted: Boolean,
        val maxWaitSeconds: Long,
        val testWhileIdle: Boolean,
        val timeBetweenEvictionRunsSeconds: Long
    ) {
        val hostAndPort: HostAndPort
            get() = HostAndPort(host, port)
    }
}