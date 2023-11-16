/*
 * $Id: BeanProperty.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.prop;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A property access for properties originating from a Java Bean component.
 */
public class BeanProperty implements Property {

    private final String name;
    private final Class type;
    private final Method getter;
    private final Method setter;

    public BeanProperty(String name, Method getter, Method setter) {
        if (getter == null) {
            throw new IllegalArgumentException("Parameter 'getter' must not be null for name = '\"+ name +\"'");
        }
        if (getter.getReturnType() == Void.TYPE) {
            throw new IllegalArgumentException("no getter method with returntype 'void' allowed here (name = '\"+ name +\"')");
        }
        if (getter.getParameterTypes().length != 0) {
            throw new IllegalArgumentException("no getter method parameters allowed here (name = '\"+ name +\"')");
        }
        if (getter == null) {
            throw new IllegalArgumentException("Parameter 'getter' must not be null for name = '\"+ name +\"'");
        }
        this.name = name;
        this.type = getter.getReturnType(); 
        this.getter = getter;
        this.setter = setter;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public Method getGetter() {
        return getter;
    }

    public Method getSetter() {
        return setter;
    }

    /**
     * Invokes the getter method on the given Java Bean instance with the the given property value.
     *
     * @param beanInstance the Java Bean instance
     * @return the property value
     */
    public Object getValue(Object beanInstance) {
        try {
            return getter.invoke(beanInstance, (Object[]) null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the setter method on the given Java Bean instance with the the given property value.
     *
     * @param beanInstance the Java Bean instance
     * @param value        the property value
     */
    public void setValue(Object beanInstance, Object value) {
        if (setter == null) {
            throw new IllegalStateException("property is not assignable. No setter method available for property '"+this.name+"'");
        }
        try {
            setter.invoke(beanInstance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAssignable(Object beanInstance) {
        return setter != null;
    }

    public void makeAssignable(Object beanInstance) {
        // ok, is already assignable
    }

    public String getTreeAsString() {
        return "BeanProperty['" + name + "'," + type.getName() + "]";
    }
}
