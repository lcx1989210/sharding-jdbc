/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MySqlInsertStatement extends SQLInsertStatement {
    
    private final List<ValuesClause> valuesList = new ArrayList<>();
    
    public ValuesClause getValues() {
        return valuesList.isEmpty() ? null : valuesList.get(0);
    }
    
    public void setValues(final ValuesClause values) {
        if (valuesList.isEmpty()) {
            valuesList.add(values);
        } else {
            valuesList.set(0, values);
        }
    }
    
    @Override
    public String getDbType() {
        return JdbcConstants.MYSQL;
    }
    
    @Override
    protected void acceptInternal(final SQLASTVisitor visitor) {
        if (!(visitor instanceof MySqlASTVisitor)) {
            throw new IllegalArgumentException("not support visitor type : " + visitor.getClass().getName());
        }
        MySqlASTVisitor mySqlASTVisitor = (MySqlASTVisitor) visitor;
        if (mySqlASTVisitor.visit(this)) {
            acceptChild(mySqlASTVisitor, getTableSource());
            acceptChild(mySqlASTVisitor, getColumns());
            acceptChild(mySqlASTVisitor, getValuesList());
            acceptChild(mySqlASTVisitor, getQuery());
        }
        mySqlASTVisitor.endVisit(this);
    }
    
    @Override
    public void output(final StringBuffer buffer) {
        new MySqlOutputVisitor(buffer).visit(this);
    }
}