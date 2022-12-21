package com.capstone.itshere.accountBook;

public class DailyNote {
    private String bigcate;
    private String date;
    private String category;
    private String note;
    private int amount;
    private String idNum;
    private String MONTH;


    public DailyNote(){}

    public DailyNote(String bigcate, String date, String category, String note, int amount, String idNum, String MONTH) {
        this.bigcate = bigcate;
        this.date = date;
        this.category = category;
        this.note = note;
        this.amount = amount;
        this.idNum = idNum;
        this.MONTH = MONTH;
    }

    public String getBigcate() {
        return bigcate;
    }

    public void setBigcate(String bigcate) {
        this.bigcate = bigcate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getMONTH() {
        return MONTH;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }
}
