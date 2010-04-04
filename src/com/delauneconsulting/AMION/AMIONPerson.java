package com.delauneconsulting.AMION;

import java.util.ArrayList;
import java.util.Comparator;

import android.view.ContextMenu;
import android.view.Menu;

public class AMIONPerson {

	public String id = "";
	public String backupId = "";
	public String className = "";
    public String name = "";
    public ArrayList<AMIONAssignment> assignments = new ArrayList<AMIONAssignment>();
    public String comment = "";
    
    public AMIONPerson() {
    }
    public AMIONPerson(String strToParse) {
    	this(strToParse, 612);
    }
    public AMIONPerson(String strToParse, int rptNumber) {
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

        AMIONAssignment a;
        switch (rptNumber) {
        case 619:
        	this.id = temp[0];
            this.backupId = temp[1];
            
            /*
            a = new AMIONAssignment();
            a.personId = this.id;
            a.name = temp[2];
            a.id = temp[3];
            a.backupId = temp[4];
            a.assignmentDate = temp[5];
            //this.assignmentStartTime = temp[6];
            //this.assignmentEndTime = temp[7];
            this.assignments.add(a);
            */
            
        	break;
    	default:
    		this.id = temp[0];
            this.backupId = temp[1];
            
            /*
            a = new AMIONAssignment();
            a.personId = this.id;
            a.name = temp[3];
            a.type = temp[2];
            a.id = temp[4];
            a.backupId = temp[5];
            this.assignments.add(a);
            */
            
    		break;
        }
    }

    
    
    public String toString() {
        String str = String.format("Id: %s\n", this.id);
        str += String.format("BackupId: %s\n", this.backupId);
        str += String.format("Name: %s\n", this.name);
        str += String.format("Class: %s\n", this.className);
        //str += String.format("Assignment: %s\n", this.assignmentName);
        //str += String.format("Type: %s\n", this.assignmentType);
        //str += String.format("Id: %s\n", this.assignmentId);
        //str += String.format("BackupId: %s", this.assignmentBackupId);
    	
    	return str;
    }

    public void populateContextMenu(ContextMenu menu) {
        menu.setHeaderTitle(this.name);

        menu.add(0, Menu.FIRST, Menu.NONE, "Show this Team");
        menu.add(0, Menu.FIRST + 1, Menu.NONE, "Set this as me");
        menu.add(0, Menu.FIRST + 2, Menu.NONE, "All Assignments");
    }

    //Default sorter is by name
    public static class AMIONPersonComparator implements Comparator<AMIONPerson> {
        public int compare(AMIONPerson p1, AMIONPerson p2) {

            String p1Name = p1.name;
            String p2Name = p2.name;

            return p1Name.compareTo(p2Name);
        }
    }
    public static class AMIONPersonClassComparator implements Comparator<AMIONPerson> {
        public int compare(AMIONPerson p1, AMIONPerson p2) {

            String p1Class = p1.className;
            String p2Class = p2.className;

            return p1Class.compareTo(p2Class);
        }
    }
    /*
    public static class AMIONPersonAssignmentComparator implements Comparator<AMIONPerson> {
        public int compare(AMIONPerson p1, AMIONPerson p2) {

            String p1Assignment = p1.assignmentName;
            String p2Assignment = p2.assignmentName;

            return p1Assignment.compareTo(p2Assignment);
        }
    }
    public static class AMIONPersonDateComparator implements Comparator<AMIONPerson> {
        public int compare(AMIONPerson p1, AMIONPerson p2) {

            String p1Date = p1.assignmentDate;
            String p2Date = p2.assignmentDate;

            return p1Date.compareTo(p2Date);
        }
    }
    */
}
