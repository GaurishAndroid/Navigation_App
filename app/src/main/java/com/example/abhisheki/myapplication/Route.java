package com.example.abhisheki.myapplication;

/**
 * Created by ABHISHEKI on 31-01-2017.
 */

public class Route {

    private String Id;
    private String Origin;
    private String Destination;
    private Double OriginLat;
    private Double OriginLong;
    private Double DestLat;
    private Double DestLong;
    private String Route;

    public Route(){}

    public void setOrigin(String origin) {  Origin = origin;   }
    public void setDestination(String destination) {  Destination = destination;    }
    public void setOriginLat(Double originLat) {   OriginLat = originLat;    }
    public void setOriginLong(Double originLong) { OriginLong = originLong; }
    public void setDestLat(Double destLat) { DestLat = destLat; }
    public void setDestLong(Double destLong) { DestLong = destLong; }
    public void setRoute(String route) {  Route = route; }


    public String getId() { return Id; }
    public String getOrigin() { return Origin; }
    public String getDestination() {  return Destination; }
    public Double getOriginLat() {  return OriginLat;  }
    public Double getOriginLong() { return OriginLong; }
    public Double getDestLat() { return DestLat; }
    public Double getDestLong() { return DestLong; }
    public String getRoute() { return Route; }
}
