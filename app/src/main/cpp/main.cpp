#include <cstring>
#include <dlfcn.h>
#include <dobby.h>
#include <jni.h>

#include "log.h"
#include "hook_il2cpp.h"

typedef void* (*dlopen_t)(const char* filename, int flag);

static dlopen_t orig_dlopen = NULL;

inline __always_inline
int ends_with(const char *str, const char *suffix)
{
    if (!str || !suffix)
        return 0;
    size_t lenstr = strlen(str);
    size_t lensuffix = strlen(suffix);
    if (lensuffix >  lenstr)
        return 0;
    return strncmp(str + lenstr - lensuffix, suffix, lensuffix) == 0;
}

void* my_dlopen(const char* filename, int flag) {
    void* handle = orig_dlopen(filename, flag);
    // 如果filename以libil2cpp.so结尾
    if (ends_with(filename, "libil2cpp.so")) {
        LOG_I("libil2cpp.so loaded.");
        LOG_I("handle: %p", handle);
        LOG_I("flag: %d", flag);
        if (flag == 2){
            DobbyDestroy((void*)my_dlopen);
            il2cpp_api_init(handle);
        }
    }
    return handle;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_axix_il2cpp_1bridge_Il2cppBridge_InitHook(JNIEnv *env, jobject thiz) {
    // hook dlsym
    DobbyHook((void*)dlopen, (void*)my_dlopen, (void**)&orig_dlopen);
}