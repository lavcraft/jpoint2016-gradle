package org.asciidoctor.extension

import groovy.util.logging.Slf4j
import org.asciidoctor.ast.DocumentRuby
/**
 * @author tolkv
 * @since 12/04/16
 */
@Slf4j
class DependencyIncludeProcessor extends IncludeProcessor {

  @Override
  boolean handles(String target) {
    log.info 'target : {}', target
    return target.startsWith('gradle')
  }

  @Override
  void process(DocumentRuby document, PreprocessorReader reader, String target, Map<String, Object> attributes) {
    reader.push_include('hello ' + System.currentTimeMillis(), target, target, 1, attributes)
  }
}