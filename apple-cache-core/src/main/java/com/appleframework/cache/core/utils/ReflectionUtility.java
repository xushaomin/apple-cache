package com.appleframework.cache.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;

/**
 * @author Cruise.Xu
 */
public final class ReflectionUtility {
   /**
    * Return actual parameter type for generic subclass or interface.
    * <p/>
    * Note:
    * <p/>
    * 1)this method assume that given class is parameterized subclass(interface) and parent class is
    * not {@link Object} so may cause {@link NullPointerException} at runtime.
    * <p/>
    * 2)this method assume that genericSuperClass is ParameterizedType and even does not validate it
    * so may produce {@link ClassCastException} at runtime.
    * <p/>
    * 3)this method assume that given index is valid so may produce
    * {@link IndexOutOfBoundsException} at runtime.
    * 
    * @param clazz
    *           target class
    * @param index
    *           position of type.
    * @return actual parameterized type.
    */
   public static Class<?> getActualGenericType(final Class<?> clazz, final int index) {
      if (clazz.isInterface()) {
         for (final Type type : clazz.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
               return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[index];
            }
         }
      }
      return (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[index];
   }

   /**
    * Build method signature and return human readable simplified signature.
    * <p/>
    * For example method
    * <code>void foo(java.land.String s, com.katesoft.jvmcluster.persistent.User)</code> will have
    * signature like this <code>foo(String, User)</code>.
    * 
    * @param m
    *           target method.
    * @return simplified signature of the given method.
    */
   public static String buildMethodSignature(final Method m) {
      final StringBuilder args = new StringBuilder();
      for (@SuppressWarnings("rawtypes")
      final Class i : m.getParameterTypes()) {
         if (args.length() > 0) {
            args.append(",");
         }
         args.append(i != null ? i.getSimpleName() : null);
      }
      return m.getName() + "(" + args.toString() + ")";
   }
   
   /**
	 * 调用Getter方法
	 * 
	 * @param object
	 *            对象
	 *            
	 * @param propertyName
	 *            属性名称
	 */
	public static Object invokeGetterMethod(Object object, String propertyName) {
		String getterMethodName = "get" + StringUtils.capitalize(propertyName);
		try {
			Method getterMethod = object.getClass().getMethod(getterMethodName);
			return getterMethod.invoke(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 调用Setter方法
	 * 
	 * @param object
	 *            对象
	 *            
	 * @param propertyName
	 *            属性名称
	 *            
	 * @param propertyValue
	 *            属性值
	 */
	public static void invokeSetterMethod(Object object, String propertyName, Object propertyValue) {
		Class<?> setterMethodClass = propertyValue.getClass();
		invokeSetterMethod(object, propertyName, propertyValue, setterMethodClass);
	}
	
	/**
	 * 调用Setter方法
	 * 
	 * @param object
	 *            对象
	 *            
	 * @param propertyName
	 *            属性名称
	 *            
	 * @param propertyValue
	 *            属性值
	 *            
	 * @param setterMethodClass
	 *            参数类型
	 */
	public static void invokeSetterMethod(Object object, String propertyName, Object propertyValue, Class<?> setterMethodClass) {
		String setterMethodName = "set" + StringUtils.capitalize(propertyName);
		try {
			Method setterMethod = object.getClass().getMethod(setterMethodName, setterMethodClass);
			setterMethod.invoke(object, propertyValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取对象属性值,无视private/protected/getter
	 * 
	 * @param object
	 *            对象
	 *            
	 * @param fieldName
	 *            属性名称
	 */
	public static Object getFieldValue(Object object, String fieldName) {
		Field field = getAccessibleField(object, fieldName);
		if (field == null) {
			throw new IllegalArgumentException("Could not find field " + fieldName);
		}
		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			
		}
		return result;
	}

	/**
	 * 设置对象属性值,无视private/protected/setter
	 * 
	 * @param object
	 *            对象
	 *            
	 * @param fieldName
	 *            属性名称
	 */
	public static void setFieldValue(Object object, String fieldName, Object value) {
		Field field = getAccessibleField(object, fieldName);
		if (field == null) {
			throw new IllegalArgumentException("Could not find field " + fieldName);
		}
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			
		}
	}

	private static Field getAccessibleField(final Object object, final String fieldName) {
		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException e) {
				
			}
		}
		return null;
	}

}
