package info.dbprefs.lib

interface ConfigKey {

    fun keyname(): String {
        return javaClass.simpleName + "_" + toString()
    }

}
