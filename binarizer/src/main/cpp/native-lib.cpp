#include <jni.h>
#include <string>

extern "C" JNIEXPORT jint JNICALL
Java_pl_padm_binarizer_processor_native_SimpleNativeProcessor_threshold(JNIEnv *env, jobject callingObject,
                                                                        jobject frame, int x, int y, int size,
                                                                        unsigned int area) {
    return 128;
}

extern "C" JNIEXPORT void JNICALL
Java_pl_padm_binarizer_processor_native_SimpleNativeProcessor_binarizeData(JNIEnv *env, jobject callingObject,
                                                                           jbyteArray data, jbyteArray binarized,
                                                                           int width, int height) {
    jbyte *frameData = env->GetByteArrayElements(data, NULL);
    jbyte *binarizedData = env->GetByteArrayElements(binarized, NULL);

    for (int row = 0; row < height; row++) {
        for (int column = 0; column < width; column++) {
            jbyte value = frameData[column + width * row];
            jint th = 128;
            if (value & 0xFF < th) binarizedData[column + width * row] = 0;
            else binarizedData[column + width * row] = -1;
        }
    }

    env->ReleaseByteArrayElements(data, frameData, 0);
    env->ReleaseByteArrayElements(binarized, binarizedData, 0);
}