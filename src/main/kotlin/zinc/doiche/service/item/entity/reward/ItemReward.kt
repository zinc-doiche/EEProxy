package zinc.doiche.service.item.entity.reward

import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import zinc.doiche.service.item.entity.ItemData
import zinc.doiche.service.user.entity.post.Post

@Entity
@Table(name = "TBL_ITEM_REWARD")
class ItemReward(
    @ManyToOne
    @JoinColumn(name = "ITEM_ID", nullable = false)
    val itemData: ItemData? = null,

    post: Post? = null,

    amount: Int = 1
): PostReward(post, amount)
