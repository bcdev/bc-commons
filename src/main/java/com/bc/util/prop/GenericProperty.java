/*
 * $Id: GenericProperty.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
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


import java.text.ParseException;
import java.util.Map;

/**
 * A property access for properties originating from a java.util.Map.
 */
public class GenericProperty implements Property {

    private String name;
    private Class type;

    public GenericProperty(String name) {
        this.name = name;
        this.type = UNKNOWN_TYPE;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public Object getValue(Object beanInstance) {
        if (beanInstance instanceof Map) {
            return ((Map) beanInstance).get(getName());
        } else {
            Property child = getChildProperty(beanInstance);
            return child.getValue(beanInstance);
        }
    }

    public void setValue(Object beanInstance, Object value) {
        if (beanInstance instanceof Map) {
            type = value != null ? value.getClass() : UNKNOWN_TYPE;
            ((Map) beanInstance).put(getName(), value);
        } else {
            Property child = getChildProperty(beanInstance);
            type = child.getType();
            if (value instanceof String && !type.isAssignableFrom(String.class)) {
                final String text = (String) value;
                try {
                    value = PropertyParser.parseValue(type, getName(), text);
                } catch (ParseException e) {
                    throw new RuntimeException("conversion error", e);
                }
            }
            child.setValue(beanInstance, value);
        }
    }

    public boolean isAssignable(Object beanInstance) {
        return true;
    }

    public void makeAssignable(Object beanInstance) {
        // ok, is already assignable
    }

    private Property getChildProperty(Object beanInstance) {
        final Property child = PropertyFactory.createChildProperty(beanInstance.getClass(), getName());
        if (child == null) {
            throw new IllegalStateException("property not found: " + getName());
        }
        return child;
    }

    public String getTreeAsString() {
        return "GenericProperty['"+name+"',"+type+"]";
    }

}
