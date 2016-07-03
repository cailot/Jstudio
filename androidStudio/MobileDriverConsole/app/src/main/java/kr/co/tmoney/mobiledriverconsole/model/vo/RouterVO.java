package kr.co.tmoney.mobiledriverconsole.model.vo;

import java.util.List;

/**
 * Created by jinseo on 2016. 6. 30..
 */
public class RouterVO {
    private String routeId;
    private String name;
    private int sortIndex;
    private List<StopVO> stops;

    public RouterVO() {
    }

    public RouterVO(String name, String routeId, int sortIndex, List<StopVO> stops) {
        this.name = name;
        this.routeId = routeId;
        this.sortIndex = sortIndex;
        this.stops = stops;
    }

    @Override
    public String toString() {
        return "RouterVO{" +
                "name='" + name + '\'' +
                ", routeId='" + routeId + '\'' +
                ", sortIndex=" + sortIndex +
                ", stops=" + stops +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public List<StopVO> getStops() {
        return stops;
    }

    public void setStops(List<StopVO> stops) {
        this.stops = stops;
    }
}