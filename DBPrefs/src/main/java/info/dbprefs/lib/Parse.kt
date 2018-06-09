package info.dbprefs.lib

import java.lang.reflect.Type

/**
 * Parser interfaces, used for parsing objects to and from json
 */
internal interface Parse {
    @Throws(Exception::class)
    fun <T> fromJson(content: String, type: Type): T?

    fun <T> toJson(body: T): String

    fun toJson(body: Any, objectType: Type): String
}
