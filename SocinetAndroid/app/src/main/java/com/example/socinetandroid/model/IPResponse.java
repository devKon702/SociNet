package com.example.socinetandroid.model;

public class IPResponse {
    private String query;
    private String status;
    private String country;
    private String regionName;
    private String city;

    public IPResponse(String query, String status, String country, String regionName, String city) {
        this.query = query;
        this.status = status;
        this.country = country;
        this.regionName = regionName;
        this.city = city;
    }

    public String getQuery() {
        return query;
    }

    public String getStatus() {
        return status;
    }

    public String getCountry() {
        return country;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getCity() {
        return city;
    }
}
