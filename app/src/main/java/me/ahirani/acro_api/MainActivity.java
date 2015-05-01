package me.ahirani.acro_api;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    // Good practice to define keys for intent extras using package name
    // This ensure uniqueness in case your app interacts with other apps
    public static final String EXTRA_MESSAGE = "me.ahirani.acro_api.MESSAGE";

    private AutoCompleteTextView editText;
    private AcroDatabase database;
    private List<String> searchTermHistory = new ArrayList<>();

    // Called when user clicks the main button
    // Must be public, void return, and pass in the view that was clicked
    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(MainActivity.this, editText);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        editText.setCursorVisible(false);
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void displaySearchResults(View view) {

        // Create an intent and pass in the context
        // Pass in the class of what the intent is being delivered to
        Intent intent = new Intent(this, AcroActivity.class);

        if (isConnected()) {

            // String to hold the searched acronym
            String searchTerm = editText.getText().toString();

            //removes spaces and periods from search string
            searchTerm = searchTerm.replaceAll("\\s+", "").replaceAll("\\.", "");
            database.insertSearchTerm(searchTerm);

            if (searchTerm != null && searchTerm.length() > 1) {

                // Key Name and value respectively
                intent.putExtra(EXTRA_MESSAGE, searchTerm);

                // Start the activity and pass in the intent
                startActivity(intent);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "You are not connected", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pass application context instead of activity context because it persists throughout app life.
        database = AcroDatabase.getInstance(getApplicationContext());

        setContentView(R.layout.activity_main);
        setupUI(findViewById(R.id.main));
        findViewById(R.id.search_button_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySearchResults(view);
            }
        });

        InputFilter lengthFilter = new InputFilter.LengthFilter(8);
        editText = (AutoCompleteTextView) findViewById(R.id.editText);
        editText.setFilters(new InputFilter[]{new InputFilter.AllCaps(), lengthFilter});
        editText.setCursorVisible(false);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setCursorVisible(true);
                if (event.getAction() == MotionEvent.ACTION_UP) {

                    // drawable is touched
                    if (event.getRawX() >= editText.getRight() - editText.getTotalPaddingRight()) {
                        editText.setText("");
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateSearchHistory();
        ArrayAdapter<String> textAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, searchTermHistory);
        editText.setAdapter(textAdapter);
    }

    @Override
    protected void onStop() {
        database.close();
        super.onStop();
    }

    private void populateSearchHistory() {
        final Cursor cursor = database.queryAllSearchTerms();
        searchTermHistory.clear();
        if (cursor.moveToFirst()) {
            do {
                String searchTerm = cursor.getString(cursor.getColumnIndex(AcroDatabase.TABLE_SEARCH_COLUMN_SEARCH_TERM));
                searchTermHistory.add((searchTerm));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
    }

/*
@Override
public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
getMenuInflater().inflate(R.menu.menu_main, menu);
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
*/
}