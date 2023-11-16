package com.bc.util.sql;

import junit.framework.TestCase;
import org.apache.commons.dbcp.BasicDataSource;

import java.util.ArrayList;


public class SqlUtilsTest extends TestCase {

    public void testAppendCriteriaToSqlBuffer() {
        ArrayList criteria = new ArrayList();
        StringBuffer sql = new StringBuffer();

        SqlUtils.appendCriteriaToSqlBuffer(criteria, sql);
        assertEquals("", sql.toString());

        sql = new StringBuffer();
        criteria.add("SourceId = ${sourceId}");
        SqlUtils.appendCriteriaToSqlBuffer(criteria, sql);
        assertEquals(" WHERE SourceId = ${sourceId}\n", sql.toString());

        sql = new StringBuffer();
        criteria.add("ProductType = ${productType}");
        SqlUtils.appendCriteriaToSqlBuffer(criteria, sql);
        assertEquals(" WHERE SourceId = ${sourceId}\n" +
                " AND ProductType = ${productType}\n", sql.toString());
    }

    public void testCreateDatasource() {
        final String driver = "driver";
        final String url = "url";
        final String user = "user";
        final String password = "password";
        final DataSourceConfig config = new DataSourceConfig(driver, url, user, password);

        final BasicDataSource source = SqlUtils.createDatasource(config);
        assertNotNull(source);
        assertEquals(driver, source.getDriverClassName());
        assertEquals(url, source.getUrl());
        assertEquals(user, source.getUsername());
        assertEquals(password, source.getPassword());
    }
}
