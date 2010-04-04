package com.delauneconsulting.AMION;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class IndividualSchedule extends Activity {

    private MyAMIONScheduleAdapter adapter;
    private AMIONDataSet amionDataSet = null;
    private AMIONPerson amionPerson = null;
    private ArrayList<AMIONAssignment> blocks = new ArrayList<AMIONAssignment>(); 
    private String pwd = null;
    private String personName = null;
    
    private ListView lstResults;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.individual_schedule);

        lstResults = (ListView) findViewById(R.id.lstIndividualSchedule);
        
        // Setup the click listener for the ListView items
        OnItemClickListener itemListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                String info = "";
            	
                AMIONAssignment a = (AMIONAssignment) parent.getItemAtPosition(position);
                
                String dateRange = "";
            	if (a.startDate != null) {
            		dateRange += String.format("%s", AMION.sdf.format(a.startDate));
            		if (a.endDate != null)
            			dateRange += String.format(" - %s", AMION.sdf.format(a.endDate));
            	}                	
                	
            	Toast.makeText(
                        getApplication(),
                        String.format("%s [%s] (%s)", a.name, a.type, dateRange),
                        Toast.LENGTH_LONG).show();
            }
        };
        lstResults.setOnItemClickListener(itemListener);

        // Register the ContextMenu for the ListView
        registerForContextMenu(lstResults);

        // Pull the data that was passed by the calling function
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pwd = extras.getString("pwd");
        personName = extras.getString("personName");
        
        amionDataSet = AMION.master.get(pwd);
        
    	//Get stored prefs
    	//SharedPreferences settings = getSharedPreferences(AMION.PREFS_NAME, 0);
        
    	
        // Setup and bind the adapter for the ListView with the ArrayList
        amionPerson = amionDataSet.masterPersonIndex.get(personName.replace(" ", ""));
        if (amionPerson != null) {
	        for (int i=0; i<amionPerson.assignments.size(); i++) {
	        	if (amionPerson.assignments.get(i).type.equalsIgnoreCase("r"))
	        		blocks.add(amionPerson.assignments.get(i));
	        }
        	Collections.sort(blocks, new AMIONAssignment.AMIONAssignmentDateComparator());
        	adapter = new MyAMIONScheduleAdapter(this, R.layout.schedule_list_item, blocks);
	        lstResults.setAdapter(adapter);
	        
	        //Make sure the the current block is visible
	        Calendar c = Calendar.getInstance();
	        int assignmentIndex = 0;
	        for (int i=0; i<blocks.size(); i++) {
        		if (Helper.isDateInRange(Helper.getFormattedDate(c), blocks.get(i))) 
        			assignmentIndex = i;
        	}
	        lstResults.setSelection(assignmentIndex);
        }
        
        // This is a meant to be a generic List page, so you can set the title
        // to whatever here.
		String title = String.format("Block Schedule for %s", personName);
        this.setTitle(title);
        
    }

    // The custom adapter that will handle the ArrayList of AMIONPerson objects
    // in the ListView
    private class MyAMIONScheduleAdapter extends ArrayAdapter<AMIONAssignment> {

        private ArrayList<AMIONAssignment> assignments = null;

        public MyAMIONScheduleAdapter(Context context, int textViewResourceId,
                ArrayList<AMIONAssignment> items) {
            super(context, textViewResourceId, items);
            if (items == null) {
                assignments = new ArrayList<AMIONAssignment>();
            } else {
            	assignments = items;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.schedule_list_item, null);
            }
            
            AMIONAssignment a = assignments.get(position);
            if (a != null && a.type.equalsIgnoreCase("r")) {
                TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
                TextView txtJob = (TextView) v.findViewById(R.id.txtAssignment);
                
                if (txtDate != null) {
                	String dateRange = "";
                	if (a.startDate != null) {
                		dateRange += String.format("%s", AMION.sdf.format(a.startDate));
                		if (a.endDate != null)
                			dateRange += String.format(" - %s", AMION.sdf.format(a.endDate));
                		txtDate.setText(dateRange);
                	}
                }
                
                if (txtJob != null) {
                	txtJob.setText(a.name);
                }
            }
            return v;
        }
    }
}
