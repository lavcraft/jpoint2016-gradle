package org.asciidoctor.extension

import org.asciidoctor.ast.DocumentRuby
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedArtifact

import java.time.ZonedDateTime

/**
 * @author tolkv
 * @since 12/04/16
 */
class DependencyIncludeProcessor extends IncludeProcessor {
  //TODO WTF .. we need to avoid static project set... However, maybe it is right way :)
  static Project project
  private Map<String, ResolvedArtifact> deps = new HashMap()

  DependencyIncludeProcessor() {
    super([:])
  }

  public DependencyIncludeProcessor(Map<String, Object> config) {
    super(config)

    project.configurations.docs.resolvedConfiguration.resolvedArtifacts.each {
//      println it.getModuleVersion().getId()
      deps.put(it.getModuleVersion().getId().toString(), it)
    }

  }

  @Override
  boolean handles(String target) {
    return target.startsWith('gradle://')
  }

  @Override
  void process(DocumentRuby document, PreprocessorReader reader, String target, Map<String, Object> attributes) {

    def dependency = target.substring(target.indexOf(':') + 3)
    def split = dependency.split('/')

    def dependencyName = split[0]
    def dependencyInsideFileName = split[1]

    String content
    if (dependencyName.startsWith(':')) {
      def artifact = project.configurations.docs.resolvedConfiguration.resolvedArtifacts.find {
        it.moduleVersion.id.name == dependencyName.replace(':', '')
      }

      if (!artifact) {
        project.logger.warn 'File {} has not found in dependency {}', dependencyInsideFileName, dependencyName
        return
      }

      File fileWithTargetContent = project.zipTree(artifact.file).find { File file ->
        file.name == dependencyInsideFileName
      } as File

      if (!fileWithTargetContent) {
        project.logger.warn 'File {} has not found in dependency {}', dependencyInsideFileName, dependencyName
        return
      } else {
        content = fileWithTargetContent.text
      }
    } else {
      content = project.configurations.docs.take(1).collect {
        project.zipTree(it).collect {
          it.readLines()
        }
      }.flatten().join '\n\n'
    }

    String s = """
    time:                     ${ZonedDateTime.now()}
    project:                  ${project?.name}
    document:                 $document
    dependencyName:           $dependencyName
    dependencyInsideFileName: $dependencyInsideFileName
    attributes:               $attributes
    dependencies:

$content
    """

    reader.push_include(s, target, target, 1, attributes)
  }
}