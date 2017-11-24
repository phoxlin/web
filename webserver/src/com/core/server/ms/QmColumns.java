package com.core.server.ms;

import com.core.enuts.ColumnType;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class QmColumns implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<QmColumn> cols = new ArrayList();
    private MsInfo msInfo;

    public QmColumns() {
    }

    public void initPageLegends() {
        Element page = DocumentHelper.createElement("page");
        page.addAttribute("columnnumber", "3");
        Element legends = page.addElement("legends");
        Element legend = legends.addElement("legend");
        legend.addAttribute("name", "basic_info");
        legend.addAttribute("title", "基本信息");

        for(int k = 0; k < this.cols.size(); ++k) {
            QmColumn col = (QmColumn)this.cols.get(k);
            Element input = legend.addElement("input");
            input.addAttribute("name", col.getCode());
            input.addAttribute("display", col.getDisplay());
            input.addAttribute("columnType", ("" + col.getType()).toLowerCase());
            if(col.isShow()) {
                input.addAttribute("hidden", "false");
            } else {
                input.addAttribute("hidden", "true");
            }

            input.addAttribute("controlType", col.getControlType());
            if(col.getBindtype() != null && col.getBindtype().length() > 0) {
                input.addAttribute("bindType", col.getBindtype());
                input.addAttribute("bindData", col.getBinddata());
            }

            input.addAttribute("height", "20");
            input.addAttribute("readonly", "false");
            input.addAttribute("unique", "false");
            input.addAttribute("oneLine", "false");
            input.addAttribute("nullable", String.valueOf(col.isNullable()));
            input.addAttribute("edit", "true");
            input.addAttribute("spanColNum", "1");
            input.addAttribute("defaultValue", "");
            input.addAttribute("sort", String.valueOf(k + 1));
        }

        this.msInfo.getDoc().getRootElement().add(page);
    }

    public void addColumn(QmColumn col) {
        this.cols.add(col);
    }

    public List<QmColumn> getCols() {
        return this.cols;
    }

    public ColumnType getColumnType(String columnname) {
        int i = 0;

        for(int l = this.cols.size(); i < l; ++i) {
            QmColumn qm = (QmColumn)this.cols.get(i);
            if(qm.getCode().equalsIgnoreCase(columnname)) {
                return qm.getType();
            }
        }

        return ColumnType.STRING;
    }

    public QmColumn getColumn(String columnname) {
        int i = 0;

        for(int l = this.cols.size(); i < l; ++i) {
            QmColumn qm = (QmColumn)this.cols.get(i);
            if(qm.getCode().equalsIgnoreCase(columnname)) {
                return qm;
            }
        }

        return null;
    }

    public MsInfo getMsInfo() {
        return this.msInfo;
    }

    public void setMsInfo(MsInfo msInfo) {
        this.msInfo = msInfo;
    }
}
