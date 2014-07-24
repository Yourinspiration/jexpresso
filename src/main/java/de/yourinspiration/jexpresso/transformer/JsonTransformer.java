package de.yourinspiration.jexpresso.transformer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.yourinspiration.jexpresso.http.ContentType;

/**
 * Transforms the model data to the JSON data format.
 * 
 * @author Marcel Härle
 *
 */
public class JsonTransformer implements ResponseTransformer {

    public static final String NAME = "JsonTransformer";

    private final Gson gson = new GsonBuilder().create();

    @Override
    public String render(final Object model) {
        return gson.toJson(model);
    }

    @Override
    public String toString() {
        return NAME;
    }

    @Override
    public ContentType contentType() {
        return ContentType.APPLICATION_JSON;
    }

}
