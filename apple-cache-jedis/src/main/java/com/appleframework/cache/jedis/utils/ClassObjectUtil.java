package com.appleframework.cache.jedis.utils;

import java.util.Date;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import com.appleframework.cache.jedis.utils.DateUtil;

public class ClassObjectUtil {

	public static Object getObject(Class<?> typeClz, String value) throws RuntimeException {
		if (value != null && StringUtils.isNotBlank(value)) {
			try {
				if (typeClz == String.class) {
					return value;
				} else if (typeClz == Date.class) {
					return DateUtil.formatToDate(value);
				} else if (typeClz == Integer.class || typeClz == int.class) {
					return Integer.valueOf(value);
				} else if (typeClz == Double.class || typeClz == double.class) {
					return Double.valueOf(value);
				} else if (typeClz == Long.class || typeClz == long.class) {
					return Long.valueOf(value);
				} else if (typeClz == Float.class || typeClz == float.class) {
					return Float.valueOf(value);
				} else if (typeClz == Short.class || typeClz == short.class) {
					return Short.valueOf(value);
				} else if (typeClz == Boolean.class || typeClz == boolean.class) {
					if("1".equals(value)) {
						return Boolean.TRUE;
					}
					else if("0".equals(value)) {
						return Boolean.FALSE;
					}
					else {
						return Boolean.valueOf(value);
					}
				} else if (typeClz == byte[].class) {
					return Hex.decodeHex(value.toCharArray());
				} else {
					return value;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return null;
		}

	}
}
