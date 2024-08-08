package zinc.doiche.service.world.repository

import zinc.doiche.database.repository.Repository
import zinc.doiche.service.world.entity.ExtensionWorld
import zinc.doiche.service.world.entity.QExtensionWorld.extensionWorld

class ExtensionWorldRepository: Repository<ExtensionWorld>() {
    override fun save(entity: ExtensionWorld) {
        entityManager.persist(entity)
    }

    override fun findById(id: Long): ExtensionWorld? {
        return entityManager.find(ExtensionWorld::class.java, id)
    }

    fun findByName(name: String): ExtensionWorld? = queryFactory
        .selectFrom(extensionWorld)
        .where(extensionWorld.displayedInfo.name.eq(name))
        .fetchOne()

    override fun delete(entity: ExtensionWorld) {
        entityManager.remove(entity)
    }
}