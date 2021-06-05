package com.zwang.project1;
import com.google.gson.annotations.SerializedName;

public class  Stock {
    private int id_;
    private String symbol_ = "";
    private String url_ = "";

    private String pictureUrl_ = "https://upload.wikimedia.org/wikipedia/en/thumb/a/a9/Fanshawe_College_Logo_vecotrized.svg/1200px-Fanshawe_College_Logo_vecotrized.svg.png";
    @SerializedName("price")
    private String price_ = "0";
    private String name_ = "";
    private String change_ = "0";

    public int getId_() {
        return id_;
    }

    public void setId_(int id_) {
        this.id_ = id_;
    }
    //public Stock(){}
    public Stock(int id_, String name_, String symbol_, String url_, String pictureUrl_, String price_, String change_) {
        this.id_ = id_;
        this.symbol_ = symbol_;
        this.url_ = url_;
        this.pictureUrl_ = pictureUrl_;
        this.price_ = price_;
        this.name_ = name_;
        this.change_ = change_;
    }

    public String getSymbol_() {
        return symbol_;
    }

    public void setSymbol_(String symbol_) {
        this.symbol_ = symbol_;
    }

    public String getUrl_() {
        return url_;
    }

    public void setUrl_(String url_) {
        this.url_ = url_;
    }

    public String getPictureUrl_() {
        return pictureUrl_;
    }

    public void setPictureUrl_(String pictureUrl_) {
        this.pictureUrl_ = pictureUrl_;
    }

    public String getPrice_() {
        return price_;
    }

    public void setPrice_(String price_) {
        this.price_ = price_;
    }

    public String getName_() {
        return name_;
    }

    public void setName_(String name_) {
        this.name_ = name_;
    }

    public String getChange_() {
        return change_;
    }

    public void setChange_(String change_) {
        this.change_ = change_;
    }
}
