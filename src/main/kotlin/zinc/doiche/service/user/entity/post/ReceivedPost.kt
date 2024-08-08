package zinc.doiche.service.user.entity.post

import jakarta.persistence.*
import zinc.doiche.service.user.entity.User
import java.time.LocalDate

@Entity
@Table(name = "TBL_RECEIVED_POST")
class ReceivedPost(
    @ManyToOne
    @JoinColumn(nullable = false, name = "POST_ID")
    val post: Post? = null,

    @ManyToOne
    @JoinColumn(nullable = false, name = "RECEIVER_ID")
    val receiver: User? = null,

    @Column(nullable = true)
    val expiredDate: LocalDate? = null
){
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null

    @Column(updatable = true)
    final var isRead: Boolean = false
        private set

    @Column(updatable = true)
    final var isClaimed: Boolean = false
        private set
}