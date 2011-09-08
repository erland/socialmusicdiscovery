/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.rcp.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.ParameterDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.beanutils.BeanUtils;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.server.support.copy.CopyHelper;

import com.google.gson.annotations.Expose;

/**
 * Some utilities for handling classes and properties. Similar to
 * {@link BeanUtils}, but adding some utils that do not depend on 
 * strict conformance to bean standards.
 * 
 * @author Peer Törngren
 * 
 */
public final class ClassUtil {

	/**
	 * Convenience method. Get all persistent fields, including private fields.
	 * 
	 * @param type
	 * @return {@link Collection}
	 */
	public static Collection<Field> getAllPersistentFields(Class type) {
		return getAllFields(type, Expose.class);
	}

	/**
	 * Get all fields, including private fields. If an annotation class is
	 * supplied, only include fields annotated with this class. This code is
	 * copy/pasted from {@link CopyHelper}.
	 * 
	 * @param type
	 * @param onlyAnnotatedWith
	 * @return {@link Collection}
	 */
	public static Collection<Field> getAllFields(Class type, Class<? extends Annotation> onlyAnnotatedWith) {
		ArrayList<Field> fields = new ArrayList<Field>();
		while (!type.equals(Object.class)) {
			if (onlyAnnotatedWith != null) {
				for (Field field : type.getDeclaredFields()) {
					if (field.isAnnotationPresent(onlyAnnotatedWith)) {
						fields.add(field);
					}
				}
			} else {
				fields.addAll(Arrays.asList(type.getDeclaredFields()));
			}
			type = type.getSuperclass();
		}
		return fields;
	}

	/**
	 * Copy all exposed fields from source to target by calling public getter
	 * and setter methods. The fields to copy are resolved by inspecting the
	 * target class.
	 * 
	 * @param source
	 * @param target
	 */
	public static void copyPersistentProperties(Object source, Object target) {
		for (Field f : getAllPersistentFields(target.getClass())) {
			String propertyName = f.getName();
			try {
				copyProperties(source, target, propertyName);
			} catch (Exception e) {
				throw new FatalApplicationException("Cannot copy property " + propertyName + " from " + source + " to " + target, e);
			}
		}
	}

	/**
	 * <p>
	 * Do a shallow copy of properties with specified names between two objects.
	 * Use public getters and setters; concerned classes must follow bean
	 * standards for getter and setter method names, but does not have to
	 * implement a matching field. Note that collections will be copied "as is";
	 * if the target needs to maintain a stable collection, the setter method
	 * must ensure that the target collection is updated rather than replaced.
	 * </p>
	 * 
	 * <p>
	 * In contrast to {@link BeanUtils#copyProperty(Object, String, Object)},
	 * this method can copy derived properties (where no field is declared on
	 * class) and handle getters and setters that have different return/argument
	 * types. This method does virtually no type checking - as long as the name
	 * pattern matches and the size of the argument list matches (0 for getters,
	 * 1 for setters), we're fine. Problems will be detected in runtime.
	 * </p>
	 * 
	 * @param source
	 * @param target
	 * @param propertyNames
	 * @see BeanUtils
	 * @see CopyHelper
	 */
	public static void copyProperties(Object source, Object target, String... propertyNames) {
		for (String propertyName : propertyNames) {
			try {
				copyProperty(source, target, propertyName);
			} catch (Exception e) {
				throw new FatalApplicationException("Failed to copy property " + propertyName + " from " + source + " to " + target, e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}

	private static void copyProperty(Object source, Object target, String propertyName) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		// TODO cache results?
		String capitalizedPropertyName = TextUtil.toInitialUppercase(propertyName);
		Method getter = findGetter(source, capitalizedPropertyName);
		Method setter = findSetter(target, capitalizedPropertyName);
		setter.invoke(target, getter.invoke(source));
	}

	private static Method findSetter(Object object, String capitalizedPropertyName) throws IntrospectionException {
		BeanInfo bi = Introspector.getBeanInfo(object.getClass(), Object.class);
		for (MethodDescriptor md : bi.getMethodDescriptors()) {
			Method m = md.getMethod();
			boolean isOneParameter = m.getParameterTypes().length == 1;
			if (isOneParameter && md.getName().equals("set" + capitalizedPropertyName)) {
				return m;
			}
		}
		throw new IllegalArgumentException("No setter method ends with " + capitalizedPropertyName);
	}

	private static Method findGetter(Object object, String capitalizedPropertyName) throws IntrospectionException {
		BeanInfo bi = Introspector.getBeanInfo(object.getClass(), Object.class);
		for (MethodDescriptor md : bi.getMethodDescriptors()) {
			ParameterDescriptor[] parameterDescriptors = md.getParameterDescriptors();
			if (parameterDescriptors==null || parameterDescriptors.length==0) {
				Method m = md.getMethod();
				if (isGetter(capitalizedPropertyName, m)) {
					return m;
				}
			}
		}
		throw new IllegalArgumentException("No getter method ends with " + capitalizedPropertyName);
	}

	private static boolean isGetter(String propertyName, Method m) {
		String methodName = m.getName();
		Class returnType = m.getReturnType();
		return isBooleanGetter(propertyName, methodName, returnType) || isNonBooleanGetter(propertyName, methodName, returnType);
	}

	private static boolean isNonBooleanGetter(String propertyName, String methodName, Class returnType) {
		return returnType!=null && methodName.equals("get" + propertyName);
	}

	private static boolean isBooleanGetter(String propertyName, String methodName, Class returnType) {
		return (boolean.class.equals(returnType) || Boolean.class.equals(returnType)) && methodName.equals("is" + propertyName);
	}
}
