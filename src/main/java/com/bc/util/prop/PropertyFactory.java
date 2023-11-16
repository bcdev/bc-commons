/*
 * $Id: PropertyFactory.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (C) 2002 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.bc.util.prop;


import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.Map;

public class PropertyFactory {

    public static Property createProperty(String fullName) {
        return createProperty(Map.class, fullName);
    }

    public static Property createProperty(Class parentType, String fullName) {
        try {
            return PropertyParser.parseProperty(parentType, fullName);
        } catch (PropertyNotFoundException e) {
            throw new RuntimeException("failed to create property '" + fullName + "': " +
                                       e.getMessage(),
                                       e);
        } catch (ParseException e) {
            throw new RuntimeException("failed to parse property '" + fullName +
                                       "': position " + e.getErrorOffset() + ": " +
                                       e.getMessage(),
                                       e);
        }
    }

    public static Property createChildProperty(final Class parentType, String name) {
        if (Property.UNKNOWN_TYPE.equals(parentType)) {
            return new GenericProperty(name);
        }
        final BeanProperty beanProperty = createBeanProperty(parentType, name);
        if (beanProperty != null) {
            return beanProperty;
        }
        if (Map.class.isAssignableFrom(parentType)) {
            return new MapProperty(name);
        }
        return null;
    }

    public static Property createNestedProperty(Property parent, String name) {
        final Property child = createChildProperty(parent.getType(), name);
        if (child != null) {
            return new NestedProperty(parent, child);
        } else {
            return null;
        }
    }

    /**
     * Creates a property access object for the given Java Bean class. The method returns null if a method with one of
     * the signatures <blockquote> <code><i>Type</i> get<i>PropertyName</i>()</code> </blockquote> and <blockquote>
     * <code><i>Type</i> is<i>PropertyName</i>()</code> </blockquote> could not be found in the supplied class. A
     * corresponding setter method with the signature <blockquote> <code>void set<i>PropertyName</i>(<i>Type</i>
     * value)</code> </blockquote> is optional.
     *
     * @param beanClass the Java Bean class
     * @param name      the property name
     *
     * @return the property access object
     *
     * @throws NullPointerException if one of the arguments is null
     */
    public static BeanProperty createBeanProperty(Class beanClass, String name) {
        if (name.length() == 0) {
            throw new IllegalArgumentException("propertyName is empty");
        }
        final String nameLC;
        final String nameUC;
        if (Character.isLowerCase(name.charAt(0))) {
            nameLC = name;
            nameUC = name.substring(0, 1).toUpperCase() + name.substring(1);
        } else {
            nameLC = name.substring(0, 1).toLowerCase() + name.substring(1);
            nameUC = name;
        }
        Method getter = BeanMethodRegistry.getGettersInstance().get(beanClass, nameUC);
        if (getter == null) {
            try {
                getter = beanClass.getMethod("get" + nameUC, (Class[]) null);
                BeanMethodRegistry.getGettersInstance().put(beanClass, nameUC, getter);
            } catch (NoSuchMethodException e) {
            }
        }
        if (getter == null) {
            try {
                getter = beanClass.getMethod("is" + nameUC, (Class[]) null);
                BeanMethodRegistry.getGettersInstance().put(beanClass, nameUC, getter);
            } catch (NoSuchMethodException e2) {
            }
        }
        if (getter == null) {
            return null;
        }

        Method setter = BeanMethodRegistry.getSettersInstance().get(beanClass, nameUC);
        if (setter == null) {
            try {
                setter = beanClass.getMethod("set" + nameUC, getter.getReturnType());
                BeanMethodRegistry.getSettersInstance().put(beanClass, nameUC, setter);
            } catch (NoSuchMethodException e) {
            }
        }

        return new BeanProperty(nameLC, getter, setter);
    }

    private PropertyFactory() {
    }
}
