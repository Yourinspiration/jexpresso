package de.yourinspiration.jexpresso.transformer;

import de.yourinspiration.jexpresso.http.ContentType;

/**
 * Transformer for text as HTML content type.
 *
 * @author Marcel Härle
 */
public class HtmlTransformer implements ResponseTransformer {

    @Override
    public String render(Object model) {
        return model != null ? model.toString() : "";
    }

    @Override
    public ContentType contentType() {
        return ContentType.TEXT_HTML;
    }

}
