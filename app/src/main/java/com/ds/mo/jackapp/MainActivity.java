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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final char POUND = '£';
    private static double balance = 0d;
    private DecimalFormat dc;

    private double[] historyAdded;
    private double[] historyWithdrawn;

    private DatePickerDialog datePickerDialog;
    private int day, month, year;
    private Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("*****On Create*****");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        initApp();    //=> MOVED TO onResume, easier initialisation on activity change
        // TODO: 18/09/2019 View History .xml file
        // TODO: 18/09/2019 Clicking view history brings up new screen
        // TODO: 18/09/2019 View history screen displays all transaction (money added and games played)
        // TODO: 18/09/2019 View history, red = games player; blue = money added
    }

    private void initApp() {
        //My decimal format
        dc = new DecimalFormat("00.00");
//        initHistoryAdded();           //Temp hardcoded data
//        initHistoryWithdrawn();       //Temp hardcoded data

        ArrayList<Session> s = IOHelper.parseOutputXML(this);
        ArrayList<Transaction> t = IOHelper.parseInputXML(this);
        if (s != null) {
            System.out.println("Session size on app start: " + s.size());
            initHistoryWithdrawn(s);
        }
        if (t != null) {
            System.out.println("Transaction size on app start: " + t.size());
            initHistoryAdded(t);
        }

        updateBalance();
        updateTextView();

        //date===================================
        date = Calendar.getInstance();
        day = date.get(Calendar.DAY_OF_MONTH);
        month = (date.get(Calendar.MONTH) + 1);
        year = date.get(Calendar.YEAR);
        System.out.printf("Today's Date: %d, %d, %d%n", day, month, year);
        //======================================

        //ADD money fab
        FloatingActionButton topUp = findViewById(R.id.top_up);
        topUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoney();
            }
        });

        //SUBTRACT money fab
        FloatingActionButton subtract = findViewById(R.id.subtract);
        subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractMoney();
            }
        });
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
        if (sessions != null) {
            System.out.println("Amount of sessions played: " + sessions.size());
            historyWithdrawn = new double[sessions.size()];
            for (int i = 0; i < sessions.size(); i++) {
                historyWithdrawn[i] = Double.valueOf(sessions.get(i).price);
            }
        }
    }

    private void initHistoryAdded(ArrayList<Transaction> transactions) {
        if (transactions != null) {
            System.out.println("Amount of transactions: " + transactions.size());
            historyAdded = new double[transactions.size()];
            for (int i = 0; i < transactions.size(); i++) {
                historyAdded[i] = Double.valueOf(transactions.get(i).price);
            }
        }
    }

    private void updateBalance() {
        System.out.println("Update balance...");
        if (historyAdded != null) {
            for (double price : historyAdded) {
                balance += price;
            }
        }
        if (historyWithdrawn != null) {
            for (double price : historyWithdrawn) {
                balance -= price;
            }
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

        //Handle date field
        final EditText dateField = addMoneyView.findViewById(R.id.date_edit_text);
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d("MainActivity", "**********HAS FOCUS***********");
                    datePickerDialog = new DatePickerDialog(
                            addMoneyView.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int y, int m, int d) {
                            System.out.printf("Date touched: %d/%d/%d\n", d, (m + 1), y);
                            dateField.setText(String.format(Locale.UK, "%d/%d/%d", d, (m + 1), y));
                        }
                    }, year, month, day);
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
                    datePickerDialog.show();
                }
            }
        });

        //Save Button
        Button saveData = addMoneyView.findViewById(R.id.button_save);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Save pressed...");

                //Handle location field -------------------------------------------> 1
                EditText locationEditText = addMoneyView.findViewById(R.id.name_edit_text);
                String inputName = locationEditText.getText().toString();
                if (TextUtils.isEmpty(inputName)) {
                    //default name
                    inputName = "BLANTTER J O";
                    Log.d("MainActivity", "***default name***\nName is: " + inputName);
                } else {
                    //CAPITALISE NAME
                    inputName = inputName.toUpperCase().trim();
                    Log.d("MainActivity", "Name is: " + inputName);


                }

                //Handle date field -----------------------------------------------> 2
                int d, m, y;
                EditText dateEditText = addMoneyView.findViewById(R.id.date_edit_text);
                String inputDate = dateEditText.getText().toString();
                if (TextUtils.isEmpty(inputDate)) {
                    //default day (current)
                    d = day;
                    m = month;
                    y = year;
                    inputDate = d + "/" + m + "/" + y;
                    Log.d("MainActivity", "***default date***\nDate is: " + inputDate);
                } else {
                    inputDate = dateEditText.getText().toString();
                    Log.d("MainActivity", inputDate);
                    String[] s = inputDate.split("/");
                    d = Integer.parseInt(s[0]);
                    m = Integer.parseInt(s[1]);
                    y = Integer.parseInt(s[2]);
                }


                EditText editText = addMoneyView.findViewById(R.id.edit_text);
//                editText.addTextChangedListener(new DecimalFilter(editText, activity));
                String inputNum = editText.getText().toString();
                if (TextUtils.isEmpty(inputNum)) {
                    Snackbar.make(v, "Price can not be empty", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                } else if (inputNum.length() == 1 && !TextUtils.isDigitsOnly(inputNum)) {
                    //Relying on Android here -> number pad does not allow more than 1 decimal point
                    Snackbar.make(v, "Price must contain a number", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else {
                    Log.d("MainActivity", "Price input: " + inputNum);
                    //1.Ensure that input is a number (input validation)

                    //2.Convert String to double
                    adjustCurrentBalance(Double.parseDouble(inputNum));

                    //3.Save to .xml file
//                    String inputName = "BLANTTER J O"; //Using default values for name and date
//                    String inputDate = day + "/" + month + "/" + year;

                    String s = inputDate + " " + inputName + " " + inputNum;
                    System.out.println("Final String is: " + s);
                    System.out.println("Now saving file...");
                    //Save file =====================================================


                    IOHelper.writeToXMLinput(addMoneyView,
                            new Transaction(String.valueOf(d), String.valueOf(m),
                                    String.valueOf(y), inputName, inputNum));
                    //===============================================================
                    updateTextView();
                    //we done, hide the pop up
//                    alertDialog.hide();
                    alertDialog.dismiss();
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
        final EditText dateField = squashView.findViewById(R.id.date_edit_text);
        dateField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d("MainActivity", "**********HAS FOCUS***********");
                    datePickerDialog = new DatePickerDialog(
                            squashView.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int y, int m, int d) {
                            System.out.printf("Date touched: %d/%d/%d\n", d, (m + 1), y);
                            dateField.setText(String.format(Locale.UK, "%d/%d/%d", d, (m + 1), y));
                        }
                    }, year, month, day);
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + 1000);
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
                    //default location
                    inputLocation = "Sobell";
                    Log.d("MainActivity", "***default location***\nLocation is: " + inputLocation);
                } else {
                    //Grantees first letter is capital even though statically set in xml file
                    inputLocation = inputLocation.substring(0, 1).toUpperCase() + inputLocation.substring(1);
                    Log.d("MainActivity", "Location is: " + inputLocation);


                }

                //Handle date field -----------------------------------------------> 2
                int d, m, y;
                EditText dateEditText = squashView.findViewById(R.id.date_edit_text);
                String inputDate = dateEditText.getText().toString();
                if (TextUtils.isEmpty(inputDate)) {
                    //default day (current)
                    d = day;
                    m = month;
                    y = year;
                    inputDate = d + "/" + m + "/" + y;
                    Log.d("MainActivity", "***default date***\nDate is: " + inputDate);
                } else {
                    inputDate = dateEditText.getText().toString();
                    Log.d("MainActivity", inputDate);
                    String[] s = inputDate.split("/");
                    d = Integer.parseInt(s[0]);
                    m = Integer.parseInt(s[1]);
                    y = Integer.parseInt(s[2]);
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
                    System.out.println("Now saving file...");

                    IOHelper.writeToXMLoutput(squashView, new Session(String.valueOf(d), String.valueOf(m),
                            String.valueOf(y), inputLocation, inputNum));

                    updateTextView();
                    //we done, hide the pop up
//                    alertDialog.hide();
                    alertDialog.dismiss();
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
        updateTextView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("*****On Resume*****");
        initApp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("*****On Pause*****");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("*****On Stop*****");
        IOHelper.sessions = null;
        IOHelper.transactions = null;
        historyAdded = null;
        historyWithdrawn = null;
        balance = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("*****On Destroy*****");
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
        if (id == R.id.action_delete) {
            Log.d("MainActivity", "DELETE");
            IOHelper.deleteFile(this, "input.xml");
            IOHelper.deleteFile(this, "output.xml");
            clearTotal();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
