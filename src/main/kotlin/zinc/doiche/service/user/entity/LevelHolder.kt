package zinc.doiche.service.user.entity

import jakarta.persistence.Embeddable

@Embeddable
class LevelHolder {
    var level: Int = 0
        protected set
    var experience: Int = 0
        protected set

    fun addLevel(amount: Int = 1) {
        level += amount
    }

    fun addExperience(amount: Int) {
        experience += amount
    }
}