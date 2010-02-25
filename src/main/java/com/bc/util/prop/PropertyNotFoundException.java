/*
 * $Id: PropertyNotFoundException.java,v 1.1 2007-02-27 12:45:29 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.prop;

public class PropertyNotFoundException extends Exception {

    public PropertyNotFoundException(String message) {
        super(message);
    }

    public PropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
