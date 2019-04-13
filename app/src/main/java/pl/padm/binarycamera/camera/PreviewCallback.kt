package pl.padm.binarycamera.camera

import android.hardware.Camera
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicLong

class PreviewCallback(val draw: (Bitmap) -> Unit, val fps: (Double) -> Unit) : Camera.PreviewCallback {
    private var lastUpdate: AtomicLong = AtomicLong(0)

    override fun onPreviewFrame(data: ByteArray?, camera: Camera) {
        Thread {
            val parameters = camera.parameters
            val width = parameters.previewSize.width
            val height = parameters.previewSize.height

            val yuv = YuvImage(data, parameters.previewFormat, width, height, null)

            val out = ByteArrayOutputStream()
            yuv.compressToJpeg(Rect(0, 0, width, height), 50, out)

            val bytes = out.toByteArray()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            draw(bitmap)
            fps(1.0 / (System.currentTimeMillis() - lastUpdate.get()) * 1000)
            lastUpdate.set(System.currentTimeMillis())
        }.start()
    }
}