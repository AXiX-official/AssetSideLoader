package top.axix.assetsideloader

class AssetSideLoader {
    external fun InitHook(pakageName: String, dataPath: String, apkPath: String, modPath: String): Void

    companion object {
        init {
            System.loadLibrary("AssetSideLoader")
        }
    }
}