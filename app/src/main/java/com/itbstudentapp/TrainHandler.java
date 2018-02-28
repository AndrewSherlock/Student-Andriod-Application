package com.itbstudentapp;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TrainHandler extends AsyncTask{

    public String getXmlStringOfDetails() throws IOException
    {
        HttpURLConnection connection;
        URL trainXML = new URL("//http://api.irishrail.ie/realtime/realtime.asmx/getStationDataByCodeXML_WithNumMins?StationCode=CMINE&NumMins=60");

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(trainXML.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("objStationData");

            for(int i = 0; i < nodeList.getLength(); i++)
            {
                Node node =  nodeList.item(i);

                Log.e("Node", node.toString());
            }

        } catch (SAXException | ParserConfigurationException e){

        }
        return null;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
