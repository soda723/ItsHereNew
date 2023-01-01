package com.capstone.itshere.accountBook;

public class StatsItem {
    private float percentage;
    private String category;
    private int amount;
    private String MONTH;

    public StatsItem(float percentage, String category, int amount, String MONTH) {
        this.percentage = percentage;
        this.category = category;
        this.amount = amount;
        this.MONTH = MONTH;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getMONTH() {
        return MONTH;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }
}
