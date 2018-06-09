package info.dbprefs.lib.database


import com.raizlabs.android.dbflow.annotation.Database

@Database(name = DBFlowDatabase.NAME, version = DBFlowDatabase.VERSION)
internal object DBFlowDatabase {

    const val NAME: String = "DBPrefs"

    const val VERSION: Int = 1

}