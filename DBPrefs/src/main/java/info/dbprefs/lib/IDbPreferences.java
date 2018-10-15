package info.dbprefs.lib;

import java.lang.reflect.Type;
import java.util.List;

import info.dbprefs.lib.ConfigKey;

/**
 * An encrypted alternative to SharedPreferences. It's based on secure Room/SQlite and uses Sqlcipher
 */
public interface IDbPreferences {

    /**
     * Put any Class
     */
    <T> Boolean put(ConfigKey key, T value);

    /**
     * Put any Class with a String key
     */
    <T> Boolean put(String key, T value);

    /**
     * Put a List
     */
    <T> Boolean put(ConfigKey key, List<T> value);

    /**
     * get a value from ConfigKey
     */
    <T> T get(ConfigKey key, Type type);

    /**
     * get a value from String
     */
    <T> T get(String key, Type type);

    /**
     * get a value with default value
     */
    <T> T get(ConfigKey key, Type type, T defaultValue);
}
