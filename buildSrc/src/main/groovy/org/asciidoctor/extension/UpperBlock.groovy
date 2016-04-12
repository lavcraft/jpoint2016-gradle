package org.asciidoctor.extension

import org.asciidoctor.ast.AbstractBlock

/**
 * @author tolkv
 * @since 12/04/16
 */
class UpperBlock extends BlockProcessor {

  UpperBlock(String name, Map<String, Object> config) {
    super(name, [contexts: [':paragraph'], content_model: ':simple'])
  }

  def process(AbstractBlock parent, Reader reader, Map<String, Object> attributes) {
    def upperLines = reader.readLines()
        .collect {it.toUpperCase()}
        .inject("") {a, b -> a + '\n' + b}

    createBlock(parent, "paragraph", [upperLines], attributes, [:])
  }
}
