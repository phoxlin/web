package com.core.server.ms;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class Param {
    private String name;
    private CompareType compare;
    private String value;

    public Param(String name, CompareType comp, String value) {
        this.name = name;
        this.compare = comp;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompare() {
        return this.compare == CompareType.大于?">":(this.compare == CompareType.大于等于?">=":(this.compare == CompareType.小于?"<":(this.compare == CompareType.小于等于?"<=":"=")));
    }

    public void setCompare(CompareType compare) {
        this.compare = compare;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
