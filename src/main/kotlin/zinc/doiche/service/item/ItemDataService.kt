package zinc.doiche.service.item

import zinc.doiche.service.Service
import zinc.doiche.service.item.repository.ItemDataRepository

class ItemDataService: Service {
    companion object {
        val repository: ItemDataRepository by lazyOf(ItemDataRepository())
    }

    override fun onEnable() {

    }

    override fun onDisable() {
    }
}