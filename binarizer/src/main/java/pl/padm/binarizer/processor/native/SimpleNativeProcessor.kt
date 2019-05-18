package pl.padm.binarizer.processor.native

import pl.padm.binarizer.Frame

@ExperimentalUnsignedTypes
class SimpleNativeProcessor : NativeProcessor() {
    override fun prepareFrame(frame: Frame) {}

    override fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt {
        throw UnsupportedOperationException()
    }

    private external fun binarizeData(data: ByteArray, width: Int, height: Int): ByteArray

    override fun binarizeFrame(frame: Frame) {
        binarizeData(frame.data.toByteArray(), frame.width, frame.height).toUByteArray().copyInto(frame.binarized)
    }

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}