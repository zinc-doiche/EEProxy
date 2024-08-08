package zinc.doiche.util

import org.slf4j.Logger

object LoggerProvider {
    lateinit var logger: Logger
        private set

    fun init(logger: Logger) {
        if(::logger.isInitialized) {
            throw IllegalStateException("LoggerProvider has already been initialized")
        }
        this.logger = logger
    }
}