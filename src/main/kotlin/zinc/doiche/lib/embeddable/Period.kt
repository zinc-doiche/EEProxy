package zinc.doiche.lib.embeddable

import jakarta.persistence.Embeddable
import java.time.LocalDateTime

@Embeddable
class Period {
    val createdDateTime: LocalDateTime = LocalDateTime.now()

    var updatedDateTime: LocalDateTime = LocalDateTime.now()
        protected set

    fun update() {
        updatedDateTime = LocalDateTime.now()
    }
}
