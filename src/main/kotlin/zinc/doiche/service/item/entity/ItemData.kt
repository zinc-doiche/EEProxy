package zinc.doiche.service.item.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_ITEM_DATA")
class ItemData(
    @Embedded
    val displayedInfo: DisplayedInfo,

    @Column(nullable = false)
    val material: String,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    val tags: MutableMap<String, Any> = mutableMapOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    fun displayName() = displayedInfo.displayName()// = miniMessage().deserialize(displayName)

    fun lore() = displayedInfo.description()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemData

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "ItemData(displayedInfo=$displayedInfo, material=$material, tags=$tags, id=$id)"
    }
}