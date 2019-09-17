package com.ds.mo.jackapp;

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

    private final String inputXML = "input.xml";
    private final String outputXML = "output.xml";

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
//        initHistoryAdded();           //Temp hardcoded data
//        initHistoryWithdrawn();       //Temp hardcoded datas
        parseOutputXML();
        parseInputXML();

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

    private void parseOutputXML() {
        try {
            XmlPullParserFactory xmlPullParser;
            xmlPullParser = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParser.newPullParser();
            InputStream is = getAssets().open(outputXML);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            processOutResult(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseInputXML() {
        try {
            XmlPullParserFactory xmlPullParser;
            xmlPullParser = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParser.newPullParser();
            InputStream is = getAssets().open(inputXML);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            processInputResult(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processOutResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Session> sessions = new ArrayList<>();
        int eventType = parser.getEventType();
        Session session = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String element = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    /*  START TAG DETECTED */
                    element = parser.getName();     //Get name of left node
                    if (element.equals("Session")) {
                        System.out.println("Creating a new session object....");
                        session = new Session();
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
                            session.location = parser.nextText();
                        } else if (element.equals("Price")) {
                            session.price = parser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    //nothing to do -> should remove, could remove
                    break;
                case XmlPullParser.END_TAG:
                    element = parser.getName();
                    if (element.equals("Session") && session != null) {
                        System.out.println("Adding the session to sessions...");
                        sessions.add(session);
                    }
            }
            eventType = parser.next();
        }
        printSessions(sessions);
        initHistoryWithdrawn(sessions);
    }

    private void processInputResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        int eventType = parser.getEventType();
        Transaction transaction = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String element = null;
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    /*  START TAG DETECTED */
                    element = parser.getName();     //Get name of left node
                    if (element.equals("Transaction")) {
                        System.out.println("Creating a new transaction object....");
                        transaction = new Transaction();
                    } else if (transaction != null) {
                        if (element.equals("Day")) {
                            transaction.day = parser.nextText();
                        } else if (element.equals("Month")) {
                            transaction.month = parser.nextText();
                        } else if (element.equals("Year")) {
                            transaction.year = parser.nextText();
                        } else if (element.equals("Name")) {
                            transaction.name = parser.nextText();
                        } else if (element.equals("Price")) {
                            transaction.price = parser.nextText();
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    //nothing to do -> should remove, could remove
                    break;
                case XmlPullParser.END_TAG:
                    element = parser.getName();
                    if (element.equals("Transaction") && transaction != null) {
                        System.out.println("Adding the transaction to transactions...");
                        transactions.add(transaction);
                    }
            }
            eventType = parser.next();
        }
        printTransaction(transactions);
        initHistoryAdded(transactions);
    }

    private void printSessions(ArrayList<Session> sessions) {
        StringBuilder sb = new StringBuilder();

        for (Session session : sessions) {
            sb.append(session.day).append("/");
            sb.append(session.month).append("/");
            sb.append(session.year).append("\n");
            sb.append(session.location).append("\n");
            sb.append(session.price).append("\n\n");
        }
        Log.d("MainActivity", sb.toString());
    }

    private void printTransaction(ArrayList<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();

        for (Transaction transaction : transactions) {
            sb.append(transaction.day).append("/");
            sb.append(transaction.month).append("/");
            sb.append(transaction.year).append("\n");
            sb.append(transaction.name).append("\n");
            sb.append(transaction.price).append("\n\n");
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

    private void initHistoryWithdrawn(ArrayList<Session> sessions) {
        System.out.println("Amount of sessions played: " + sessions.size());
        historyWithdrawn = new double[sessions.size()];
        for (int i = 0; i < sessions.size(); i++) {
            historyWithdrawn[i] = Double.valueOf(sessions.get(i).price);
        }
    }

    private void initHistoryAdded(ArrayList<Transaction> transactions) {
        System.out.println("Amount of transactions: " + transactions.size());
        historyAdded = new double[transactions.size()];
        for (int i = 0; i < transactions.size(); i++) {
            historyAdded[i] = Double.valueOf(transactions.get(i).price);
        }
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
        final View addMoneyView = layoutInflater.inflate(R.layout.input_add_money, null);
        alertDialogBuilder.setView(addMoneyView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        //Save Button
        Button saveData = addMoneyView.findViewById(R.id.button_save);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Save pressed...");
                EditText editText = addMoneyView.findViewById(R.id.edit_text);
//                editText.addTextChangedListener(new DecimalFilter(editText, activity));
                String inputNum = editText.getText().toString();
                if (TextUtils.isEmpty(inputNum)) {
                    Snackbar.make(v, "Price can not be empty", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                } else if (inputNum.length() == 1 && !TextUtils.isDigitsOnly(inputNum)) {
                    //Relaying on Android here -> number pad does not allow more than 1 decimal point
                    Snackbar.make(v, "Price must contain a number", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else {
                    Log.d("MainActivity", "Price input: " + inputNum);
                    //Ensure that input is a number

                    //Convert String to double // TODO: 16/09/2019 should use BIG INTEGER
                    double money = Double.parseDouble(inputNum);
                    adjustCurrentBalance(money);
                    updateTextView();


                    //we done, hide the pop up
                    alertDialog.hide();
                }
            }
        });

        Button cancelButton = addMoneyView.findViewById(R.id.button_cancel);
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
        final View squashView = layoutInflater.inflate(R.layout.input_squash_games, null);
        alertDialogBuilder.setView(squashView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        //Handle location field
        EditText locationField = squashView.findViewById(R.id.location_edit_text);
        locationField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        //Handle date field
        EditText dateField = squashView.findViewById(R.id.date_edit_text);
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
        Button saveData = squashView.findViewById(R.id.button_save);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Save pressed...");

                //Handle location field -------------------------------------------> 1
                EditText locationEditText = squashView.findViewById(R.id.location_edit_text);
                String inputLocation = locationEditText.getText().toString();
                if (TextUtils.isEmpty(inputLocation)) {
                    //default
                    inputLocation = "Sobell";
                    Log.d("MainActivity", "***default location***\nLocation is: " + inputLocation);
                } else {
                    //Grantees first letter is capital even though statically set in xml file
                    inputLocation = inputLocation.substring(0, 1).toUpperCase() + inputLocation.substring(1);
                    Log.d("MainActivity", "Location is: " + inputLocation);


                }

                //Handle date field -----------------------------------------------> 2
                EditText dateEditText = squashView.findViewById(R.id.date_edit_text);
                String inputDate = dateEditText.getText().toString();
                if (TextUtils.isEmpty(inputDate)) {
                    inputDate = day + "/" + (month + 1) + "/" + year;
                    Log.d("MainActivity", "***default date***\nDate is: " + inputDate);
                } else {
                    Log.d("MainActivity", inputDate);
                }


                //Handle price field ----------------------------------------------> 3
                EditText priceEditText = squashView.findViewById(R.id.price_edit_text);
                String inputNum = priceEditText.getText().toString();
                if (TextUtils.isEmpty(inputNum)) {
                    Snackbar.make(v, "Price can not be empty", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                } else if (inputNum.length() == 1 && !TextUtils.isDigitsOnly(inputNum)) {
                    //Relaying on Android here -> number pad does not allow more than 1 decimal point
                    Snackbar.make(v, "Price must contain a number", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else {
                    Log.d("MainActivity", "Price input: " + inputNum);
                    //1.Ensure that input is a number (input validation)

                    //2.Convert String to double
                    adjustCurrentBalance(-Double.parseDouble(inputNum));

                    //3. TODO: 17/09/2019 SAVE STRING -> APPEND TO output.xml
                    String s = inputDate + " " + inputLocation + " " + inputNum;
                    System.out.println("Final String is: " + s);
                    System.out.println("Now saving file...(not implemented yet)");

                    updateTextView();
                    //we done, hide the pop up
                    alertDialog.hide();
                }
            }
        });

        Button cancelButton = squashView.findViewById(R.id.button_cancel);
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

    private void adjustCurrentBalance(double money) {
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
