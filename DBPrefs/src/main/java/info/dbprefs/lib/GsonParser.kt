package info.dbprefs.lib

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.lang.reflect.Type

class GsonParser(private val gson: Gson) : Parse {

    @Throws(JsonSyntaxException::class)
    override fun <T> fromJson(content: String, type: Type): T? {
        return if (content.isBlank()) {
            null
        } else gson.fromJson<T>(content, type)
    }

    override fun <T> toJson(body: T): String {
        return gson.toJson(body)
    }

    override fun toJson(body: Any, objectType: Type): String {
        return gson.toJson(body, objectType)
    }
}
