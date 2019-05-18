#include <jni.h>
#include <string>
#include <math.h>

extern "C" JNIEXPORT jbyteArray JNICALL
Java_pl_padm_binarizer_processor_native_SimpleNativeProcessor_binarizeData(JNIEnv *env, jobject callingObject,
                                                                           jbyteArray data, jint width, jint height) {
    jbyte *frameData = env->GetByteArrayElements(data, nullptr);
    jbyteArray binarized = env->NewByteArray(width * height);
    jbyte *binarizedData = env->GetByteArrayElements(binarized, nullptr);

    for (int row = 0; row < height; row++) {
        for (int column = 0; column < width; column++) {
            jbyte value = frameData[column + width * row];
            jint th = 128;
            if ((value & 0xFF) < th) binarizedData[column + width * row] = 0;
            else binarizedData[column + width * row] = -1;
        }
    }

    env->ReleaseByteArrayElements(data, frameData, 0);
    env->ReleaseByteArrayElements(binarized, binarizedData, 0);
    return binarized;
}

jlong getIntegral(jlong *integral, int width, int x, int y) {
    return integral[x + width * y];
}

jlong getIntegralAverage(jlong *integral, int width, int x, int y, int size, int area) {
    jlong one = getIntegral(integral, width, x + size, y + size);
    jlong two = getIntegral(integral, width, x - size - 1, y - size - 1);
    jlong three = getIntegral(integral, width, x - size - 1, y + size);
    jlong four = getIntegral(integral, width, x + size, y - size - 1);
    return ((one + two - three - four) / area);
}

jint bradleyThreshold(jlong *integral, int width, int height, int x, int y, int size, int area, double factor) {
    int myX = x;
    int myY = y;
    if (x < size + 1) myX = size + 1;
    if (y < size + 1) myY = size + 1;
    if (x > height - size - 1) myX = height - size - 1;
    if (y > width - size - 1) myY = width - size - 1;

    jlong average = getIntegralAverage(integral, width, myX, myY, size, area);
    return static_cast<jint>(average * (1 - factor));
}

//void calculateIntegral(jbyte *frame, jlong *integral, int height, int width) {
//    for (int row = 0; row < height; row++) {
//        long sum = 0;
//        long sumSquare = 0;
//        for (int column = 0; column < width; column++) {
//            sum = sum + frame[column + width * row];
//            sumSquare = sumSquare + frame[column + width * row] * frame[column + width * row];
//            if (row == 0) {
//                integral[column + width * row] = sum;
//            } else {
//                integral[column + width * row] = integral[column + width * (row - 1)] + sum;
//            }
//        }
//    }
//}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_pl_padm_binarizer_processor_native_BradleyNativeProcessor_binarizeData(JNIEnv *env, jobject callingObject,
                                                                            jbyteArray data, jlongArray integral,
                                                                            int width, int height, jdouble factor,
                                                                            jint size, jint area) {

    jbyte *frameData = env->GetByteArrayElements(data, nullptr);
    jlong *integralData = env->GetLongArrayElements(integral, nullptr);
    jbyteArray binarized = env->NewByteArray(width * height);
    jbyte *binarizedData = env->GetByteArrayElements(binarized, nullptr);

    for (int row = 0; row < height; row++) {
        for (int column = 0; column < width; column++) {
            jint value = frameData[column + width * row];
            jint th = bradleyThreshold(integralData, width, height, column, row, size, area, factor);
            if ((value & 0xFFFFFFFF) < th)
                binarizedData[column + width * row] = 0;
            else binarizedData[column + width * row] = -1;
        }
    }

    env->ReleaseByteArrayElements(data, frameData, 0);
    env->ReleaseLongArrayElements(integral, integralData, 0);
    env->ReleaseByteArrayElements(binarized, binarizedData, 0);
    return binarized;
}


jint sauvolaThreshold(jlong *integral, jlong *integralSquare, int width, int height, int x, int y, int size, int area,
                      double factor) {
    int myX = x;
    int myY = y;
    if (x < size + 1) myX = size + 1;
    if (y < size + 1) myY = size + 1;
    if (x > height - size - 1) myX = height - size - 1;
    if (y > width - size - 1) myY = width - size - 1;

    jlong average = getIntegralAverage(integral, width, myX, myY, size, area);
    jlong variance = getIntegralAverage(integralSquare, width, myX, myY, size, area) - average * average;
    jlong sd = static_cast<jlong>(variance <= 0 ? 0 : sqrt(variance));

    return static_cast<jint>(average * (1 + factor * (sd / 128 - 1.0)));
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_pl_padm_binarizer_processor_native_SauvolaNativeProcessor_binarizeData(JNIEnv *env, jobject callingObject,
                                                                            jbyteArray data, jlongArray integral,
                                                                            jlongArray integralSquare, jint width,
                                                                            jint height, jdouble factor, jint size,
                                                                            jint area) {

    jbyte *frameData = env->GetByteArrayElements(data, nullptr);
    jlong *integralData = env->GetLongArrayElements(integral, nullptr);
    jlong *integralSquareData = env->GetLongArrayElements(integralSquare, nullptr);
    jbyteArray binarized = env->NewByteArray(width * height);
    jbyte *binarizedData = env->GetByteArrayElements(binarized, nullptr);

    for (int row = 0; row < height; row++) {
        for (int column = 0; column < width; column++) {
            jint value = frameData[column + width * row];
            jint th = sauvolaThreshold(integralData, integralSquareData, width, height, column, row, size, area, factor);
            if ((value & 0xFFFFFFFF) < th)
                binarizedData[column + width * row] = 0;
            else binarizedData[column + width * row] = -1;
        }
    }

    env->ReleaseByteArrayElements(data, frameData, 0);
    env->ReleaseLongArrayElements(integral, integralData, 0);
    env->ReleaseLongArrayElements(integralSquare, integralSquareData, 0);
    env->ReleaseByteArrayElements(binarized, binarizedData, 0);
    return binarized;
}