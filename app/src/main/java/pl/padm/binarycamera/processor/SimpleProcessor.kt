package pl.padm.binarycamera.processor

import pl.padm.binarycamera.camera.Frame

@ExperimentalUnsignedTypes
class SimpleProcessor : Processor() {
    override fun prepareFrame(frame: Frame) {}

    override fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt = 128u
}