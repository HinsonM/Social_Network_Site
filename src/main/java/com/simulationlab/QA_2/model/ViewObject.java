package com.simulationlab.QA_2.model;

import java.util.HashMap;
import java.util.Map;


// com.fasterxml.jackson.databind.exc.InvalidDefinitionException: No serializer found for class com.simulationlab.QA_2.model.ViewObject and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: java.util.ArrayList[0])


public class ViewObject {
    private Map<String, Object> objs = new HashMap<>();

    public Object get(String key) {
        return objs.get(key);
    }

    public void set(String key, Object val) {
        objs.put(key, val);
    }

    public Map<String, Object> getObjs() {
        return objs;
    }
}


//public class ViewObject {
//    private Map<String, Object> objs = new HashMap<>();
//    public void set(String key, Object value) {
//        objs.put(key, value);
//    }
//
//    public Object get(String key) {
//        return objs.get(key);
//    }
//
//    public Map<String, Object> getObjs() {
//        return objs;
//    }
//}
