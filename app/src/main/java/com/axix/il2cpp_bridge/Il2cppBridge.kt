package com.axix.il2cpp_bridge

class Il2cppBridge {
    external fun InitHook(): Void

    companion object {
        init {
            System.loadLibrary("il2cpp-bridge")
        }
    }
}