package com.axix.assetsideloader

class AssetSideLoader {
    external fun InitHook(pakageName: String): Void

    companion object {
        init {
            System.loadLibrary("AssetSideLoader")
        }
    }
}