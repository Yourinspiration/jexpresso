package de.yourinspiration.jexpresso;

import java.util.Map;

/**
 * Maps a template engine to file extensions.
 * 
 * @author Marcel Härle
 *
 */
public interface TemplateEngine {

    /**
     * Renders the template file with the given data.
     * 
     * @param template
     *            the template file
     * @param options
     *            the data to be rendered
     * @return returns the rendered content
     */
    String render(String template, Map<String, Object> options);

}
