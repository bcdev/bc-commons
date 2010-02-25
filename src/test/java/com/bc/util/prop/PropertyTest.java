/*
 * Created at 23.03.2004 14:39:41
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.prop;

import junit.framework.TestCase;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class PropertyTest extends TestCase {

    public PropertyTest(String s) {
        super(s);
    }

    public void testGetName() {
        Property p;

        p = PropertyFactory.createProperty("  rallo");
        assertEquals("rallo", p.getName());

        p = PropertyFactory.createProperty(TestBean.class, " ai[8  ]");
        assertEquals("ai[8]", p.getName());
// todo 3 nf/** - getting error "'aaf' is not a Java array", why?
//        p = PropertyFactory.createProperty("aaf [8] [4]");
//        assertEquals("aaf[8][4]", p.getName());

        p = PropertyFactory.createProperty("m  .rallo");
        assertEquals("m.rallo", p.getName());
        p = PropertyFactory.createProperty("m[\"rallo\"]  ");
        assertEquals("m.rallo", p.getName());
        p = PropertyFactory.createProperty(" m [ \"ral-lo\" ]");
        assertEquals("m[\"ral-lo\"]", p.getName());
    }

    public void testGetSetValue() {
        Property p;

        final TestBean testBean = new TestBean();

        p = PropertyFactory.createProperty(TestBean.class, "vi");
        assertOutIsIn(testBean, p, new Integer(4357));

        p = PropertyFactory.createProperty(TestBean.class, "vf");
        assertOutIsIn(testBean, p, new Double(3425.2436));

        p = PropertyFactory.createProperty(TestBean.class, "vs");
        assertOutIsIn(testBean, p, new String("rallamann"));

        p = PropertyFactory.createProperty(TestBean.class, "vd");
        assertOutIsIn(testBean, p, new Date());

        p = PropertyFactory.createProperty(TestBean.class, "vo");
        assertOutIsIn(testBean, p, new TestBean());

        p = PropertyFactory.createProperty(TestBean.class, "vo.vf");
        assertOutIsIn(testBean, p, new Double(0.05));

        p = PropertyFactory.createProperty(TestBean.class, "vo.vd");
        assertOutIsIn(testBean, p, null);

        p = PropertyFactory.createProperty(TestBean.class, "vm");
        assertOutIsIn(testBean, p, new HashMap());

        p = PropertyFactory.createProperty(TestBean.class, "vm.x");
        assertOutIsIn(testBean, p, new Float(3.5f));

        p = PropertyFactory.createProperty(TestBean.class, "vm.y");
        assertOutIsIn(testBean, p, new Float(-2.8f));

        p = PropertyFactory.createProperty(TestBean.class, "vm.b");
        assertOutIsIn(testBean, p, new TestBean());

        p = PropertyFactory.createProperty(TestBean.class, "vm.b.vi");
        assertOutIsIn(testBean, p, new Integer(23));

        p = PropertyFactory.createProperty(TestBean.class, "ai");
        assertOutIsIn(testBean, p, new int[3]);

        p = PropertyFactory.createProperty(TestBean.class, "ai[0]");
        assertOutIsIn(testBean, p, new Integer(43));

        p = PropertyFactory.createProperty(TestBean.class, "ai[1]");
        assertOutIsIn(testBean, p, new Integer(324));

        p = PropertyFactory.createProperty(TestBean.class, "ai[2]");
        assertOutIsIn(testBean, p, new Integer(-43));

        p = PropertyFactory.createProperty(TestBean.class, "af");
        assertOutIsIn(testBean, p, new double[0]);

        p = PropertyFactory.createProperty(TestBean.class, "as");
        assertOutIsIn(testBean, p, new String[0]);

        p = PropertyFactory.createProperty(TestBean.class, "ao");
        assertOutIsIn(testBean, p, new TestBean[3]);

        p = PropertyFactory.createProperty(TestBean.class, "ao[2]");
        assertOutIsIn(testBean, p, new TestBean());

        p = PropertyFactory.createProperty(TestBean.class, "ao[2].vs");
        assertOutIsIn(testBean, p, "Manometer!");

        p = PropertyFactory.createProperty(TestBean.class, "am");
        assertOutIsIn(testBean, p, new HashMap[3]);

        p = PropertyFactory.createProperty(TestBean.class, "am[1]");
        assertOutIsIn(testBean, p, new HashMap());

        p = PropertyFactory.createProperty(TestBean.class, "am[1].name");
        assertOutIsIn(testBean, p, "Bibo");

        p = PropertyFactory.createProperty(TestBean.class, "am[1].entries");
        assertOutIsIn(testBean, p, new HashMap());

        p = PropertyFactory.createProperty(TestBean.class, "am[1].entries.age");
        assertOutIsIn(testBean, p, new Integer(32));
    }

    private void assertOutIsIn(final TestBean testBean, Property p, Object in) {
        Object out;
        p.setValue(testBean, in);
        out = p.getValue(testBean);
        assertEquals(in, out);
    }

    public void testMakeAssignableForMap() {
        Property p;
        HashMap m1, m2, m3;
        Object a, b, c;
        p = PropertyFactory.createProperty(Map.class, "a");
        m1 = new HashMap();
        p.makeAssignable(m1);
        a = m1.get("a");
        assertNull(a);
        p.setValue(m1, "x");
        a = m1.get("a");
        assertEquals("x", a);

        p = PropertyFactory.createProperty(Map.class, "a.b");
        m1 = new HashMap();
        p.makeAssignable(m1);
        a = m1.get("a");
        assertTrue(a instanceof HashMap);
        m2 = (HashMap) a;
        b = m2.get("b");
        assertNull(b);
        p.setValue(m1, "x");
        b = m2.get("b");
        assertEquals("x", b);

        p = PropertyFactory.createProperty(Map.class, "a.b.c");
        m1 = new HashMap();
        p.makeAssignable(m1);
        a = m1.get("a");
        assertTrue(a instanceof HashMap);
        m2 = (HashMap) a;
        b = m2.get("b");
        assertTrue(b instanceof HashMap);
        m3 = (HashMap) b;
        c = m3.get("c");
        assertNull(c);
        p.setValue(m1, "x");
        c = m3.get("c");
        assertEquals("x", c);
    }

    public void testMakeAssignableForBean() {
        Property p;
        TestBean b;

        p = PropertyFactory.createProperty(TestBean.class, "vi");
        b = new TestBean();
        assertEquals(0, b.getVi());
        p.makeAssignable(b);
        assertEquals(0, b.getVi());
        p.setValue(b, new Integer(4));
        assertEquals(4, b.getVi());

        p = PropertyFactory.createProperty(TestBean.class, "vo.vi");
        b = new TestBean();
        assertNull(b.getVo());
        p.makeAssignable(b);
        assertNotNull(b.getVo());
        assertEquals(0, b.getVo().getVi());
        p.setValue(b, new Integer(45));
        assertEquals(45, b.getVo().getVi());

        p = PropertyFactory.createProperty(TestBean.class, "vo.vo.vi");
        b = new TestBean();
        assertNull(b.getVo());
        p.makeAssignable(b);
        assertNotNull(b.getVo());
        assertNotNull(b.getVo().getVo());
        assertEquals(0, b.getVo().getVo().getVi());
        p.setValue(b, new Integer(456));
        assertEquals(456, b.getVo().getVo().getVi());
    }

    public void testMakeAssignableForArray() throws ParseException {
        Property p;
        TestBean b;

        p = PropertyFactory.createProperty(TestBean.class, "ai");
        b = new TestBean();
        assertNull(b.getAi());
        p.makeAssignable(b);
        assertNull(b.getAi());
        p.setValue(b, new int[0]);
        assertNotNull(b.getAi());

        p = PropertyFactory.createProperty(TestBean.class, "ao[2].vi");
        b = new TestBean();
        assertNull(b.getAo());
        p.makeAssignable(b);
        assertNotNull(b.getAo());
        assertEquals(3, b.getAo().length);
        assertNull(b.getAo()[0]);
        assertNull(b.getAo()[1]);
        assertNotNull(b.getAo()[2]);
        assertEquals(0, b.getAo()[2].getVi());
        p.setValue(b, new Integer(45));
        assertEquals(45, b.getAo()[2].getVi());

        p = PropertyFactory.createProperty(TestBean.class, "vo.ao[2].vi");
        b = new TestBean();
        assertNull(b.getVo());
        p.makeAssignable(b);
        assertNotNull(b.getVo());
        assertNotNull(b.getVo().getAo());
        assertEquals(3, b.getVo().getAo().length);
        assertNull(b.getVo().getAo()[0]);
        assertNull(b.getVo().getAo()[1]);
        assertNotNull(b.getVo().getAo()[2]);
        assertEquals(0, b.getVo().getAo()[2].getVi());
        p.setValue(b, new Integer(456));
        assertEquals(456, b.getVo().getAo()[2].getVi());
    }

    public void testGenericPropertyBehaviour() {
        final TestBean testBean = new TestBean();
        testBean.setVm(new HashMap());
        testBean.getVm().put("b", new TestBean());

        final Property vm = PropertyFactory.createChildProperty(TestBean.class, "vm");
        final Property b = PropertyFactory.createChildProperty(Map.class, "b");
        final Property vi = new GenericProperty("vi");
        final NestedProperty n1 = new NestedProperty(vm, b);
        final NestedProperty n2 = new NestedProperty(n1, vi);
        assertEquals(Property.UNKNOWN_TYPE, n2.getType());
        assertEquals(new Integer(0), n2.getValue(testBean));
        n2.setValue(testBean, new Integer(5));
        assertEquals(int.class, n2.getType());
        assertEquals(new Integer(5), n2.getValue(testBean));
        n2.setValue(testBean, "120");
        assertEquals(int.class, n2.getType());
        assertEquals(new Integer(120), n2.getValue(testBean));
    }


    public void testGetTreeAsString() {
        TestBean testBean = new TestBean();
        HashMap testMap = new HashMap();

        testMap.put("testBean", testBean);

        Property property = PropertyFactory.createProperty("BlankLinesStatistic.radiance_11.counts");
        assertEquals(
                "NestedProperty[NestedProperty[MapProperty['BlankLinesStatistic',interface com.bc.util.prop.Property$Unknown],GenericProperty['radiance_11',interface com.bc.util.prop.Property$Unknown]],GenericProperty['counts',interface com.bc.util.prop.Property$Unknown]]",
                property.getTreeAsString());
    }
}
