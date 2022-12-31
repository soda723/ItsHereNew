package com.capstone.itshere.membership;

public class MBSitem {
    private String ID;
    private String contents;
    private Integer color;
    private String barcode;

    public MBSitem(String ID, String contents, Integer color, String barcode) {
        this.ID = ID;
        this.contents = contents;
        this.color = color;
        this.barcode = barcode;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
