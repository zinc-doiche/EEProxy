package zinc.doiche.service.world.entity

import jakarta.persistence.*
import zinc.doiche.lib.embeddable.Period
import zinc.doiche.service.user.entity.User

@Entity
@Table(name = "TBL_USER_ACCESS_LEVEL_AUTHENTICATION")
class UserAccessLevelAuthentication(
    @Embedded
    val period: Period
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

//    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userAccessLevelAuthentication")
//    val user: User? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCESS_LEVEL_ID")
    val accessLevel: AccessLevel? = null
}