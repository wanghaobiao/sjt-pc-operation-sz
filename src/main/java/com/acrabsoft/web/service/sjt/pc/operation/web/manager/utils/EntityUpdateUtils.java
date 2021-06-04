package com.acrabsoft.web.service.sjt.pc.operation.web.manager.utils;

import org.springframework.util.ObjectUtils;

import java.lang.reflect.Field;

public class EntityUpdateUtils {

    public static void updateField(Object source, Object update) throws IllegalAccessException {
        Class c = source.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(update);

            if (!ObjectUtils.isEmpty(value)) {
                field.set(source, value);
            }
        }
    }
}
