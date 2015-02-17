package me.ahirani.acro_api;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class AcroActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acro);

        // Every activity is invoked by an intent

        // getIntent gets the intent and the data contained within it
        Intent intent = getIntent();
        String searchTerm = intent.getStringExtra(MainActivity.EXTRA_MESSAGE).toUpperCase();
        String temp = "";

        for(int i = 0; i < searchTerm.length(); i++) {

            temp += searchTerm.charAt(i);
            temp += '.';
        }
        searchTerm = temp;

        // Populate the textview with the search term
        TextView textView = (TextView) findViewById(R.id.textView_acro);
        textView.setText(searchTerm);

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
}
