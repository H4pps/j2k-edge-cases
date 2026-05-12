package org.example.edgecases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exercises raw collections and unchecked casts typically found in legacy Java APIs.
 */
public final class RawTypesUncheckedCastsCase {

    /**
     * Reads a typed string list from a raw map key.
     *
     * @param rawMap raw input map
     * @param key map key
     * @return copied typed list
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<String> readStringList(Map rawMap, String key) {
        Object value = rawMap.get(key);
        if (value == null) {
            return List.of();
        }

        List unchecked = (List) value;
        List<String> typed = new ArrayList<>();
        for (Object item : unchecked) {
            typed.add(String.valueOf(item));
        }
        return typed;
    }
}
