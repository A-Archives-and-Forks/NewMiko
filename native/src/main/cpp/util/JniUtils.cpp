#include "JniUtils.h"

#include <jni.h>
#include <sys/uio.h>
#include <sys/types.h>
#include <unistd.h>
#include <dirent.h>
#include <fcntl.h>
#include <vector>
#include <thread>

using namespace std;


// java.lang.String 2 char*

char *jstring2Char(JNIEnv *env, jstring jstr) {
    char *rtn = nullptr;
    jclass jString = env->FindClass("java/lang/String");
    jstring str_utf_8 = env->NewStringUTF("UTF-8");
    jmethodID mid = env->GetMethodID(jString, "getBytes", "(Ljava/lang/String;)[B");
    auto barr = (jbyteArray) env->CallObjectMethod(jstr, mid, str_utf_8);
    auto alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}

jstring char2jstring(JNIEnv *env, const char *pat) {
    return env->NewStringUTF(pat);
}