/*
 * Created at 23.03.2004 14:39:41
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.prop;

import junit.framework.TestCase;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;


public class PropertyParserTest extends TestCase {

    public PropertyParserTest(String s) {
        super(s);
    }

    public void testMapPropertyParsing() throws ParseException,
                                                PropertyNotFoundException {
        Property p;
        NestedProperty np1, np2;
        MapProperty mp1;
        GenericProperty gp2, gp3;

        p = PropertyParser.parseProperty(Map.class, "a");
        assertTrue(p instanceof MapProperty);
        assertEquals("a", p.getName());
        assertEquals(Property.UNKNOWN_TYPE, p.getType());

        p = PropertyParser.parseProperty(Map.class, "a.b");
        assertTrue(p instanceof NestedProperty);
        np1 = (NestedProperty) p;
        assertEquals("a.b", np1.getName());
        assertEquals(Property.UNKNOWN_TYPE, np1.getType());
        assertEquals(MapProperty.class, np1.getParent().getClass());
        assertEquals(GenericProperty.class, np1.getChild().getClass());
        mp1 = (MapProperty) np1.getParent();
        gp2 = (GenericProperty) np1.getChild();
        assertEquals("a", mp1.getName());
        assertEquals(Property.UNKNOWN_TYPE, mp1.getType());
        assertEquals("b", gp2.getName());
        assertEquals(Property.UNKNOWN_TYPE, gp2.getType());

        p = PropertyParser.parseProperty(Map.class, "a.b.c");
        assertTrue(p instanceof NestedProperty);
        np2 = (NestedProperty) p;
        assertEquals("a.b.c", np2.getName());
        assertEquals(Property.UNKNOWN_TYPE, np2.getType());
        assertEquals(NestedProperty.class, np2.getParent().getClass());
        assertEquals(GenericProperty.class, np2.getChild().getClass());
        np1 = (NestedProperty) np2.getParent();
        gp3 = (GenericProperty) np2.getChild();
        assertEquals("a.b", np1.getName());
        assertEquals(Property.UNKNOWN_TYPE, np1.getType());
        assertEquals(MapProperty.class, np1.getParent().getClass());
        assertEquals(GenericProperty.class, np1.getChild().getClass());
        mp1 = (MapProperty) np1.getParent();
        gp2 = (GenericProperty) np1.getChild();
        assertEquals("a", mp1.getName());
        assertEquals(Property.UNKNOWN_TYPE, mp1.getType());
        assertEquals("b", gp2.getName());
        assertEquals(Property.UNKNOWN_TYPE, gp2.getType());
        assertEquals("c", gp3.getName());
        assertEquals(Property.UNKNOWN_TYPE, gp3.getType());
    }

    public void testSimpleBeanPropertyParsing() throws ParseException,
                                                       PropertyNotFoundException {
        Property p;

        p = PropertyParser.parseProperty(TestBean.class, "vi");
        assertTrue(p instanceof BeanProperty);
        assertEquals("vi", p.getName());
        assertEquals(int.class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "vf");
        assertTrue(p instanceof BeanProperty);
        assertEquals("vf", p.getName());
        assertEquals(double.class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "vs");
        assertTrue(p instanceof BeanProperty);
        assertEquals("vs", p.getName());
        assertEquals(String.class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "vd");
        assertTrue(p instanceof BeanProperty);
        assertEquals("vd", p.getName());
        assertEquals(Date.class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "vo");
        assertTrue(p instanceof BeanProperty);
        assertEquals("vo", p.getName());
        assertEquals(TestBean.class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "vm");
        assertTrue(p instanceof BeanProperty);
        assertEquals("vm", p.getName());
        assertEquals(Map.class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "ai");
        assertTrue(p instanceof BeanProperty);
        assertEquals("ai", p.getName());
        assertEquals(int[].class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "af");
        assertTrue(p instanceof BeanProperty);
        assertEquals("af", p.getName());
        assertEquals(double[].class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "as");
        assertTrue(p instanceof BeanProperty);
        assertEquals("as", p.getName());
        assertEquals(String[].class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "ad");
        assertTrue(p instanceof BeanProperty);
        assertEquals("ad", p.getName());
        assertEquals(Date[].class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "ao");
        assertTrue(p instanceof BeanProperty);
        assertEquals("ao", p.getName());
        assertEquals(TestBean[].class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "am");
        assertTrue(p instanceof BeanProperty);
        assertEquals("am", p.getName());
        assertEquals(Map[].class, p.getType());
    }

    public void testArrayBeanPropertyParsing() throws ParseException,
                                                      PropertyNotFoundException {
        Property p;
        ArrayProperty ap;

        p = PropertyParser.parseProperty(TestBean.class, "ai[0]");
        assertTrue(p instanceof ArrayProperty);
        ap = (ArrayProperty) p;
        assertTrue(ap.getArray() instanceof BeanProperty);
        assertEquals("ai[0]", ap.getName());
        assertEquals(int.class, ap.getType());
        assertEquals(0, ap.getIndex());

        p = PropertyParser.parseProperty(TestBean.class, "af[354]");
        assertTrue(p instanceof ArrayProperty);
        ap = (ArrayProperty) p;
        assertTrue(ap.getArray() instanceof BeanProperty);
        assertEquals("af[354]", ap.getName());
        assertEquals(double.class, ap.getType());
        assertEquals(354, ap.getIndex());

        p = PropertyParser.parseProperty(TestBean.class, "as[54]");
        assertTrue(p instanceof ArrayProperty);
        ap = (ArrayProperty) p;
        assertTrue(ap.getArray() instanceof BeanProperty);
        assertEquals("as[54]", ap.getName());
        assertEquals(String.class, ap.getType());
        assertEquals(54, ap.getIndex());

        p = PropertyParser.parseProperty(TestBean.class, "ad[987]");
        assertTrue(p instanceof ArrayProperty);
        ap = (ArrayProperty) p;
        assertTrue(ap.getArray() instanceof BeanProperty);
        assertEquals("ad[987]", ap.getName());
        assertEquals(Date.class, ap.getType());
        assertEquals(987, ap.getIndex());

        p = PropertyParser.parseProperty(TestBean.class, "ao[632]");
        assertTrue(p instanceof ArrayProperty);
        ap = (ArrayProperty) p;
        assertTrue(ap.getArray() instanceof BeanProperty);
        assertEquals("ao[632]", ap.getName());
        assertEquals(TestBean.class, ap.getType());
        assertEquals(632, ap.getIndex());
    }

    public void testDelegateBeanPropertyParsing() throws ParseException,
                                                         PropertyNotFoundException {
        Property p;
        NestedProperty dp;

        p = PropertyParser.parseProperty(TestBean.class, "vo.vi");
        assertTrue(p instanceof NestedProperty);
        dp = (NestedProperty) p;
        assertTrue(dp.getParent() instanceof BeanProperty);
        assertTrue(dp.getChild() instanceof BeanProperty);
        assertEquals("vo.vi", dp.getName());
        assertEquals(int.class, dp.getType());

        p = PropertyParser.parseProperty(TestBean.class, "vo.vo.vf");
        assertTrue(p instanceof NestedProperty);
        dp = (NestedProperty) p;
        assertTrue(dp.getParent() instanceof NestedProperty);
        assertTrue(dp.getChild() instanceof BeanProperty);
        assertEquals("vo.vo.vf", dp.getName());
        assertEquals(double.class, dp.getType());

        p = PropertyParser.parseProperty(TestBean.class, "vo.vo.vo.vs");
        assertTrue(p instanceof NestedProperty);
        dp = (NestedProperty) p;
        assertTrue(dp.getParent() instanceof NestedProperty);
        assertTrue(dp.getChild() instanceof BeanProperty);
        assertEquals("vo.vo.vo.vs", dp.getName());
        assertEquals(String.class, dp.getType());

        p = PropertyParser.parseProperty(TestBean.class, "vo.vo.vo.vo.vd");
        assertTrue(p instanceof NestedProperty);
        dp = (NestedProperty) p;
        assertTrue(dp.getParent() instanceof NestedProperty);
        assertTrue(dp.getChild() instanceof BeanProperty);
        assertEquals("vo.vo.vo.vo.vd", dp.getName());
        assertEquals(Date.class, dp.getType());
    }

    public void testArrayDelegateBeanPropertyParsing() throws ParseException,
                                                              PropertyNotFoundException {
        Property p;
        NestedProperty dp;

        p = PropertyParser.parseProperty(TestBean.class, "ao[56].ai");
        assertNotNull(p);
        assertEquals(NestedProperty.class, p.getClass());
        dp = (NestedProperty) p;
        assertTrue(dp.getParent() instanceof ArrayProperty);
        assertTrue(dp.getChild() instanceof BeanProperty);
        assertEquals("ao[56].ai", dp.getName());
        assertEquals(int[].class, dp.getType());

        p = PropertyParser.parseProperty(TestBean.class, "ao[56].vo.ad[6]");
        assertNotNull(p);
        assertEquals(NestedProperty.class, p.getClass());
        dp = (NestedProperty) p;
        assertTrue(dp.getParent() instanceof NestedProperty);
        assertTrue(dp.getChild() instanceof ArrayProperty);
        assertEquals("ao[56].vo.ad[6]", p.getName());
        assertEquals(Date.class, p.getType());

        p = PropertyParser.parseProperty(TestBean.class, "vo.ao[0].vs");
        assertNotNull(p);
        assertEquals(NestedProperty.class, p.getClass());
        dp = (NestedProperty) p;
        assertTrue(dp.getParent() instanceof NestedProperty);
        assertTrue(dp.getChild() instanceof BeanProperty);
        assertEquals("vo.ao[0].vs", p.getName());
        assertEquals(String.class, p.getType());
    }

    public void testParseExceptionNotThrown() throws PropertyNotFoundException {
        assertParseExceptionNotThrown(TestBean.class, "vo  ");
        assertParseExceptionNotThrown(TestBean.class, "  vo");
        assertParseExceptionNotThrown(TestBean.class, " vo ");
        assertParseExceptionNotThrown(TestBean.class, " vo  . ai[ 6 ] ");
        assertParseExceptionNotThrown(TestBean.class, " vo  . am[ 6 ].bibo  ");
        assertParseExceptionNotThrown(TestBean.class, " vo  . am[ 6 ][ \"bi-bo\"]  ");
        assertParseExceptionNotThrown(TestBean.class, " vo  . vm.bibo ");
        assertParseExceptionNotThrown(TestBean.class, " vo  . vm[ \"bi-bo\"] ");
        assertParseExceptionNotThrown(TestBean.class, " vm[ \"bi-bo\"].erno ");
        assertParseExceptionNotThrown(TestBean.class, " vm[ \"bi-bo\"][\"er-no\"] ");
    }

    public void testPropertyNotFoundExceptionThrown() throws ParseException {
        assertPropertyNotFoundExceptionThrown(TestBean.class, "a", "'a' not found");
        assertPropertyNotFoundExceptionThrown(TestBean.class, "vo.a", "'a' not found");
        assertPropertyNotFoundExceptionThrown(TestBean.class, "ai.a", "'a' not found");
        assertPropertyNotFoundExceptionThrown(TestBean.class, "ai[2].a", "'a' not found");
    }

    public void testParseExceptionThrown() throws PropertyNotFoundException {
        assertParseExceptionThrown(TestBean.class, "+", "name expected, got +");
        assertParseExceptionThrown(TestBean.class, "]", "name expected, got ]");
        assertParseExceptionThrown(TestBean.class, " ", "name expected, got  ");
        assertParseExceptionThrown(TestBean.class, "vo. ", "name expected, got vo. ");
        assertParseExceptionThrown(TestBean.class, "vo.+", "name expected, got vo.+");
        assertParseExceptionThrown(TestBean.class, "vo.]", "name expected, got vo.]");
        assertParseExceptionThrown(TestBean.class, "vo+", "'.' or '[' or EOS expected");
        assertParseExceptionThrown(TestBean.class, "vo]", "'.' or '[' or EOS expected");
        assertParseExceptionThrown(TestBean.class, "ai[ ", "index or key expected");
        assertParseExceptionThrown(TestBean.class, "ai[+", "index or key expected");
        assertParseExceptionThrown(TestBean.class, "ai[]", "index or key expected");
        assertParseExceptionThrown(TestBean.class, "ai[x", "index or key expected");
        assertParseExceptionThrown(TestBean.class, "ai[1 ", "']' expected");
        assertParseExceptionThrown(TestBean.class, "ai[1+", "']' expected");
        assertParseExceptionThrown(TestBean.class, "ai[1[", "']' expected");
        assertParseExceptionThrown(TestBean.class, "ai[1x", "']' expected");
        assertParseExceptionThrown(TestBean.class, "vi[0]", "'vi' is not a Java array");
        assertParseExceptionThrown(TestBean.class, "ao[2][3]", "'ao[2]' is not a Java array");
        assertParseExceptionThrown(TestBean.class, "ai[\"a\"]", "'ai' is not a java.util.Map");
        assertParseExceptionThrown(TestBean.class, "vm[\"a]", "string delimitter expected");
    }

    private void assertPropertyNotFoundExceptionThrown(Class baseType, String fullName, String expectedMsg) throws ParseException {
        try {
            PropertyParser.parseProperty(baseType, fullName);
            fail();
        } catch (PropertyNotFoundException expected) {
            assertEquals(expectedMsg, expected.getMessage());
        }
    }

    private void assertParseExceptionNotThrown(final Class baseType, final String fullName) throws PropertyNotFoundException {
        try {
            PropertyParser.parseProperty(baseType, fullName);
        } catch (ParseException unexpected) {
            fail(fullName + ": offset " + unexpected.getErrorOffset() + ": " + unexpected.getMessage());
        }
    }

    private void assertParseExceptionThrown(final Class baseType, final String fullName, final String expectedMsg) throws PropertyNotFoundException {
        try {
            PropertyParser.parseProperty(baseType, fullName);
            fail();
        } catch (ParseException expected) {
            assertEquals(expectedMsg, expected.getMessage());
        }
    }
}
