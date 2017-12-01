package valenciaprogrammers.com.waterapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class DateRangeActivity extends AppCompatActivity {

    private TextView getTidalData;
    String noaaURL;
    String siteID;

    EditText startDay;
    EditText startMonth;
    EditText startYear;
    EditText endDay;
    EditText endMonth;
    EditText endYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_range);

        Bundle bundle = getIntent().getExtras();
        siteID = bundle.getString("key");
        Log.d("onCreate: ", siteID);

//            dateView = (TextView) findViewById(R.id.textView2);
//            dateView2 = (TextView) findViewById(R.id.textView4);
        getTidalData = (TextView) findViewById(R.id.button4);

        getTidalData.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startDay = (EditText) findViewById(R.id.startDay);
                startMonth = (EditText) findViewById(R.id.startMonth);
                startYear = (EditText) findViewById(R.id.startYear);
                endDay = (EditText) findViewById(R.id.endDay);
                endMonth = (EditText) findViewById(R.id.endMonth);
                endYear = (EditText) findViewById(R.id.endYear);

                String startDate = startYear.getText().toString() + startMonth.getText().toString() + startDay.getText().toString();
                Log.d("onClick: ", startYear.getText().toString());
                Log.d("onClick: ", startDay.getText().toString());
                Log.d("onClick: ", startMonth.getText().toString());

                String endDate = endYear.getText().toString() + endMonth.getText().toString() + endDay.getText().toString();

                noaaURL = "https://tidesandcurrents.noaa.gov/api/datagetter?begin_date=" + startDate + "&end_date=" + endDate + "&station=" + siteID + "&product=PREDICTIONS&datum=MHHW&units=english&time_zone=gmt&application=web_services&format=xml";


                Intent intent = new Intent(DateRangeActivity.this, MarineGraphActivity.class);
                intent.putExtra("URL", noaaURL);
                startActivity(intent);
            }
        }));
    }
}