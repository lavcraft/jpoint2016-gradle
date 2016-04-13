package ru.jpoint.plugin

import org.asciidoctor.gradle.AsciidoctorPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * Created by aleksandr on 13.04.16.
 */
class DocumentationPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.plugins.withType(AsciidoctorPlugin) {
            project.afterEvaluate { p ->
                org.asciidoctor.extension.DependencyIncludeProcessor.project = p
            }
        }
    }
}
