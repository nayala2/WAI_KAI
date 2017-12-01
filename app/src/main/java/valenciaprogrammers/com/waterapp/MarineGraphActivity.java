package valenciaprogrammers.com.waterapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MarineGraphActivity extends AppCompatActivity {

    GraphView graphView;
    LineGraphSeries<DataPoint> series;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    ArrayList<TidalEntry> tidalEntries;
//    ArrayList<GraphValueEntry> valueOnlySeries;
//    ArrayList<GraphNameEntry> nameOnlySeries;

    ArrayList<String> array3 = new ArrayList<String>();
    ArrayList<String> array4 = new ArrayList<String>();

    DataPoint[] dp;

    Bundle bundle;
    String noaaURL;
    private static final String TAG = "MarineGraphActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("onCreate: ", "here???");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marine_graph);
        graphView = (GraphView) findViewById(R.id.marineGraph);

        bundle = getIntent().getExtras();
        noaaURL = bundle.getString("URL");

        Log.d("onCreate: ", noaaURL);


        new downloadXMLForMarineGraph().execute(noaaURL);
    }

    // DownloadXML AsyncTask
    private class downloadXMLForMarineGraph extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(String... Url) {
            try {

                Log.d("doInBackground: ", "am i here o rno?");
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Download the XML file
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Source xmlSource = new DOMSource(doc);
                javax.xml.transform.Result outputTarget = new StreamResult(outputStream);
                TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
                Log.d("doInBackground: ", "made it to preIS");

                InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
                tidalEntries = new TidalParsing().parse(is);

                Log.d("doInBackground: ", "made it to MarineGraph");
//                InputStream is1 = new ByteArrayInputStream(outputStream.toByteArray());
//                valueOnlySeries = new GraphValueParsing().parse(is1);
//
//                InputStream is2 = new ByteArrayInputStream(outputStream.toByteArray());
//                nameOnlySeries = new GraphNameParsing().parse(is2);

            } catch (ParserConfigurationException pce) {
                pce.printStackTrace();
            } catch (IOException io) {
                io.printStackTrace();
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            Log.d("onPostExecute: ", "made it to post executenull");

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            Log.d("onPostExecute: ", "made it to post execute");
            try {
//                currentValue = valueOnlySeries.get(valueOnlySeries.size() - 1).value;
//                currentDate = dateOnlyEntrySeries.get(dateOnlyEntrySeries.size() - 1).date;
//                currentName = nameOnlySeries.get(0).siteName;
//                Log.d("onCreate: ", currentName);

                int n = 0;
                Log.d("onPostExecute: ", "MADE IT TO PRE LOOP");

                for (int j = 0; j < tidalEntries.size(); j++) {

                    Log.d("onPostExecute: ", Integer.toString(tidalEntries.size()));
                    String remove = "-";
                    String rawTide = tidalEntries.get(j).tidePrediction;
                    String finalTide;
                    if (rawTide.substring(0, 1).equals("-")) {
                        finalTide = tidalEntries.get(j).tidePrediction.replace(remove, "");
                        Log.d("nononono: ", finalTide);
                    } else {
                        finalTide = "-" + tidalEntries.get(j).tidePrediction;
                        Log.d("-----: ", finalTide);
                    }

                    String date = tidalEntries.get(j).date;

                    Log.d("onPostExecute: ", date + ", " + rawTide);

                    array3.add(finalTide);
                    array4.add(date);

                }

                dp = new DataPoint[array3.size()];
                int arraySize = array3.size();

                for (int i = 0; i < array3.size(); i++) {
                    Log.d("Array3Size: ", Integer.toString(arraySize));
                    Double tidePrediction = Double.parseDouble(array3.get(i));
                    String newStringDate = array4.get(i);

                    dp[i] = new DataPoint(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(newStringDate), tidePrediction);

                    Log.d("did i make it here: ", dp[i].toString());
                }

                series = new LineGraphSeries<>(dp);

//                series.appendData(finalDataPoint, true, 30);
                graphView.addSeries(series);

                NumberFormat nf = NumberFormat.getInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMinimumIntegerDigits(1);
                graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));

                graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        Log.d("onPostExecute: ", "did i make it to labelrender");
                        if (isValueX) {
                            Log.d("formatLabel: ", sdf.format(new Date((long) value)));
                            return sdf.format(new Date((long) value));
                        } else {
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });
//                graphView.setTitle(tidalEntries.get(0).tidePrediction);

                //"Most current reading on " + currentDate + " at " + currentValue);

//                if (tidalEntries.get(0).highLow.length() > 40) {
//                    graphView.setTitleTextSize(35);
//                } else {
//                    graphView.setTitleTextSize(50);
//                }

//                graphView.getGridLabelRenderer().setHumanRounding(false);

                series.setColor(Color.WHITE);
                series.setThickness(8);

                graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);

                graphView.getGridLabelRenderer().setTextSize(35f);
                graphView.getGridLabelRenderer().setNumHorizontalLabels(tidalEntries.size());

                //                graphView.getGridLabelRenderer().setHorizontalLabelsVisible(true);
                // globally
                TextView myAwesomeTextView = (TextView) findViewById(R.id.textView4);
//                myAwesomeTextView.setText(currentValue + " ft3/s on " + currentDate);

                TextView mostRecentDate = (TextView) findViewById(R.id.textView2);
//                mostRecentDate.setText(currentDate + "  ");

                TextView earliestDate = (TextView) findViewById(R.id.textView3);
//                earliestDate.setText("      " + array3.get(0));

            } catch (ParseException pe) {
                pe.printStackTrace();
            }
        }
    }
}



