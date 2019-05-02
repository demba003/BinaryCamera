package pl.padm.binarizer

@ExperimentalUnsignedTypes
data class Frame(
    val data: UByteArray,
    val width: Int,
    val height: Int,
    val binarized: UByteArray = data.copyOf(),
    val integral: UIntArray = UIntArray(width * height),
    val integralSquare: UIntArray = UIntArray(width * height)
) {
    fun calculateIntegral() {
        for (row in 0 until height) {
            var sum: UInt = 0u
            var sumSquare: UInt = 0u
            for (column in 0 until width) {
                sum = sum + data[column + width * row]
                sumSquare = sumSquare + data[column + width * row] * data[column + width * row]
                if (row == 0) {
                    integral[column + width * row] = sum
                } else {
                    integral[column + width * row] = integral[column + width * (row - 1)] + sum
                }
            }
        }
    }

    fun calculateIntegralAndSquare() {
        for (row in 0 until height) {
            var sum: UInt = 0u
            var sumSquare: UInt = 0u
            for (column in 0 until width) {
                sum = sum + data[column + width * row]
                sumSquare = sumSquare + data[column + width * row] * data[column + width * row]
                if (row == 0) {
                    integral[column + width * row] = sum
                    integralSquare[column + width * row] = sumSquare
                } else {
                    integral[column + width * row] = integral[column + width * (row - 1)] + sum
                    integralSquare[column + width * row] = integralSquare[column + width * (row - 1)] + sumSquare
                }
            }
        }
    }

    fun setBinarized(x: Int, y: Int, value: UByte) {
        binarized[x + y * width] = value
    }

    fun getData(x: Int, y: Int): UByte {
        return data[x + width * y]
    }

    private fun getIntegral(x: Int, y: Int): UInt {
            return integral[y + width * x]
    }

    private fun getIntegralSquare(x: Int, y: Int): UInt {
        return integralSquare[y + width * x]
    }

    fun getIntegralAverage(x: Int, y: Int, size: Int, area: UInt): UByte {
        val one = getIntegral(x + size, y + size)
        val two = getIntegral(x - size - 1, y - size - 1)
        val three = getIntegral(x - size - 1, y + size)
        val four = getIntegral(x + size, y - size - 1)
        return ((one + two - three - four) / area).toUByte()
    }

    fun getIntegralSquareAverage(x: Int, y: Int, size: Int, area: UInt): UByte {
        val one = getIntegralSquare(x + size, y + size)
        val two = getIntegralSquare(x - size - 1, y - size - 1)
        val three = getIntegralSquare(x - size - 1, y + size)
        val four = getIntegralSquare(x + size, y - size - 1)
        return ((one + two - three - four) / area).toUByte()
    }
}