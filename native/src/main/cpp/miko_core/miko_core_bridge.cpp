//
// Created by admin on 2025/7/2.
//


#include <jni.h>
#include <cstdlib>
#include "util/JniUtils.h"

using namespace std;

extern "C" {
JNIEXPORT jboolean JNICALL
Java_im_mingxi_miko_util_JniBridge_cmd(JNIEnv *env, jclass clz, jstring cmd) {
    system(jstring2Char(env, cmd));
    return (jboolean) true;
}
}