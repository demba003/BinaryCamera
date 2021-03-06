package pl.padm.binarizer.processor.jvm

import pl.padm.binarizer.Frame

@ExperimentalUnsignedTypes
class BradleyProcessor : JVMProcessor() {
    override  fun prepareFrame(frame: Frame) {
        frame.calculateIntegral()
    }

    override fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt {
        var myX = x
        var myY = y
        if (x < size + 1) myX = size + 1
        if (y < size + 1) myY = size + 1
        if (x > frame.height - size - 1) myX = frame.height - size - 1
        if (y > frame.width - size - 1) myY = frame.width - size - 1

        val average = frame.getIntegralAverage(myX, myY, size, area).toDouble()
        return (average * (1 - factor)).toUInt()
    }
}