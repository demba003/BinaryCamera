package pl.padm.binarycamera.camera

import android.hardware.Camera
import android.graphics.Bitmap
import pl.padm.binarycamera.processor.SimpleProcessor
import java.util.concurrent.atomic.AtomicLong

typealias DrawCallback = (Bitmap) -> Unit
typealias FpsCallback = (Double) -> Unit

class PreviewCallback(val draw: DrawCallback, val fps: FpsCallback, val width: Int, val height: Int) :
    Camera.PreviewCallback {
    private var lastUpdate: AtomicLong = AtomicLong(0)
    private val processor = SimpleProcessor()

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        Thread {
            updateFps()
            val bitmap = processor.encodeFrame(data, width, height)
            draw(bitmap)
        }.start()
    }

    private fun updateFps() {
        fps(1.0 / (System.currentTimeMillis() - lastUpdate.get()) * 1000)
        lastUpdate.set(System.currentTimeMillis())
    }
}