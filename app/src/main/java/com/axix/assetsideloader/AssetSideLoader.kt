package com.axix.assetsideloader

class AssetSideLoader {
    external fun InitHook(): Void

    companion object {
        init {
            System.loadLibrary("AssetSideLoader")
        }
    }
}