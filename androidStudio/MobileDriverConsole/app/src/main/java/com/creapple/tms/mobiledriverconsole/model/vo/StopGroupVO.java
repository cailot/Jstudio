package com.creapple.tms.mobiledriverconsole.model.vo;

import java.io.Serializable;

/**
 * This class is just for listing Stop Group - fareStops - in Route.
 * It doesn't need to bring vehicle list all together so will eliminate vehicle property
 * Created by jinseo on 2016. 6. 30..
 */
public class StopGroupVO implements Serializable{
    private String name;
    private int index;

    public StopGroupVO(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StopGroupVO{" +
                "name='" + name + '\'' +
                ", index=" + index +
                '}';
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public StopGroupVO() {

    }
}