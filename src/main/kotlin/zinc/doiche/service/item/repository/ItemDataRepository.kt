package zinc.doiche.service.item.repository

import zinc.doiche.lib.Pageable
import zinc.doiche.database.repository.Repository
import zinc.doiche.service.item.entity.ItemData
import zinc.doiche.service.item.entity.QItemData.itemData

class ItemDataRepository: Repository<ItemData>() {
    override fun save(entity: ItemData) {
        entityManager.persist(entity)
    }

    fun findAllNames(): List<String> = queryFactory
        .select(itemData.displayedInfo.name)
        .from(itemData)
        .fetch()

    fun findByPage(page: Int, size: Long): Pageable<String> {
        val count = queryFactory.select(itemData.count())
            .from(itemData)
            .fetchOne() ?: 0
        val content = queryFactory.select(itemData.displayedInfo.name)
            .from(itemData)
            .orderBy(itemData.displayedInfo.name.asc())
            .offset((page - 1) * size)
            .limit(size)
            .fetch()
        return Pageable(content, count, page)
    }

    fun findByName(name: String): ItemData? = queryFactory
        .selectFrom(itemData)
        .where(itemData.displayedInfo.name.eq(name))
        .fetchFirst()

    override fun findById(id: Long): ItemData? {
        return entityManager.find(ItemData::class.java, id)
    }

    override fun delete(entity: ItemData) {
        entityManager.remove(entity)
    }
}