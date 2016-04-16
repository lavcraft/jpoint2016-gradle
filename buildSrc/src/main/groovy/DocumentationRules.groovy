import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.bundling.Zip
import org.gradle.model.ModelMap
import org.gradle.model.Mutate
import org.gradle.model.RuleSource
/**
 * Created by aleksandr on 16.04.16.
 */
class DocumentationRules extends RuleSource {
    public static final String DOCUMENTATION_DIST_TASK_NAME = 'documentationDistZipNew'

    @Mutate void create (ModelMap<Task> tasks, Documentation documentation, ExtensionContainer extensionContainer) {
        tasks.create(DOCUMENTATION_DIST_TASK_NAME, Zip) {
            group 'Documentation'
            description 'Create documentation sources archive'
        }
    }
}
