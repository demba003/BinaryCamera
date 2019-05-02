package pl.padm.binarizer.processor.jvm

import pl.padm.binarizer.Frame

@ExperimentalUnsignedTypes
class SimpleProcessor : JVMProcessor() {
    override fun prepareFrame(frame: Frame) {}

    override fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt = 128u
}