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
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

    ArrayList<TidalEntry> tidalEntries;
    ArrayList<ErrorEntry> errorEntries;

    Boolean hilo = false;

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

                if (tidalEntries == null) {
                    String errorURL = noaaURL.replace("&units=english", "&units=english&interval=hilo");

                    Log.d("doInBackground: ", errorURL);
                    url = new URL(errorURL);
                    dbf = DocumentBuilderFactory
                            .newInstance();
                    db = dbf.newDocumentBuilder();
                    // Download the XML file
                    doc = db.parse(new InputSource(url.openStream()));
                    doc.getDocumentElement().normalize();

                    outputStream = new ByteArrayOutputStream();
                    xmlSource = new DOMSource(doc);
                    outputTarget = new StreamResult(outputStream);
                    TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);

                    is = new ByteArrayInputStream(outputStream.toByteArray());
                    tidalEntries = new TidalParsing().parse(is);
                    hilo = true;
                }
                Log.d("doInBackground: ", "made it to MarineGraph");

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

                int n = 0;
                Log.d("onPostExecute: ", "MADE IT TO PRE LOOP");

                for (int j = 0; j < tidalEntries.size(); j++) {

                    Log.d("onPostExecute: ", Integer.toString(tidalEntries.size()));
                    String remove = "-";
                    String finalTide = tidalEntries.get(j).tidePrediction;

                    if (hilo = false) {

                        if (finalTide.substring(0, 1).equals("-")) {
                            finalTide = tidalEntries.get(j).tidePrediction.replace(remove, "");
                            Log.d("nononono: ", finalTide);
                        } else {
                            finalTide = "-" + tidalEntries.get(j).tidePrediction;
                            Log.d("-----: ", finalTide);
                        }
                    }

                    String[] splitDate = tidalEntries.get(j).date.split("-");
                    String date = splitDate[1] + "-" + splitDate[2];

                    Log.d("onPostExecute: ", date + ", " + finalTide);

                    array3.add(finalTide);
                    array4.add(date);

                }

                dp = new DataPoint[array3.size()];
                int arraySize = array3.size();

                for (int i = 0; i < array3.size(); i++) {
                    Log.d("Array3Size: ", Integer.toString(arraySize));
                    Double tidePrediction = Double.parseDouble(array3.get(i));
                    String newStringDate = array4.get(i);

                    dp[i] = new DataPoint(new SimpleDateFormat("MM-dd HH:mm").parse(newStringDate), tidePrediction);

                    Log.d("did i make it here: ", dp[i].toString());
                }

                series = new LineGraphSeries<>(dp);

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

                series.setColor(Color.WHITE);
                series.setThickness(8);

                graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);

                graphView.getGridLabelRenderer().setTextSize(35f);
                if (hilo = false) {
                    graphView.getGridLabelRenderer().setNumHorizontalLabels(array4.size());
                } else {
                    graphView.getGridLabelRenderer().setNumHorizontalLabels(10);

                }
                graphView.getGridLabelRenderer().setHorizontalLabelsAngle(90);

                Date date1 = new SimpleDateFormat("MM-dd HH:mm").parse(array4.get(0));
                Date date2 = new SimpleDateFormat("MM-dd HH:mm").parse(array4.get(array4.size() - 1));

                graphView.getViewport().setMinX(date1.getTime());
                graphView.getViewport().setMaxX(date2.getTime());
                graphView.getViewport().setXAxisBoundsManual(true);

                TextView myAwesomeTextView = (TextView) findViewById(R.id.textView4);

                TextView mostRecentDate = (TextView) findViewById(R.id.textView2);

                TextView earliestDate = (TextView) findViewById(R.id.textView3);

            } catch (ParseException pe) {
                pe.printStackTrace();
            }

        }


    }
}



