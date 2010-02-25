/*
 * $Id: MapProperty.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
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


import com.bc.util.string.StringUtils;

import java.util.Map;

/**
 * A property access for properties originating from a java.util.Map.
 */
public class MapProperty implements Property {

    private String name;
    private Class type;
    private boolean nameJavaIdentifier;

    public MapProperty(String name) {
        this.name = name;
        this.type = UNKNOWN_TYPE;
        this.nameJavaIdentifier = StringUtils.isJavaIdentifier(name);
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    public boolean isNameJavaIdentifier() {
        return nameJavaIdentifier;
    }

    public Object getValue(Object beanInstance) {
        return ((Map) beanInstance).get(getName());
    }

    public void setValue(Object beanInstance, Object value) {
        type = value != null ? value.getClass() : UNKNOWN_TYPE;
        ((Map) beanInstance).put(getName(), value);
    }

    public boolean isAssignable(Object beanInstance) {
        return true;
    }

    public void makeAssignable(Object beanInstance) {
        // ok, is already assignable
    }

    public String getTreeAsString() {
        return "MapProperty['" + name + "'," + type + "]";
    }

}
