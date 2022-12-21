package com.capstone.itshere.accountBook;

public class StatsItem {
    private float percentage;
    private String category;
    private int amount;

    public StatsItem(float percentage, String category, int amount) {
        this.percentage = percentage;
        this.category = category;
        this.amount = amount;
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
}
