package com.delauneconsulting.AMION;

import java.util.Comparator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;

public class AMIONPerson {

	public String id = "";
	public String backupId = "";
	public String className = "";
    public String name = "";
    public String assignmentDate = "";
    public String assignmentStartTime = "";
    public String assignmentEndTime = "";
    public String assignmentName = "";
    public String assignmentType = "";
    public String comment = "";
    
    public boolean existsInContacts = false;
    public String phoneNumber = "";

    public AMIONPerson() {
    }
    public AMIONPerson(String strToParse) {
    	this.comment = strToParse;

        int index = strToParse.indexOf("\"", 2);
        String personName = strToParse.substring(1, index);

        // TODO: Split first and last names out
        this.name = personName;

        // get rid of the name field, since we already have it, then
        // clean up everything else
        strToParse = strToParse.replace("\"" + personName + "\",", "");
        String[] temp = strToParse.split(",");
        for (int j = 0; j < temp.length; j++) {
            temp[j] = temp[j].trim();
            if (temp[j].contains("\"")) {
                temp[j] = temp[j].replace("\"", "").trim();
            }
        }

        //this is the person id value
        this.id = temp[0];
        this.backupId = temp[1];
        
        //this is the job value
        this.assignmentName = temp[2];
    }

    public String toString() {
        return name;
    }

    public void populateContextMenu(ContextMenu menu) {
        menu.setHeaderTitle(toString());

        menu.add(0, Menu.FIRST, Menu.NONE, "View Schedule");
        menu.add(0, Menu.FIRST + 1, Menu.NONE, "Send Page");
        menu.add(0, Menu.FIRST + 2, Menu.NONE, "Link Contact");
    }

    public void onContextMenuItemSelected(Context context, MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(item.getItemId() + " " + item.getTitle() + " " + toString());
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    
    public static class AMIONPersonJobComparator implements Comparator<AMIONPerson> {
        public int compare(AMIONPerson p1, AMIONPerson p2) {

            // parameter are of type Object, so we have to downcast it to Employee
            // objects
            String p1Job = p1.assignmentName;
            String p2Job = p2.assignmentName;

            // uses compareTo method of String class to compare names of the
            // employee
            return p1Job.compareTo(p2Job);
        }
    }
    
    public static class AMIONPersonComparator implements Comparator<AMIONPerson> {
        public int compare(AMIONPerson p1, AMIONPerson p2) {

            // parameter are of type Object, so we have to downcast it to Employee
            // objects
            String p1Name = p1.toString();
            String p2Name = p2.toString();

            // uses compareTo method of String class to compare names of the
            // employee
            return p1Name.compareTo(p2Name);
        }
    }
}
