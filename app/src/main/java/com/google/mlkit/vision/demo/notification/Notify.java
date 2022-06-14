package com.google.mlkit.vision.demo.notification;

public class Notify {
    String tieude;
    String chitiet;
    String mota;
    String thoigian;


    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public String getTieude() {
        return tieude;
    }

    public void setTieude(String tieude) {
        this.tieude = tieude;
    }

    public String getChitiet() {
        return chitiet;
    }

    public void setChitiet(String chitiet) {
        this.chitiet = chitiet;
    }



    public String getThoigian() {
        return thoigian;
    }

    public void setThoigian(String thoigian) {
        this.thoigian = thoigian;
    }

    public Notify(String tieude, String mota, String thoigian, String chitiet) {
        this.tieude = tieude;
        this.mota = mota;
        this.thoigian = thoigian;
        this.chitiet = chitiet;
    }

    public Notify() {
    }
}
