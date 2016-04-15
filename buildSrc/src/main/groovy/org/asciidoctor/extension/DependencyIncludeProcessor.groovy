package org.asciidoctor.extension

import org.asciidoctor.ast.DocumentRuby
import org.gradle.api.Project

import java.time.ZonedDateTime

/**
 * @author tolkv
 * @since 12/04/16
 */
class DependencyIncludeProcessor extends IncludeProcessor {
  //TODO WTF .. we need to avoid static project set... However, maybe it is right way :)
  public static Project project

  DependencyIncludeProcessor() {
    super([:])
  }

  public DependencyIncludeProcessor(Map<String, Object> config) {
    super(config)
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

    def artifact = project.configurations.docs.resolvedConfiguration.resolvedArtifacts.find {
      def version = it.moduleVersion.id
      project.logger.debug """
      dependencyName              $dependencyName
      fileName                    $dependencyInsideFileName
      it.moduleVersion.id.name    ${version.name}
      it.moduleVersion.id.group   ${version.group}
      it.moduleVersion.id.version ${version.version}
      """.stripMargin().stripIndent()

      if (dependencyName.startsWith(':'))
        return version.name == dependencyName.replace(':', '')
      else {
        return dependencyName == version.toString()
      }
    }

    if (!artifact) {
      project.logger.warn 'Dependency {} not found', dependencyName
      return
    }
    if (!artifact.file.exists()) {
      project.logger.warn 'Dependency represented in file {} not found', artifact.file?.absolutePath
      return
    }

    File fileWithTargetContent = project.zipTree(artifact.file).find {
      it.name == dependencyInsideFileName
    } as File

    if (!fileWithTargetContent) {
      project.logger.warn 'File {} has not found in dependency {}', dependencyInsideFileName, dependencyName
      return
    } else {
      content = fileWithTargetContent.text
    }

    String metadataWithContent = """
    time:                     ${ZonedDateTime.now()}
    project:                  ${project?.name}
    document:                 $document
    dependencyName:           $dependencyName
    dependencyInsideFileName: $dependencyInsideFileName
    attributes:               $attributes
    dependencies:

$content
    """
    reader.push_include(metadataWithContent, target, target, 1, attributes)
  }
}