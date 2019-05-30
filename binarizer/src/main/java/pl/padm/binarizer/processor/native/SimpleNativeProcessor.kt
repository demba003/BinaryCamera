package pl.padm.binarizer.processor.native

import pl.padm.binarizer.Frame

@ExperimentalUnsignedTypes
class SimpleNativeProcessor : NativeProcessor() {
    override fun prepareFrame(frame: Frame) {}

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