/*
 * $Id: TemplateTransaction.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;




/**
 * A transactions which uses a {@link Template} to store SQL and execute statements.
 */
public abstract class TemplateTransaction implements Transaction {

    private final Template template;
    private final Object parameterObject;

    /**
     * Constructs a new template transaction which is supposed not to return a value and has no parameter source. This
     * type of transaction can be useful for e.g. "CREATE TABLE" statements.
     *
     * @param sql the SQL code
     */
    protected TemplateTransaction(String sql) {
        this(sql, null, null);
    }

    /**
     * Constructs a new template transaction for the given template SQL, result type and input object. If the input
     * object is not null, the parameter type for the template is taken from the parameter object.
     *
     * @param templateSql     the template SQL, refer to {@link Template} for it's syntax. Must not be null.
     * @param resultType      the type of the result, can be null no result object is expected
     * @param parameterObject the parameter object, can be null if the template SQL does not contain parameters
     */
    protected TemplateTransaction(String templateSql, Class resultType, Object parameterObject) {
        this.template = new Template(templateSql,
                                     parameterObject != null ? parameterObject.getClass() : null,
                                     resultType);
        this.parameterObject = parameterObject;
    }

    /**
     * Gets the underlying {@link Template} to which execution is delegated.
     *
     * @return the template, never null
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Gets the parameter object.
     *
     * @return the parameter object, can be null
     */
    public Object getParameterObject() {
        return parameterObject;
    }
}
