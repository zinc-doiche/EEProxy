package zinc.doiche.service.image.entity

import jakarta.persistence.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

@Entity
@Table(name = "TBL_IMAGE")
class Image(
    @Column(nullable = false, unique = true)
    val key: String,

    @Column(nullable = false, unique = true)
    val unicode: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    fun asTitle(): Component = Component.text(unicode).color(TextColor.fromHexString("#FEFEFE"))
}