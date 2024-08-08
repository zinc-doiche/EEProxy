package zinc.doiche.service.gui.entity

import jakarta.persistence.*
import net.kyori.adventure.text.Component

@Entity
@Table(name = "TBL_GUI")
class GUI(
    @Column(nullable = false, unique = true) val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val type: GUIType
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @OneToMany(mappedBy = "gui", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val slots: MutableList<GUISlot> = mutableListOf()

    @OneToMany(mappedBy = "gui", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val titles: MutableList<GUITitle> = mutableListOf()
}

enum class GUIType(
    val size: Int
) {
    GENERIC_9X1(9),
    GENERIC_9X2(18),
    GENERIC_9X3(27),
    GENERIC_9X4(36),
    GENERIC_9X5(45),
    GENERIC_9X6(54),
    ANVIL(3),
    MERCHANT(3),
    BEACON(1),
    BREWING(5),
    CRAFTING(10),
    ENCHANTING(2),
    FURNACE(3),
    GRINDSTONE(1),
    ;

    val isGeneric = this.name.contains("GENERIC")
}