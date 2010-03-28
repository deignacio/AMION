package com.delauneconsulting.AMION;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class JobFilterList extends ListActivity {

    JobFilterAdapter adapter;
    AMIONReport amionReport = null;
    private String pwd = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Setup the click listener for the ListView items
        OnItemClickListener itemListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                String chosenFilter = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplication(), ResultList.class);
                intent.putExtra("pwd", pwd);
                intent.putExtra("filter", chosenFilter);
                startActivity(intent);
            }
        };
        getListView().setOnItemClickListener(itemListener);

        // Pull the data that was passed by the calling function
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pwd = extras.getString("pwd");

        amionReport = AMION.reports.get(pwd);

        // This is a meant to be a generic List page, so you can set the title
        // to whatever here.
        this.setTitle(amionReport.getDefaultTitle());

        // Setup and bind the adapter for the ListView with the ArrayList
        adapter = new JobFilterAdapter(this, R.layout.list_item, amionReport.getFilters());
        setListAdapter(adapter);
    }

    // The custom adapter that will handle the ArrayList of AMIONPerson objects
    // in the ListView
    private class JobFilterAdapter extends ArrayAdapter<String> {

        private ArrayList<String> filters= null;

        public JobFilterAdapter(Context context, int textViewResourceId,
                ArrayList<String> items) {
            super(context, textViewResourceId, items);
            if (items == null) {
                filters = new ArrayList<String>();
            } else {
                filters = items;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            String filter = filters.get(position);
            if (filters != null) {
                TextView txtName = (TextView) v.findViewById(R.id.txtName);
                TextView txtJob = (TextView) v.findViewById(R.id.txtJob);
                if (txtName != null) {
                    txtName.setText(filter);
                }
                if (txtJob != null) {
                    int size = amionReport.getPeople(filter).size();
                    String subtitle;
                    if (size == 1) {
                        subtitle = size + "job";
                    } else {
                        subtitle = size + "jobs";
                    }
                    txtJob.setText(subtitle);
                }
            }
            return v;
        }
    }
}
