package info.dbprefs.lib

import java.lang.reflect.Type

/**
 * An encrypted alternative to SharedPreferences. It's based on secure Room/SQlite and uses Sqlcipher
 */
interface IDbPreferencesK {

    /**
     * Put any Class
     */
    fun <T> put(key: ConfigKey, value: T): Boolean

    /**
     * Put any Class with a String key
     */
    fun <T> put(key: String, value: T): Boolean

    /**
     * Put a List
     */
    fun <T> put(key: ConfigKey, value: List<T>): Boolean

    /**
     * get a value from ConfigKey
     */
    fun <T> get(key: ConfigKey, type: Type): T?

    /**
     * get a value from String
     */
    fun <T> get(key: String, type: Type): T?

    /**
     * get a value with default value
     */
    fun <T> get(key: String, type: Type, default: T?): T?
}