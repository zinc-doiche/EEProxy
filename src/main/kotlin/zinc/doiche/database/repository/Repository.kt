package zinc.doiche.database.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import zinc.doiche.EEProxy.Companion.proxy
import zinc.doiche.database.DatabaseFactoryProvider
import zinc.doiche.database.extension.transaction

abstract class Repository<E> {
    abstract fun save(entity: E)
    abstract fun findById(id: Long): E?
    abstract fun delete(entity: E)

    protected val entityManager: EntityManager by lazy {
        try {
            proxy.entityManager
        } catch (e: Exception) {
            DatabaseFactoryProvider.create()?.createEntityManager() ?: throw IllegalStateException("entity manager is null")
        }
    }

    protected val queryFactory: JPAQueryFactory by lazy {
        try {
            proxy.jpaQueryFactory
        } catch (e: Exception) {
            JPAQueryFactory(entityManager)
        }
    }

    fun transaction(block: Repository<E>.(EntityManager) -> Unit) {
        entityManager.transaction {
            block(this, entityManager)
        }
    }
}