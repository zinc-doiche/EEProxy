package zinc.doiche.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.Entity
import jakarta.persistence.EntityManagerFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.service.ServiceRegistry
import org.reflections.Reflections
import zinc.doiche.util.FileUtils
import zinc.doiche.util.LoggerProvider

object DatabaseFactoryProvider {
    private const val CONNECTION_CONFIG_PATH = "database/connection.json"
    private const val HIKARI_CONFIG_PATH = "database/hikari.json"
    private const val HIBERNATE_CONFIG_PATH = "database/hibernate.json"

    private var entityManagerFactory: EntityManagerFactory? = null

    val isInit: Boolean
        get() = entityManagerFactory != null

    fun close() {
        LoggerProvider.logger.info("Closing EntityManagerFactory")
        entityManagerFactory?.let {
            if(it.isOpen) {
                it.close()
            }
        }
    }

    fun create(): EntityManagerFactory? {
        if (isInit) {
            return entityManagerFactory
        }

        val connectionConfig = FileUtils.read(CONNECTION_CONFIG_PATH, ConnectionConfig::class.java)
        val hikariConfig = FileUtils.read(HIKARI_CONFIG_PATH, HikariConfiguration::class.java)
        val hibernateConfig = FileUtils.read(HIBERNATE_CONFIG_PATH, HibernateConfig::class.java)

        entityManagerFactory = initEntityManagerFactory(connectionConfig, hikariConfig, hibernateConfig)

        return entityManagerFactory
    }

    fun initEntityManagerFactory(
        connectionConfig: ConnectionConfig,
        hikariConfiguration: HikariConfiguration,
        hibernateConfig: HibernateConfig
    ): EntityManagerFactory {
        if(isInit) {
            throw IllegalStateException("EntityManagerFactory is already initialized.")
        }

//        Thread.currentThread().contextClassLoader = javaClass.classLoader
        val hikariConfig = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = connectionConfig.getURL()
            username = connectionConfig.username
            password = connectionConfig.password
            isAutoCommit = hibernateConfig.isAutoCommit
            maximumPoolSize = hikariConfiguration.maximumPoolSize
            minimumIdle = hikariConfiguration.minimumIdle
            idleTimeout = hikariConfiguration.idleTimeout
            connectionTimeout = hikariConfiguration.connectionTimeout

            initializationFailTimeout = -1
        }
        val dataSource = HikariDataSource(hikariConfig)
        val properties = mapOf(
            "jakarta.persistence.nonJtaDataSource" to dataSource,
            "hibernate.show_sql" to hibernateConfig.showSQL,
//            "hibernate.dialect" to "org.hibernate.dialect.PostgreSQLDialect",
            "hibernate.hbm2ddl.auto" to hibernateConfig.hbm2ddl,
            "hibernate.cache.use_second_level_cache" to true,
            "hibernate.globally_quoted_identifiers" to true,
            "hibernate.cache.region.factory_class" to "org.hibernate.cache.jcache.internal.JCacheRegionFactory"
        )
        val reflections = Reflections("zinc.doiche")
        val entityClasses = reflections.getTypesAnnotatedWith(Entity::class.java)

        this.entityManagerFactory = Configuration().run {
            // Add all entity classes to the configuration
            for (entityClass in entityClasses) {
                addAnnotatedClass(entityClass)
            }
            this.properties.putAll(properties)
            val serviceRegistry: ServiceRegistry = StandardServiceRegistryBuilder()
                .applySettings(this.properties)
                .build()
            buildSessionFactory(serviceRegistry)
        }
        return entityManagerFactory!!
    }

    data class ConnectionConfig(
        val host: String,
        val port: Int,
        val database: String,
        val username: String,
        val password: String,
    ) {
        fun getURL(): String = "jdbc:postgresql://$host:$port/$database"
    }

    data class HikariConfiguration(
        val maximumPoolSize: Int,
        val minimumIdle: Int,
        val idleTimeout: Long,
        val connectionTimeout: Long
    )

    data class HibernateConfig(
        val showSQL: Boolean,
        val hbm2ddl: String,
        val isAutoCommit: Boolean
    )
}