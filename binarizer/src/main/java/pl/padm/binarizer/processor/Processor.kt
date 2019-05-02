package pl.padm.binarizer.processor

import android.graphics.*
import pl.padm.binarizer.Frame
import java.io.ByteArrayOutputStream

@ExperimentalUnsignedTypes
abstract class Processor {
    var factor = 0.15
    val size = 7
    protected val area = ((2 * size + 1) * (2 * size + 1)).toUInt()

    protected abstract fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt
    protected abstract fun prepareFrame(frame: Frame)
    protected abstract fun binarizeFrame(frame: Frame)

    fun encodeFrame(frame: Frame, format: Int = ImageFormat.NV21): Bitmap {
        binarizeFrame(frame)
        val yuv = YuvImage(frame.binarized.toByteArray(), format, frame.width, frame.height, null)
        val out = ByteArrayOutputStream()
        yuv.compressToJpeg(Rect(0, 0, frame.width, frame.height), 50, out)
        val bytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}