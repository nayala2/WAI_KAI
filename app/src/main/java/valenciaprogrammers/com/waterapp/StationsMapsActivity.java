package valenciaprogrammers.com.waterapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class StationsMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    GoogleMap googleMap;
    SupportMapFragment mapFragment;
    String URL = "https://waterservices.usgs.gov/nwis/iv/?format=waterml,2.0&stateCd=fl&parameterCd=00060,00065&siteType=LK,ST,ST-CA,ST-DCH,ST-TS,SP&siteStatus=active";

    String URLGraph;
    ArrayList<NameCoordsEntry> entries;
    ArrayList<FlowOnlyEntry> flowOnlyEntries;
    ArrayList<DateEntry> dateOnlyEntries;
    ArrayList<SiteEntry> siteOnlyEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        this.googleMap = googleMap;

        // Adds focal point to map so that Florida is centered on the screen
//        LatLng focalPoint = new LatLng(28.468357, -83.331241);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((focalPoint), 5.8f));

        zoomInOnCoord(StartActivity.lat, StartActivity.lon);

        new DownloadXML().execute(URL);
    }

    private class DownloadXML extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... Url) {

            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Download the XML file
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                // Locate the Tag Name
//                nodelist = doc.getElementsByTagName("item");

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Source xmlSource = new DOMSource(doc);
                javax.xml.transform.Result outputTarget = new StreamResult(outputStream);
                TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);

                InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
                entries = new NameCoordsParsing().parse(is);

                InputStream is2 = new ByteArrayInputStream(outputStream.toByteArray());
                flowOnlyEntries = new FlowOnlyParsing().parse(is2);

                InputStream is3 = new ByteArrayInputStream(outputStream.toByteArray());
                dateOnlyEntries = new DateOnlyParsing().parse(is3);

                InputStream is4 = new ByteArrayInputStream(outputStream.toByteArray());
                siteOnlyEntries = new SiteOnlyParsing().parse(is4);


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            for (int i = 0; i < entries.size() && i < flowOnlyEntries.size() && i < dateOnlyEntries.size(); i++) {

                String[] latlong = entries.get(i).coordinates.split(" ");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng lng = new LatLng(latitude, longitude);

                String stationName = entries.get(i).siteName;
                String flow = flowOnlyEntries.get(i).flow;
                String date = dateOnlyEntries.get(i).date;
                String siteID = siteOnlyEntries.get(i).site;
                Log.d("doInBackground: ", "did I make it here???");

                Marker destination = googleMap.addMarker(new MarkerOptions()
                        .position(lng)
                        .title(stationName)
                        .snippet("Flow: " + flow + " ft3/s  " + "Date: " + date
                                + "\nSite ID: " + siteID)

                );


                destination.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.liquid));

            }


            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    String fullSnippet = marker.getSnippet();
                    String[] splitSnippet = fullSnippet.split("Date: ");
                    String snippet = splitSnippet[1];
                    String[] snippetSplit = snippet.split("Site ID: ");
                    String markerDate = snippetSplit[0];
                    Log.d("markerDate: ", markerDate + "test");
                    String siteID = snippetSplit[1];

                    String coordinates = marker.getPosition().toString();

                    String[] dateSplit = markerDate.split("-");
                    int month = Integer.parseInt(dateSplit[1]);
                    int year = Integer.parseInt(dateSplit[0]);

                    int previousMonth;
                    int previousYear;


                    if (month != 1) {
                        previousMonth = month - 1;
                        URLGraph = "https://waterservices.usgs.gov/nwis/iv/?format=waterml,2.0&sites=" + siteID + "&startDT=" + year + "-" + previousMonth + "-28" + "&endDT=" + markerDate + "&parameterCd=00065&siteType=ES,LK,ST,ST-CA,ST-DCH,ST-TS,SP&siteStatus=active";
                        String URLGraph2 = URLGraph.replace("\n", "").replace("\r", "");
                        Log.d("url", URLGraph2);
                        Intent intent = new Intent(StationsMapsActivity.this, GraphActivity.class);

                        intent.putExtra("key", URLGraph2);
                        startActivity(intent);

                    } else {
                        previousMonth = 12;
                        previousYear = year - 1;
                        URLGraph = "https://waterservices.usgs.gov/nwis/iv/?format=waterml,2.0&sites=" + siteID + "&startDT=" + previousYear + "-" + previousMonth + "-28" + "&endDT=" + markerDate + "&parameterCd=00065&siteType=ES,LK,ST,ST-CA,ST-DCH,ST-TS,SP&siteStatus=active";
                        String URLGraph2 = URLGraph.replace("\n", "").replace("\r", "");
                        Log.d("url", URLGraph2);
                        Intent intent = new Intent(StationsMapsActivity.this, GraphActivity.class);

                        intent.putExtra("key", URLGraph2);
                        startActivity(intent);
                    }


                }
            });

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    public void zoomInOnCoord(double lat, double lon) {
        LatLng city = new LatLng(lat, lon);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(city));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(city)
                .zoom(10)
                .bearing(0)
                .tilt(90)
                .build();
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 10));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3500, null);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
