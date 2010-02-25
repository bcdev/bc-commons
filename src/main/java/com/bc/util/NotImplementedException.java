package com.bc.util;

/**
 * This exception may replace code passages like
 * <code>
 * // implement method
 * throw new IllegalStateException("not implemented");
 * </code>
 * with code that may be found using the "Find Reference" feature
 * of your favourite IDE. Search for usages of this exceptions
 * constructor...
 */
public class NotImplementedException extends IllegalStateException {
    public NotImplementedException() {
        super("not implemented");
    }
}
