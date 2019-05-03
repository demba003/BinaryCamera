package pl.padm.binarizer.processor.native

import pl.padm.binarizer.Frame
import pl.padm.binarizer.processor.Processor

@ExperimentalUnsignedTypes
class BradleyNativeProcessor : Processor() {
    override fun prepareFrame(frame: Frame) {
        frame.calculateIntegral()
    }

    override fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt {
        throw UnsupportedOperationException()
    }

    private external fun binarizeData(data: ByteArray, integral: IntArray, width: Int, height: Int): ByteArray

    override fun binarizeFrame(frame: Frame) {
        prepareFrame(frame)
        binarizeData(frame.data.toByteArray(), frame.integral.toIntArray(), frame.width, frame.height).toUByteArray().copyInto(frame.binarized)
    }

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}