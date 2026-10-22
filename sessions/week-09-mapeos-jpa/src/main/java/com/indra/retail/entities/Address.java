package com.indra.retail.entities;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Address {

    private String street;
    private String city;
    private String region;
    private String postalCode;
    private String country;

    protected Address() {
    }

    public Address(String street, String city, String region, String postalCode, String country) {
        this.street = Objects.requireNonNull(street, "street is required");
        this.city = Objects.requireNonNull(city, "city is required");
        this.region = Objects.requireNonNull(region, "region is required");
        this.postalCode = Objects.requireNonNull(postalCode, "postalCode is required");
        this.country = Objects.requireNonNull(country, "country is required");
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }
}