package org.asciidoctor.extension

import groovy.util.logging.Slf4j
import org.asciidoctor.Asciidoctor
import org.asciidoctor.extension.spi.ExtensionRegistry
/**
 * @author tolkv
 * @since 12/04/16
 */
@Slf4j
class DependencyIncludeRegistry implements ExtensionRegistry {
  @Override
  void register(Asciidoctor asciidoctor) {
    log.info 'registering dependency include processor..'
    final JavaExtensionRegistry registry = asciidoctor.javaExtensionRegistry()
    registry.block 'BIG', UpperBlock.class
    registry.includeProcessor(DependencyIncludeProcessor.class)
  }
}
