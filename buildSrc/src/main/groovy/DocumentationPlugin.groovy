import org.asciidoctor.extension.DependencyIncludeProcessor
import org.asciidoctor.gradle.AsciidoctorPlugin
import org.asciidoctor.gradle.AsciidoctorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip

/**
 * @author aleksandr
 * @author tolkv
 * @since 13.04.16
 */
class DocumentationPlugin implements Plugin<Project> {

  public static final String EXTENSION_NAME_DOCS = 'docs'
  public static final String DOCUMENTATION_CONFIGURATION_NAME = 'documentationConfiguration'
  public static final String DOCUMENTATION_DOCS_CONFIGURATION_NAME = 'docs'
  public static final String DOCUMENTATION_DIST_TASK_NAME = 'documentationDistZip'

  @Override
  void apply(Project project) {
    project.configure(project) {
      apply plugin: 'base'
//шаг 1 конфигурируем создаем все что необходиом
      DocumentationPluginExtension extension = extensions.create(EXTENSION_NAME_DOCS, DocumentationPluginExtension, project)

      configurations.maybeCreate(extension.documentationConfigurationName)
      configurations.maybeCreate(extension.documentationSourceConfigurationName)

      tasks.create(
          name: extension.documentationDistTaskName,
          type: Zip,
          group: 'Documentation',
          description: 'Create documentation sources archive')

//шаг последний, а почему бы нам не добавить все зависимости? а то некрасиво
      def handler = dependencies

      dependencies {
        (rootProject.subprojects - project).each { p ->
          "${extension.documentationSourceConfigurationName}" handler.project(path: ':' + p.name, configuration: 'documentationConfiguration')
        }
      }

//шаг 2, сконфигурируем существующие таски
      afterEvaluate {
        def dTask = tasks.findByName(extension.documentationDistTaskName).configure {
          from fileTree(dir: project.file('src/docs/'), include: '**/*.adoc')
        }

        artifacts {
          "$extension.documentationConfigurationName" dTask
        }

        project.dependencies.createArtifactResolutionQuery()
        plugins.withType(AsciidoctorPlugin) {
//шаг 3, а почему нам и asciidoctorj не вынести сюда?
          asciidoctorj {
            version = '1.5.4'
            groovyDslVersion = '1.0.0.Alpha1'
          }
          project.tasks.withType(AsciidoctorTask) { task ->
            doFirst {
              DependencyIncludeProcessor.project = project
//шаг 4, а почему не дать пользователям делать отладочный рендеринг?
              DependencyIncludeProcessor.debug = extension.debug
            }

            mustRunAfter extension.documentationDistTaskName
            inputs.file project.configurations.docs.incoming.collect { it.files }

            List<String> docsSources = fileTree(dir: project.projectDir, include: '**/*.adoc')
                .collect { relativePath(it) }

            sourceDir project.projectDir

            sources {
              setIncludes docsSources
            }

            outputDir = new File("$project.buildDir/docs")
            attributes(['source-highlighter': 'coderay',
                        doctype             : 'book',
                        toc                 : 'left',
                        idprefix            : '',
                        idseparator         : '-'])
          }
        }
      }
    }
  }
}
