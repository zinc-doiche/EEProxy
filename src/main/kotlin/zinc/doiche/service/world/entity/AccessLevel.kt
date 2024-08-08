package zinc.doiche.service.world.entity

import jakarta.persistence.*
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_ACCESS_LEVEL")
class AccessLevel(
    @Embedded
    val displayedInfo: DisplayedInfo,

    @Column(nullable = false)
    val level: Int
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXTENSION_WORLD_ID")
    val extensionWorld: ExtensionWorld? = null

//    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
//    val regions: MutableList<Region> = mutableListOf()
}