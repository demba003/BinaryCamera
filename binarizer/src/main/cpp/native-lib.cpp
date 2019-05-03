#include <jni.h>
#include <string>

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

jint getIntegral(jint *integral, int width, int x, int y) {
    return integral[x + width * y];
}

jint getIntegralAverage(jint *integral, int width, int x, int y, int size, int area) {
    jint one = getIntegral(integral, width, x + size, y + size);
    jint two = getIntegral(integral, width, x - size - 1, y - size - 1);
    jint three = getIntegral(integral, width, x - size - 1, y + size);
    jint four = getIntegral(integral, width, x + size, y - size - 1);
    return ((one + two - three - four) / area);
}

jint bradleyThreshold(jint *integral, int width, int height, int x, int y, int size, int area) {
    int myX = x;
    int myY = y;
    if (x < size + 1) myX = size + 1;
    if (y < size + 1) myY = size + 1;
    if (x > height - size - 1) myX = height - size - 1;
    if (y > width - size - 1) myY = width - size - 1;

    jlong average = getIntegralAverage(integral, width, myX, myY, size, area);
    return static_cast<jint>(average * (1 - 0.15));
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_pl_padm_binarizer_processor_native_BradleyNativeProcessor_binarizeData(JNIEnv *env, jobject callingObject,
                                                                            jbyteArray data, jintArray integral,
                                                                            int width, int height) {

    jbyte *frameData = env->GetByteArrayElements(data, nullptr);
    jint *integralData = env->GetIntArrayElements(integral, nullptr);
    jbyteArray binarized = env->NewByteArray(width * height);
    jbyte *binarizedData = env->GetByteArrayElements(binarized, nullptr);

    for (int row = 0; row < height; row++) {
        for (int column = 0; column < width; column++) {
            jbyte value = frameData[column + width * row];
            jint th = bradleyThreshold(integralData, width, height, column, row, 7, 256);
            if ((value & 0xFF) < th) binarizedData[column + width * row] = 0;
            else binarizedData[column + width * row] = -1;
        }
    }

    env->ReleaseByteArrayElements(data, frameData, 0);
    env->ReleaseIntArrayElements(integral, integralData, 0);
    env->ReleaseByteArrayElements(binarized, binarizedData, 0);
    return binarized;
}