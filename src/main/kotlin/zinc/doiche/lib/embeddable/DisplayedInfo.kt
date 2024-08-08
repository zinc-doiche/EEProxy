package zinc.doiche.lib.embeddable

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import net.kyori.adventure.text.minimessage.MiniMessage
import zinc.doiche.database.StringArrayConverter

@Embeddable
class DisplayedInfo(
    @Column(unique = true, nullable = false)
    val name: String,

    val displayName: String,

    @Convert(converter = StringArrayConverter::class, attributeName = "description")
    @Column(nullable = true)
    val description: Array<String>? = null
) {
    fun displayName() = MiniMessage.miniMessage().deserialize(displayName)

    fun displayName(replace: (String) -> String) = MiniMessage.miniMessage().deserialize(replace(displayName))

    fun description() = description?.map(MiniMessage.miniMessage()::deserialize)

    fun description(replace: (String) -> String) = description?.map {
        MiniMessage.miniMessage().deserialize(replace(displayName))
    }
}