package org.asciidoctor.extension

import org.asciidoctor.ast.DocumentRuby

/**
 * @author tolkv
 * @since 12/04/16
 */
class DependencyIncludeProcessor extends IncludeProcessor {

  public DependencyIncludeProcessor(Map<String, Object> config) {
    super(config)
  }

  @Override
  boolean handles(String target) {
    return target.startsWith('gradle://')
  }

  @Override
  void process(DocumentRuby document, PreprocessorReader reader, String target, Map<String, Object> attributes) {
    String s = 'hello ' + System.currentTimeMillis()
    reader.push_include(s, target, target, 1, attributes)
  }
}