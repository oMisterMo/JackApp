package com.ds.mo.jackapp;

import android.app.Activity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class DecimalFilter implements TextWatcher {

    int count = -1;
    EditText editText;
    Activity activity;

    public DecimalFilter(EditText editText, Activity activity){
        this.editText = editText;
        this.activity = activity;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //nothing here
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //nothing here
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            String str = editText.getText().toString();
            editText.setOnKeyListener(new View.OnKeyListener() {

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        count--;
                        InputFilter[] fArray = new InputFilter[1];
                        fArray[0] = new InputFilter.LengthFilter(100);//Re sets the maxLength of edittext to 100.
                        editText.setFilters(fArray);
                    }
                    if (count > 2) {
                        Toast.makeText(activity, "Sorry! You cant enter more than two digits after decimal point!", Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });

            char t = str.charAt(s.length() - 1);

            if (t == '.') {
                count = 0;
            }

            if (count >= 0) {
                if (count == 2) {
                    InputFilter[] fArray = new InputFilter[1];
                    fArray[0] = new InputFilter.LengthFilter(s.length());
                    editText.setFilters(fArray); // sets edittext's maxLength to number of digits now entered.

                }
                count++;
            }
        }
    }
}
