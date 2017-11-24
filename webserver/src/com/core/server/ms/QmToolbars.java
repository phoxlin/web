package com.core.server.ms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class QmToolbars implements Serializable{
    private static final long serialVersionUID = 1L;
    private List<QmToolbar> toolbars = new ArrayList();

    public QmToolbars() {
    }

    public void addQmToolbar(QmToolbar bar) {
        this.toolbars.add(bar);
    }

    public List<QmToolbar> getToolbars() {
        return this.toolbars;
    }
}
