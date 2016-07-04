package kr.co.tmoney.mobiledriverconsole.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class is just for listing Route in TripOff.
 * It doesn't need to bring vehicle list all together so will eliminate vehicle property
 * Created by jinseo on 2016. 6. 30..
 */
//@JsonIgnoreProperties({"vehicles"})
@JsonIgnoreProperties({"routeStop", "vehicles"})
public class RouteVO {
    private String name;
    private int sortIndex;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RouterVO{" +
                "name='" + name + '\'' +
                ", sortIndex=" + sortIndex +
                '}';
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public RouteVO() {

    }
}