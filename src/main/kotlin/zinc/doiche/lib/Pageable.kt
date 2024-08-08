package zinc.doiche.lib

import kotlin.math.floor

data class Pageable<E>(
    val content: List<E>,
    val total: Long,
    val page: Int
) {
    val maxPage: Int
        get() = floor(total.toFloat() / content.size).toInt()

    override fun toString(): String {
        return "Pageable(content=$content, total=$total, page=$page, maxPage=$maxPage)"
    }
}