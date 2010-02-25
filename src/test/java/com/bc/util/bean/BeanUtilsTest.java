/*
 * Created at 23.03.2004 14:39:41
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.bean;

import junit.framework.TestCase;

import java.awt.Color;
import java.io.File;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BeanUtilsTest extends TestCase {

    public BeanUtilsTest(String s) {
        super(s);
    }

    public void testSupportedPropertyTypes() throws ParseException {

        final Properties properties = new Properties();
        properties.put("booleanProp", "true");
        properties.put("booleanObjProp", "true");

        properties.put("byteProp", "35");
        properties.put("byteObjProp", "-36");

        properties.put("charProp", "p");
        properties.put("charObjProp", "q");

        properties.put("shortProp", "1293");
        properties.put("shortObjProp", "-1294");

        properties.put("intProp", "85208");
        properties.put("intObjProp", "-85209");

        properties.put("longProp", "98432985245");
        properties.put("longObjProp", "-98432985246");

        properties.put("floatProp", "785.435");
        properties.put("floatObjProp", "-785.436");

        properties.put("doubleProp", "2809.987465");
        properties.put("doubleObjProp", "-2809.987466");

        properties.put("stringObjProp", "Rallomat");
        properties.put("fileObjProp", "/usr/local");
        properties.put("colorObjProp", "65,96,238");

        properties.put("delegateProp.doubleProp", "1234.5678");
        properties.put("delegateProp.colorObjProp", "86,154,12");

        properties.put("objArrayProp[0].doubleProp", "0");
        properties.put("objArrayProp[0].colorObjProp", "1,2,3,4");
        properties.put("objArrayProp[1].doubleProp", "1");
        properties.put("objArrayProp[1].colorObjProp", "2,3,4,5");

        properties.put("intArrayProp[0]", "10");
        properties.put("intArrayProp[1]", "20");
        properties.put("intArrayProp[2]", "30");

        properties.put("mapProp.prop1", "Hanni");
        properties.put("mapProp.prop2", "Nanni");
        properties.put("mapProp.prop3.prop31", "Pfanni");
        properties.put("mapProp.prop3.prop32", "Susanni");
        properties.put("mapProp.prop4.doubleProp", "0.12345");
        properties.put("mapProp.prop4.fileObjProp", "/usr/local");

        final TestBean bean = new TestBean();
//        final HashMap prop3 = new HashMap();
//        prop3.put("prop31", "Pfanni");
//        prop3.put("prop32", "Susanni");
//        bean.getMapProp().put("prop3", prop3);
        bean.getMapProp().put("prop4", new TestBean());
        BeanUtils.setBeanProperties(bean, properties);

        assertEquals(true, bean.getBooleanProp());
        assertEquals(Boolean.TRUE, bean.getBooleanObjProp());

        assertEquals(35, bean.getByteProp());
        assertEquals(new Byte((byte) -36), bean.getByteObjProp());

        assertEquals('p', bean.getCharProp());
        assertEquals(new Character('q'), bean.getCharObjProp());

        assertEquals(1293, bean.getShortProp());
        assertEquals(new Short((short) -1294), bean.getShortObjProp());

        assertEquals(85208, bean.getIntProp());
        assertEquals(new Integer(-85209), bean.getIntObjProp());

        assertEquals(98432985245L, bean.getLongProp());
        assertEquals(new Long(-98432985246L), bean.getLongObjProp());

        assertEquals(785.435, bean.getFloatProp(), 1e-5);
        assertEquals(new Float(-785.436), bean.getFloatObjProp());

        assertEquals(2809.987465, bean.getDoubleProp(), 1e-10);
        assertEquals(new Double(-2809.987466), bean.getDoubleObjProp());

        assertEquals("Rallomat", bean.getStringObjProp());
        assertEquals(new File("/usr/local"), bean.getFileObjProp());
        assertEquals(new Color(65, 96, 238), bean.getColorObjProp());

        assertNotNull(bean.getDelegateProp());
        assertNull(bean.getDelegateProp().getDelegateProp());
        assertEquals(1234.5678, bean.getDelegateProp().getDoubleProp(), 1e-10);
        assertEquals(new Color(86, 154, 12), bean.getDelegateProp().getColorObjProp());

        assertNotNull(bean.getObjArrayProp());
        assertEquals(2, bean.getObjArrayProp().length);
        assertEquals(0.0, bean.getObjArrayProp()[0].getDoubleProp(), 1e-10);
        assertEquals(new Color(1, 2, 3, 4), bean.getObjArrayProp()[0].getColorObjProp());
        assertEquals(1.0, bean.getObjArrayProp()[1].getDoubleProp(), 1e-10);
        assertEquals(new Color(2, 3, 4, 5), bean.getObjArrayProp()[1].getColorObjProp());

// todo 3 nf/** - implement if required (and time allows)
//        assertNotNull(bean.getIntArrayProp());
//        assertEquals(3, bean.getIntArrayProp().length);
//        assertEquals(10, bean.getIntArrayProp()[0]);
//        assertEquals(20, bean.getIntArrayProp()[1]);
//        assertEquals(30, bean.getIntArrayProp()[2]);

        assertEquals("Hanni", bean.getMapProp().get("prop1"));
        assertEquals("Nanni", bean.getMapProp().get("prop2"));

        assertEquals(HashMap.class, bean.getMapProp().get("prop3").getClass());
        assertEquals("Pfanni", ((Map) bean.getMapProp().get("prop3")).get("prop31"));
        assertEquals("Susanni", ((Map) bean.getMapProp().get("prop3")).get("prop32"));

        assertEquals(0.12345, ((TestBean) bean.getMapProp().get("prop4")).getDoubleProp(), 1e-10);
        assertEquals(new File("/usr/local"), ((TestBean) bean.getMapProp().get("prop4")).getFileObjProp());
    }

    public void testUnsupportedPropertyTypes() throws ParseException {
        final Properties properties = new Properties();
        properties.put("delegateProp", "Na, na, na, na!");

        final TestBean bean = new TestBean();
        try {
            BeanUtils.setBeanProperties(bean, properties);
            fail("ParseException expected");
        } catch (ParseException expected) {
        }
    }

    public void testPropertyNamingConventions() throws ParseException {
        final Properties properties = new Properties();
        final TestBean bean = new TestBean();

        properties.put("booleanProp", "true");
        properties.put("intProp", "3456");
        BeanUtils.setBeanProperties(bean, properties);
        assertEquals(true, bean.getBooleanProp());
        assertEquals(3456, bean.getIntProp());

        properties.remove("booleanProp");
        properties.remove("intProp");

        properties.put("BooleanProp", "false");
        properties.put("IntProp", "-6543");
        BeanUtils.setBeanProperties(bean, properties);
        assertEquals(false, bean.getBooleanProp());
        assertEquals(-6543, bean.getIntProp());

        properties.remove("booleanProp");
        properties.remove("intProp");

        properties.put("BOOLEANPROP", "true");
        properties.put("INTPROP", "3456");
        BeanUtils.setBeanProperties(bean, properties);
        assertEquals(false, bean.getBooleanProp());
        assertEquals(-6543, bean.getIntProp());
    }

    public void testInvalidPropertyValues() {
        try {
            final Properties properties = new Properties();
            final TestBean bean = new TestBean();
            properties.put("charProp", "abc");
            BeanUtils.setBeanProperties(bean, properties);
            fail();
        } catch (ParseException expected) {
        }

        try {
            final Properties properties = new Properties();
            final TestBean bean = new TestBean();
            properties.put("intProp", "hj845");
            BeanUtils.setBeanProperties(bean, properties);
            fail();
        } catch (ParseException expected) {
        }

        try {
            final Properties properties = new Properties();
            final TestBean bean = new TestBean();
            properties.put("doubleProp", "845,543");
            BeanUtils.setBeanProperties(bean, properties);
            fail();
        } catch (ParseException expected) {
        }

        try {
            final Properties properties = new Properties();
            final TestBean bean = new TestBean();
            properties.put("colorObjProp", "32,z9,43");
            BeanUtils.setBeanProperties(bean, properties);
            fail();
        } catch (ParseException expected) {
        }
    }

    public static class TestBean {

        private boolean booleanProp;
        private Boolean booleanObjProp;
        private byte byteProp;
        private Byte byteObjProp;
        private char charProp;
        private Character charObjProp;
        private short shortProp;
        private Short shortObjProp;
        private int intProp;
        private Integer intObjProp;
        private long longProp;
        private Long longObjProp;
        private float floatProp;
        private Float floatObjProp;
        private double doubleProp;
        private Double doubleObjProp;
        private String stringObjProp;
        private File fileObjProp;
        private Color colorObjProp;
        private TestBean delegateProp;
        private TestBean[] objArrayProp;
        private int[] intArrayProp;
        private Map mapProp;

        public TestBean() {
            mapProp = new HashMap();
        }

        public boolean getBooleanProp() {
            return booleanProp;
        }

        public void setBooleanProp(boolean booleanProp) {
            this.booleanProp = booleanProp;
        }

        public Boolean getBooleanObjProp() {
            return booleanObjProp;
        }

        public void setBooleanObjProp(Boolean booleanObjProp) {
            this.booleanObjProp = booleanObjProp;
        }

        public byte getByteProp() {
            return byteProp;
        }

        public void setByteProp(byte byteProp) {
            this.byteProp = byteProp;
        }

        public Byte getByteObjProp() {
            return byteObjProp;
        }

        public void setByteObjProp(Byte byteObjProp) {
            this.byteObjProp = byteObjProp;
        }

        public char getCharProp() {
            return charProp;
        }

        public void setCharProp(char charProp) {
            this.charProp = charProp;
        }

        public Character getCharObjProp() {
            return charObjProp;
        }

        public void setCharObjProp(Character charObjProp) {
            this.charObjProp = charObjProp;
        }

        public short getShortProp() {
            return shortProp;
        }

        public void setShortProp(short shortProp) {
            this.shortProp = shortProp;
        }

        public Short getShortObjProp() {
            return shortObjProp;
        }

        public void setShortObjProp(Short shortObjProp) {
            this.shortObjProp = shortObjProp;
        }

        public int getIntProp() {
            return intProp;
        }

        public void setIntProp(int intProp) {
            this.intProp = intProp;
        }

        public Integer getIntObjProp() {
            return intObjProp;
        }

        public void setIntObjProp(Integer intObjProp) {
            this.intObjProp = intObjProp;
        }

        public long getLongProp() {
            return longProp;
        }

        public void setLongProp(long longProp) {
            this.longProp = longProp;
        }

        public Long getLongObjProp() {
            return longObjProp;
        }

        public void setLongObjProp(Long longObjProp) {
            this.longObjProp = longObjProp;
        }

        public float getFloatProp() {
            return floatProp;
        }

        public void setFloatProp(float floatProp) {
            this.floatProp = floatProp;
        }

        public Float getFloatObjProp() {
            return floatObjProp;
        }

        public void setFloatObjProp(Float floatObjProp) {
            this.floatObjProp = floatObjProp;
        }

        public double getDoubleProp() {
            return doubleProp;
        }

        public void setDoubleProp(double doubleProp) {
            this.doubleProp = doubleProp;
        }

        public Double getDoubleObjProp() {
            return doubleObjProp;
        }

        public void setDoubleObjProp(Double doubleObjProp) {
            this.doubleObjProp = doubleObjProp;
        }

        public String getStringObjProp() {
            return stringObjProp;
        }

        public void setStringObjProp(String stringObjProp) {
            this.stringObjProp = stringObjProp;
        }

        public File getFileObjProp() {
            return fileObjProp;
        }

        public void setFileObjProp(File fileObjProp) {
            this.fileObjProp = fileObjProp;
        }

        public Color getColorObjProp() {
            return colorObjProp;
        }

        public void setColorObjProp(Color colorObjProp) {
            this.colorObjProp = colorObjProp;
        }

        public TestBean getDelegateProp() {
            return delegateProp;
        }

        public void setDelegateProp(TestBean delegateProp) {
            this.delegateProp = delegateProp;
        }

        public TestBean[] getObjArrayProp() {
            return objArrayProp;
        }

        public void setObjArrayProp(TestBean[] objArrayProp) {
            this.objArrayProp = objArrayProp;
        }

        public int[] getIntArrayProp() {
            return intArrayProp;
        }

        public void setIntArrayProp(int[] intArrayProp) {
            this.intArrayProp = intArrayProp;
        }

        public Map getMapProp() {
            return mapProp;
        }

        public void setMapProp(Map mapProp) {
            this.mapProp = mapProp;
        }
    }

}
