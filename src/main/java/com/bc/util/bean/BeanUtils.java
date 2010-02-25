/*
 * Created at 23.03.2004 15:21:03
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.bean;

import com.bc.util.prop.MapProperty;
import com.bc.util.prop.Property;
import com.bc.util.prop.PropertyNotFoundException;
import com.bc.util.prop.PropertyParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Provides utility functions for Java Bean components.
 */
public class BeanUtils {

    /**
     * Sets the properties of a given Java Bean instance parsed from the property values contained in the supplied Java
     * properties file.
     *
     * @param beanInstance   the bean instance
     * @param propertiesFile the Java properties file
     * @throws IOException on IO errors
     * @throws ParseException if one of the supplied property (text) values could not be parsed
     */
    public static void setBeanProperties(final Object beanInstance, File propertiesFile) throws IOException,
            ParseException {
        final Properties properties = loadProperties(propertiesFile);
        setBeanProperties(beanInstance, properties);
    }

    /**
     * Sets the properties of a given Java Bean instance parsed from the property values
     * contained in the supplied Java properties style InputStream.
     *
     * @param beanInstance the bean instance
     * @param in           the Java properties InputStream
     * @throws IOException on IO errors
     * @throws ParseException if one of the supplied property (text) values could not be parsed
     */
    public static void setBeanProperties(final Object beanInstance, InputStream in) throws IOException,
            ParseException {
        final Properties properties = loadProperties(in);
        setBeanProperties(beanInstance, properties);
    }

    /**
     * Sets the properties of a given Java Bean instance parsed from the supplied properties map.
     *
     * @param beanInstance the bean instance
     * @param properties   the properties map
     * @throws ParseException if one of the supplied property (text) values could not be parsed
     */
    public static void setBeanProperties(final Object beanInstance, Map properties) throws ParseException {
        final Iterator iterator = properties.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry) iterator.next();
            final String name = (String) entry.getKey();
            final String value = (String) entry.getValue();
            try {
                setBeanPropertyFromText(beanInstance, name, value);
            } catch (PropertyNotFoundException e) {
                // @todo 3 tb/* handle this
            }
        }
    }

    /**
     * Utility function which loads a Java properties file. Even in case of an I/O error,
     * the file opened is always finally closed.
     *
     * @param file the properties file
     * @return the properties
     * @throws IOException if an I/O error occurs
     */
    public static Properties loadProperties(File file) throws IOException {
        final FileInputStream fileInputStream = new FileInputStream(file);
        return loadProperties(fileInputStream);
    }

    /**
     * Utility function which loads a Java properties file from an InputStream.
     * Even in case of an I/O error, the file opened is always finally closed.
     *
     * @param in the properties stream
     * @return the properties
     * @throws IOException if an I/O error occurs
     */
    public static Properties loadProperties(final InputStream in) throws IOException {
        final Properties properties = new Properties();
        try {
            properties.load(in);
        } finally {
            in.close();
        }
        return properties;
    }

    public static void setBeanPropertyFromText(final Object beanInstance,
                                               final String name,
                                               final String text) throws ParseException,
            PropertyNotFoundException {
        final Property property = PropertyParser.parseProperty(beanInstance.getClass(), name);
        final Class type = property.getType();
        final Object value = PropertyParser.parseValue(type != MapProperty.UNKNOWN_TYPE ? type : String.class, name,
                text);
        property.makeAssignable(beanInstance);
        property.setValue(beanInstance, value);
    }
}
