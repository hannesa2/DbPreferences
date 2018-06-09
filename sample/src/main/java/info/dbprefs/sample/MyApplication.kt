package info.dbprefs.sample

import android.app.Application
import info.dbprefs.lib.DBPrefs

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DBPrefs.init(this, "your secret")
    }

    override fun onTerminate() {
        super.onTerminate()
        DBPrefs.destroy()
    }

}
