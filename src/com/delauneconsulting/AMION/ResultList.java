package com.delauneconsulting.AMION;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ResultList extends Activity {

    MyAMIONPersonAdapter adapter;
    AMIONReport amionReport = null;
    //private final Context context = this;
    private String pwd = null;
    private String filter = null;
    private String currentSort = "Sort by Name";
    
    private ListView lstResults;
    private TextView lblCurrentDate;
    private ImageButton btnPrevDate;
    private ImageButton btnNextDate;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultlist);

        lstResults = (ListView) findViewById(R.id.lstResults);
        
        // Setup the click listener for the ListView items
        OnItemClickListener itemListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Toast.makeText(
                        getApplication(),
                        ((AMIONPerson) parent.getItemAtPosition(position)).comment,
                        Toast.LENGTH_SHORT).show();
            }
        };
        
        //getListView().setOnItemClickListener(itemListener);
        lstResults.setOnItemClickListener(itemListener);

        // Register the ContextMenu for the ListView
        registerForContextMenu(lstResults);

        // Pull the data that was passed by the calling function
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pwd = extras.getString("pwd");
        filter = extras.getString("filter"); // we want the null if no filter

        amionReport = AMION.reports.get(pwd);

        lblCurrentDate = (TextView) findViewById(R.id.lblCurrentDate);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    	Calendar c = amionReport.getDate();
    	c.add(Calendar.DATE, 1);
    	lblCurrentDate.setText(sdf.format(c.getTime()));
        
    	OnClickListener btnDateListener = new OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplication(),
                        amionReport.getUrl(),
                        Toast.LENGTH_SHORT).show();
            }
        };
        lblCurrentDate.setOnClickListener(btnDateListener);
        
    	// This is a meant to be a generic List page, so you can set the title
        // to whatever here.
        this.setTitle(amionReport.getTitle(filter));

        // Setup the click listener for the next day button
        btnNextDate = (ImageButton) findViewById(R.id.btnNextDate);
        OnClickListener btnNextDateListener = new OnClickListener() {
            public void onClick(View v) {
            	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            	Calendar c = amionReport.getDate();
            	c.add(Calendar.DATE, 1);
            	lblCurrentDate.setText(sdf.format(c.getTime()));
            	
            	amionReport = new OncallReport(pwd, c);
            	ArrayList<AMIONPerson> tempList = amionReport.getPeople(filter);
                adapter.people.clear();
                adapter.people.addAll(tempList);
                PerformSort();
                adapter.notifyDataSetChanged();
            }
        };
        btnNextDate.setOnClickListener(btnNextDateListener);
        
        // Setup the click listener for the prev day button
        btnPrevDate = (ImageButton) findViewById(R.id.btnPrevDate);
        OnClickListener btnPrevDateListener = new OnClickListener() {
            public void onClick(View v) {
            	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            	Calendar c = amionReport.getDate();
            	c.add(Calendar.DATE, -1);
            	lblCurrentDate.setText(sdf.format(c.getTime()));
            	
            	amionReport = new OncallReport(pwd, c);
            	ArrayList<AMIONPerson> tempList = amionReport.getPeople(filter);
                adapter.people.clear();
                adapter.people.addAll(tempList);
                PerformSort();
                adapter.notifyDataSetChanged();
            }
        };
        btnPrevDate.setOnClickListener(btnPrevDateListener);
        
        // Setup and bind the adapter for the ListView with the ArrayList
        ArrayList<AMIONPerson> tempList = amionReport.getPeople(filter);
        adapter = new MyAMIONPersonAdapter(this, R.layout.list_item, tempList);
        PerformSort();
        lstResults.setAdapter(adapter);
        //setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        AMIONPerson p = (AMIONPerson) lstResults.getAdapter().getItem(info.position);
        p.populateContextMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        AMIONPerson p = (AMIONPerson) lstResults.getAdapter().getItem(info.position);
        p.onContextMenuItemSelected(getApplication(), item);
        return true;
    }

    @Override
    // Create the main options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, Menu.FIRST, Menu.NONE, "Refresh").setIcon(R.drawable.ic_menu_refresh);
        menu.add(0, Menu.FIRST+1, Menu.NONE, "Sort by Job").setIcon(android.R.drawable.ic_menu_sort_alphabetically);
        menu.add(0, Menu.FIRST+2, Menu.NONE, "Filter Jobs").setIcon(android.R.drawable.ic_menu_search);

        return true;
    }
    @Override
    // Setup the action performed for menu items (the right way)
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == 2) {
            if (currentSort.equalsIgnoreCase("Sort by Job")) {
            	item.setTitle(currentSort);
            	currentSort = "Sort by Name";
            } else if (currentSort.equalsIgnoreCase("Sort by Name")) {
            	item.setTitle(currentSort);
            	currentSort = "Sort by Job";
            }
            PerformSort();
            adapter.notifyDataSetChanged();
        } else if (itemId == 3) {
            Intent intent = new Intent(getApplication(), JobFilterList.class);
            intent.putExtra("pwd", pwd);
            startActivity(intent);
        }
        return true;
    }

    //Sorting
    private void PerformSort() {
    	
    	if (currentSort.equalsIgnoreCase("Sort by Job")) {
            Collections.sort(adapter.people, new AMIONPersonJobComparator());
        } else if (currentSort.equalsIgnoreCase("Sort by Name")) {
            Collections.sort(adapter.people, new AMIONPersonComparator());
        }
    	
    }
    
    // The custom adapter that will handle the ArrayList of AMIONPerson objects
    // in the ListView
    private class MyAMIONPersonAdapter extends ArrayAdapter<AMIONPerson> {

        private ArrayList<AMIONPerson> people = null;

        public MyAMIONPersonAdapter(Context context, int textViewResourceId,
                ArrayList<AMIONPerson> items) {
            super(context, textViewResourceId, items);
            if (items == null) {
                people = new ArrayList<AMIONPerson>();
            } else {
                people = items;
            }
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
