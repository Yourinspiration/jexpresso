package de.yourinspiration.jexpresso.transformer;

import de.yourinspiration.jexpresso.http.ContentType;

/**
 * Transforms the data created by a RouteHandler to a specific content type.
 * 
 * @author Marcel Härle
 *
 */
public interface ResponseTransformer {

    /**
     * Transform the model to the specific content type.
     * 
     * @param model
     *            the model to be transformed
     * @return the transformed data
     */
    String render(Object model);

    /**
     * Get the generated content type,
     * 
     * @return the content type
     */
    ContentType contentType();

}
