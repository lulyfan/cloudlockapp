package com.ut.lib2_smallest.entity;

/**
 * author : zhouyubin
 * time   : 2018/12/08
 * desc   :
 * version: 1.0
 */
public class Dimen {
    private String attribute;
    private float value;
    private String unit;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Dimen{" +
                "attribute='" + attribute + '\'' +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                '}';
    }
}
