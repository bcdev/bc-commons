/*
 * $Id: NestedProperty.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
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

import java.util.HashMap;
import java.util.Map;


public class NestedProperty implements Property {

    private Property parent;
    private Property child;


    protected NestedProperty(Property parent, Property child) {
        setParent(parent);
        setChild(child);
    }

    public String getName() {
        if (child instanceof MapProperty) {
            final String childName = child.getName();
            if (((MapProperty) child).isNameJavaIdentifier()) {
                return parent.getName() + "." + childName;
            } else {
                return parent.getName() + "[\"" + childName + "\"]"; // todo 3 nf/** - escape '"'
            }
        } else {
            return parent.getName() + "." + child.getName();
        }
    }

    public Class getType() {
        return child.getType();
    }

    public Property getParent() {
        return parent;
    }

    public void setParent(Property parent) {
        if (parent == null) {
            throw new IllegalArgumentException("parent is null");
        }
        this.parent = parent;
    }

    public Property getChild() {
        return child;
    }

    public void setChild(Property child) {
        if (child == null) {
            throw new IllegalArgumentException("child is null");
        }
        if (child instanceof NestedProperty) {
            throw new IllegalArgumentException("child is a NestedProperty");
        }
        this.child = child;
    }

    public Object getValue(Object beanInstance) {
        final Object parentValue = parent.getValue(beanInstance);
        return child.getValue(parentValue);
    }

    public void setValue(Object beanInstance, Object value) {
        final Object parentValue = parent.getValue(beanInstance);
        child.setValue(parentValue, value);
    }

    public boolean isAssignable(Object beanInstance) {
        return child.isAssignable(beanInstance) && parent.isAssignable(beanInstance);
    }

    public void makeAssignable(Object beanInstance) {
        parent.makeAssignable(beanInstance);
        Object parentValue = parent.getValue(beanInstance);
        if (parentValue == null) {
            final Class parentType = parent.getType();
            if (Map.class.equals(parentType)) {
                parentValue = new HashMap();
            } else if (Property.UNKNOWN_TYPE.equals(parentType)) {
                parentValue = new HashMap();
            } else {
                try {
                    parentValue = parentType.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException("failed to make property '" + getName() + "' assignable", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("failed to make property '" + getName() + "' assignable", e);
                }
            }
            parent.setValue(beanInstance, parentValue);
        }
        child.makeAssignable(parentValue);
    }

    public String getTreeAsString() {
        return "NestedProperty["+parent.getTreeAsString()+","+child.getTreeAsString()+"]";
    }
}
