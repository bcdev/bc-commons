/*
 * Created at 23.03.2004 14:39:41
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.prop;

import junit.framework.TestCase;


public class PropertyFactoryTest extends TestCase {

    public PropertyFactoryTest(String s) {
        super(s);
    }

    public void testCreatePropertyOneArg() {
        Property p;

        p = PropertyFactory.createProperty(" a");
        assertTrue(p instanceof MapProperty);
        assertEquals("a", p.getName());
        assertEquals(Property.UNKNOWN_TYPE, p.getType());

        p = PropertyFactory.createProperty(" a. b");
        assertTrue(p instanceof NestedProperty);
        assertEquals("a.b", p.getName());
        assertEquals(Property.UNKNOWN_TYPE, p.getType());
        final Property parent = ((NestedProperty) p).getParent();
        assertTrue(parent instanceof MapProperty);
        assertEquals("a", parent.getName());
        assertEquals(Property.UNKNOWN_TYPE, parent.getType());
        final Property child = ((NestedProperty) p).getChild();
        assertTrue(child instanceof GenericProperty);
        assertEquals("b", child.getName());
        assertEquals(Property.UNKNOWN_TYPE, child.getType());
    }

    public void testCreatePropertyTwoArgs() {
        Property p;

        p = PropertyFactory.createProperty(TestBean.class, " vi ");
        assertTrue(p instanceof BeanProperty);
        assertEquals("vi", p.getName());
        assertEquals(int.class, p.getType());

        p = PropertyFactory.createProperty(TestBean.class, " vo . vi");
        assertTrue(p instanceof NestedProperty);
        assertEquals("vo.vi", p.getName());
        assertEquals(int.class, p.getType());
        final Property parent = ((NestedProperty) p).getParent();
        assertTrue(parent instanceof BeanProperty);
        assertEquals("vo", parent.getName());
        assertEquals(TestBean.class, parent.getType());
        final Property child = ((NestedProperty) p).getChild();
        assertTrue(child instanceof BeanProperty);
        assertEquals("vi", child.getName());
        assertEquals(int.class, child.getType());
    }

    public void testCreatePropertyThrowsRuntimeExceptionInCaseIllegalPropertyString() {
        try {
            PropertyFactory.createProperty(TestBean.class, "?-08nf");
        } catch (RuntimeException expected) {
        }
    }

    public void testCreateBeanProperty() {
        BeanProperty bp = PropertyFactory.createBeanProperty(TestBean.class, "vs");
        assertEquals(java.lang.String.class, bp.getType());
        assertEquals("getVs", bp.getGetter().getName());
        assertEquals("setVs", bp.getSetter().getName());
    }

    public void testCreateBeanPropertyWithoutSetter() {
        BeanProperty bp = PropertyFactory.createBeanProperty(TestBean.class, "onlyGetterAvailable");
        assertEquals(java.lang.String.class, bp.getType());
        assertEquals("getOnlyGetterAvailable", bp.getGetter().getName());
        assertNull(bp.getSetter());
    }

    // todo - 3 tb/nf what about only setter beans properties??
//    public void testCreateBeanPropertyWithoutGetter() {
//        BeanProperty bp = PropertyFactory.createBeanProperty(TestBean.class, "onlySetterAvailable");
//        assertNotNull(bp);
//        assertEquals(java.lang.String.class, bp.getType());
//        assertEquals("getOnlySetterAvailable", bp.getSetter().getName());
//        assertNull(bp.getGetter());
//    }
}
