package pl.padm.binarycamera.processor

import android.graphics.*
import java.io.ByteArrayOutputStream

abstract class Processor {
    protected abstract fun binarizeFrame(data: ByteArray, width: Int, height: Int)

    fun encodeFrame(data: ByteArray, width: Int, height: Int, format: Int = ImageFormat.NV21): Bitmap {
        val dataToProcess = data.copyOf()
        binarizeFrame(dataToProcess, width, height)
        val yuv = YuvImage(dataToProcess, format, width, height, null)
        val out = ByteArrayOutputStream()
        yuv.compressToJpeg(Rect(0, 0, width, height), 50, out)
        val bytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}