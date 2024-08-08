package zinc.doiche.service.world

import zinc.doiche.service.Service
import zinc.doiche.service.world.repository.ExtensionWorldRepository

class ExtensionWorldService: Service {
    companion object {
        val repository: ExtensionWorldRepository by lazy {
            ExtensionWorldRepository()
        }
    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }
}