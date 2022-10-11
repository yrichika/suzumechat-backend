package com.example.suzumechat.utility;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class JsonHelper {

    /**
     * This method just checks all fields name exists in the given json. It's NOT
     * perfect to check whether it's a certain class, but sufficient for now.
     */
    public <T> boolean hasAllFieldsOf(final String json, final Class<T> jsonType) {
        final Field[] allFields = jsonType.getDeclaredFields();
        final List<Boolean> fieldNamesExist = Arrays.stream(allFields).map(field -> {
            Pattern pattern = Pattern.compile(fieldNamePattern(field.getName()));
            return pattern.matcher(json).find();
        }).collect(Collectors.toList());
        return !fieldNamesExist.contains(false);

    }

    protected String fieldNamePattern(final String fieldName) {
        return "\"" + fieldName + "\"\s*:\s*.+";
    }
}
