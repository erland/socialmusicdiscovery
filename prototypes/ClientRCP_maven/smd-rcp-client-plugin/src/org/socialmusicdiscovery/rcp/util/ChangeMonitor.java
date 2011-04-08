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

import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.socialmusicdiscovery.rcp.error.FatalApplicationException;
import org.socialmusicdiscovery.rcp.event.Observable;

/**
 * A utility for monitoring property changes using nested property names.
 * Whenever the specified property or any of the intermediate elements change,
 * the listener is notified.
 * 
 * @author Peer Törngren
 * 
 */
public class ChangeMonitor {

	/* package */ static class PropertyData {

		private Class<?> beanType;
		public boolean isCollection;
		public String propertyName;
		public Class<?> propertyType;
		public Class<?> elementType;

		public PropertyData(Class<?> type) {
			this.beanType = type;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName()+": "
				+"\n\tclass: "+beanType
				+"\n\tproperty name: "+propertyName
				+"\n\tproperty type: "+propertyType
				+"\n\telement type: "+elementType
				+"\n\tisCollection: "+isCollection;
		}

	}

	public static void observe(final Runnable listener, Observable observable, String... propertyNames) {
		List<PropertyData> data = getPropertyData(observable.getClass(), propertyNames);
		hook(observable, data, listener);
	}
	
	public static List<PropertyData> getPropertyData(Class type, String... propertyNames) {
		List<PropertyData> result = new ArrayList<PropertyData>();
		Class currentType = type; 
		for (String name : propertyNames) {
			PropertyDescriptor descriptor = getDescriptor(currentType, name);
			boolean isCollection = isCollectionType(descriptor.getPropertyType());
			
			PropertyData data = new PropertyData(currentType);
			data.isCollection = isCollection;
			data.propertyName = descriptor.getName();
			data.propertyType = descriptor.getPropertyType();
			if (isCollection) {
				data.elementType = resolveElementType(currentType, descriptor);
				currentType = data.elementType;
			} else {
				currentType = data.propertyType;
			}
			result.add(data);
		}
		return result;
	}

	private static Class resolveElementType(Class<?> type, PropertyDescriptor descriptor) {
		try {
			String readMethodName = descriptor.getReadMethod().getName();
			Type returnType = type.getMethod(readMethodName).getGenericReturnType();

			if(returnType instanceof ParameterizedType){
			    ParameterizedType parameterizedReturnType = (ParameterizedType) returnType;
			    Type[] typeArguments = parameterizedReturnType.getActualTypeArguments();
			    if (typeArguments.length!=1) {
			    	throw new IllegalArgumentException("Expect exactly one generic type: "+Arrays.asList(typeArguments));
			    }
			    for(Type typeArgument : typeArguments){
			        return (Class) typeArgument;
			    }
			}
			throw new IllegalArgumentException("Expect exactly one generic type, found none: "+returnType);
		} catch (SecurityException e) {
			throw new FatalApplicationException("Unable to determine element type of collection property: "+descriptor, e);  //$NON-NLS-1$
		} catch (NoSuchMethodException e) {
			throw new FatalApplicationException("Unable to determine element type of collection property: "+descriptor, e);  //$NON-NLS-1$
		}
	}

	private static boolean isCollectionType(Class currentType) {
		return Collection.class.isAssignableFrom(currentType);
	}

	private static PropertyDescriptor getDescriptor(Class beanClass, String propertyName) {
		// Silly, but beanutils only seem to offer methods to find distinct descriptor on instance, not class?
		for (PropertyDescriptor d : PropertyUtils.getPropertyDescriptors(beanClass)) {
			if (d.getName().equals(propertyName)) {
				return d;
			}
		}
		throw new IllegalArgumentException("Property with name "+propertyName+" not found in class "+beanClass);
	}

	static void hook(Observable observable, List<PropertyData> data, Runnable listener) {
			new LinkedListener(observable, data, listener);
	}


}
