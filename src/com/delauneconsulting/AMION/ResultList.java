package com.delauneconsulting.AMION;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ResultList extends ListActivity {

    MyAMIONPersonAdapter adapter;
    AMIONReport amionReport = null;
    private final Context context = this;
    private String filter = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the click listener for the ListView items
        OnItemClickListener itemListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Toast.makeText(
                        context,
                        ((AMIONPerson) parent.getItemAtPosition(position)).comment + " "
                                + ((AMIONPerson) parent.getItemAtPosition(position)).phoneNumber,
                        Toast.LENGTH_SHORT).show();
            }
        };
        getListView().setOnItemClickListener(itemListener);

        // Register the ContextMenu for the ListView
        registerForContextMenu(getListView());

        // Pull the data that was passed by the calling function
        Intent intent = getIntent();
        // String httpResponse = intent.getDataString();
        Bundle extras = intent.getExtras();
        String pwd = extras.getString("pwd");
        filter = extras.getString("filter");

        amionReport = AMION.reports.get(pwd);

        // This is a meant to be a generic List page, so you can set the title
        // to whatever here.
        this.setTitle(amionReport.getTitle(filter));

        // Setup and bind the adapter for the ListView with the ArrayList
        adapter = new MyAMIONPersonAdapter(this, R.layout.list_item, amionReport.getPeople(filter));
        setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        AMIONPerson p = (AMIONPerson) getListAdapter().getItem(info.position);
        p.populateContextMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        AMIONPerson p = (AMIONPerson) getListAdapter().getItem(info.position);
        p.onContextMenuItemSelected(context, item);
        return true;
    }

    @Override
    // Create the main options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, Menu.FIRST, Menu.NONE, "Refresh"); //.setIcon(android.R.drawable.ic_menu_refresh);
        menu.add(0, Menu.FIRST+1, Menu.NONE, "Sort by Job").setIcon(android.R.drawable.ic_menu_sort_alphabetically);
        
        return true;
    }
    @Override
    // Setup the action performed for menu items (the right way)
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 2) {
            if (item.getTitle().toString().equalsIgnoreCase("Sort by Job")) {
                Collections.sort(amionReport.getPeople(filter), new AMIONPersonJobComparator());
                item.setTitle("Sort by Name");
            } else if (item.getTitle().toString().equalsIgnoreCase("Sort by Name")) {
                Collections.sort(amionReport.getPeople(filter), new AMIONPersonComparator());
                item.setTitle("Sort by Job");
            }
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    // The custom adapter that will handle the ArrayList of AMIONPerson objects
    // in the ListView
    private class MyAMIONPersonAdapter extends ArrayAdapter<AMIONPerson> {

        private ArrayList<AMIONPerson> people = null;

        public MyAMIONPersonAdapter(Context context, int textViewResourceId,
                ArrayList<AMIONPerson> items) {
            super(context, textViewResourceId, items);
            this.people = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            AMIONPerson p = people.get(position);
            if (p != null) {
                TextView txtName = (TextView) v.findViewById(R.id.txtName);
                TextView txtJob = (TextView) v.findViewById(R.id.txtJob);
                if (txtName != null) {
                    txtName.setText(p.toString());
                }
                if (txtJob != null) {
                    txtJob.setText(p.currentJob);
                }
            }
            return v;
        }
    }
}
