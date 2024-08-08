package zinc.doiche.service.image

import zinc.doiche.service.Service
import zinc.doiche.service.image.repository.ImageRepository

class ImageService: Service {
    companion object {
        val repository = ImageRepository("image")
    }

    override fun onEnable() {

    }

    override fun onDisable() {

    }
}