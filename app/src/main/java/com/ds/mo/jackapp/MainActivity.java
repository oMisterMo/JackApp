package com.ds.mo.jackapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public static final char POUND = '£';
    private static double balance = 0d;
    private DecimalFormat dc;

    private ArrayList<Double> totalAdded;
    private ArrayList<Double> totalWithdrawn;
    private double[] historyAdded;
    private double[] historyWithdrawn;

    private DatePickerDialog datePickerDialog;
    private int day, month, year;
    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //My decimal format
        dc = new DecimalFormat("00.00");
        totalAdded = new ArrayList<>();
        totalWithdrawn = new ArrayList<>();
        initHistoryAdded();
        initHistoryWithdrawn();
        parseXML();

        updateBalance();
        updateTextView();

        //date===================================
        date = Calendar.getInstance();
        day = date.get(Calendar.DAY_OF_MONTH);
        month = date.get(Calendar.MONTH);
        year = date.get(Calendar.YEAR);
        System.out.printf("MO: %d, %d, %d%n", day, month, year);
        datePickerDialog = new DatePickerDialog(
                this, null, year, month, day);
        //There's also set min date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
        datePickerDialog.hide();
        //======================================

        //ADD money
        FloatingActionButton topUp = findViewById(R.id.top_up);
        topUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoney();
            }
        });

        //SUBTRACT money
        FloatingActionButton subtract = findViewById(R.id.subtract);
        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractMoney();
            }
        });
    }

    private void parseXML() {
        try {
            XmlPullParserFactory xmlPullParser;
            xmlPullParser = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParser.newPullParser();
            InputStream is = getAssets().open("output.xml");
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            processResult(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Session> sessions = new ArrayList<>();
        int eventType = parser.getEventType();
        Session session = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String element = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    /*  START TAG DETECTED */
                    element = parser.getName();     //Get name of parent node
                    if (element.equals("Session")) {
                        System.out.println("Creating a new session object....");
                        session = new Session();
                        System.out.println("Adding the session to sessions...");
                        sessions.add(session);
                    } else if (session != null) {
                        if (element.equals("Date")) {
//                            String date = "";
//                            session.date = parser.nextText();
                        } else if (element.equals("Day")) {
                            session.day = parser.nextText();
                        } else if (element.equals("Month")) {
                            session.month = parser.nextText();
                        } else if (element.equals("Year")) {
                            session.year = parser.nextText();
                        } else if (element.equals("Location")) {
                            session.locatiion = parser.nextText();
                        } else if (element.equals("Price")) {
                            session.price = parser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    //empty
                    break;
                case XmlPullParser.END_TAG:
                    //empty
            }
            eventType = parser.next();
        }
        printSessions(sessions);
    }

    private void printSessions(ArrayList<Session> sessions) {
        StringBuilder sb = new StringBuilder();

        for (Session session : sessions) {
            sb.append(session.day).append("/");
            sb.append(session.month).append("/");
            sb.append(session.year).append("\n");
            sb.append(session.locatiion).append("\n");
            sb.append(session.price).append("\n\n");
        }
        Log.d("MainActivity", sb.toString());
    }

    private void initHistoryAdded() {
        historyAdded = new double[2];
        historyAdded[0] = 20d;
        historyAdded[1] = 40d;
    }

    private void initHistoryWithdrawn() {
        historyWithdrawn = new double[4];
        historyWithdrawn[0] = 2.50d;
        historyWithdrawn[1] = 5d;
        historyWithdrawn[2] = 6.66d;
        historyWithdrawn[3] = 2d;
    }

    private void updateBalance() {
        for (double price : historyAdded) {
            balance += price;
        }
        for (double price : historyWithdrawn) {
            balance -= price;
        }
    }

    private void addMoney() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//        alertDialogBuilder.setTitle("Enter Price:");

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View inputPrice = layoutInflater.inflate(R.layout.input_add_money, null);
        alertDialogBuilder.setView(inputPrice);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        //Save Button
        Button saveData = inputPrice.findViewById(R.id.button_save);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Save pressed...");
                EditText editText = inputPrice.findViewById(R.id.edit_text);
//                editText.addTextChangedListener(new DecimalFilter(editText, activity));
                String inputNum = editText.getText().toString();
                if (TextUtils.isEmpty(inputNum)) {
                    Snackbar.make(v, "Price can not be empty", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                } else {
                    Log.d("MainActivity", "Price input: " + inputNum);
                    //Ensure that input is a number

                    //Convert String to double // TODO: 16/09/2019 should use BIG INTEGER
                    double money = Double.parseDouble(inputNum);
                    adjustBalance(money);
                    updateTextView();


                    //we done, hide the pop up
                    alertDialog.hide();
                }
            }
        });

        Button cancelButton = inputPrice.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Cancel pressed...");
                alertDialog.cancel();
            }
        });
    }

    private void subtractMoney() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        final View inputPrice = layoutInflater.inflate(R.layout.input_squash_games, null);
        alertDialogBuilder.setView(inputPrice);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        //Handle location field
        EditText locationField = inputPrice.findViewById(R.id.location_edit_text);
        locationField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        //Handle date field
        EditText dateField = inputPrice.findViewById(R.id.date_edit_text);
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d("MainActivity", "**********HAS FOCUS***********");
                    datePickerDialog.show();
                }
            }
        });


        //Save Button
        Button saveData = inputPrice.findViewById(R.id.button_save);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Save pressed...");

                //Handle location field -------------------------------------------> 1
                EditText locationEditText = inputPrice.findViewById(R.id.location_edit_text);
                String inputLocation = locationEditText.getText().toString();
                if (TextUtils.isEmpty(inputLocation)) {
                    //default
                    inputLocation = "Sobell";
                    Log.d("MainActivity", "***default***");
                    Log.d("MainActivity", "Location is: " + inputLocation);
                } else {
                    // TODO: 16/09/2019 make first letter capital
                    Log.d("MainActivity", "Location is: " + inputLocation);

                }

                //Handle date field -----------------------------------------------> 2
                EditText dateEditText = inputPrice.findViewById(R.id.date_edit_text);
                String inputDate = dateEditText.getText().toString();
                if (TextUtils.isEmpty(inputDate)) {
                    Log.d("MainActivity", "***default***");
                    Log.d("MainActivity", inputDate);
                    inputDate = day + "/" + (month + 1) + "/" + year;
                }
                Log.d("MainActivity", inputDate);


                //Handle price field ----------------------------------------------> 3
                EditText priceEditText = inputPrice.findViewById(R.id.price_edit_text);
                String inputPrice = priceEditText.getText().toString();
                if (TextUtils.isEmpty(inputPrice)) {
                    Snackbar.make(v, "Price can not be empty", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                } else {
                    Log.d("MainActivity", "Price input: " + inputPrice);
                    //Ensure that input is a number

                    //Convert String to double // TODO: 16/09/2019 should use BIG INTEGER
                    double money = Double.parseDouble(inputPrice);
                    adjustBalance(-money);
                    updateTextView();


                    //we done, hide the pop up
                    alertDialog.hide();
                }
            }
        });

        Button cancelButton = inputPrice.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Cancel pressed...");
                alertDialog.cancel();
            }
        });
    }

    private void updateTextView() {
        //Convert input to String
        String s = POUND + dc.format(balance);

        //Update View
        TextView textView = findViewById(R.id.total);
        textView.setText(s);
    }

    private void adjustBalance(double money) {
        balance += money;
    }

    private void clearTotal() {
        //Reset count
        balance = 0;
        //Do this later
        TextView textView = findViewById(R.id.total);
        String s = POUND + dc.format(balance);
        textView.setText(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_history) {
            Log.d("MainActivity", "HISTORY");
            return true;
        }
        if (id == R.id.action_clear) {
            Log.d("MainActivity", "CLEAR");
            clearTotal();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
