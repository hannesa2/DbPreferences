package info.dbprefs.sample

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import info.dbprefs.lib.DbPreferences
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("com.example.app", Context.MODE_PRIVATE)
        val l = sharedPreferences.getLong("xxx", Date().time)
        sharedPreferences.edit().putLong("xxx", 11).apply()

        val btn = Button(this)
        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                Log.d("x", "y")
            }
        })

        buttonRead.setOnClickListener {
            run {
                val value: String? = DbPreferences().get(MyConfigKeys.KEY_STRING, String::class.java)
                val testClass = DbPreferences().get<TestClass>(MyConfigKeys.KEY_OBJECT, TestClass::class.java)
                textViewObject.setText(testClass?.memberA ?: "empty")
                textView.setText(value ?: "empty")
                textViewInsert.text = (value ?: "empty") + " isOpen=" + DbPreferences().isOpen()
            }
        }

        buttonSave.setOnClickListener {
            run {
                DbPreferences().put(MyConfigKeys.KEY_STRING, textView.text.toString())
                DbPreferences().put(MyConfigKeys.KEY_OBJECT, TestClass(textViewObject.text.toString(), "B"))
            }
        }

        buttonPerformance.setOnClickListener {
            textViewInsert.text = ""
            textViewRead.text = ""
            run {
                var start = System.currentTimeMillis()
                val prefs = DbPreferences()
                for (item: Int in 1..2000) {
                    prefs.put(MyConfigKeys.KEY_STRING, "value$item")
                }
                textViewInsert.text = "time 2000 insert " + (System.currentTimeMillis() - start).toString() + " ms"

                start = System.currentTimeMillis()
                for (item: Int in 1..2000) {
                    @Suppress("UNUSED_VARIABLE")
                    var value: String? = prefs.get(MyConfigKeys.KEY_STRING, String::class.java)
                }
                textViewRead.text = "time 2000 get " + (System.currentTimeMillis() - start).toString() + " ms"
            }
        }

        buttonList.setOnClickListener {
            run {
                val listSource: ArrayList<TestClass> = ArrayList()
                listSource.add(TestClass("A", "aa"))
                listSource.add(TestClass("B", "bb"))
                DbPreferences().put(MyConfigKeys.KEY_LIST, listSource)

                val listType = object : TypeToken<ArrayList<TestClass>>() {
                }.type
                val myList: ArrayList<TestClass>? = DbPreferences().get(MyConfigKeys.KEY_LIST, listType)
                textViewInsert.text = ""
                textViewRead.text = getString(R.string.readListCount) + myList?.size.toString()
            }
        }

    }
}
