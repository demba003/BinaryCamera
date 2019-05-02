package pl.padm.binarizer.processor.native

import pl.padm.binarizer.Frame
import pl.padm.binarizer.processor.Processor

@ExperimentalUnsignedTypes
class SimpleNativeProcessor : Processor() {
    override fun prepareFrame(frame: Frame) {}

    external override fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt
    private external fun binarizeData(data: ByteArray, binarized: ByteArray, width: Int, height: Int)

    override fun binarizeFrame(frame: Frame) {
        binarizeData(frame.data.toByteArray(), frame.binarized.toByteArray(), frame.width, frame.height)
    }

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}