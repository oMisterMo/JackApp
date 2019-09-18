package com.ds.mo.jackapp;

public class Transaction {

    public String day, month, year, name, price;

    public Transaction() {

    }

    public Transaction(String day, String month, String year, String name, String price) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.name = name;
        this.price = price;
    }
}
