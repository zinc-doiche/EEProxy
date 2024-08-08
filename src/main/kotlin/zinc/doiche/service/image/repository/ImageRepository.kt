package zinc.doiche.service.image.repository

import zinc.doiche.database.repository.CachedKeyRepository
import zinc.doiche.service.image.entity.Image
import zinc.doiche.service.image.entity.QImage.image

class ImageRepository(
    override val prefix: String
) : CachedKeyRepository<String, Image>() {

    override fun save(entity: Image) {
        entityManager.persist(entity)
    }

    fun findByKeyInCache(key: String): Image? {
        return getId(key)?.let {
            findById(it)
        } ?: findByKey(key)
    }

    private fun findByKey(key: String): Image? = queryFactory
        .selectFrom(image)
        .where(image.key.eq(key))
        .fetchFirst()

    override fun findById(id: Long): Image? {
        return entityManager.find(Image::class.java, id)
    }

    override fun delete(entity: Image) {
        entityManager.remove(entity)
    }
}