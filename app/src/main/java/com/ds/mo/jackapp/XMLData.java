package com.ds.mo.jackapp;

public interface XMLData extends Comparable<XMLData> {

    int[] getDate();

    @Override
    int compareTo(XMLData o);
}
