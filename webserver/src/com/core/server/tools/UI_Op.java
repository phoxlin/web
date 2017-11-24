package com.core.server.tools;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class UI_Op {
    private String style;
    private String props;

    public UI_Op(String style, String props) {
        this.style = style;
        this.props = props;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getProps() {
        return this.props;
    }

    public void setProps(String props) {
        this.props = props;
    }
}
