package pl.padm.binarizer.processor.native

import pl.padm.binarizer.Frame

@ExperimentalUnsignedTypes
class BradleyNativeProcessor : NativeProcessor() {
    override fun prepareFrame(frame: Frame) {
        frame.calculateIntegral()
    }

    private external fun binarizeData(
        data: ByteArray,
        integral: LongArray,
        width: Int,
        height: Int,
        factor: Double,
        size: Int,
        area: Int
    ): ByteArray

    override fun binarizeFrame(frame: Frame) {
        prepareFrame(frame)

        binarizeData(
            frame.data.toByteArray(),
            frame.integral.map { it.toLong() }.toLongArray(),
            frame.width,
            frame.height,
            factor,
            size,
            area.toInt()
        )
            .toUByteArray().copyInto(frame.binarized)
    }

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}