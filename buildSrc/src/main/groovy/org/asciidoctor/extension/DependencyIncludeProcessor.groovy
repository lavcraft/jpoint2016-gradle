package org.asciidoctor.extension

import org.asciidoctor.ast.DocumentRuby
import org.gradle.api.Project

/**
 * @author tolkv
 * @since 12/04/16
 */
class DependencyIncludeProcessor extends IncludeProcessor {
  //TODO WTF .. we need to avoid static project set... However, maybe it is right way :)
  static Project project

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

    def dependencies = project.configurations.docs.collect {
      project.zipTree(it).collect {
        it.readLines()
      }
    }.flatten().join '\n\n'

    String s = """
    time:            ${System.currentTimeMillis()}
    project:         ${project?.name}
    document:        $document
    target:          $target
    attributes:      $attributes
    dependencies:

$dependencies
    """

    reader.push_include(s, target, target, 1, attributes)
  }
}