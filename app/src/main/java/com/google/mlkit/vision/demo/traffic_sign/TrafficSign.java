package com.google.mlkit.vision.demo.traffic_sign;


public class TrafficSign {
    private String nameSign;
    private String numberSign;
    private String description;
    private String imgUrl;

    public String getNameSign() {
        return nameSign;
    }

    public void setNameSign(String nameSign) {
        this.nameSign = nameSign;
    }

    public String getNumberSign() {
        return numberSign;
    }

    public void setNumberSign(String numberSign) {
        this.numberSign = numberSign;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TrafficSign{");
        sb.append("nameSign='").append(nameSign).append('\'');
        sb.append(", numberSign='").append(numberSign).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", imgUrl='").append(imgUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
