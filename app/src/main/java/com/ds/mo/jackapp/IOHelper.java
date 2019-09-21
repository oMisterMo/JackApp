package com.ds.mo.jackapp;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

public final class IOHelper {

    public static ArrayList<Transaction> transactions = new ArrayList<>();
    public static ArrayList<Session> sessions = new ArrayList<>();

    private final static String inputXML = "input.xml";
    private final static String outputXML = "output.xml";

    public static void deleteFile(Context context, String fileName) {
        System.out.println("Trying to delete " + fileName + "...");
        boolean b = context.deleteFile(fileName);
        System.out.println("Success: " + b);

    }

    public static void writeToFile(Context context, String fileName, String str) {
        try {
            FileOutputStream os = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            os.write(str.getBytes(), 0, str.length());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=========== ADD MONEY ========================================================================

    public static void writeToXMLinput(View view, Transaction t) {
        System.out.println("WRITING TO FILE................");
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(t);
        //=====================================================================

        //Convert transaction array to xml format
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("utf-8", true);
            serializer.startTag("", "Input");

            for (Transaction transaction : transactions) {
                serializer.startTag("", "Transaction");

                serializer.startTag("", "Date");

                serializer.startTag("", "Day");
                serializer.text(transaction.day);
                serializer.endTag("", "Day");
                serializer.startTag("", "Month");
                serializer.text(transaction.month);
                serializer.endTag("", "Month");
                serializer.startTag("", "Year");
                serializer.text(transaction.year);
                serializer.endTag("", "Year");

                serializer.endTag("", "Date");

                serializer.startTag("", "Name");
                serializer.text(transaction.name);
                serializer.endTag("", "Name");

                serializer.startTag("", "Price");
                serializer.text(transaction.price);
                serializer.endTag("", "Price");

                serializer.endTag("", "Transaction");
            }
            serializer.endTag("", "Input");
            serializer.endDocument();

            //Test conversion
            String result = writer.toString();
            System.out.println("Test xml writer..." + inputXML);
            System.out.println(result);

            //Write to xml file on the internal storage
            IOHelper.writeToFile(view.getContext(), inputXML, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Transaction> parseInputXML(Context context) {
        try {
            XmlPullParserFactory xmlPullParser;
            xmlPullParser = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParser.newPullParser();
//            InputStream is = getAssets().open(inputXML);
            InputStream is = context.openFileInput(inputXML);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            return processInputResult(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<Transaction> processInputResult(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        if (transactions == null) {
            transactions = new ArrayList<>();
        } else {
            transactions.clear();
        }
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
//        initHistoryAdded(transactions);
        return transactions;
    }


    private static void printTransaction(ArrayList<Transaction> transactions) {
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

    //=========== REMOVE MONEY ===================================================+++++=============
    public static void writeToXMLoutput(View view, Session s) {
        System.out.println("WRITING TO FILE................");
        if (sessions == null) {
            sessions = new ArrayList<>();
        }
        sessions.add(s);
        //=====================================================================

        //Convert sessions array to xml format
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("utf-8", true);
            serializer.startTag("", "Output");

            for (Session session : sessions) {
                serializer.startTag("", "Session");

                serializer.startTag("", "Date");

                serializer.startTag("", "Day");
                serializer.text(session.day);
                serializer.endTag("", "Day");
                serializer.startTag("", "Month");
                serializer.text(session.month);
                serializer.endTag("", "Month");
                serializer.startTag("", "Year");
                serializer.text(session.year);
                serializer.endTag("", "Year");

                serializer.endTag("", "Date");

                serializer.startTag("", "Location");
                serializer.text(session.location);
                serializer.endTag("", "Location");

                serializer.startTag("", "Price");
                serializer.text(session.price);
                serializer.endTag("", "Price");

                serializer.endTag("", "Session");
            }
            serializer.endTag("", "Output");
            serializer.endDocument();

            //Test conversion
            String result = writer.toString();
            System.out.println("Test xml writer..." + outputXML);
            System.out.println(result);

            //Write to xml file on the internal storage
            IOHelper.writeToFile(view.getContext(), outputXML, result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Session> parseOutputXML(Context context) {
        try {
            XmlPullParserFactory xmlPullParser;
            xmlPullParser = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParser.newPullParser();
//            InputStream is = getAssets().open(outputXML);
            InputStream is = context.openFileInput(outputXML);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);

            return processOutResult(parser);

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<Session> processOutResult(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        if (sessions == null) {
            sessions = new ArrayList<>();
        } else {
            sessions.clear();
        }
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
//        initHistoryWithdrawn(sessions);
        return sessions;
    }


    private static void printSessions(ArrayList<Session> sessions) {
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
}
