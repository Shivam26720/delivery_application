package com.ab.sabjiwala.model;

public class orderlist_model {
    String title,price,quantity,unit;
    public orderlist_model(){}
    public orderlist_model(String title, String price,String quantity,String unit) {
        this.title = title;
        this.price = price;
        this.quantity=quantity;
        this.unit=unit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
