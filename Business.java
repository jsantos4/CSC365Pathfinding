package YelpDataPathfinding;

import java.util.ArrayList;

public class Business {
    private String id;
    private String name;
    private String city;
    private String state;
    private double latitude;
    private double longitude;
    double categoryScore;
    ArrayList<String> categories;


    Business(String id, String name, String city, String state) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        categories = new ArrayList<String>();
    }

    void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    String getCity() {
        return city;
    }

    String getState() {
        return state;
    }

    double getLatitude() {
        return latitude;
    }

    double getLongitude() {
        return longitude;
    }

    public String toString() {
        return getName() + ", " + getCity() + ", " + getState() + ", (Latitude, Longitude): (" + getLatitude() + ", " + getLongitude() + ")";
    }

}
