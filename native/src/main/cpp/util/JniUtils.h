

#ifndef MIKO_JNIUTILS_H
#define MIKO_JNIUTILS_H


#include <jni.h>


char *jstring2Char(JNIEnv *env, jstring jstr);

jstring char2jstring(JNIEnv *env, const char *pat);


#endif //MIKO_JNIUTILS_H
