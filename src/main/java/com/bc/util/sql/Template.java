/*
 * Created at 06.04.2004 21:27:16
 * Copyright (c) 2004 by Norman Fomferra
 */
package com.bc.util.sql;

import com.bc.util.prop.Property;
import com.bc.util.prop.PropertyNotFoundException;
import com.bc.util.prop.PropertyParser;
import com.bc.util.sql.conv.DefaultValueConverter;
import com.bc.util.sql.conv.JavaToJdbcValueConverter;
import com.bc.util.sql.conv.JdbcToJavaValueConverter;
import com.bc.util.sql.conv.ValueConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <p> A utility class which represents a dynamic template for SQL. Instances of this class are created using "template
 * SQL" code. Template SQL code is standard SQL plus placeholders for parameter values as used in update statements. A
 * placeholder has the form <code>${</code><i>propertyName</i><code>}</code>. These placeholders provide the link
 * between Java Bean or map properties and the actual SQL parameters. For example, the SQL template code
 * <pre>
 *     SELECT user.name AS userName, user.passwd AS userPassword
 *     FROM user WHERE user.id = ${userId}
 * </pre>
 * expects that <code>userName</code> and <code>userPassword</code> are writable properties of the result object,
 * whereas <code>userId</code> must be a readable property of the parameter object.
 * <p/>
 * <p>Query statements are executed using the {@link #executeQueryForObject(java.sql.Connection, Object)} or {@link
 * #executeQueryForList(java.sql.Connection, Object)} which take the database connection and the parameter object. Both
 * methods are creating and returning instances of the result type. The property values of the result objects are
 * obtained using the property names as defined in the "SELECT" clause of the SQL code.
 * <p/>
 * <p>Update statements are executed using the {@link #executeUpdate(java.sql.Connection, Object)} method, which also
 * takes the database connection and the parameter object.
 */
public class Template {

    private final String sql;
    private final Class parameterType;
    private final Class resultType;
    private final Property[] parameterProperties;
    private final Property[] resultProperties;
    private final ValueProperty resultValueProperty;
    private Map javaToJdbcValueConverterMap;
    private Map jdbcToJavaValueConverterMap;
    private final JavaToJdbcValueConverter defaultJavaToJdbcValueConverter;
    private final JdbcToJavaValueConverter defaultJdbcToJavaValueConverter;

    /**
     * Constructs a new template using the given template SQL, parameter and result types.
     *
     * @param templateSql   the template SQL code
     * @param parameterType the type of parameter objects, usually null for SQL queries ("SELECT" statements). Must be
     *                      either Java Bean type or must be-a or extend the <code>java.util.Map.class</code>
     *                      interface.
     * @param resultType    the type of result objects, usually null for SQL updates ("UPDATE" or "DELETE" statements).
     *                      Must be either Java Bean type or must be-a or extend the <code>java.util.Map.class</code>
     *                      interface.
     */
    public Template(String templateSql, Class parameterType, Class resultType) {
        final StringTokenizer st = new StringTokenizer(templateSql, " \t\n\r,()", false);
        final List paramNameList = new ArrayList();
        final List resultNameList = new ArrayList();
        boolean asSeen = false;
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (!asSeen) {
                if (token.equalsIgnoreCase("AS")) {
                    asSeen = true;
                } else if (token.startsWith("${") && token.endsWith("}")) {
                    paramNameList.add(token);
                }
            } else {
                resultNameList.add(token);
                asSeen = false;
            }
        }
        if (paramNameList.size() > 0) {
            final StringBuffer sb = new StringBuffer(templateSql);
            for (int i = 0; i < paramNameList.size(); i++) {
                final String token = (String) paramNameList.get(i);
                final int index = sb.indexOf(token);
                sb.replace(index, index + token.length(), "?");
                paramNameList.set(i, token.substring(2, token.length() - 1));
            }
            templateSql = sb.toString();
        }

        final ValueConverter defaultValueConverter = new DefaultValueConverter();

        this.sql = templateSql;
        this.parameterType = parameterType;
        this.resultType = resultType;
        this.resultValueProperty = isValueType(resultType) ? new ValueProperty(resultType) : null;
        this.parameterProperties = createProperties(paramNameList, parameterType, true);
        this.resultProperties = createProperties(resultNameList, resultType, true);
        this.javaToJdbcValueConverterMap = null;
        this.jdbcToJavaValueConverterMap = null;
        this.defaultJavaToJdbcValueConverter = defaultValueConverter;
        this.defaultJdbcToJavaValueConverter = defaultValueConverter;
    }

    /**
     * Returns the underlying SQL code as used for statement execution. This is NOT the template SQL passed into the
     * constructor.
     *
     * @return the underlying SQL, never null
     */
    public String getSql() {
        return sql;
    }

    /**
     * Gets the parameter type.
     *
     * @return the parameter type or null
     */
    public Class getParameterType() {
        return parameterType;
    }

    /**
     * Gets the result type.
     *
     * @return the result type or null
     */
    public Class getResultType() {
        return resultType;
    }

    /**
     * Returns the properties of the parameter type.
     *
     * @return the properties of the parameter type or null if a parameter type is not specified
     */
    public Property[] getParameterProperties() {
        return parameterProperties;
    }

    /**
     * Returns the properties of the result type.
     *
     * @return the properties of the result type or null if a result type is not specified
     */
    public Property[] getResultProperties() {
        return resultProperties;
    }

    public void addJavaToJdbcValueConverter(String name, JavaToJdbcValueConverter converter) {
        if (javaToJdbcValueConverterMap == null) {
            javaToJdbcValueConverterMap = new HashMap();
        }
        javaToJdbcValueConverterMap.put(name, converter);
    }

    public void removeJavaToJdbcValueConverter(String name) {
        if (javaToJdbcValueConverterMap != null) {
            javaToJdbcValueConverterMap.remove(name);
            if (javaToJdbcValueConverterMap.isEmpty()) {
                javaToJdbcValueConverterMap = null;
            }
        }
    }

    public void addJdbcToJavaValueConverter(String name, JdbcToJavaValueConverter converter) {
        if (jdbcToJavaValueConverterMap == null) {
            jdbcToJavaValueConverterMap = new HashMap();
        }
        jdbcToJavaValueConverterMap.put(name, converter);
    }

    public void removeJdbcToJavaValueConverter(String name) {
        if (jdbcToJavaValueConverterMap != null) {
            jdbcToJavaValueConverterMap.remove(name);
            if (jdbcToJavaValueConverterMap.isEmpty()) {
                jdbcToJavaValueConverterMap = null;
            }
        }
    }

    public Object executeQueryForObject(Connection connection, Object parameterObject) throws SQLException {
        final PreparedStatement stmt = prepareStatement(connection, parameterObject);
        try {
            final ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    return createOutputObject(rs);
                }

                // @todo 2 ok/** fail if more than one result available?
                return null;
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
    }

    /**
     * return the results of the query represented by this template as a list.
     * Note: You should rather provide the list yourself and call executeQueryForCollection. This
     * also enables you to provide a preallocated list based on the number of results that you expect
     * or provide a Set if sorted or unique results are expected.
     * This method is not (yet) set to deprecated, because up until executeQueryForCollection has been
     * introduced, this was <i>the</i> method to retrieve a result and is very widely used.
     * @param connection
     * @param parameterObject
     * @return
     * @throws SQLException
     */
    
    public List executeQueryForList(Connection connection, Object parameterObject) throws SQLException {
    	List result = new ArrayList();
        executeQueryForCollection(connection, parameterObject, result);
        return result;
    }

    /**
     * return the results of the query represented by this template as a list.
     * Note: You should rather provide the list yourself and call executeQueryForCollection. This
     * also enables you to provide a preallocated list based on the number of results that you expect
     * or provide a Set if sorted or unique results are expected.
     * This method is not (yet) set to deprecated, because up until executeQueryForCollection has been
     * introduced, this was <i>the</i> method to retrieve a result and is very widely used.
     * @param connection
     * @param parameterObject
     * @return
     * @throws SQLException
     */
    public List executeQueryForList(Connection connection, Object parameterObject, List list) throws SQLException {
        if (list == null) {
            list = new ArrayList();
        }
        executeQueryForCollection(connection, parameterObject, list);
        return list;
    }

    /**
     * add the results of the query to the collection passed as input parameter.
	 * As this method provides a possibility to specify the type of the result collection (by simply
	 * passing it into this method) this is the preferred way to retrieve a result set 
	 * from a database.
     * @param connection
     * @param parameterObject
     * @return
     * @throws SQLException
     */
    
    public void executeQueryForCollection(Connection connection, Object parameterObject, Collection resultContainer)
			throws SQLException {
		final PreparedStatement stmt = prepareStatement(connection, parameterObject);
        try {
            final ResultSet rs = stmt.executeQuery();
            try {
                while (rs.next()) {
                    final Object outputObject = createOutputObject(rs);
                    resultContainer.add(outputObject);
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
	}

    public Integer executeCount(Connection connection, Object parameterObject) throws SQLException {
        final Integer count;
        final PreparedStatement stmt = prepareStatement(connection, parameterObject);
        try {
            final ResultSet rs = stmt.executeQuery();
            try {
                rs.next();
                count = new Integer(rs.getInt(1));
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }
        return count;
    }


    /**
     * Executes an update statement. The method calls the {@link #prepareStatement} method to obtain the prepared
     * statement. Then  <code>executeUpdate</code> and <code>close</code> are called on this statement.
     *
     * @param connection      the database connection, must not be null
     * @param parameterObject the parameter object providing the property values, can be null if this template does not
     *                        have parameters
     *
     * @return a statement and database specific return value
     *
     * @throws SQLException
     */
    public int executeUpdate(Connection connection, Object parameterObject) throws SQLException {
        final PreparedStatement stmt = prepareStatement(connection, parameterObject);
        try {
            return stmt.executeUpdate();
        } finally {
            stmt.close();
        }
    }

    /**
     * Creates a prepared statement for the given connection and parameter object. The provided parameter object
     * provides the parameter values for the statement via its properties.
     *
     * @param connection      the database connection, must not be null
     * @param parameterObject the parameter object providing the property values, can be null if this template does not
     *                        have parameters
     *
     * @return a prepared statement, never null
     *
     * @throws SQLException if a database error occurs
     */
    public PreparedStatement prepareStatement(Connection connection, Object parameterObject) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        if (parameterProperties != null) {
            if (parameterObject == null) {
                throw new IllegalArgumentException("parameterObject is null");
            }
            if (parameterObject != null && !parameterType.isAssignableFrom(parameterObject.getClass())) {
                throw new IllegalArgumentException("parameterObject is not a " + parameterType.getName());
            }
            for (int i = 0; i < parameterProperties.length; i++) {
                final Property property = parameterProperties[i];
                final Object javaValue = property.getValue(parameterObject);
                final Object jdbcValue = convertJavaToJdbcValue(property, javaValue);
                stmt.setObject(i + 1, jdbcValue);
            }
        }
        return stmt;
    }

    private Object createOutputObject(ResultSet rs) throws SQLException {
        if (isValueType(resultType)) {
            final Property property = resultValueProperty;
            final Object jdbcValue;
            if (resultProperties != null) {
                jdbcValue = rs.getObject(property.getName());
            } else {
                jdbcValue = rs.getObject(1);
            }
            return convertJdbcToJavaValue(property, jdbcValue);
        } else {
            final Object resultObject;
            if (Map.class.isAssignableFrom(resultType)) {
                resultObject = new HashMap();
            } else {
                try {
                    resultObject = resultType.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            for (int i = 0; i < resultProperties.length; i++) {
                final Property property = resultProperties[i];
                final Object jdbcValue = rs.getObject(property.getName());
                final Object javaValue;
                try {
                    javaValue = convertJdbcToJavaValue(property, jdbcValue);
                } catch (RuntimeException e) {
                    throw new RuntimeException(e.getClass().getName() + " during Conversion of property '" +
                                               property.getName() + "' of object '" + resultObject + "': " + e.getMessage(),
                                               e);
                }
                property.setValue(resultObject, javaValue);
            }
            return resultObject;
        }
    }

    private Object convertJavaToJdbcValue(final Property property, final Object javaValue) {
        JavaToJdbcValueConverter javaToJdbcValueConverter = getJavaToJdbcValueConverter(property);
        return javaToJdbcValueConverter.convertJavaToJdbcValue(property, javaValue);
    }

    /**
     * @param property
     *
     * @return never null
     */
    private JavaToJdbcValueConverter getJavaToJdbcValueConverter(final Property property) {
        JavaToJdbcValueConverter javaToJdbcValueConverter = null;
        if (javaToJdbcValueConverterMap != null) {
            javaToJdbcValueConverter = (JavaToJdbcValueConverter) javaToJdbcValueConverterMap.get(property.getName());
        }
        if (javaToJdbcValueConverter == null) {
            javaToJdbcValueConverter = defaultJavaToJdbcValueConverter;
        }
        if (javaToJdbcValueConverter == null) {
            throw new IllegalStateException("no default javaToJdbcValueConverter available");
        }
        return javaToJdbcValueConverter;
    }

    private Object convertJdbcToJavaValue(final Property property, final Object jdbcValue) {
        JdbcToJavaValueConverter jdbcToJavaValueConverter = getJdbcToJavaValueConverter(property);
        return jdbcToJavaValueConverter.convertJdbcToJavaValue(property, jdbcValue);
    }

    /**
     * @param property
     *
     * @return never null
     */
    private JdbcToJavaValueConverter getJdbcToJavaValueConverter(final Property property) {
        JdbcToJavaValueConverter jdbcToJavaValueConverter = null;
        if (jdbcToJavaValueConverterMap != null) {
            jdbcToJavaValueConverter = (JdbcToJavaValueConverter) jdbcToJavaValueConverterMap.get(property.getName());
        }
        if (jdbcToJavaValueConverter == null) {
            jdbcToJavaValueConverter = defaultJdbcToJavaValueConverter;
        }
        if (jdbcToJavaValueConverter == null) {
            throw new IllegalStateException("no default jdbcToJavaValueConverter available");
        }
        return jdbcToJavaValueConverter;
    }


    private static Property[] createProperties(final List nameList, final Class beanType, boolean strict) {
        if (nameList == null || beanType == null) {
            return null;
        }
        ArrayList propertyList = new ArrayList();
        if (isValueType(beanType)) {
            for (int i = 0; i < nameList.size(); i++) {
                final String propertyName = (String) nameList.get(i);
                if (propertyName.equalsIgnoreCase(ValueProperty.NAME)) {
                    final Property property = new ValueProperty(beanType);
                    propertyList.add(property);
                } else if (strict) {
                    throw new IllegalArgumentException("parameter property '" + ValueProperty.NAME + "' expected but found '" +
                                                       propertyName + "'");
                }
            }
        } else {
            for (int i = 0; i < nameList.size(); i++) {
                final String propertyName = (String) nameList.get(i);
                try {
                    final Property property = PropertyParser.parseProperty(beanType, propertyName);
                    propertyList.add(property);
                } catch (ParseException e) {
                    if (strict) {
                        throw new RuntimeException(
                                "failed to parse parameter property '" + propertyName + "' for class '" + beanType.getName() + "'",
                                e);
                    }
                } catch (PropertyNotFoundException e) {
                    if (strict) {
                        throw new RuntimeException(
                                "parameter property '" + propertyName + "' not found in class '" + beanType.getName() + "'",
                                e);
                    }
                }
            }
        }
        if (propertyList.size() == 0) {
            return null;
        }
        Property[] properties = new Property[propertyList.size()];
        propertyList.toArray(properties);
        return properties;
    }

    private static boolean isValueType(final Class beanType) {
        if (beanType == null) {
            return false;
        }
        return beanType.isPrimitive() ||
               java.lang.Boolean.class.isAssignableFrom(beanType) ||
               java.lang.Character.class.isAssignableFrom(beanType) ||
               java.lang.Number.class.isAssignableFrom(beanType) ||
               java.lang.String.class.isAssignableFrom(beanType) ||
               java.util.Date.class.isAssignableFrom(beanType);
    }

    private static class ValueProperty implements Property {

        private static final String NAME = "value";
        private final Class type;

        public String getTreeAsString() {
            return "ValueProperty['" + NAME + "'," + type + "]";
        }

        public ValueProperty(Class type) {
            this.type = type;
        }

        public String getName() {
            return NAME;
        }

        public Class getType() {
            return type;
        }

        public Object getValue(Object beanInstance) {
            return beanInstance;
        }

        public void setValue(Object beanInstance, Object value) {
            throw new IllegalStateException("cannot set special property '" + getName() + "'");
        }

        public boolean isAssignable(Object beanInstance) {
            return true;
        }

        public void makeAssignable(Object beanInstance) {
        }
    }

}


