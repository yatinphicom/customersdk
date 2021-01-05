package com.payphi.customersdk;

public class UpiModel {
    String name;
    String imageUrl;
    String upiUrl;
    public String getUpiUrl() {
        return upiUrl;
    }

    public void setUpiUrl(String upiUrl) {
        this.upiUrl = upiUrl;
    }
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    String action;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
