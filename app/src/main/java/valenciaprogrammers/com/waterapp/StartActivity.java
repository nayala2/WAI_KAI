package valenciaprogrammers.com.waterapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class StartActivity extends AppCompatActivity {

    InputStream inputStream;
    String[] data;

    ArrayList<String> latitude = new ArrayList<String>();
    ArrayList<String> longitude = new ArrayList<String>();
    ArrayList<String> cities = new ArrayList<String>();

    public static int ready = 0;

    public static double lat;
    public static double lon;

    int cityCounter = 0;

    AutoCompleteTextView searchBar;
    TextView about;

    int counter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, cities);

        inputStream = getResources().openRawResource(R.raw.revised_cities);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null){
                data = csvLine.split(",");
                try {
                    //Log.e("DataCities", "" + data[0]);
                    cities.add("" + data[0].toLowerCase());
                    latitude.add("" + data[1]);
                    longitude.add("" + data[2]);

                } catch (Exception e) {
                    Log.e("Problem", e.toString());
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }



        searchBar = (AutoCompleteTextView) findViewById(R.id.searchCounty);
        searchBar.setAdapter(adapter);
        about = (TextView) findViewById(R.id.aboutActivator);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.magnifier);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter += 1;
                Log.d("imagine", "imagine");
                if (counter % 2 == 0) {
                    searchBar.setVisibility(View.VISIBLE);
                    searchBar.requestFocus();
                    ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchBar, InputMethodManager.SHOW_FORCED);
                } else {
                    searchBar.setVisibility(View.INVISIBLE);
                    searchBar.setText("");
                    //searchBar.setFocusable(false);
                }

            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartActivity.this, AboutActivity.class);
                startActivity(i);
            }
        });

//        searchBar.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    Toast.makeText(StartActivity.this, "it worked!", Toast.LENGTH_SHORT).show();
//                    searchBar.setVisibility(View.INVISIBLE);
//                    counter = 1;
//
//                    String search = searchBar.getText().toString().toLowerCase();
//
//                    for (int j = 0; j < cities.size(); j++) {
//                        if (search.contains(cities.get(j))) {
//                            lat = Double.parseDouble(latitude.get(j));
//                            lon = Double.parseDouble(longitude.get(j));
//
//                            Toast.makeText(StartActivity.this, "Latitude and Longitude for that city is " + latitude.get(j) + ", " + longitude.get(j), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    searchBar.setText("");
//
//                    Intent i = new Intent(StartActivity.this, StationsMapsActivity.class);
//                    startActivity(i);
//
//                    return true;
//                }
//                return false;
//            }
//        });

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Toast.makeText(StartActivity.this, "it worked!", Toast.LENGTH_SHORT).show();
                searchBar.setVisibility(View.INVISIBLE);
                counter = 1;

                String search = searchBar.getText().toString().toLowerCase();

                for (int j = 0; j < cities.size(); j++) {
                    if (search.contains(cities.get(j))) {
                        lat = Double.parseDouble(latitude.get(j));
                        lon = Double.parseDouble(longitude.get(j));

                        Toast.makeText(StartActivity.this, "Latitude and Longitude for that city is " + latitude.get(j) + ", " + longitude.get(j), Toast.LENGTH_SHORT).show();
                    }
                }

                searchBar.setText(" ");


                Intent i = new Intent(StartActivity.this, StationsMapsActivity.class);
                startActivity(i);
                return false;
            }
        });
    }

}
