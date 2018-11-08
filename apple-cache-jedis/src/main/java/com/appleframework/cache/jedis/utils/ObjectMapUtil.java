package com.appleframework.cache.jedis.utils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectMapUtil {

	private static final Logger logger = LoggerFactory.getLogger(ObjectMapUtil.class);

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
			try {
				if (value == null || "".equals(value))
					continue;
				Class<?> typeClz = field.getType();
				if (value != null) {
					if (typeClz == String.class) {
						// field.set(object, value);
						ReflectionUtility.invokeSetterMethod(object, name, value);
					} else if (typeClz == Date.class) {
						// field.set(object, new Date(Long.valueOf(value)));
						ReflectionUtility.invokeSetterMethod(object, name, DateUtil.formatToDate(value));
					} else if (typeClz == Integer.class || typeClz == int.class) {
						// field.set(object, Integer.valueOf(value));
						ReflectionUtility.invokeSetterMethod(object, name, Integer.valueOf(value));
					} else if (typeClz == Double.class || typeClz == double.class) {
						// field.set(object, Double.valueOf(value));
						ReflectionUtility.invokeSetterMethod(object, name, Double.valueOf(value));
					} else if (typeClz == Long.class || typeClz == long.class) {
						// field.set(object, Long.valueOf(value));
						ReflectionUtility.invokeSetterMethod(object, name, Long.valueOf(value));
					} else if (typeClz == Float.class || typeClz == float.class) {
						// field.set(object, Float.valueOf(value));
						ReflectionUtility.invokeSetterMethod(object, name, Float.valueOf(value));
					} else if (typeClz == byte[].class) {
						// field.set(object, Hex.decodeHex(value.toCharArray()));
						ReflectionUtility.invokeSetterMethod(object, name, Hex.decodeHex(value.toCharArray()));
					} else {
						field.set(object, value);
						ReflectionUtility.invokeSetterMethod(object, name, value);
					}
				}
			} catch (Exception e) {
				logger.error("name={},value={},hexval={}", name, value, Hex.encodeHexString(value.getBytes()));
				logger.error(e.getMessage(), e);
			}
		}
		return object;
	}
}
