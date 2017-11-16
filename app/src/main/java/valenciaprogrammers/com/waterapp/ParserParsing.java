package valenciaprogrammers.com.waterapp;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by nayala2 on 9/22/17.
 */
public class ParserParsing {
    private static final String ns = null;

    public ArrayList<Entry> parse(InputStream in)
    {
        ArrayList<Entry> list = null;

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            list=readFeed(parser);
            for(int i=0;i<list.size();i++)
            {
                Log.i(".......",list.get(i).lat);
                Log.i(".......",list.get(i).lng);
                Log.i(".......",list.get(i).icon);
            }
        } catch(Exception e){

        }
        return list;
    }
    private  ArrayList<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        ArrayList<Entry> entry= new ArrayList<Entry>();
        parser.require(XmlPullParser.START_TAG, ns, "mapper");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.i("............",name);
            // Starts by looking for the entry tag
            if (name.equals("site")) {
                entry.add(readMarker(parser));
            } else {
                skip(parser);
            }
        }
        return entry;
    }
    private Entry readMarker(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "site");
        String lat = null;
        String lng = null;
        String icon =null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            Log.i("............",name);
            if (name.equals("lat")) {
                lat = readLat(parser);
            } else if (name.equals("lng")) {
                lng = readLng(parser);
            } else if (name.equals("sna")) {
                icon = readIcon(parser);
            }
            else {
                skip(parser);
            }
        }
        return new Entry(lat,lng,icon);
    }
    private String readLat(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lat");
        String lat = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lat");
        return lat;
    }
    private String readLng(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lng");
        String lng = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lng");
        return lng;
    }
    private String readIcon(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "sna");
        String icon = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "sna");
        return icon;
    }


    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}