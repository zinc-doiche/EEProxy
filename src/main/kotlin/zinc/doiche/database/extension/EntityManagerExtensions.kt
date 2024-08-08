package zinc.doiche.database.extension

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityTransaction
import zinc.doiche.util.LoggerProvider

internal inline fun EntityManager.transaction(crossinline block: (EntityTransaction) -> Unit) = transaction.run {
    runCatching {
        begin()
        block(this)
    }.onFailure { e ->
        LoggerProvider.logger.error("트랜잭션 중 실패. Rollback 실행.", e)
        rollback()
    }.onSuccess {
        commit()
    }
}