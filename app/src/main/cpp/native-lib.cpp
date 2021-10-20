#include <string>
#include <jni.h>
#include <android/input.h>
#include "AudioEngine.h"

static AudioEngine *audioEngine = new AudioEngine();

extern "C" {

JNIEXPORT jstring JNICALL
Java_com_omarmelade_studio_protosynth_MainActivity_stringFromJNI( JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

JNIEXPORT void JNICALL
Java_com_omarmelade_studio_protosynth_MainActivity_touchEvent(JNIEnv *env, jobject obj, jint action) {
    switch (action) {
        case AMOTION_EVENT_ACTION_DOWN :
            audioEngine->setToneOn(true);
            break;
        case AMOTION_EVENT_ACTION_UP :
            audioEngine->setToneOn(false);
            break;
        default :
            break;
    }
}

JNIEXPORT void JNICALL
Java_com_omarmelade_studio_protosynth_MainActivity_playEngine(JNIEnv *env, jobject obj,
                                                              jboolean play) {
    audioEngine->setToneOn(play);
}

JNIEXPORT void JNICALL
Java_com_omarmelade_studio_protosynth_MainActivity_setIsSin(JNIEnv *env, jobject obj,
                                                              jboolean sinus) {
    audioEngine->setisSin(sinus);
}


JNIEXPORT void JNICALL
Java_com_omarmelade_studio_protosynth_MainActivity_startEngine(JNIEnv *env, jobject  /* this */) {
    audioEngine->start();
}

JNIEXPORT void JNICALL
Java_com_omarmelade_studio_protosynth_MainActivity_setFreq(JNIEnv *env, jobject  /* this */, double freq) {
    audioEngine->setFreq(freq);
}

JNIEXPORT void JNICALL
Java_com_omarmelade_studio_protosynth_MainActivity_stopEngine(JNIEnv *env, jobject  /* this */) {
    audioEngine->stop();
}

}