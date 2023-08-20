package com.ab.sabjiwala.model;

public class vegitable_detail {
    String name,perkg,quantity,total;

    public vegitable_detail(String name, String perkg, String quantity,String total) {
        this.name = name;
        this.perkg = perkg;
        this.quantity = quantity;
        this.total=total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerkg() {
        return perkg;
    }

    public void setPerkg(String perkg) {
        this.perkg = perkg;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
