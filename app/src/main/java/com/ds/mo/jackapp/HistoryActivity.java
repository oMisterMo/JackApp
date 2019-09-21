package com.ds.mo.jackapp;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.appcompat.app.AppCompatActivity;

public class HistoryActivity extends AppCompatActivity {

    // TODO: 18/09/2019 View History .xml file
    // TODO: 18/09/2019 Clicking view history brings up new screen
    // TODO: 18/09/2019 View history screen displays all transaction (money added and games played)
    // TODO: 18/09/2019 View history, red = games player; blue = money added

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        System.out.println("*****On Create HISTORY*****");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("*****On Resume HISTORY*****");
        System.out.println("***HISTORY***");
        //1.Load input.xml
        ArrayList<Transaction> transactions = IOHelper.parseInputXML(this);
        //2.Load output.xml
        ArrayList<Session> sessions = IOHelper.parseOutputXML(this);

        //2.5 Add to common array list -> sort by date
        ArrayList<XMLData> data = new ArrayList<>();
        if (transactions != null) {
            data.addAll(transactions);
        }
        if (sessions != null) {
            data.addAll(sessions);
        }
        //sort array
        Collections.sort(data, new Comparator<XMLData>() {
            @Override
            public int compare(XMLData o1, XMLData o2) {
                return o1.compareTo(o2);
            }
        });

        //3.Display results to text view
        StringBuilder sb = new StringBuilder();

        for(XMLData d: data){
            if(d instanceof Transaction){
                Transaction transaction = (Transaction) d;
                sb.append("<font color='#00AA00'>");
                sb.append(transaction.day).append("/");
                sb.append(transaction.month).append("/");
                sb.append(transaction.year).append("<br>");
                sb.append(transaction.name).append("<br>");
                sb.append('£').append(transaction.price).append("<br><br>");
                sb.append("</font>");
            }
            if(d instanceof Session){
                Session session = (Session) d;
                sb.append(session.day).append("/");
                sb.append(session.month).append("/");
                sb.append(session.year).append("<br>");
                sb.append(session.location).append("<br>");
                sb.append('£').append(session.price).append("<br><br>");
            }
        }

        TextView historyView = findViewById(R.id.history);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            historyView.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY),
                    TextView.BufferType.SPANNABLE);
        } else {
            historyView.setText(Html.fromHtml(sb.toString()), TextView.BufferType.SPANNABLE);
        }

        System.out.println("Total elements: " + data.size());
        historyView.append("Total transactions: " + data.size());
//        historyView.setTextColor(Color.argb(255, 0, 150, 0));
//        historyView.setText(inputBuilder.toString());

//        historyView.setTextColor(Color.BLACK);
//        historyView.append(outputBuilder.toString());
    }
}
