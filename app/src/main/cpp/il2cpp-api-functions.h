#ifndef DO_API_NO_RETURN
#define DO_API_NO_RETURN(r, n, p) DO_API(r,n,p)
#endif

DO_API(Il2CppMethodPointer, il2cpp_resolve_icall, (const char* name));
DO_API(const void**, il2cpp_domain_get_assemblies, (const void * domain, size_t * size));
DO_API(bool, il2cpp_is_vm_thread, (void * thread));
DO_API(Il2CppChar*, il2cpp_string_chars, (void * str));
DO_API(void*, il2cpp_string_new, (const char* str));
DO_API(const void*, il2cpp_get_corlib, ());