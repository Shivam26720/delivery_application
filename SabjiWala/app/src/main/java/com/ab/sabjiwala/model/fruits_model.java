package com.ab.sabjiwala.model;

public class fruits_model {
    String img,title,price,quantity,unit;
    public fruits_model(){}

    public fruits_model(String img, String title, String price,String quantity,String unit) {
        this.img = img;
        this.title = title;
        this.price = price;
        this.quantity=quantity;
        this.unit=unit;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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