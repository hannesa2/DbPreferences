package info.dbprefs.androidtests.typeadapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

import java.io.IOException

//taken from https://www.tutorialspoint.com/gson/gson_custom_adapters.htm
internal class StudentAdapter : TypeAdapter<Student>() {
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Student {
        val student = Student()
        reader.beginObject()
        var fieldname: String? = null

        while (reader.hasNext()) {
            var token = reader.peek()

            if (token == JsonToken.NAME) {
                //get the current token
                fieldname = reader.nextName()
            }
            if ("name" == fieldname) {
                //move to next token
                token = reader.peek()
                student.name = reader.nextString()
            }
            if ("rollNo" == fieldname) {
                //move to next token
                token = reader.peek()
                student.rollNo = reader.nextInt()
            }
        }
        reader.endObject()
        return student
    }

    @Throws(IOException::class)
    override fun write(writer: JsonWriter, student: Student) {
        writer.beginObject()
        writer.name("name")
        writer.value(student.name)
        writer.name("rollNo")
        writer.value(student.rollNo.toLong())
        writer.endObject()
    }
}