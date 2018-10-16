package info.dbprefs.lib

import androidx.room.Room
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.google.gson.Gson
import info.dbprefs.lib.room.PreferencesDatabase
import info.dbprefs.lib.room.entity.PreferenceRoom
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type

/**
 * An encrypted alternative to SharedPreferences. It's based on secure Room/SQlite and uses sqlcipher
 */
class DbPreferences {

    private var mParse: Parse

    constructor(gson: Gson) {
        mParse = GsonParser(gson)
    }

    constructor() {
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

    public fun putSerialized(key: ConfigKey, value: String?): Boolean {
        if (value != null) {
            val pref = PreferenceRoom()
            pref.key = key.keyname()
            pref.value = value
            appDatabase.preferenceDao().insert(pref)
        }
        return true
    }

    @Deprecated(message = "Try to avoid using a String")
    public fun putSerialized(key: String, value: String?): Boolean {
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

    fun <T> get(key: String, type: Type, default: T?): T? {
        var returningClass: T?
        val decodedText = getSerialized(key)
        decodedText?.let {
            try {
                returningClass = mParse.fromJson<T>(it, type)
                return returningClass
            } catch (e: Exception) {
                Log.e(e.message, "Exception for class $type decoded Text: $decodedText")
            }
        }
        return default
    }

    public fun getSerialized(key: ConfigKey): String? {
        val value = appDatabase.preferenceDao().getValue(key.keyname())
        return if (value == null)
            return null
        else
            return value.value
    }

    @Deprecated(message = "Try to avoid using a String")
    public fun getSerialized(key: String): String? {
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

    fun clearAllExcept(vararg keys: ConfigKey) {
        appDatabase.preferenceDao().all.forEach { item ->
            var keyList = keys.map { it.keyname() }
            if (!keys.map { it.keyname() }.contains(item.key)) {
                appDatabase.preferenceDao().deleteByKey(item.key)
            }
        }
    }

    fun getAll(): List<String> {
        return appDatabase.preferenceDao().all.map { it.key }
    }

    @Suppress("unused")
    companion object {
        lateinit var appDatabase: PreferencesDatabase

        @JvmOverloads
        fun init(context: Context, password: String = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID)) {
            // Room
            val factory = SafeHelperFactory(password.toCharArray())
            appDatabase = Room.databaseBuilder(context, PreferencesDatabase::class.java, PreferencesDatabase.ROOM_DATABASE_NAME)
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
