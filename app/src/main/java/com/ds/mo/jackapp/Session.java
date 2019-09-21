package com.ds.mo.jackapp;

import androidx.annotation.NonNull;

public class Session implements XMLData {

    public String day, month, year, location, price;

    public Session() {

    }

    public Session(String day, String month, String year, String location, String price) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.location = location;
        this.price = price;
    }

    @Override
    public int[] getDate() {
        return new int[]{Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year)};
    }

    @Override
    public int compareTo(XMLData o) {
        int d = Integer.parseInt(day);
        int m = Integer.parseInt(month);
        int y = Integer.parseInt(year);

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
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        String s = day + "/" + month + "/" + year + " " + location + " " + price;
        return s;
    }
}
