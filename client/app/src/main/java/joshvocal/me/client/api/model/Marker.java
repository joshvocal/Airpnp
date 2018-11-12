package joshvocal.me.client.api.model;

import java.io.Serializable;

public class Marker implements Serializable {

    private int id;
    private double lat;
    private double lng;

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return "Marker{" +
                "id=" + id +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
