package com.google.mlkit.vision.demo.history;

public class History {
    String tieude;
    String batdau;
    String ketthuc;
    String thoigian;
    String key;
    String ngaythang;


    public String getNgaythang() {
        return ngaythang;
    }

    public void setNgaythang(String ngaythang) {
        this.ngaythang = ngaythang;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTieude() {
        return tieude;
    }

    public void setTieude(String tieude) {
        this.tieude = tieude;
    }

    public String getBatdau() {
        return batdau;
    }

    public void setBatdau(String batdau) {
        this.batdau = batdau;
    }

    public String getKetthuc() {
        return ketthuc;
    }

    public void setKetthuc(String ketthuc) {
        this.ketthuc = ketthuc;
    }

    public String getThoigian() {
        return thoigian;
    }

    public void setThoigian(String thoigian) {
        this.thoigian = thoigian;
    }

    public History(String title, String startDestination, String endDestination, String totalTime, String ngaythang) {
        this.tieude = title;
        this.batdau = startDestination;
        this.ketthuc = endDestination;
        this.thoigian = totalTime;
        this.ngaythang = ngaythang;
    }

    public History() {
    }
}
