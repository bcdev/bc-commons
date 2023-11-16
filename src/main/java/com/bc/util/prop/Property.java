/*
 * $Id: Property.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
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


public interface Property {

    Class UNKNOWN_TYPE = Unknown.class;

    String getName();

    Class getType();

    Object getValue(Object beanInstance);

    void setValue(Object beanInstance, Object value);

    boolean isAssignable(Object beanInstance);

    void makeAssignable(Object beanInstance);

    String getTreeAsString();

    interface Unknown {

    }
}
