//
// Created by admin on 2025/7/2.
//


#include <jni.h>
#include <cstdlib>
#include "util/JniUtils.h"
#include "util/FileUtil.h"

using namespace std;

extern "C" {
JNIEXPORT jboolean JNICALL
Java_im_mingxi_miko_util_JniBridge_cmd(JNIEnv *env, jclass clz, jstring cmd) {
    system(jstring2Char(env, cmd));
    return (jboolean) true;
}
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_im_mingxi_mm_hook_HdHook_initOnce(JNIEnv *env, jobject thiz) {
    delete_directory("/data/data/com.tencent.mm/files/fastkv");
    return (jboolean) true;
}

