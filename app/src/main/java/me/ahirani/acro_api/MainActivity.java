package me.ahirani.acro_api;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    // Good practice to define keys for intent extras using package name
    // This ensure uniqueness in case your app interacts with other apps
    public static final String EXTRA_MESSAGE = "me.ahirani.acro_api.MESSAGE";

    public EditText editText;

    // Called when user clicks the main button
    // Must be public, void return, and pass in the view that was clicked

    public void displaySearchResults(View view){

        // Create an intent and pass in the context
        // Pass in the class of what the intent is being delivered to
        Intent intent = new Intent(this, AcroActivity.class);

        // Get the EditText element
        editText = (EditText) findViewById(R.id.editText);

        // String to hold the searched acronym
        String nameHolder = editText.getText().toString();

        // Key Name and value respectively
        intent.putExtra(EXTRA_MESSAGE, nameHolder);

        // Start the activity and pass in the intent
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button x = (Button) findViewById(R.id.clear_txt);
        x.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editText = (EditText) findViewById(R.id.editText);
                editText.setText("");
            }});
    }

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
}