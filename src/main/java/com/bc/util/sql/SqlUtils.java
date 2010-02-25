package com.bc.util.sql;

import org.apache.commons.dbcp.BasicDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class SqlUtils {

    public static void appendCriteriaToSqlBuffer(ArrayList criteria, StringBuffer sql) {
        if (criteria.size() > 0) {
            sql.append(" WHERE ");
            for (int i = 0; i < criteria.size(); i++) {
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append(criteria.get(i));
                sql.append('\n');
            }
        }
    }

    // @todo 2 tb/tb add test and javadoc
    public static void appendWildcardAlternatives(String columnName, String columnValuePattern, String parameterName,
                                                  Map parameterMap, List criteriaList) {
        appendWildcardAlternatives(columnName, columnValuePattern, parameterName, parameterMap, criteriaList, true);
    }

    // @todo 2 tb/tb add test and javadoc
    public static void appendWildcardAlternatives(String columnName, String columnValuePattern, String parameterName,
                                                  Map parameterMap, List criteriaList, boolean underscoreSensitive) {
        final StringTokenizer st = new StringTokenizer(columnValuePattern, ";");
        final StringBuffer exprBuffer = new StringBuffer(CAPACITY);
        int index = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (token.length() > 0) {
                token = token.replace('*', '%');

                if (underscoreSensitive) {
                    // following two lines are to replace "_" with "\_"
                    // Undersore is in SQL a wildcard for one character
                    token = token.replaceAll("_", "*_");
                    token = token.replace('*', '\\');
                }

                if (index > 0) {
                    parameterName += "_" + (index + 1);
                    exprBuffer.append(" OR ");
                }
                exprBuffer.append(columnName);
                if (token.indexOf("%") != -1 || token.indexOf("\\_") != -1) {
                    exprBuffer.append(" LIKE ${");
                } else {
                    exprBuffer.append(" = ${");
                }
                exprBuffer.append(parameterName);
                exprBuffer.append('}');
                index++;

                parameterMap.put(parameterName, token);
            }
        }
        if (index > 1) {
            exprBuffer.insert(0, '(');
            exprBuffer.append(')');
        }
        final String expression = exprBuffer.toString();
        if (expression.length() > 0) {
            criteriaList.add(expression);
        }
    }

    public static BasicDataSource createDatasource(DataSourceConfig dataSourceConfig) {
        final BasicDataSource result = new BasicDataSource();

        result.setDriverClassName(dataSourceConfig.getDriver());
        result.setUrl(dataSourceConfig.getUrl());
        result.setUsername(dataSourceConfig.getUsername());
        result.setPassword(dataSourceConfig.getPassword());

        return result;
    }

    ///////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ///////////////////////////////////////////////////////////////////////////

    private static final int CAPACITY = 256;

}
