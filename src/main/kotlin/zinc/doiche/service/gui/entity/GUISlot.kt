package zinc.doiche.service.gui.entity

import jakarta.persistence.*
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_GUI_SLOT")
class GUISlot(
    @Column(nullable = false)
    val displayedInfo: DisplayedInfo,

    val material: String,

    val customModelData: Int = 0
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GUI_ID")
    val gui: GUI? = null
}