package com.delauneconsulting.AMION;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ResultList extends Activity {
	
    private MyAMIONPersonAdapter adapter;
    //private AMIONReport amionReport = null;
    private AMIONDataSet amionDataSet = null;
    private String pwd = null;
    private Calendar c = Calendar.getInstance();
    
    private String currentSort = "Sort by Name";
    
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    
    private ListView lstResults;
    private TextView lblCurrentDate;
    private ImageButton btnPrevDate;
    private ImageButton btnNextDate;
    private Spinner ddClasses;
    private Spinner ddAssignments;

    static final int DATE_DIALOG_ID = 0;
    //static final int FILTER_DIALOG_ID = 1;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultlist);

        lstResults = (ListView) findViewById(R.id.lstResults);
        
        // Setup the click listener for the ListView items
        OnItemClickListener itemListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                
            	AMIONPerson p = (AMIONPerson) parent.getItemAtPosition(position);
            	
            	Intent intent = new Intent(getApplication(), IndividualSchedule.class);
                intent.putExtra("pwd", pwd);
                intent.putExtra("personName", p.name);
                startActivity(intent);
            	
                /*
            	String assignments = "";
            	AMIONAssignment a;
            	for (int i=0; i<p.assignments.size(); i++) {
            		a = p.assignments.get(i);
            		if (Helper.isDateInRange(Helper.getFormattedDate(c), a))
            			assignments += "*";
            		assignments += String.format("%s [%s] ", a.name, a.type);
            		
            		if (a.startDate != null) {
            			assignments += String.format(" (%s", AMION.sdf.format(a.startDate));
            			if (a.endDate != null)
            				assignments += String.format("-%s", AMION.sdf.format(a.endDate));
            			assignments += ")";
            		}
            		assignments += "\n";
            	}
            	
            	Toast.makeText(
                        getApplication(),
                        assignments,
                        //"toString():\n" + ((AMIONPerson) parent.getItemAtPosition(position)).toString() +
                        //"\n\nRAW:\n" + ((AMIONPerson) parent.getItemAtPosition(position)).comment + 
                        //"\n\nUrl for Report:\n" + amionReport.getUrl(),
                        Toast.LENGTH_LONG).show();
                */
            	
            }
        };
        lstResults.setOnItemClickListener(itemListener);

        // Register the ContextMenu for the ListView
        registerForContextMenu(lstResults);

        // Pull the data that was passed by the calling function
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        pwd = extras.getString("pwd");
        
        //amionReport = new OncallReport(pwd, c);
        amionDataSet = AMION.master.get(pwd);
        if (amionDataSet == null) {
        	amionDataSet = new AMIONDataSet(pwd);
        	AMION.master.put(pwd, amionDataSet);
        }
        
    	//Get stored prefs
    	SharedPreferences settings = getSharedPreferences(AMION.PREFS_NAME, 0);
        
    	//Setup the drop downs
    	//TODO: Should the last selected be maintained according to password used?
    	ddClasses = (Spinner) findViewById(R.id.ddClasses);
    	ArrayAdapter<String> ddClassesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, amionDataSet.classList);
        ddClasses.setAdapter(ddClassesAdapter);
        if (settings.getString("lastClass", "").length() > 0)
        	ddClasses.setSelection(amionDataSet.classList.indexOf(settings.getString("lastClass", "")));
        ddClasses.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
        ddAssignments = (Spinner) findViewById(R.id.ddAssignments);
        Collections.sort(amionDataSet.rotationList);
    	ArrayAdapter<String> ddAssignmentsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, amionDataSet.rotationList);
        ddAssignments.setAdapter(ddAssignmentsAdapter);
        if (settings.getString("lastAssignment", "").length() > 0)
        	ddAssignments.setSelection(amionDataSet.rotationList.indexOf(settings.getString("lastAssignment", "")));
        ddAssignments.setOnItemSelectedListener(new MyOnItemSelectedListener());
        
    	//Set the date in the header control
        lblCurrentDate = (TextView) findViewById(R.id.lblCurrentDate);
        lblCurrentDate.setText(Helper.getFormattedDate(c));
        
        //Setup the button listener for the date control
    	OnClickListener btnDateListener = new OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplication(), amionReport.getUrl(), Toast.LENGTH_SHORT).show();
            	showDialog(DATE_DIALOG_ID);
            }
        };
        lblCurrentDate.setOnClickListener(btnDateListener);
        
        //Setup the DatePicker dialog for when a user click the date button
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				//Calendar c = amionReport.getDate();
            	c.set(year, monthOfYear, dayOfMonth);
            	lblCurrentDate.setText(Helper.getFormattedDate(c));
            	
            	//amionReport = new OncallReport(pwd, c);
            	refreshReport();
			}
		};
		
        // Setup the click listener for the next day button
        btnNextDate = (ImageButton) findViewById(R.id.btnNextDate);
        OnClickListener btnNextDateListener = new OnClickListener() {
            public void onClick(View v) {
            	//Calendar c = amionReport.getDate();
            	c.add(Calendar.DATE, 1);
            	lblCurrentDate.setText(Helper.getFormattedDate(c));
            	
            	//amionReport = new OncallReport(pwd, c);
            	refreshReport();
            }
        };
        btnNextDate.setOnClickListener(btnNextDateListener);
        
        // Setup the click listener for the prev day button
        btnPrevDate = (ImageButton) findViewById(R.id.btnPrevDate);
        OnClickListener btnPrevDateListener = new OnClickListener() {
            public void onClick(View v) {
            	//Calendar c = amionReport.getDate();
            	c.add(Calendar.DATE, -1);
            	lblCurrentDate.setText(Helper.getFormattedDate(c));
            	
            	//amionReport = new OncallReport(pwd, c);
            	refreshReport();
            }
        };
        btnPrevDate.setOnClickListener(btnPrevDateListener);
        
        // Setup and bind the adapter for the ListView with the ArrayList
        ArrayList<AMIONPerson> tempList = new ArrayList<AMIONPerson>();
        Collections.sort(tempList, new AMIONPerson.AMIONPersonComparator());
        adapter = new MyAMIONPersonAdapter(this, R.layout.list_item, tempList);
        //PerformSort();
        lstResults.setAdapter(adapter);
        
        // This is a meant to be a generic List page, so you can set the title
        // to whatever here.
		//String title = amionReport.getTitle();
        this.setTitle("AMION - " + pwd);
    }

    private void refreshReport() {
    	//Toast.makeText(getApplication(), ddClasses.getSelectedItem().toString() + " " + ddAssignments.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
    	
    	//ArrayList<AMIONPerson> tempList = amionReport.getPeople(ddClasses.getSelectedItem().toString(), ddAssignments.getSelectedItem().toString());
    	ArrayList<AMIONPerson> tempList = amionDataSet.getPeople(c, ddClasses.getSelectedItem().toString(), ddAssignments.getSelectedItem().toString());
    	Collections.sort(tempList, new AMIONPerson.AMIONPersonComparator());
        adapter.people.clear();
        adapter.people.addAll(tempList);
        //PerformSort();
        adapter.notifyDataSetChanged();
        
        /*
        String title = amionReport.getDefaultTitle();
		if(filters.size()>0) {
			title+=" (filter enabled)";
		}
        this.setTitle(title);
        */
    }
    
    public class MyOnItemSelectedListener implements OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        	SharedPreferences settings = getSharedPreferences(AMION.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("lastClass", ddClasses.getSelectedItem().toString());
            editor.putString("lastAssignment", ddAssignments.getSelectedItem().toString());
            editor.commit();
        	
        	refreshReport();
        }

        public void onNothingSelected(AdapterView parent) {
          // Do nothing.
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    	case DATE_DIALOG_ID:
    		//Calendar calendar = amionReport.getDate();
    		return new DatePickerDialog(this, mDateSetListener, c.getTime().getYear()+1900, c.getTime().getMonth(), c.getTime().getDate());
    	}
    	return null;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        AMIONPerson p = (AMIONPerson) lstResults.getAdapter().getItem(info.position);
        p.populateContextMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    	AMIONPerson p = (AMIONPerson) lstResults.getAdapter().getItem(info.position);
    	
        int itemId = item.getItemId();
        switch (itemId) {
        case 1:
        	ddClasses.setSelection(0);
        	//ddAssignments.setSelection(amionReport.getAssignments().indexOf(p.assignmentName));
        	break;
        case 2:
        	SharedPreferences settings = getSharedPreferences(AMION.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(String.format("myId%s", pwd), p.id);
            editor.commit();
            
            Toast.makeText(getApplication(), String.format("%s has been set as you.", p.name), Toast.LENGTH_SHORT).show();
        	break;
        case 3:

        	String assignments = "";
        	AMIONAssignment a;
        	for (int i=0; i<p.assignments.size(); i++) {
        		a = p.assignments.get(i);
        		if (Helper.isDateInRange(Helper.getFormattedDate(c), a))
        			assignments += "*";
        		assignments += String.format("%s [%s] ", a.name, a.type);
        		
        		if (a.startDate != null) {
        			assignments += String.format(" (%s", AMION.sdf.format(a.startDate));
        			if (a.endDate != null)
        				assignments += String.format("-%s", AMION.sdf.format(a.endDate));
        			assignments += ")";
        		}
        		assignments += "\n";
        	}
        	
        	Toast.makeText(
                    getApplication(),
                    assignments,
                    //"toString():\n" + ((AMIONPerson) parent.getItemAtPosition(position)).toString() +
                    //"\n\nRAW:\n" + ((AMIONPerson) parent.getItemAtPosition(position)).comment + 
                    //"\n\nUrl for Report:\n" + amionReport.getUrl(),
                    Toast.LENGTH_LONG).show();
        	
        	break;
        }
        
        //p.onContextMenuItemSelected(getApplication(), item);
        
    	/*
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(item.getTitle() + " " + p.toString());
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        */
    	
        return true;
    }

    @Override
    // Create the main options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, Menu.FIRST, Menu.NONE, "Refresh").setIcon(R.drawable.ic_menu_refresh);
        menu.add(0, Menu.FIRST+1, Menu.NONE, "Sort by Job").setIcon(android.R.drawable.ic_menu_sort_alphabetically);
        //menu.add(0, Menu.FIRST+2, Menu.NONE, "Filter Jobs").setIcon(android.R.drawable.ic_menu_search);

        return true;
    }
    @Override
    // Setup the action performed for menu items (the right way)
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        
        if (itemId == 1) {
        	refreshReport();
        } else if (itemId == 2) {
            if (currentSort.equalsIgnoreCase("Sort by Job")) {
            	item.setTitle(currentSort);
            	currentSort = "Sort by Name";
            } else if (currentSort.equalsIgnoreCase("Sort by Name")) {
            	item.setTitle(currentSort);
            	currentSort = "Sort by Job";
            }
            PerformSort();
            adapter.notifyDataSetChanged();
        }
        
        return true;
    }

    //Sorting
    private void PerformSort() {
    	
    	/*
    	if (currentSort.equalsIgnoreCase("Sort by Job")) {
            Collections.sort(adapter.people, new AMIONPerson.AMIONPersonAssignmentComparator());
        } else if (currentSort.equalsIgnoreCase("Sort by Name")) {
            Collections.sort(adapter.people, new AMIONPerson.AMIONPersonComparator());
        }
        */
    	
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
            
            //see if "me" has been set
            //SharedPreferences settings = getSharedPreferences(AMION.PREFS_NAME, 0);
            //String myId = settings.getString(String.format("myId%s", pwd), ""); 
            
            AMIONPerson p = people.get(position);
            if (p != null) {
                TextView txtName = (TextView) v.findViewById(R.id.txtName);
                TextView txtJob = (TextView) v.findViewById(R.id.txtAssignment);
                
                boolean onCall = false;
                if (txtJob != null) {
                    String assignments = "";
                    int counter = 0;
                	for (int i=0; i<p.assignments.size(); i++) {
                		if (Helper.isDateInRange(Helper.getFormattedDate(c), p.assignments.get(i))) {
                			if (counter>0)
                				assignments += " | ";
                			assignments += p.assignments.get(i).name;
                			counter++;
                			
                			if (p.assignments.get(i).type.equalsIgnoreCase("s"))
                				onCall = true;
                		}
                	}
                    txtJob.setText(assignments);
                }
                
                String personName = p.name;
                if (onCall)
                	personName += " *";
                if (txtName != null) {
                    txtName.setText(personName);
                }
                
                //if (p.id.equalsIgnoreCase(myId)) {
                //	txtName.setTextColor(Color.RED);
                //}
            }
            return v;
        }
    }
}
