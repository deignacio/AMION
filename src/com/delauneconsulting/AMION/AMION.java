package com.delauneconsulting.AMION;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class AMION extends Activity {

    public static final String PREFS_NAME = "AmionPrefs";
    public static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
    public static SharedPreferences settings;
    Context context = this;

    public static HashMap<String, AMIONDataSet> master = new HashMap<String, AMIONDataSet>();
    
    private Button loginButton;
    private EditText password;

    private ListView lstRecentSearches;
    ArrayAdapter<String> recentSearchesAdapter;
    ArrayList<String> searchList;
    String recentSearchItemSelected;
    //public static HashMap<String, AMIONReport> reports = new HashMap<String, AMIONReport>();;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get Controls
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login);
        lstRecentSearches = (ListView) findViewById(R.id.lstRecentSearches);

        // Set the click listener for the loginButton
        // will PerformSearch with the text from the password EditView
        OnClickListener loginButtonListener = new OnClickListener() {
            public void onClick(View v) {
                Editable pwd = password.getText();
                PerformSearch(pwd.toString(), true);
            }
        };
        loginButton.setOnClickListener(loginButtonListener);

        // Check to see if there are any saved searches
        // saves as a comma delimited string in a single prefs item
        settings = getSharedPreferences(PREFS_NAME, 0);
        String recentString = settings.getString("recentSearches", "");
        // fill an ArrayList with the saved search items
        searchList = new ArrayList<String>(Arrays.asList(recentString.split(",")));
        // create and fill an adapter for the ListView
        recentSearchesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                searchList);
        // set the adapter for the ListView
        lstRecentSearches.setAdapter(recentSearchesAdapter);

        // set the click listener for the ListView items.
        // will PerformSearch with the item
        OnItemClickListener lstRecentSearchesClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                PerformSearch(parent.getItemAtPosition(position).toString(), false);
            }
        };
        lstRecentSearches.setOnItemClickListener(lstRecentSearchesClickListener);

        // set the longclick listener for the ListView item
        // will prompt the user if they want to delete the item
        OnItemLongClickListener lstRecentSearchesLongClickListener = new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View arg1, int position, long arg3) {

                recentSearchItemSelected = parent.getItemAtPosition(position).toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // builder.setMessage("Do you really want to delete " +
                // recentSearchItemSelected + "?");
                builder.setMessage("Do you really want to delete ?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        recentSearchesAdapter.remove(recentSearchItemSelected);
                        SaveSearches();
                        recentSearchesAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
                return false;
            }
        };
        lstRecentSearches.setOnItemLongClickListener(lstRecentSearchesLongClickListener);
    }

    @Override
    // Setup the "menu" for this screen (when the menu button is clicked on the
    // phone)
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, Menu.FIRST, Menu.NONE, "Settings").setIcon(
                android.R.drawable.ic_menu_preferences);
        menu.add(0, Menu.FIRST + 1, Menu.NONE, "About").setIcon(
                android.R.drawable.ic_menu_info_details);
        menu.add(0, Menu.FIRST + 2, Menu.NONE, "Quit").setIcon(
                android.R.drawable.ic_menu_close_clear_cancel);

        return true;
    }

    @Override
    // Setup the action performed for menu items (the right way)
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == 1) {
            Intent intent = new Intent(getApplication(), AMIONPrefs.class);
            startActivity(intent);
        } else if (item.getItemId() == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.aboutUs);
            builder.setCancelable(true);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else if (item.getItemId() == 3) {
        	finish();
        }
        return true;
    }

    // Takes the password, trims it, asks the website for the info based on the
    // password,
    // saves the password (if necessary) and passes raw response to the next
    // screen.
    private void PerformSearch(String pwd, Boolean saveSearch) {
        try {

            pwd = pwd.trim();

            //AMIONReport report = null;
            //if (reports.containsKey(pwd)) {
            //    report = reports.get(pwd);
            //} else {
                // currently only OncallReport is supported
            	//Calendar c = Calendar.getInstance();
                //report = new OncallReport(pwd, c);
                //reports.put(pwd, report);
            //}

            if (saveSearch)
                SaveLastSearch(pwd.toString());

            Intent intent = new Intent(getApplication(), ResultList.class);
            intent.putExtra("pwd", pwd);
            startActivity(intent);

        } catch (Exception e) {
        }
    }

    // Saves the last password used if not in the list
    private void SaveLastSearch(String pwd) {
        if (pwd.length() > 0) {
            if (!searchList.contains(pwd)) {
                recentSearchesAdapter.add(pwd);
                SaveSearches();
                recentSearchesAdapter.notifyDataSetChanged();
            }
        }
    }

    // Writes all of the saved passwords to prefs in to correct format.
    private void SaveSearches() {
        String recentString = "";
        if (searchList.size() > 0) {
            for (int i = 0; i < searchList.size(); i++) {
                recentString += searchList.get(i).toString();
                if (i != searchList.size() - 1)
                    recentString += ",";
            }
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("recentSearches", recentString);
        editor.commit();
    }

}
