package de.yourinspiration.jexpresso.template;

import java.util.Map;

public interface TemplateEngine {

    String render(String template, Map<String, Object> options);

}
