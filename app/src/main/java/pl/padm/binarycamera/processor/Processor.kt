package pl.padm.binarycamera.processor

import android.graphics.*
import pl.padm.binarycamera.camera.Frame
import java.io.ByteArrayOutputStream

@ExperimentalUnsignedTypes
abstract class Processor {
    var factor = 0.15

    protected abstract fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt
    protected abstract fun prepareFrame(frame: Frame)

    private fun binarizeFrame(frame: Frame) {
        prepareFrame(frame)
        val size = 7
        val area = ((2 * size + 1) * (2 * size + 1)).toUInt()
        val threads = mutableListOf<Thread>()

        for (threadId in 0 until THREADS) {
            threads.add(Thread {
                for (row in frame.height * threadId / THREADS until frame.height * (threadId + 1) / THREADS) {
                    for (column in 0 until frame.width) {
                        val value = frame.getData(column, row)
                        val th = threshold(frame, row, column, size, area)
                        if (value < th) frame.setBinarized(column, row, 0u)
                        else frame.setBinarized(column, row, 255u)
                    }
                }
            })
            threads[threadId].start()
        }

        threads.forEach { it.join() }
    }

    fun encodeFrame(frame: Frame, format: Int = ImageFormat.NV21): Bitmap {
        binarizeFrame(frame)
        val yuv = YuvImage(frame.binarized.toByteArray(), format, frame.width, frame.height, null)
        val out = ByteArrayOutputStream()
        yuv.compressToJpeg(Rect(0, 0, frame.width, frame.height), 50, out)
        val bytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    companion object {
        private const val THREADS = 8
    }
}