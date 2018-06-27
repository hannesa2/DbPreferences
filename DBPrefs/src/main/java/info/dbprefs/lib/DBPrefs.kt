package info.dbprefs.lib

import android.arch.persistence.room.Room
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.google.gson.Gson
import com.raizlabs.android.dbflow.config.DatabaseConfig
import com.raizlabs.android.dbflow.config.FlowConfig
import com.raizlabs.android.dbflow.config.FlowManager
import com.raizlabs.android.dbflow.runtime.ContentResolverNotifier
import com.raizlabs.android.dbflow.sql.language.SQLite
import info.audio.analysis.room.AppDatabase
import info.dbprefs.lib.dbflow.DBFlowDatabase
import info.dbprefs.lib.dbflow.SQLCipherHelper
import info.dbprefs.lib.dbflow.model.Preference
import info.dbprefs.lib.dbflow.model.Preference_Table
import java.lang.reflect.Type

class DBPrefs {

    private var mParse: Parse

    init {
        mParse = GsonParser(Gson())
    }

    fun <T> put(key: ConfigKey, value: T): Boolean {
        return putSerialized<Any>(key, mParse.toJson(value))
    }

    fun <T> put(key: ConfigKey, value: List<T>): Boolean {
        return putSerialized<Any>(key, mParse.toJson(value))
    }

    fun <T> putSerialized(key: ConfigKey?, value: String?): Boolean {
        if (key == null) {
            throw IllegalArgumentException(ERROR_VALUE_CANNOT_BE_NULL)
        }
        //if the value is null, simply remove it
        if (value == null) {
            return remove(key)
        }

        val model: Preference
        val values = SQLite.select()
                .from(Preference::class.java)
                .where(Preference_Table.key.eq(key.toString())).queryList()

        if (values.size > 0) {
            model = values[0]
        } else {
            model = Preference()
            model.key = key.toString()
        }
        model.value = value

        val database = FlowManager.getDatabase(DBFlowDatabase::class.java)
        val transaction = database.beginTransactionAsync { databaseWrapper -> model.save(databaseWrapper) }.build()
        transaction.execute()

        return true
    }

    operator fun <T> get(key: ConfigKey, type: Type): T? {
        val returningClass: T?
        val decodedText = getSerialized(key)
        if (decodedText == "") {
            return null
        }
        try {
            returningClass = mParse.fromJson<T>(decodedText!!, type)
        } catch (e: Exception) {
            Log.e(e.localizedMessage, "Exception for class $type decoded Text: $decodedText")
            throw IllegalStateException(ERROR_COULD_NOT_PARSE_JSON_INTO + type, e)
        }

        return returningClass
    }

    private fun getSerialized(key: ConfigKey?): String? {
        if (key == null) {
            throw IllegalArgumentException(ERROR_KEY_CANNOT_BE_NULL)
        }

        val values = SQLite.select()
                .from(Preference::class.java)
                .where(Preference_Table.key.eq(key.toString())).queryList()

        return if (values.size > 0) {
            values[0].value
        } else {
            ""
        }
    }

    operator fun <T> get(key: ConfigKey, type: Type, defaultValue: T): T {
        val storage = get<T>(key, type)
        return if (storage == null || storage == "") {
            defaultValue
        } else storage
    }

    fun remove(key: ConfigKey): Boolean {
        return SQLite.delete()
                .from(Preference::class.java)
                .where(Preference_Table.key.eq(key.toString())).longValue() > 0
    }

    operator fun contains(key: ConfigKey): Boolean {
        return SQLite.select()
                .from(Preference::class.java)
                .where(Preference_Table.key.eq(key.toString())).longValue() > 0
    }

    companion object {
        lateinit var appDatabase: AppDatabase

        fun init(context: Context) {
            init(context, Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID))
        }

        fun init(context: Context, password: String) {
            // dbflow
            FlowManager.init(FlowConfig.Builder(context)
                    .addDatabaseConfig(DatabaseConfig.Builder(DBFlowDatabase::class.java)
                            .modelNotifier(ContentResolverNotifier(BuildConfig.APPLICATION_ID + ".authority"))
                            .openHelper({ databaseDefinition, listener -> SQLCipherHelper(databaseDefinition, listener, password) })
                            .build())
                    .openDatabasesOnInit(true)
                    .build())

            // Room
            val factory = SafeHelperFactory(password.toCharArray())
            appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.ROOM_DATABASE_NAME)
                    .openHelperFactory(factory)
                    .build();
        }

        fun destroy() {
            FlowManager.destroy()
        }

        private val ERROR_VALUE_CANNOT_BE_NULL = "Value cannot be null"
        private val ERROR_KEY_CANNOT_BE_NULL = "Key cannot be null"
        private val ERROR_COULD_NOT_PARSE_JSON_INTO = "could not parse json into "
    }

}
