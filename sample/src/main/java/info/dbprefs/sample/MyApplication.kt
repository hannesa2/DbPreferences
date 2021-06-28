package info.dbprefs.sample

import android.app.Application
import android.os.StrictMode
import android.util.Log
import info.dbprefs.lib.DbPreferences


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DbPreferences.init(this, "your secret")

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build())

        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build())


        Log.w("onCreate()", "dbOpen=s" + DbPreferences().isOpen())
        val value: String? = DbPreferences().get(MyConfigKeys.KEY_STRING, String::class.java)
        Log.w("onCreate()", "value=" + value + " dbOpen=s" + DbPreferences().isOpen())
    }

    override fun onTerminate() {
        super.onTerminate()
        DbPreferences.destroy()
    }

}
