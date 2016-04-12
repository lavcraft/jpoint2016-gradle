package org.asciidoctor.extension

import org.asciidoctor.Asciidoctor
import org.asciidoctor.extension.spi.ExtensionRegistry

/**
 * @author tolkv
 * @since 12/04/16
 */
class DependencyIncludeRegistry implements ExtensionRegistry {
  @Override
  void register(Asciidoctor asciidoctor) {
    final JavaExtensionRegistry registry = asciidoctor.javaExtensionRegistry()
    registry.block 'BIG', UpperBlock.class
    registry.includeProcessor(DependencyIncludeProcessor.class)
  }
}
