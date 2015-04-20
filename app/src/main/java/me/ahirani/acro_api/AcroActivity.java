package me.ahirani.acro_api;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;


public class AcroActivity extends ActionBarActivity {

    private static final String ACRO_LF = "lf";
    private static final String ACRO_SINCE = "since";

    private ListView listView;

    static ArrayList<HashMap<String, String>> longFormList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fade animation because yolo
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setContentView(R.layout.activity_acro);

        // getIntent gets the intent and the data contained within it
        Intent intent = getIntent();
        String searchTerm = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

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

        longFormList = new ArrayList<HashMap<String, String>>();

        listView = (ListView) findViewById(R.id.list);

        /*
        TextView networkStatus = (TextView) findViewById(R.id.network_status);

        // check if you are connected or not
        if (isConnected()) {
        networkStatus.setText("Connected");
        } else {
        networkStatus.setText("NOT connected");
        }
        */
        new FetchAcroTask().execute(searchTerm);
    }

    public void searchWeb(String query) {

        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_acro, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public static List GET(String url) {
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
                rawJson = "";

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

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static ArrayList<HashMap<String, String>> getAcroResultsFromJson(String rawJson)
            throws JSONException {

        // Array of long forms
        final String ACRO_LFS = "lfs";

        // Long Form
        //final String ACRO_LF = "lf";

        // Frequency
        final String ACRO_FREQ = "freq";

        // Origin date
        //final String ACRO_SINCE = "since";

        JSONArray acroJsonArray = new JSONArray(rawJson);
        JSONObject acroJson = acroJsonArray.getJSONObject(0);
        JSONArray acroArray = acroJson.getJSONArray(ACRO_LFS);

        for (int i = 0; i < acroArray.length(); i++) {

            String longForm;
            String frequency;
            String originDate;

            // Represents the current entry
            JSONObject currentEntry = acroArray.getJSONObject(i);

            longForm = currentEntry.getString(ACRO_LF);
            frequency = currentEntry.getString(ACRO_FREQ);
            originDate = currentEntry.getString(ACRO_SINCE);

            // Capitalize words
            longForm = capitalizeString(longForm);

            // tmp hashmap for single long form entry
            HashMap<String, String> longFormMap = new HashMap<String, String>();

            // Add each child node to HashMap key
            longFormMap.put(ACRO_LF, longForm);
            longFormMap.put(ACRO_SINCE, originDate);

            longFormList.add(longFormMap);
        }

        return longFormList;
    }

    public class FetchAcroTask extends AsyncTask<String, Void, List> {

        ProgressBar progressBar;


        @Override
        protected List doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            return GET(params[0]);
        }

        @Override
        protected void onPostExecute(List resultStrs) {
            // Dismiss loading circle
            progressBar = (ProgressBar) findViewById(R.id.progressBar1);
            progressBar.setVisibility(View.INVISIBLE);

            View emptyTextView = findViewById(R.id.empty);
            if (longFormList != null && longFormList.size() > 0) {
                ListAdapter adapter = new SimpleAdapter(
                        AcroActivity.this, longFormList,
                        R.layout.list_item, new String[]{ACRO_LF, ACRO_SINCE}, new int[]
                        {R.id.longform, R.id.year});
                listView.setAdapter(adapter);

                listView.setVisibility(View.VISIBLE);

                // Gone is cheaper actually removes it
                emptyTextView.setVisibility(View.GONE);
            } else {
                listView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
            }

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (isConnected()) {

                        HashMap tempMap = (HashMap) parent.getItemAtPosition(position);
                        String myLongForm = (String) tempMap.get(ACRO_LF);

                        searchWeb(myLongForm);

                        Toast toast = Toast.makeText(getApplicationContext(), "Searched: " + myLongForm, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Toast toast1 = Toast.makeText(getApplicationContext(), "You are not connected", Toast.LENGTH_SHORT);
                        toast1.show();
                    }
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    HashMap tempMap = (HashMap) parent.getItemAtPosition(position);
                    String myLongForm = (String) tempMap.get(ACRO_LF);

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Long Form", myLongForm);
                    clipboard.setPrimaryClip(clip);

                    Toast toast = Toast.makeText(getApplicationContext(), "Copied: " + myLongForm, Toast.LENGTH_SHORT);
                    toast.show();
                    return true;
                }
            });
        }
    }
}