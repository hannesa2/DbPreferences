package info.dbprefs.lib

import android.annotation.SuppressLint
import android.arch.persistence.room.Room
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.google.gson.Gson
import info.dbprefs.lib.room.AppDatabase
import info.dbprefs.lib.room.entity.PreferenceRoom
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type

class DBPrefs {

    private var mParse: Parse

    init {
        mParse = GsonParser(Gson())
    }

    fun <T> put(key: ConfigKey, value: T): Boolean {
        return putSerialized(key, mParse.toJson(value))
    }

    fun <T> put(key: String, value: T): Boolean {
        return putSerialized(key, mParse.toJson(value))
    }

    fun <T> put(key: ConfigKey, value: List<T>): Boolean {
        return putSerialized(key, mParse.toJson(value))
    }

    private fun putSerialized(key: ConfigKey, value: String?): Boolean {
        if (value != null) {
            val pref = PreferenceRoom()
            pref.key = key.keyname()
            pref.value = value
            appDatabase.preferenceDao().insert(pref)
        }
        return true
    }

    @Deprecated(message = "Try to avoid using a String")
    private fun putSerialized(key: String, value: String?): Boolean {
        if (value != null) {
            val pref = PreferenceRoom()
            pref.key = key
            pref.value = value
            appDatabase.preferenceDao().insert(pref)
        }
        return true
    }

    fun <T> get(key: ConfigKey, type: Type): T? {
        val returningClass: T?
        val decodedText = getSerialized(key) ?: return null
        try {
            returningClass = mParse.fromJson<T>(decodedText, type)
            return returningClass
        } catch (e: Exception) {
            Log.e(e.message, "Exception for class $type decoded Text: $decodedText")
        }
        return null
    }

    fun <T> get(key: String, type: Type): T? {
        val returningClass: T?
        val decodedText = getSerialized(key) ?: return null
        try {
            returningClass = mParse.fromJson<T>(decodedText, type)
            return returningClass
        } catch (e: Exception) {
            Log.e(e.message, "Exception for class $type decoded Text: $decodedText")
        }
        return null
    }

    fun <T> get(key: String, type: Type, default : T?): T? {
        val returningClass: T?
        val decodedText = getSerialized(key) ?: return null
        try {
            returningClass = mParse.fromJson<T>(decodedText, type)
            return returningClass
        } catch (e: Exception) {
            Log.e(e.message, "Exception for class $type decoded Text: $decodedText")
        }
        return default
    }

    private fun getSerialized(key: ConfigKey): String? {
        val value = appDatabase.preferenceDao().getValue(key.keyname())
        return if (value == null)
            return null
        else
            return value.value
    }

    @Deprecated(message = "Try to avoid using a String")
    private fun getSerialized(key: String): String? {
        val value = appDatabase.preferenceDao().getValue(key)
        return if (value == null)
            return null
        else
            return value.value
    }

    fun <T> getFlowableValue(key: ConfigKey, type: Type): Flowable<T>? {
        return appDatabase
                .preferenceDao()
                .getValueFlowable(key.keyname())
                .map {
                    val returningClass: T?
                    val decodedText = it.value
                    try {
                        returningClass = mParse.fromJson<T>(decodedText, type)
                        Flowable.just(returningClass)
                    } catch (e: Exception) {
                        Log.e(e.message, "Exception for class $type decoded Text: $decodedText")
                    }
                    null
                }
    }

    fun <T> get(key: ConfigKey, type: Type, defaultValue: T): T {
        val storage = get<T>(key, type)
        return if (storage == null || storage == "") {
            defaultValue
        } else storage
    }

    fun remove(key: ConfigKey): Boolean {
        appDatabase.preferenceDao().deleteByKey(key.keyname())
        return true
    }

    fun remove(key: String): Boolean {
        appDatabase.preferenceDao().deleteByKey(key)
        return true
    }

    fun clearAll() {
        appDatabase.clearAllTables()
    }

    fun close() {
        appDatabase.close()
    }

    fun contains(key: ConfigKey): Boolean {
        return appDatabase.preferenceDao().countKey(key.keyname()) == 1
    }

    fun contains(key: String): Boolean {
        return appDatabase.preferenceDao().countKey(key) == 1
    }

    fun count(): Int {
        return appDatabase.preferenceDao().all.size
    }

    fun clearAllExcept(vararg keys: String) {
        appDatabase.preferenceDao().all.forEach {
            if (!keys.contains(it.key)) {
                appDatabase.preferenceDao().deleteByKey(it.key)
            }
        }
    }

    fun getAll() : List<String> {
        return appDatabase.preferenceDao().all.map { it.key }
    }

    @Suppress("unused")
    companion object {
        lateinit var appDatabase: AppDatabase

        @SuppressLint("HardwareIds")
        fun init(context: Context) {
            init(context, Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))
        }

        fun init(context: Context, password: String) {
            // Room
            val factory = SafeHelperFactory(password.toCharArray())
            appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.ROOM_DATABASE_NAME)
                    .openHelperFactory(factory).allowMainThreadQueries()
                    .build()

            val start = System.currentTimeMillis()

            // the first access need > 150 ms, to avoid the long time on main thread, it makes the first call in io-thread
            Completable.fromAction {
                appDatabase.preferenceDao().getValueFlowable("reduceFirstAccessTime")
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { Log.d("time init", (System.currentTimeMillis() - start).toString() + " ms") }
        }

        fun destroy() {
            appDatabase.close()
        }

        private val ERROR_KEY_CANNOT_BE_NULL = "Key cannot be null"
    }

}
