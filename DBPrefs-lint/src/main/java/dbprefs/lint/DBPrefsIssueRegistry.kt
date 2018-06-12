package dbprefs.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

import java.util.Arrays

class DBPrefsIssueRegistry : IssueRegistry() {
    override val issues: List<Issue>
        get() = Arrays.asList(*DBPrefsUsageDetector.getIssues())

    override val api: Int
        get() = CURRENT_API
}
