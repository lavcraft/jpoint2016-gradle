import org.gradle.api.Project

/**
 * @author tolkv
 * @since 15/04/16
 */
class DocumentationPluginExtension {
  String documentationConfigurationName = DocumentationPlugin.DOCUMENTATION_CONFIGURATION_NAME
  String documentationSourceConfigurationName = DocumentationPlugin.DOCUMENTATION_DOCS_CONFIGURATION_NAME
  String documentationDistTaskName = DocumentationPlugin.DOCUMENTATION_DIST_TASK_NAME

  /**
   * enable/disable rich logging
   */
  boolean debug

  final Project project

  DocumentationPluginExtension(Project project) {
    this.project = project
  }
}
