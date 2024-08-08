package zinc.doiche.service.item.entity.reward

import jakarta.persistence.*
import zinc.doiche.service.user.entity.post.Post

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class PostReward(
    @ManyToOne
    @JoinColumn(name = "POST_ID", nullable = false)
    val post: Post? = null,

    val amount: Int = 1
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
}