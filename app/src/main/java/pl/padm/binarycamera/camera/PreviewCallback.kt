package pl.padm.binarycamera.camera

import android.hardware.Camera
import android.graphics.Bitmap
import pl.padm.binarycamera.processor.Processor
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

typealias DrawCallback = (Bitmap) -> Unit
typealias FpsCallback = (Double) -> Unit

@ExperimentalUnsignedTypes
class PreviewCallback(
    val draw: DrawCallback,
    val fps: FpsCallback,
    val processor: Processor,
    private val width: Int,
    private val height: Int
) : Camera.PreviewCallback {
    private var lastUpdate: AtomicLong = AtomicLong(0)
    private var counter = AtomicInteger(0)

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        if (counter.get() < 4) {
            updateFps()
            Thread {
                counter.incrementAndGet()
                val frame = Frame(data.toUByteArray(), width, height)
                val bitmap = processor.encodeFrame(frame)
                draw(bitmap)
                counter.decrementAndGet()
            }.start()
        }
    }

    private fun updateFps() {
        fps(1.0 / (System.currentTimeMillis() - lastUpdate.get()) * 1000)
        lastUpdate.set(System.currentTimeMillis())
    }
}