package pl.padm.binarycamera.processor

import pl.padm.binarycamera.camera.Frame


@ExperimentalUnsignedTypes
class SauvolaProcessor : Processor() {
    override fun prepareFrame(frame: Frame) {
        frame.calculateIntegralAndSquare()
    }

    override fun threshold(frame: Frame, x: Int, y: Int, size: Int, area: UInt): UInt {
        var myX = x
        var myY = y
        if (x < size + 1) myX = size + 1
        if (y < size + 1) myY = size + 1
        if (x > frame.height - size - 1) myX = frame.height - size - 1
        if (y > frame.width - size - 1) myY = frame.width - size - 1

        val avg = frame.getIntegralAverage(myX, myY, size, area).toDouble()
        val variance = frame.getIntegralSquareAverage(myX, myY, size, area).toDouble() - avg * avg
        val sd = if (variance <= 0.0) 0.0 else Math.sqrt(variance)
        return (avg * (1 + 0.12 * (sd / 128 - 1.0))).toUInt()
    }
}