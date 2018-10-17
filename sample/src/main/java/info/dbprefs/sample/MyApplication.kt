package info.dbprefs.sample

import info.dbprefs.lib.DbPreferences
import android.os.StrictMode
import androidx.multidex.MultiDexApplication


class MyApplication : MultiDexApplication() {

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
    }

    override fun onTerminate() {
        super.onTerminate()
        DbPreferences.destroy()
    }

}
