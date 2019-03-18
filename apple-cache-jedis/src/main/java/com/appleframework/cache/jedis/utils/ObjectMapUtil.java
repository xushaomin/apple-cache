package com.appleframework.cache.jedis.utils;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.appleframework.cache.core.utils.ReflectionUtility;

public class ObjectMapUtil {

	public static <T> T getObjectFromMap(final Class<T> clz, Map<String, String> map) {
		T object = null;
		try {
			object = clz.newInstance();
		} catch (Exception e1) {
		}
		Class<?> tempClass = clz;
		Field[] fields = ReflectionUtil.getAllField(tempClass);
		for (Field field : fields) {
			String name = field.getName();
			String value = map.get(name);
			if (value == null || "".equals(value) || "serialVersionUID".equals(value)) {
				continue;
			}
			Class<?> typeClz = field.getType();
			if (!StringUtils.isBlank(value)) {
				Object valueObject = ClassObjectUtil.getObject(typeClz, value);
				ReflectionUtility.invokeSetterMethod(object, name, valueObject);
			}
		}
		return object;
	}
}
