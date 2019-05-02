package pl.padm.binarizer.processor.jvm

import pl.padm.binarizer.Frame
import pl.padm.binarizer.processor.Processor

@ExperimentalUnsignedTypes
abstract class JVMProcessor : Processor() {
    override fun binarizeFrame(frame: Frame) {
        prepareFrame(frame)

        val threads = mutableListOf<Thread>()

        for (threadId in 0 until THREADS) {
            threads.add(Thread {
                for (row in frame.height * threadId / THREADS until frame.height * (threadId + 1) / THREADS) {
                    for (column in 0 until frame.width) {
                        val value = frame.getData(column, row)
                        val th = threshold(frame, row, column, size, area)
                        if (value < th) frame.setBinarized(column, row, 0u)
                        else frame.setBinarized(column, row, 255u)
                    }
                }
            })
            threads[threadId].start()
        }

        threads.forEach { it.join() }
    }

    companion object {
        private const val THREADS = 8
    }
}