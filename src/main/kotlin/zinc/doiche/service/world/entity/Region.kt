package zinc.doiche.service.world.entity

import jakarta.persistence.*
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_REGION")
class Region(
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    val biome: String,

    @Embedded
    val displayedInfo: DisplayedInfo
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @ManyToOne
    @JoinColumn(name = "ACCESS_LEVEL_ID")
    val accessLevel: AccessLevel? = null
}