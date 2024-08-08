package zinc.doiche.service.gui.repository

import zinc.doiche.database.repository.CachedKeyRepository
import zinc.doiche.service.gui.entity.GUI
import zinc.doiche.service.gui.entity.QGUI.gUI

class GUIRepository(override val prefix: String) : CachedKeyRepository<String, GUI>() {
    override fun save(entity: GUI) {
        entityManager.persist(entity)
    }

    override fun findById(id: Long): GUI? {
        return entityManager.find(GUI::class.java, id)
    }

    override fun delete(entity: GUI) {
        entityManager.remove(entity)
    }

    fun findByName(name: String): GUI? = queryFactory
        .selectFrom(gUI)
        .where(gUI.name.eq(name))
        .fetchOne()

    fun findCacheByName(name: String): GUI? {
        return getId(name)?.let { id ->
            findById(id)
        } ?: findByName(name)?.let { gui ->
            saveId(gui.name, gui.id!!)
            gui
        }
    }
}