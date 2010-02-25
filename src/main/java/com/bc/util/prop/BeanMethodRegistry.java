/*
 * Created at 16.04.2004 20:22:28
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.prop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanMethodRegistry {

    private static final BeanMethodRegistry settersInstance = new BeanMethodRegistry();
    private static final BeanMethodRegistry gettersInstance = new BeanMethodRegistry();
    private Map typeMap;

    private BeanMethodRegistry() {
        typeMap = new HashMap();
    }

    public static BeanMethodRegistry getSettersInstance() {
        return settersInstance;
    }

    public static BeanMethodRegistry getGettersInstance() {
        return gettersInstance;
    }

    public synchronized Method get(Class beanClass, String propertyName) {
        checkArgs(beanClass, propertyName);
        final Map map = (Map) typeMap.get(beanClass);
        if (map == null) {
            return null;
        }
        return (Method) map.get(propertyName);
    }

    public synchronized void put(Class beanClass, String propertyName, Method method) {
        checkArgs(beanClass, propertyName);
        if (get(beanClass, propertyName) != null) {
            throw new IllegalArgumentException("method is already registered");
        }
        Map map = (Map) typeMap.get(beanClass);
        if (map == null) {
            map = new HashMap();
            typeMap.put(beanClass, map);
        }
        map.put(propertyName, method);

//        System.out.println("BeanMethodRegistry.put:");
//        System.out.println("  beanClass = " + beanClass.getName());
//        System.out.println("  propertyName = " + propertyName);
//        System.out.println("  method = " + method.getName());
    }

    public synchronized Method remove(Class beanClass, String propertyName) {
        checkArgs(beanClass, propertyName);
        Map map = (Map) typeMap.get(beanClass);
        if (map != null) {
            return (Method) map.remove(propertyName);
        } else {
            return null;
        }
    }

    public void clear() {
        typeMap.clear();
    }

    private void checkArgs(Class beanClass, String propertyName) {
        if (beanClass == null) {
            throw new IllegalArgumentException("beanClass is null");
        }
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName is null");
        }
    }
}
