package zinc.doiche.service.user

import zinc.doiche.service.Service
import zinc.doiche.service.user.repository.UserRepository

class UserService: Service {
    companion object {
        val repository: UserRepository by lazyOf(UserRepository("user"))
    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }

}