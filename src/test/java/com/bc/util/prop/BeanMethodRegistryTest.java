/*
 * Created at 23.03.2004 14:39:41
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.prop;

import junit.framework.TestCase;

import java.lang.reflect.Method;


public class BeanMethodRegistryTest extends TestCase {

    public BeanMethodRegistryTest(String s) {
        super(s);
    }

    protected void setUp() throws Exception {
        clearRegistry();
    }

    protected void tearDown() throws Exception {
        clearRegistry();
    }

    private void clearRegistry() {
        BeanMethodRegistry.getGettersInstance().clear();
        BeanMethodRegistry.getSettersInstance().clear();
    }

    public void testGettersInstancePutGet() throws NoSuchMethodException {
        final Method x = TestBean.class.getMethod("getVi", (Class[]) null);
        final Method y = TestBean.class.getMethod("getVs", (Class[]) null);
        BeanMethodRegistry.getGettersInstance().put(TestBean.class, "x", x);
        BeanMethodRegistry.getGettersInstance().put(TestBean.class, "y", y);
        assertSame(x, BeanMethodRegistry.getGettersInstance().get(TestBean.class, "x"));
        assertSame(y, BeanMethodRegistry.getGettersInstance().get(TestBean.class, "y"));

        try {
            BeanMethodRegistry.getGettersInstance().put(TestBean.class, "y", x);
        } catch (IllegalArgumentException expected) {

        }
    }
}
