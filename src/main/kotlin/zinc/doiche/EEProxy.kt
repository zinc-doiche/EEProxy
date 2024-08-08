package zinc.doiche

import com.google.inject.Inject
import com.querydsl.jpa.impl.JPAQueryFactory
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import jakarta.persistence.EntityManager
import org.slf4j.Logger
import redis.clients.jedis.JedisPooled
import zinc.doiche.database.CachePoolFactory
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.lib.init.ClassLoader
import zinc.doiche.lib.init.ProcessorFactory
import zinc.doiche.service.Service
import zinc.doiche.util.LoggerProvider
import java.nio.file.Path

@Plugin(
    id = "ee-proxy",
    name = "EEProxy",
    version = "alpha",
    authors = ["Doiche"]
)
class EEProxy @Inject constructor(
    val proxyServer: ProxyServer,

    @DataDirectory
    val dataDirectory: Path,

    val logger: Logger
) {
    companion object {
        internal lateinit var proxy: EEProxy
            private set
    }

    val jedisPooled: JedisPooled by lazy {
        CachePoolFactory().create() ?: throw IllegalStateException("jedis pooled is null")
    }
    val entityManager: EntityManager by lazy {
        DatabaseFactoryProvider.create()?.createEntityManager() ?: throw IllegalStateException("factory is null")
    }
    val jpaQueryFactory: JPAQueryFactory by lazy {
        JPAQueryFactory(entityManager)
    }

    private val services: MutableList<Service> = mutableListOf()

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        proxy = this
        LoggerProvider.init(logger)

        process()
        loadServices()
    }

    private fun process() {
        ClassLoader()
            .add(ProcessorFactory.translatable {
                it.replace("<brace>", "[")
                    .replace("</brace>", "]")
                    .replace("<curlyBrace>", "{")
                    .replace("</curlyBrace>", "}")
            })
            .add(ProcessorFactory.service())
            .add(ProcessorFactory.listener(this, proxyServer.eventManager))
            .add(ProcessorFactory.command(proxyServer.commandManager))
            .process()
    }

    private fun loadServices() {
        services.forEach(Service::onEnable)
    }

    fun register(vararg service: Service) {
        services.addAll(service)
    }
}


