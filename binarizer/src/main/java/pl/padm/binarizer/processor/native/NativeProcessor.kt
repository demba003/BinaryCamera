package pl.padm.binarizer.processor.native

import pl.padm.binarizer.Frame
import pl.padm.binarizer.processor.Processor

@ExperimentalUnsignedTypes
abstract class NativeProcessor: Processor() {
    override fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt {
        throw UnsupportedOperationException()
    }
}