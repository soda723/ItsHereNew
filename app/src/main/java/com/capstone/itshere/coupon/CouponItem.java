package com.capstone.itshere.coupon;

public class CouponItem {
    private String name;
    private String date;
    private String dday;
    private String idNum;

    public CouponItem(String name, String date, String dday, String idNum) {
        this.name = name;
        this.date = date;
        this.dday = dday;
        this.idNum = idNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDday() {
        return dday;
    }

    public void setDday(String dday) {
        this.dday = dday;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }
}
