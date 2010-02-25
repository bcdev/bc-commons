/*
 * $Id: QueryCountTransaction.java,v 1.1 2007-02-27 12:45:30 eugen Exp $
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
package com.bc.util.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class QueryCountTransaction extends TemplateTransaction {

    private Integer count;

    public QueryCountTransaction(String templateSql, Object parameterMapOrObject) {
        super(templateSql, Integer.class, parameterMapOrObject);
    }

    public boolean isUpdate() {
        return false;
    }

    public void execute(Connection connection) throws SQLException {
        this.count = getTemplate().executeCount(connection, getParameterObject());
    }

    public int fetchCount() {
        final Integer count = this.count;
        this.count = null;
        return count != null ? count.intValue() : 0;
    }
}
