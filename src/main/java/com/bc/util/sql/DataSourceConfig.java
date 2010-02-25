/*
 * $Id: DataSourceConfig.java,v 1.2 2007-12-19 17:17:05 tom Exp $
 *
 * Copyright (c) 2003 Brockmann Consult GmbH. All right reserved.
 * http://www.brockmann-consult.de
 */
package com.bc.util.sql;

public class DataSourceConfig {

    public DataSourceConfig() {
    }

    public DataSourceConfig(String driver, String url, String user, String password) {
        this.driver = driver;
        this.url = url;
        this.username = user;
        this.password = password;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////// END OF PUBLIC
    ////////////////////////////////////////////////////////////////////////////////

    private String driver;
    private String url;
    private String username;
    private String password;
}
