#ifndef IL2CPPBRIDGE_LOG_H
#define IL2CPPBRIDGE_LOG_H

#include <android/log.h>

#define LOG_TAG "IL2CPP_Bridge"
#define LOG_D(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOG_W(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOG_E(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOG_I(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#endif //IL2CPPBRIDGE_LOG_H