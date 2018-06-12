package dbprefs.lint;

import com.android.tools.lint.client.api.JavaEvaluator;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.LintFix;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.PsiMethod;

import org.jetbrains.uast.UBinaryExpression;
import org.jetbrains.uast.UCallExpression;
import org.jetbrains.uast.UExpression;

import java.util.Arrays;
import java.util.List;

import static com.android.tools.lint.detector.api.ConstantEvaluator.evaluateString;
import static org.jetbrains.uast.UastLiteralUtils.isStringLiteral;
import static org.jetbrains.uast.UastUtils.evaluateString;

public final class DBPrefsUsageDetector extends Detector implements Detector.UastScanner {

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList("getSharedPreferences"); //"edit",
    }

    @Override
    public void visitMethod(JavaContext context, UCallExpression call, PsiMethod method) {
        String methodName = call.getMethodName();
        JavaEvaluator evaluator = context.getEvaluator();

        if (evaluator.isMemberInClass(method, "android.content.ContextWrapper")) {
            LintFix fix = quickFixIssueLog(call);
            context.report(ISSUE_SHAREDPREFS, call, context.getLocation(call), "Using 'SharedPreferences' instead of 'DBPrefs'", fix);
            return;
        }
//        if (evaluator.isMemberInClass(method, "android.content.SharedPreferences")) {
//            LintFix fix = quickFixIssueLog(call);
//            context.report(ISSUE_SHAREDPREFS, call, context.getLocation(call), "Using 'SharedPreferences' instead of 'DBPrefs'", fix);
//            return;
//        }
    }

    private LintFix quickFixIssueLog(UCallExpression logCall) {
        String fixSource2 = "DBPrefs()";

        String logCallSource = logCall.asSourceString();
        LintFix.GroupBuilder fixGrouper = fix().group();
        fixGrouper.add(fix().replace().text(logCallSource).shortenNames().reformat(true).with(fixSource2).build());
        return fixGrouper.build();
    }

    static Issue[] getIssues() {
        return new Issue[]{
                ISSUE_SHAREDPREFS
        };
    }

    public static final Issue ISSUE_SHAREDPREFS =
            Issue.create("SharedPrefsUsage", "SharedPreferences are used, use DBPrefs instead",
                    "Since SharedPreferences are used in the project, it is likely that calls to Preferences should instead be going to DBPrefs.",
                    Category.USABILITY, 5, Severity.INFORMATIONAL,
                    new Implementation(DBPrefsUsageDetector.class, Scope.JAVA_FILE_SCOPE));
}
