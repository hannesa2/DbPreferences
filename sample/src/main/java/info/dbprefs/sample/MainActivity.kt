package info.dbprefs.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import info.dbprefs.lib.DBPrefs
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

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

        var start = System.currentTimeMillis()
        val prefs = DBPrefs()
        for (item: Int in 1..2000) {
            prefs.put(MyConfigKeys.KEY_STRING, "value" + item)
        }
        Log.d("time test insert", (System.currentTimeMillis() - start).toString() + " ms")
        textViewInsert.setText("time 2000 insert " + (System.currentTimeMillis() - start).toString() + " ms")

        start = System.currentTimeMillis()
        for (item: Int in 1..2000) {
            var value = prefs.get<String>(MyConfigKeys.KEY_STRING, String::class.java)
        }
        Log.d("time test get", (System.currentTimeMillis() - start).toString() + " ms")
        textViewRead.setText("time 2000 get " + (System.currentTimeMillis() - start).toString() + " ms")
    }
}
