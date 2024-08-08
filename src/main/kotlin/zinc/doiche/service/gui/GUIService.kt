package zinc.doiche.service.gui

import zinc.doiche.service.Service
import zinc.doiche.service.gui.repository.GUIRepository

class GUIService: Service {
    companion object {
        val repository: GUIRepository by lazy {
            GUIRepository("gui")
        }
    }

    override fun onEnable() {
    }

    override fun onDisable() {
    }
}