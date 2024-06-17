#include <cstring>
#include <jni.h>
#include <cinttypes>
#include <string>
#include <unistd.h>
#include <fstream>
#include <iostream>
#include <filesystem>
#include <dobby.h>
#include "log.h"
#include "utf8.h"
#include "xdl.h"
#include "il2cpp-class.h"

//------------------------------------------------------------------------------------------------//

#define DO_API(r, n, p) r (*n) p

#include "il2cpp-api-functions.h"

#undef DO_API

void init_il2cpp_api(void *handle) {
#define DO_API(r, n, p) {                      \
    n = (r (*) p)xdl_sym(handle, #n, nullptr); \
    if(!n) {                                   \
        LOG_W("api not found %s", #n);          \
    }                                          \
}

#include "il2cpp-api-functions.h"

#undef DO_API
}

//------------------------------------------------------------------------------------------------//

typedef struct {
    void* handle;
    const char* pakageName;
    const char* dataDir;
    const char* apkDir;
    const char* modDir;
} AppInfo;

AppInfo appInfo;

//------------------------------------------------------------------------------------------------//

void* ReplacePath(std::string path) {
    std::string new_path;
    if (path.find(appInfo.apkDir) == 0) {
        new_path = appInfo.modDir + path.substr(strlen(appInfo.apkDir));
    } else if (path.find(appInfo.dataDir) == 0) {
        new_path = appInfo.modDir + path.substr(strlen(appInfo.dataDir));
    } else {
        new_path = path;
    }
    if (std::__fs::filesystem::exists(new_path)) {
        LOG_D("Replace path: %s -> %s", path.c_str(), new_path.c_str());
        return il2cpp_string_new(new_path.c_str());
    }
    return il2cpp_string_new(path.c_str());
}

//------------------------------------------------------------------------------------------------//

std::string Il2CppStringToStdString(void *str) {
    const Il2CppChar* chars = il2cpp_string_chars(str);
    const Il2CppChar *ptr = chars;
    size_t length = 0;
    while (*ptr) {
        ptr++;
        length++;
        if (length == -1)
            break;
    }
    std::string utf8String;
    utf8String.reserve(length);
    utf8::unchecked::utf16to8(chars, ptr, std::back_inserter(utf8String));
    return utf8String;
}

//------------------------------------------------------------------------------------------------//

typedef void* (*LoadFromFile_Internal)(void*, uint32_t, uint64_t);
typedef void* (*LoadFromFileAsync_Internal)(void*, uint32_t, uint64_t);
LoadFromFile_Internal AssetBundle_LoadFromFile_Internal = nullptr;
LoadFromFileAsync_Internal AssetBundle_LoadFromFileAsync_Internal = nullptr;

void* Hook_LoadFromFile_Internal(void* path, uint32_t crc, uint64_t offset){
    std::string pathStr = Il2CppStringToStdString(path);
    return AssetBundle_LoadFromFile_Internal(ReplacePath(pathStr), crc, offset);
}

void* Hook_LoadFromFileAsync_Internal(void* path, uint32_t crc, uint64_t offset){
    std::string pathStr = Il2CppStringToStdString(path);
    return AssetBundle_LoadFromFileAsync_Internal(ReplacePath(pathStr), crc, offset);
}

//------------------------------------------------------------------------------------------------//

void hook_funcs(){
    LOG_I("Init Il2cpp api...");
    init_il2cpp_api(appInfo.handle);
    LOG_I("hooking...");

    uint64_t LoadFromFile_Internal_addr = (uint64_t)il2cpp_resolve_icall("UnityEngine.AssetBundle::LoadFromFile_Internal(System.String,System.UInt32,System.UInt64)");
    if (LoadFromFile_Internal_addr) {
        LOG_D("LoadFromFile_Internal_addr: %" PRIx64"", LoadFromFile_Internal_addr);
        DobbyHook(
                (void *)LoadFromFile_Internal_addr,
                (void *)Hook_LoadFromFile_Internal,
                (void **)&AssetBundle_LoadFromFile_Internal
        );
    }

    uint64_t LoadFromFileAsync_Internal_addr = (uint64_t)il2cpp_resolve_icall("UnityEngine.AssetBundle::LoadFromFileAsync_Internal(System.String,System.UInt32,System.UInt64)");
    if (LoadFromFileAsync_Internal_addr) {
        LOG_D("LoadFromFileAsync_Internal_addr: %" PRIx64"", LoadFromFileAsync_Internal_addr);
        DobbyHook(
                (void *)LoadFromFileAsync_Internal_addr,
                (void *)Hook_LoadFromFileAsync_Internal,
                (void **)&AssetBundle_LoadFromFileAsync_Internal
        );
    }
}

//------------------------------------------------------------------------------------------------//

typedef int (*il2cpp_init_func)(const char *);

il2cpp_init_func il2cpp_init_origin = nullptr;

int hook_il2cpp_init(const char *domain_name) {
    int result = il2cpp_init_origin(domain_name);
    DobbyDestroy((void*)hook_il2cpp_init);
    LOG_D("il2cpp_init finished with result: %d", result);
    if (result == 1){
        DobbyDestroy((void*)hook_il2cpp_init);
        appInfo.handle = xdl_open("libil2cpp.so", 0);
        LOG_D("libil2cpp.so handle: %p", appInfo.handle);
        hook_funcs();
    }
    return result;
}

//------------------------------------------------------------------------------------------------//

typedef void* (*dlsym_t)(void* handle, const char* symbol);

static dlsym_t orig_dlsym = nullptr;

void* my_dlsym(void* handle, const char* symbol){
    void* addr = orig_dlsym(handle, symbol);
    if (strcmp(symbol, "il2cpp_init") == 0) {
        LOG_D("symbol il2cpp_init found at: %p", addr);
        DobbyDestroy((void*)my_dlsym);
        DobbyHook(
                (void*)addr,
                (void*)hook_il2cpp_init,
                (void**)&il2cpp_init_origin
        );
    }
    return addr;
}

//------------------------------------------------------------------------------------------------//

extern "C"
JNIEXPORT void JNICALL
Java_top_axix_assetsideloader_AssetSideLoader_InitHook(JNIEnv *env, jobject thiz, jstring pgn, jstring dataPath, jstring apkPath, jstring modPath) {
    LOG_D("Native hook init...");
    appInfo.pakageName = env->GetStringUTFChars(pgn, NULL);
    LOG_D("Package name: %s", appInfo.pakageName);
    appInfo.dataDir = env->GetStringUTFChars(dataPath, NULL);
    LOG_D("Data dir: %s", appInfo.dataDir);
    appInfo.apkDir = env->GetStringUTFChars(apkPath, NULL);
    LOG_D("Apk dir: %s", appInfo.apkDir);
    appInfo.modDir = env->GetStringUTFChars(modPath, NULL);
    LOG_D("Mod dir: %s", appInfo.modDir);
    LOG_D("Hooking dlsym...");

    DobbyHook((void*) dlsym, (void*)my_dlsym, (void**)&orig_dlsym);
}