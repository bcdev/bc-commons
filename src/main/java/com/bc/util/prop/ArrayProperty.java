/*
 * $Id: ArrayProperty.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
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

import java.lang.reflect.Array;

public class ArrayProperty implements Property {

    private Property array;
    private int index;

    protected ArrayProperty(Property array, int index) {
        setArray(array);
        setIndex(index);
    }

    public String getName() {
        return array.getName() + "[" + index + "]";
    }

    public Class getType() {
        return array.getType().getComponentType();
    }

    public Property getArray() {
        return array;
    }

    public void setArray(Property array) {
        if (array == null) {
            throw new IllegalArgumentException("array is null");
        }
        if (array.getType() != Property.UNKNOWN_TYPE && !array.getType().isArray()) {
            throw new IllegalArgumentException("array type is not a Java array");
        }
        this.array = array;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index is negative");
        }
        this.index = index;
    }

    public Object getValue(Object beanInstance) {
        Object arrayValue = array.getValue(beanInstance);
        return Array.get(arrayValue, index);
    }

    public void setValue(Object beanInstance, Object value) {
        Object arrayValue = array.getValue(beanInstance);
        Array.set(arrayValue, index, value);
    }

    public boolean isAssignable(Object beanInstance) {
        final Object arrayValue = array.getValue(beanInstance);
        return arrayValue != null && arrayValue.getClass().isArray();
    }

    public void makeAssignable(Object beanInstance) {
        array.makeAssignable(beanInstance);
        Object arrayValue = array.getValue(beanInstance);
        final int minLength = getIndex() + 1;
        if (arrayValue == null || Array.getLength(arrayValue) < minLength) {
            final Class arrayType = array.getType();
            Object arrayValue2 = Array.newInstance(arrayType.getComponentType(), minLength);
            if (arrayValue != null) {
                System.arraycopy(arrayValue, 0, arrayValue2, 0, Array.getLength(arrayValue));
            }
            array.setValue(beanInstance, arrayValue2);
        }
    }
    public String getTreeAsString() {
        return "ArrayProperty["+array.getTreeAsString()+","+index+"]";
    }

}
