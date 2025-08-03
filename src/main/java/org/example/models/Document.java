package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Document {
    private String id;
    private String name;
    private String type;

    @JsonProperty("totalAPagar")
    private double totalAPagar;

    @JsonProperty("medioPago")
    private String medioPago;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTotalAPagar() {
        return totalAPagar;
    }

    public void setTotalAPagar(double totalAPagar) {
        this.totalAPagar = totalAPagar;
    }

    public String getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(String medioPago) {
        this.medioPago = medioPago;
    }
}
