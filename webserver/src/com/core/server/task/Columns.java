package com.core.server.task;

import com.core.enuts.ColumnType;
import com.core.server.db.Column;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/27.
 */
public class Columns implements Serializable{
    private static final long serialVersionUID = 1L;
    private List<Column> cols = new ArrayList();

    public Columns() {
    }

    public void addColumn(Column col) {
        this.cols.add(col);
    }

    public List<Column> getCols() {
        return this.cols;
    }

    public ColumnType getColumnType(String columnname) {
        int i = 0;

        for(int l = this.cols.size(); i < l; ++i) {
            Column qm = (Column)this.cols.get(i);
            if(qm.getName().equalsIgnoreCase(columnname)) {
                return qm.getType();
            }
        }

        return ColumnType.STRING;
    }

    public Column getColumn(String columnname) {
        int i = 0;

        for(int l = this.cols.size(); i < l; ++i) {
            Column qm = (Column)this.cols.get(i);
            if(qm.getName().equalsIgnoreCase(columnname)) {
                return qm;
            }
        }

        return null;
    }
}
