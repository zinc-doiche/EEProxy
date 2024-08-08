package zinc.doiche.database

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class StringArrayConverter : AttributeConverter<Array<String>, String> {
    override fun convertToDatabaseColumn(attribute: Array<String>): String {
        return attribute.joinToString()
    }

    override fun convertToEntityAttribute(dbData: String): Array<String> {
        if (dbData.trim { it <= ' ' }.isEmpty()) {
            return emptyArray()
        }
        return dbData.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }
}