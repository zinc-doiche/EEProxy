package zinc.doiche.service.user.entity.post

import jakarta.persistence.*
import zinc.doiche.lib.embeddable.DisplayedInfo

@Entity
@Table(name = "TBL_POST")
class Post(
    @Column(nullable = false)
    val displayedInfo: DisplayedInfo,

    val material: String,

    @Column(nullable = false)
    val content: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
}