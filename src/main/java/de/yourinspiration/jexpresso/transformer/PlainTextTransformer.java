package de.yourinspiration.jexpresso.transformer;

import de.yourinspiration.jexpresso.http.ContentType;

/**
 * Transforms the model data to plain text without any type specific
 * transformation.
 * 
 * @author Marcel Härle
 *
 */
public class PlainTextTransformer implements ResponseTransformer {

    @Override
    public String render(Object model) {
        return model != null ? model.toString() : "";
    }

    @Override
    public ContentType contentType() {
        return ContentType.TEXT_PLAIN;
    }

}
