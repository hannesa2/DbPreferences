package info.dbprefs.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.gson.reflect.TypeToken
import info.dbprefs.lib.DBPrefs
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonLoad.setOnClickListener {
            run {
                val string: String? = DBPrefs().get(MyConfigKeys.KEY_STRING, String::class.java)
                val testClass = DBPrefs().get<TestClass>(MyConfigKeys.KEY_OBJECT, TestClass::class.java)
                textViewObject.setText(testClass?.memberA ?: "empty")
                textView.setText(string ?: "empty")
            }
        }

        buttonSave.setOnClickListener {
            run {
                DBPrefs().put(MyConfigKeys.KEY_STRING, textView.text.toString())
                DBPrefs().put(MyConfigKeys.KEY_OBJECT, TestClass(textViewObject.text.toString(), "B"))
            }
        }

        buttonPerformance.setOnClickListener {
            textViewInsert.setText("")
            textViewRead.setText("")
            run {
                var start = System.currentTimeMillis()
                val prefs = DBPrefs()
                for (item: Int in 1..2000) {
                    prefs.put(MyConfigKeys.KEY_STRING, "value" + item)
                }
                textViewInsert.setText("time 2000 insert " + (System.currentTimeMillis() - start).toString() + " ms")

                start = System.currentTimeMillis()
                for (item: Int in 1..2000) {
                    @Suppress("UNUSED_VARIABLE")
                    var value: String? = prefs.get(MyConfigKeys.KEY_STRING, String::class.java)
                }
                textViewRead.setText("time 2000 get " + (System.currentTimeMillis() - start).toString() + " ms")
            }
        }

        buttonList.setOnClickListener {
            run {
                val listSource: ArrayList<TestClass> = ArrayList()
                listSource.add(TestClass("A", "aa"))
                listSource.add(TestClass("B", "bb"))
                DBPrefs().put(MyConfigKeys.KEY_LIST, listSource)

                val listType = object : TypeToken<ArrayList<TestClass>>() {
                }.type
                val myList: ArrayList<TestClass>? = DBPrefs().get(MyConfigKeys.KEY_LIST, listType)
                textViewInsert.setText("")
                textViewRead.setText("read list has " + myList?.size.toString() + " items")
            }
        }

    }
}
