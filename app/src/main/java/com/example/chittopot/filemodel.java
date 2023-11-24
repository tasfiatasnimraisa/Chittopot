package com.example.chittopot;

public class filemodel {
    String title,vurl,catagory;

    public filemodel(String title, String vurl, String catagory) {
        this.title = title;
        this.vurl = vurl;
        this.catagory = catagory;
    }

    public filemodel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVurl() {
        return vurl;
    }

    public void setVurl(String vurl) {
        this.vurl = vurl;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }
}
