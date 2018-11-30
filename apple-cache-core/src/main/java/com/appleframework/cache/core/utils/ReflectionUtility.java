package com.appleframework.cache.core.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

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
	
	public static Field[] getFields(Class<?> clazz) {
		return clazz.getDeclaredFields();
	}

	/**
	 * 获取所有属性集合(包含父类)
	 * @param clazz
	 * @return
	 */
	public static Field[] getAllField(Class<?> clazz) {
		List<Field> allFields = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();
		allFields.addAll(Arrays.asList(fields));
		for (Class<?> superClazz = clazz.getSuperclass(); superClazz != Object.class; superClazz = superClazz.getSuperclass()) {
			Field[] superFields = superClazz.getDeclaredFields();
			allFields.addAll(Arrays.asList(superFields));
		}
		return allFields.toArray(new Field[allFields.size()]);
	}

	/**
	 * 获取所有方法集合(包含父类)
	 * @param clazz
	 * @return
	 */
	public static Method[] getAllMethod(Class<?> clazz) {
		List<Method> allMethods = new ArrayList<>();
		Method[] fields = clazz.getDeclaredMethods();
		allMethods.addAll(Arrays.asList(fields));
		for (Class<?> superClazz = clazz.getSuperclass(); superClazz != Object.class; superClazz = superClazz.getSuperclass()) {
			Method[] superFields = superClazz.getDeclaredMethods();
			allMethods.addAll(Arrays.asList(superFields));
		}
		return allMethods.toArray(new Method[allMethods.size()]);
	}

	/**
	 * 获取属性的值
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Object getFieldValue2(Object obj, String fieldName) {
		try {
			if (obj instanceof Collection<?>) {
				return getCollectionFieldValue((Collection<?>) obj, fieldName);
			}
			if (obj instanceof Integer) {
				return obj;
			}
			if (obj instanceof Long) {
				return obj;
			}
			if (obj instanceof String) {
				return obj;
			}
			if (obj instanceof Boolean) {
				return obj;
			}
			Object value = PropertyUtils.getProperty(obj, fieldName);
			if (value == null) {
				return null;
			}
			if ((value instanceof String) && StringUtils.isBlank(value.toString())) {
				return null;
			}
			if (value instanceof Date) {
				return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
			}
			return value;
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		return null;
	}

	private static Object getCollectionFieldValue(Collection<?> collection, String fieldName) {
		if (CollectionUtils.isEmpty(collection)) {
			return null;
		}
		Object[] arrays = collection.toArray();
		int index = fieldName.indexOf(".");
		if (index != -1) {
			String indexObj = fieldName.substring(0, index);
			String indexField = fieldName.substring(index + 1);
			if ("last".equals(indexObj)) {
				return getFieldValue2(arrays[arrays.length - 1], indexField);
			}
			return getFieldValue2(arrays[Integer.parseInt(indexObj)], indexField);
		}
		return getFieldValue2(arrays[0], fieldName);
	}

	/** 
	 * 根据目标方法和注解类型  得到该目标方法的指定注解 
	 */
	public static Annotation getAnnotationByMethod(Method method, Class<?> annoClass) {
		Annotation[] annotations = method.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.annotationType() == annoClass) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * @param clazz
	 * @param methodName
	 * @return
	 */
	public static Method getMethodByClassAndName(Class<?> clazz, String methodName) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		return null;
	}

}
