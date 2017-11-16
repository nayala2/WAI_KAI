package valenciaprogrammers.com.waterapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class StationsMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Adds focal point to map so that Florida is centered on the screen
//        LatLng focalPoint = new LatLng(28.468357, -83.331241);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom((focalPoint), 5.8f));
        zoomInOnCoord(StartActivity.lat, StartActivity.lon);
        // this code saves the webpage as an xml file. the file is saved internally
        // and is replaced everytime users calls for this data type. The data pulled
        // at the request is the most recent data available from USGS.
        //Note: This code has not been tested/finalized yet.

        try {
            OutputStream out = new FileOutputStream("/Users/nayala2/Documents");

            URL url = new URL("http://www.oracle.com/technetwork/java/index.html");
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();

            copy(is, out);
            is.close();
            out.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }

        //this code opens the xml file for parsing by the appropriate methods
        //then saves the output into an array. Then the coordinates are converted
        //to doubles, then LatLng. The station name is edited to remove extra text,
        //and the flow is presented with its unit of measurement.
        //These are used to generate markers on the map as the method loops through
        //the arrays.
        //Note: I need to add the date to the markers.

        try {
            InputStream is = StationsMapsActivity.this.getResources()
                    .getAssets().open("streamflow.xml");
            ArrayList<NameCoordsEntry> entries = new NameCoordsParsing().parse(is);

            InputStream is2 = StationsMapsActivity.this.getResources()
                    .getAssets().open("streamflow.xml");
            ArrayList<FlowOnlyEntry> flowOnlyEntries = new FlowOnlyParsing().parse(is2);

            for(int i=0;i<entries.size() && i < flowOnlyEntries.size();i++) {

                String[] latlong = entries.get(i).coordinates.split(" ");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                LatLng lng = new LatLng(latitude, longitude);

//                    Log.d("I made it here", flow);

                String remove = "Timeseries collected at ";
                String stationName = entries.get(i).siteName.replace(remove, "");

                    //                Log.d("flow:.....", stationName);

                    String flow = flowOnlyEntries.get(i).flow;

                    Marker destination = googleMap.addMarker(new MarkerOptions()
                            .position(lng)
                            .title(stationName)
                            .snippet("Flow: " + flow + " ft3/s")
                    );
            }

        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }

    private static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buffer = new byte[4096];
        while (true) {
            int numBytes = from.read(buffer);
            if (numBytes == -1) {
                break;
            }
            to.write(buffer, 0, numBytes);
        }
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
