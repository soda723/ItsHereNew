package com.capstone.itshere.coupon;

public class CouponItem {
    private String idNum;
    private String contents;
    private String date;
    private String dday;

    public CouponItem(String idNum, String contents, String date, String dday) {
        this.idNum = idNum;
        this.contents = contents;
        this.date = date;
        this.dday = dday;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
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
}
