package com.ds.mo.jackapp;

import androidx.annotation.NonNull;

public class Transaction implements XMLData {

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

    @Override
    public int[] getDate() {
        // TODO: 20/09/2019 Maybe store date as an integer local variable
        return new int[]{Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year)};
    }

    @Override
    public int compareTo(XMLData o) {
        //-1 (less than), 0 (equal), 1 (greater)
        int d = Integer.parseInt(day);
        int m = Integer.parseInt(month);
        int y = Integer.parseInt(year);

        // TODO: 20/09/2019 Refactor this beast
        // TODO: 20/09/2019 Convert to streams (needs minimum API level 24)
        int[] other = o.getDate();
        if (y < other[2]) {
            return -1;
        } else if (y > other[2]) {
            return 1;
        } else {
            //years the same (check months)
            if (m < other[1]) {
                return -1;
            } else if (m > other[1]) {
                return 1;
            } else {
                //months the same (check day)
                if (d < other[0]) {
                    return -1;
                } else if (d > other[0]) {
                    return 1;
                }
            }
        }
        System.out.println("Both the same");
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        String s = day + "/" + month + "/" + year + " " + name + " " + price;
        return s;
    }
}
