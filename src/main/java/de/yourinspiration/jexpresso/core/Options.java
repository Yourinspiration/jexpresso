package de.yourinspiration.jexpresso.core;

import java.util.HashMap;
import java.util.Map;

public class Options {

    private final Map<String, Object> map = new HashMap<>();

    public Options() {

    }

    public Options(String name, Object value) {
        map.put(name, value);
    }

    public Options add(String name, Object value) {
        map.put(name, value);
        return this;
    }

    public Map<String, Object> create() {
        return map;
    }

}
