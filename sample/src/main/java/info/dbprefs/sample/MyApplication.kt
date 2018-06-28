package info.dbprefs.sample

import android.app.Application
import info.dbprefs.lib.DBPrefs
import android.os.StrictMode
import android.support.multidex.MultiDexApplication


class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        DBPrefs.init(this, "your secret")

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
        DBPrefs.destroy()
    }

}
