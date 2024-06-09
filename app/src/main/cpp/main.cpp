#include <cstring>
#include <jni.h>
#include <cinttypes>
#include <string>
#include <unistd.h>
#include <fstream>
#include <dobby.h>
#include "xdl.h"
#include "log.h"
#include "utf8.h"
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

void* Handle = nullptr;

std::string pakageName = "";

std::string modDir = "/storage/emulated/0/Android/data/";

//------------------------------------------------------------------------------------------------//

void* ReplacePath(std::string path) {
    if (path.find("AssetBundles") != std::string::npos) {
        std::string modPath = modDir + path.substr(path.find("AssetBundles") + 12);
        if (modPath.find(".ys") != std::string::npos) {
            //去除.ys后缀
            modPath = modPath.substr(0, modPath.find(".ys"));
        }
        // 判断文件是否存在
        std::ifstream file(modPath);
        if (file) {
            LOG_I("Redirect from: %s", path.c_str());
            LOG_I("To: %s", modPath.c_str());
            return il2cpp_string_new(modPath.c_str());
        }
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
    init_il2cpp_api(Handle);
    LOG_I("hooking...");

    modDir += pakageName;
    modDir += "/files/mods";
    LOG_I("modDir: %s", modDir.c_str());

    uint64_t LoadFromFile_Internal_addr = (uint64_t)il2cpp_resolve_icall("UnityEngine.AssetBundle::LoadFromFile_Internal(System.String,System.UInt32,System.UInt64)");
    if (LoadFromFile_Internal_addr) {
        LOG_I("LoadFromFile_Internal_addr: %" PRIx64"", LoadFromFile_Internal_addr);
        DobbyHook(
                (void *)LoadFromFile_Internal_addr,
                (void *)Hook_LoadFromFile_Internal,
                (void **)&AssetBundle_LoadFromFile_Internal
        );
    }

    uint64_t LoadFromFileAsync_Internal_addr = (uint64_t)il2cpp_resolve_icall("UnityEngine.AssetBundle::LoadFromFileAsync_Internal(System.String,System.UInt32,System.UInt64)");
    if (LoadFromFileAsync_Internal_addr) {
        LOG_I("LoadFromFileAsync_Internal_addr: %" PRIx64"", LoadFromFileAsync_Internal_addr);
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
    LOG_I("il2cpp_init finished with result: %d", result);
    if (result == 1){
        DobbyDestroy((void*)hook_il2cpp_init);
        Handle = xdl_open("libil2cpp.so", 0);
        LOG_I("libil2cpp.so handle: %p", Handle);
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
        LOG_I("symbol il2cpp_init found at: %p", addr);
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
Java_com_axix_assetsideloader_AssetSideLoader_InitHook(JNIEnv *env, jobject thiz, jstring str) {
    pakageName = env->GetStringUTFChars(str, NULL);
    LOG_D("Hooking dlsym...");
    DobbyHook((void*) dlsym, (void*)my_dlsym, (void**)&orig_dlsym);
}