package dbprefs.lint

import com.android.tools.lint.detector.api.*
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression
import java.util.*

class DBPrefsUsageDetector : Detector(), Detector.UastScanner {

    override fun getApplicableMethodNames(): List<String> {
        return Arrays.asList("getSharedPreferences") //"edit",
    }

    override fun visitMethod(context: JavaContext, call: UCallExpression, method: PsiMethod) {
        val methodName = call.methodName
        val evaluator = context.evaluator

        if (evaluator.isMemberInClass(method, "android.content.ContextWrapper")) {
            System.err.println("lint found $method")
            val fix = quickFixIssueLog(call)
            context.report(ISSUE_SHARED_PREFS, call, context.getLocation(call), "Using 'SharedPreferences' instead of 'DBPrefs'", fix)
            return
        } else
            System.err.println("lint not found $method")
        //        if (evaluator.isMemberInClass(method, "android.content.SharedPreferences")) {
        //            LintFix fix = quickFixIssueLog(call);
        //            context.report(ISSUE_SHARED_PREFS, call, context.getLocation(call), "Using 'SharedPreferences' instead of 'DBPrefs'", fix);
        //            return;
        //        }
    }

    private fun quickFixIssueLog(logCall: UCallExpression): LintFix {
        val fixSource2 = "DBPrefs()"

        val logCallSource = logCall.asSourceString()
        val fixGrouper = fix().group()
        fixGrouper.add(fix().replace().text(logCallSource).shortenNames().reformat(true).with(fixSource2).build())
        return fixGrouper.build()
    }

    companion object {

        internal val issues: Array<Issue>
            get() = arrayOf(ISSUE_SHARED_PREFS)

        val ISSUE_SHARED_PREFS = Issue.create("SharedPrefsUsage", "SharedPreferences are used, use DBPrefs instead",
                "Since SharedPreferences are used in the project, it is likely that calls to Preferences should instead be going to DBPrefs.",
                Category.USABILITY, 5, Severity.WARNING,
                Implementation(DBPrefsUsageDetector::class.java, Scope.JAVA_FILE_SCOPE))
    }
}
