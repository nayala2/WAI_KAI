package valenciaprogrammers.com.waterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ChooseTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_type);

    }

    public void toFreshwaterMap(View view) {
        Intent intent = new Intent(ChooseTypeActivity.this, StationsMapsActivity.class);
        startActivity(intent);
    }

    public void toMarineMap(View view) {
        Intent intent = new Intent(ChooseTypeActivity.this, MarineMapsActivity.class);
        startActivity(intent);
    }
}
