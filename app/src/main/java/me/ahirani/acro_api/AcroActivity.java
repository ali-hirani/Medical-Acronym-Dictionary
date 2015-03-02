package me.ahirani.acro_api;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AcroActivity extends ActionBarActivity {

    private ArrayAdapter<String> acroAdapter;
    private String searchTerm;
    private String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acro);

        // Every activity is invoked by an intent

        // getIntent gets the intent and the data contained within it
        Intent intent = getIntent();
        searchTerm = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        String searchTermDisplay = searchTerm.toUpperCase();

        String temp = "";

        for (int i = 0; i < searchTermDisplay.length(); i++) {

            temp += searchTermDisplay.charAt(i);
            temp += '.';
        }
        searchTermDisplay = temp;

        // Populate the textview with the search term
        TextView textView = (TextView) findViewById(R.id.textView_acro);
        textView.setText(searchTermDisplay);

        // Dummy Data
        data = new String[]{

                "Long Forms",
                "-------------------"
                // "mitoxantrone, 1983",
                // "Migration inhibition test, 1970",
                // "monoiodotyrosine, 1973",
                // "Magnetic induction tomography, 2000",
                // "metal-insulator transition, 2000",
                // "mouse inoculation test, 1969",
                // "Massachusetts Institute of Technology, 1989",
                // "Mitochondria, 1975",
                // "multiple insulin injection therapy, 1976",
                // "Minimally invasive therapy, 1993",
                // "maximal intimal thickness, 1995",
                // "Minimal invasive techniques, 2004",
                // "mitomycin, 1982",
                // "marrow iron turnover, 1982",
                // "N-methylisothiazol-3-one, 1990",
                // "The mean input time, 1993"
        };

        List<String> acroList = new ArrayList<>(Arrays.asList(data));

        acroAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.adapter_layout,
                        acroList);

        ListView listView = (ListView) findViewById(R.id.listview_acro);

        // Binds the listview with the array adapter
        listView.setAdapter(acroAdapter);

        TextView networkStatus = (TextView) findViewById(R.id.network_status);

        // check if you are connected or not
        if (isConnected()) {
            networkStatus.setText("Connected");
        } else {
            networkStatus.setText("NOT connected");
        }
        // call AsyncTask to perform network operation on separate thread
        //new FetchAcroTask().execute("http://www.nactem.ac.uk/software/acromine/dictionary.py?sf=nasa");
        new FetchAcroTask().execute(searchTerm);
        //Toast.makeText(getBaseContext(), "Executed!", Toast.LENGTH_LONG).show();
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public static String[] GET(String url) {
        InputStream inputStream = null;
        String rawJson = "";
        try {

            // Construct the URL
            final String ACROMINE_BASE_URL =
                    "http://www.nactem.ac.uk/software/acromine/dictionary.py?";

            // Short form user input
            final String QUERY_PARAM = "sf";

            Uri builtUri = Uri.parse(ACROMINE_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, url)
                    .build();

            String theURL = builtUri.toString();

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(theURL));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                rawJson = convertInputStreamToString(inputStream);
            else
                rawJson = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        try {
            return getAcroResultsFromJson(rawJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String[] getAcroResultsFromJson(String rawJson)
            throws JSONException {

        // Array of long forms
        final String ACRO_LFS = "lfs";

        // Long Form
        final String ACRO_LF = "lf";

        // Frequency
        final String ACRO_FREQ = "freq";

        // Origin date
        final String ACRO_SINCE = "since";

        JSONArray acroJsonArray = new JSONArray(rawJson);
        JSONObject acroJson = acroJsonArray.getJSONObject(0);
        JSONArray acroArray = acroJson.getJSONArray(ACRO_LFS);

        // Array holding built result strings
        String[] resultStrs = new String[acroArray.length()];

        for (int i = 0; i < acroArray.length(); i++) {

            String longForm;
            String frequency;
            String originDate;

            // Represents the current entry
            JSONObject currentEntry = acroArray.getJSONObject(i);

            longForm = currentEntry.getString(ACRO_LF);
            frequency = currentEntry.getString(ACRO_FREQ);
            originDate = currentEntry.getString(ACRO_SINCE);

            resultStrs[i] = longForm + ", " + frequency + "," + originDate;
        }

        return resultStrs;
    }

    public class FetchAcroTask extends AsyncTask<String, Void, String[]> {

        ProgressBar progressBar;

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            return GET(params[0]);
        }

        @Override
        protected void onPostExecute(String[] resultStrs) {

            if (resultStrs == null) {
                acroAdapter.add("No results were found");
            } else {
                acroAdapter.addAll(resultStrs);
            }

            // Dismiss loading circle
            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}