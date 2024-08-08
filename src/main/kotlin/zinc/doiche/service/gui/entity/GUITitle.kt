package zinc.doiche.service.gui.entity

import jakarta.persistence.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import zinc.doiche.service.image.entity.Image

/**
 * Mapping Class between GUI and Image

 */
@Entity
@Table(name = "TBL_GUI_TITLE")
class GUITitle(
    @Column(nullable = false, unique = true)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID", nullable = false)
    val image: Image,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GUI_ID", nullable = false)
    val gui: GUI
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
}