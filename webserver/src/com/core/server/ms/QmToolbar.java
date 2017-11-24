package com.core.server.ms;

import com.core.server.tools.Utils;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class QmToolbar implements Serializable {
    private static final long serialVersionUID = 1L;
    private String _class;
    private String text;
    private String js;
    private String name;
    private QmAlign align;
    private String[] roles;
    private String[] visibleCodes;
    private List<QmToolbar> subItems = new ArrayList();

    public QmToolbar() {
    }

    public JSONObject toJson() {
        JSONObject o = new JSONObject();
        o.put("_class", this.get_class());
        o.put("text", this.getText());
        o.put("js", this.getJs());
        o.put("name", this.getName());
        o.put("align", this.getAlign());
        o.put("roles", Utils.getListString(this.getRoles()));
        o.put("visibleCodes", Utils.getListString(this.getVisibleCodes()));
        return o;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getJs() {
        return this.js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public String getName() {
        return this.name;
    }

    public String get_class() {
        return this._class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QmAlign getAlign() {
        return this.align;
    }

    public void setAlign(QmAlign align) {
        this.align = align;
    }

    public String[] getRoles() {
        return this.roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String[] getVisibleCodes() {
        return this.visibleCodes;
    }

    public void setVisibleCodes(String[] visibleCodes) {
        this.visibleCodes = visibleCodes;
    }

    public List<QmToolbar> getSubItems() {
        return this.subItems;
    }
}
