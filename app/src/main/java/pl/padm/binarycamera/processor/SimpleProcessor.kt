package pl.padm.binarycamera.processor

class SimpleProcessor : Processor()  {
    override fun binarizeFrame(data: ByteArray, width: Int, height: Int) {
        for (x in 0 until width - 1) {
            for (y in 0 until height - 1) {
                if (data[x + width * y] < 0) data[x + width * y] = -1
                else data[x + width * y] = 0
            }
        }
    }
}