package dbprefs.lint

import com.android.tools.lint.checks.infrastructure.TestFiles.java
import com.android.tools.lint.checks.infrastructure.TestLintTask.lint
import org.junit.Test

class DBPrefsUsageDetectorTest {
    private val TIMBER_STUB = java("""
      |package timber.log;
      |public class Timber {
      |  public static void d(String s, Object... args) {}
      |  public static void d(Throwable t, String s, Object... args) {}
      |  public static Tree tag(String tag) { return new Tree(); }
      |  public static class Tree {
      |    public void d(String s, Object... args) {}
      |    public void d(Throwable t, String s, Object... args) {}
      |  }
      |  private static final Tree TREE_OF_SOULS = new Tree();
      |}""".trimMargin())

    @Test
    fun usingSharedPreferences() {
        lint()
                .files(
                        java("""
                |package foo;
                |import android.app.Activity;
                |import android.content.SharedPreferences;
                |public class Example extends Activity {
                |  public void useIt() {
                |    SharedPreferences sharedPreferences = getSharedPreferences("filename", 0);
                |    sharedPreferences.edit().apply();
                |  }
                |}""".trimMargin())
                )
                .issues(DBPrefsUsageDetector.ISSUE_SHARED_PREFS)
                .run()
                .expect("""
                |src/foo/Example.java:6: Information: Using 'SharedPreferences' instead of 'DBPrefs' [SharedPrefsUsage]
                |    SharedPreferences sharedPreferences = getSharedPreferences("filename", 0);
                |                                          ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                |0 errors, 0 warnings
                |""".trimMargin())
                .expectFixDiffs("""
                |Fix for src/foo/Example.java line 5: Replace with DBPrefs():
                |@@ -6 +6
                |-     SharedPreferences sharedPreferences = getSharedPreferences("filename", 0);
                |+     SharedPreferences sharedPreferences = DBPrefs();
                |""".trimMargin())
    }

}
