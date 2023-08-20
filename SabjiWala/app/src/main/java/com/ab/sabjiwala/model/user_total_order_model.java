package com.ab.sabjiwala.model;

public class user_total_order_model {
    String name,method,delevery_status,total;

    public user_total_order_model(){

    }
    public user_total_order_model(String name, String method, String delevery_status, String total) {
        this.name = name;
        this.method = method;
        this.delevery_status = delevery_status;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDelevery_status() {
        return delevery_status;
    }

    public void setDelevery_status(String delevery_status) {
        this.delevery_status = delevery_status;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
