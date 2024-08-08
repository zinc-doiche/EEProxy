package zinc.doiche.service.world.entity

import jakarta.persistence.*
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_EXTENSION_WORLD")
class ExtensionWorld(
    @Embedded
    val displayedInfo: DisplayedInfo,

    @Column(nullable = false)
    val customModelData: Int = 0
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "extensionWorld", cascade = [CascadeType.ALL], orphanRemoval = true)
//    val accessLevels: MutableList<AccessLevel> = mutableListOf()

}