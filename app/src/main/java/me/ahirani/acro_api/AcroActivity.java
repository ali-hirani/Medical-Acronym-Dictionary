package me.ahirani.acro_api;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

        // Dummy Data
        String[] data = {
                "mitoxantrone, 1983",
                "Migration inhibition test, 1970",
                "monoiodotyrosine, 1973",
                "Magnetic induction tomography, 2000",
                "metal-insulator transition, 2000",
                "mouse inoculation test, 1969",
                "Massachusetts Institute of Technology, 1989",
                "Mitochondria, 1975",
                "multiple insulin injection therapy, 1976",
                "Minimally invasive therapy, 1993",
                "maximal intimal thickness, 1995",
                "Minimal invasive techniques, 2004",
                "mitomycin, 1982",
                "marrow iron turnover, 1982",
                "N-methylisothiazol-3-one, 1990",
                "The mean input time, 1993"
        };

        List<String> acroList = new ArrayList<>(Arrays.asList(data));

        ArrayAdapter<String> acroAdapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.adapter_layout,
                        acroList);

        ListView listView = (ListView) findViewById(R.id.listview_acro);
        listView.setAdapter(acroAdapter);

        //if (savedInstanceState == null) {
        //    getSupportFragmentManager().beginTransaction()
        //            .add(R.id.containerAcro, new AcroFragment())
        //            .commit();
        }

/*
    public static class AcroFragment extends Fragment {

    public AcroFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {

    // Dummy Data
    String[] data = {
    "mitoxantrone, 1983",
    "Migration inhibition test, 1970",
    "monoiodotyrosine, 1973",
    "Magnetic induction tomography, 2000",
    "metal-insulator transition, 2000",
    "mouse inoculation test, 1969",
    "Massachusetts Institute of Technology, 1989"
    };

    List<String> acroList = new ArrayList<String>(Arrays.asList(data));

    ArrayAdapter<String> acroAdapter =
    new ArrayAdapter<String>(
    getActivity(),
    R.layout.activity_acro,
    R.id.listview_acro,
    acroList);

    View rootView = inflater.inflate(R.layout.activity_acro, container, false);

    ListView listView = (ListView) rootView.findViewById(R.id.listview_acro);
    listView.setAdapter(acroAdapter);
    return rootView;
    }
    }
*/


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
