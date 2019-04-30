package pl.padm.binarycamera.processor

import pl.padm.binarycamera.camera.Frame


@ExperimentalUnsignedTypes
class BradleyProcessor: Processor() {
    override fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt {
        var myX = x
        var myY = y
        if (x < size + 1) myX = size + 1
        if (y < size + 1) myY = size + 1
        if (x > frame.height - size - 1) myX = frame.height - size - 1
        if (y > frame.width - size - 1) myY = frame.width - size - 1

        val average = frame.getIntegralAverage(myX, myY, size, area).toDouble()
        return (average * (1 - 0.11)).toUInt()
    }
}