package info.dbprefs.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import info.dbprefs.lib.DBPrefs
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonLoad.setOnClickListener {
            run {
                val string = DBPrefs().get<String>(MyConfigKeys.KEY_STRING, String::class.java)
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
    }
}
