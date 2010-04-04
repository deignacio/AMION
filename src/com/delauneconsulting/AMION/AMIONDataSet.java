package com.delauneconsulting.AMION;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import android.content.SharedPreferences;

public class AMIONDataSet {

	public String passwd;;
	public HashMap<String, AMIONPerson> masterPersonIndex = new HashMap<String, AMIONPerson>();
	public ArrayList<String> rotationList = new ArrayList<String>();
	public ArrayList<String> classList = new ArrayList<String>();
	
	private final String urlBasePattern = "http://www.amion.com/cgi-bin/ocs?Login=%s&Rpt=%s";
	private final String urlDay = "&Day=%s";
	private final String urlMonth = "&Month=%s";
	private final String urlDays = "&Days=%s";
	
	public AMIONDataSet(String pwd) {
		this.passwd = pwd;
		
		//initialize the lookups
		this.rotationList.add("");
		this.classList.add("");
		
		//run report 610 to get blocks, classList and rotationList
		//generate list of people with their block assignments
		runReport(610);
        
		//run report 612 
		//runReport(612);
		
		//run report 619 for today's date for 30 days
		//attach each non "r" type assignment to the correct person
		runReport(619);
	}
	
	private void runReport(int rptNumber) {
		runReport(rptNumber, "");
	}
	private void runReport(int rptNumber, String date) {
		
		String url = "";
		Calendar c = Helper.getDateFromString(date);
		
		String response = "";
		
		switch(rptNumber) {
		case 610:
			//format the url
			url = String.format(urlBasePattern, this.passwd, 610);
			
			break;
		case 612:
			//format the url
			url = String.format(urlBasePattern, this.passwd, 612);
			break;
		case 619:
			//format the url
			url = urlBasePattern + urlDay + urlMonth + urlDays;
			url = String.format(url, this.passwd, 619, c.getTime().getDate(), c.getTime().getMonth()+1, 30);
			
			break;
		}
		
		if (url.length() > 0) {
	        response = AMION.settings.getString(url, "");
	        if (response.length() == 0) { 
	    		response = Helper.getHttpResponseAsString(url);
	
	    		SharedPreferences.Editor editor = AMION.settings.edit();
	            editor.putString(url, response);
	            editor.commit();
	    	}
        }
		
		//parse the data
		tokenizePeople(rptNumber, response);
	}
	
	private void tokenizePeople(int rptNumber, String response) {
        try {
            StringTokenizer responseTokens = new StringTokenizer(response, "\r\n|\r|\n");

            AMIONPerson p;
            AMIONAssignment a;
            String responseLine;
            String[] temp;
            
            while (responseTokens.hasMoreTokens()) {
                responseLine = responseTokens.nextToken();

                if (responseLine.length() > 0) {
                    
                	switch(rptNumber) {
                	case 610: //Block Schedules for the entire year for everyone, tab delimited
                		// CLASS_NAME START_DATE END_DATE PERSON_NAME ROTATION_NAME
                		
                		//Split the lines on tabs
                		temp = responseLine.split("\t");
                		//Log.v(String.valueOf(rptNumber), responseLine);
                		if (temp.length > 0) {
                			p = new AMIONPerson();
                			//Set the raw line to the comment field, for debugging
                			p.comment = responseLine;
                			p.className = temp[0].trim();
                			p.name = temp[3].trim();
                			
                			//add this class to the master list
                			addClassToMaster(p.className);
                			
                			//Check if person already exists in MPI
                			AMIONPerson tempPerson = getPersonFromMaster(p);
                			if (tempPerson == null)
                				masterPersonIndex.put(p.name.replace(" ", ""), p);
                			else
                				p = tempPerson;
                			
                			a = new AMIONAssignment();
                			a.startDate = Helper.parseDateString(temp[1].trim());
                			a.endDate = Helper.parseDateString(temp[2].trim());
                			a.name = temp[4].trim();
                			a.type = "r"; //all assignments from this report are of type "r"
                			
                			//add this rotation to the master list
                			addRotationToMaster(a.name);
                			
                			//Check to see if assignment already exists for people in MPI
                			AMIONAssignment tempAssignment = getAssignmentFromMaster(p, a);
                			if (tempAssignment == null)
                				p.assignments.add(a);
                			
                			//Log.v(String.valueOf(rptNumber), String.format("name: %s, job: %s, start: %s, end: %s, class: %s", p.name, a.name, a.assignmentStartDate, a.assignmentEndDate, p.className));                				
                		}
                		
                		break;
                	case 612: //Complete Schedule for everyone for a single day
                		// "PERSON_NAME", ID, BACKUP_ID, TYPE_OF_DUTY, "ASSIGNMENT_NAME", ID, BACKUP_ID
                		
                		//On valid if the line starts with a double-quote
                		if (responseLine.startsWith("\"")) {
                			//Log.v(String.valueOf(rptNumber), responseLine);
                    		
                			p = new AMIONPerson();
                			
                			//Set the raw line to the comment field, for debugging
                			p.comment = responseLine;
                			
                			//TODO: Fix this ugly, horrible code!!!
                			//Find the name (in quotes) parse it out, then remove it so we can split on commas
                			int index = responseLine.indexOf("\"", 2);
                	        String personName = responseLine.substring(1, index);
                	        p.name = personName;
                	        responseLine = responseLine.replace("\"" + personName + "\",", "");
                	        temp = responseLine.split(",");
                	        
                			if (temp.length > 0) {
                				
                				//clean it up a little and remove the double-quotes
                				for (int j = 0; j < temp.length; j++) {
                    	            temp[j] = temp[j].trim();
                    	            if (temp[j].contains("\"")) {
                    	                temp[j] = temp[j].replace("\"", "").trim();
                    	            }
                    	        }
                				
                				//p.id = temp[0];
                				//p.backupId = temp[1];
                				
	                			//Check if person already exists in MPI
	                			AMIONPerson tempPerson = getPersonFromMaster(p);
	                			if (tempPerson == null)
	                				masterPersonIndex.put(p.name.replace(" ", ""), p);
	                			else
	                				p = tempPerson;
	                			
	                			a = new AMIONAssignment();
	                			a.name = temp[3].trim();
	                			//TODO: Fix this, right now it will get today's date
	                			a.startDate = Helper.getDateFromString("").getTime();
	                			a.type = temp[2].trim();
	                			
	                			//Check to see if assignment already exists for people in MPI
	                			AMIONAssignment tempAssignment = getAssignmentFromMaster(p, a);
	                			if (tempAssignment == null)
	                				p.assignments.add(a);
	                			
	                			//Log.v(String.valueOf(rptNumber), String.format("name: %s, job: %s, start: %s, end: %s, class: %s", p.name, a.name, a.assignmentStartDate, a.assignmentEndDate, p.className));
	                		}
                		}
                		
                		break;
                	case 619: //Call Schedule for everyone based on date range comma delimited
                		// "PERSON_NAME", ID, BACKUP_ID, "ASSIGNMENT_NAME", ID, BACKUP_ID, DATE, START_TIME, END_TIME
                		
                		//On valid if the line starts with a double-quote
                		if (responseLine.startsWith("\"")) {
                			//Log.v(String.valueOf(rptNumber), responseLine);
                    		
                			p = new AMIONPerson();
                			
                			//Set the raw line to the comment field, for debugging
                			p.comment = responseLine;
                			
                			//TODO: Fix this ugly, horrible code!!!
                			//Find the name (in quotes) parse it out, then remove it so we can split on commas
                			int index = responseLine.indexOf("\"", 2);
                	        String personName = responseLine.substring(1, index);
                	        p.name = personName;
                	        responseLine = responseLine.replace("\"" + personName + "\",", "");
                	        temp = responseLine.split(",");
                	        
                			if (temp.length > 0) {
                				
                				//clean it up a little and remove the double-quotes
                				for (int j = 0; j < temp.length; j++) {
                    	            temp[j] = temp[j].trim();
                    	            if (temp[j].contains("\"")) {
                    	                temp[j] = temp[j].replace("\"", "").trim();
                    	            }
                    	        }
                				
                				//p.id = temp[0];
                				//p.backupId = temp[1];
                				
	                			//Check if person already exists in MPI
	                			AMIONPerson tempPerson = getPersonFromMaster(p);
	                			if (tempPerson == null)
	                				masterPersonIndex.put(p.name.replace(" ", ""), p);
	                			else
	                				p = tempPerson;
	                			
	                			a = new AMIONAssignment();
	                			a.name = temp[2].trim();
	                			a.startDate = Helper.parseDateString(temp[5]);
	                			a.type = "s"; //TODO: are all assignments from this report are of type "s"
	                			
	                			//Check to see if assignment already exists for people in MPI
	                			AMIONAssignment tempAssignment = getAssignmentFromMaster(p, a);
	                			if (tempAssignment == null)
	                				p.assignments.add(a);
	                			
	                			//Log.v(String.valueOf(rptNumber), String.format("name: %s, job: %s, start: %s, end: %s, class: %s", p.name, a.name, a.assignmentStartDate, a.assignmentEndDate, p.className));
	                		}
                		}
                		
                		break;
                	}
                }
            }
        } catch (Exception e) {
        }
    }
    
	private AMIONPerson getPersonFromMaster(AMIONPerson p) {
		return masterPersonIndex.get(p.name.replace(" ", ""));
	}
	private AMIONAssignment getAssignmentFromMaster(AMIONPerson p, AMIONAssignment a) {
		AMIONAssignment assignment;
		AMIONPerson currentPerson = getPersonFromMaster(p);
		if (currentPerson != null) {
			for (int i=0; i<currentPerson.assignments.size(); i++) {
				assignment = currentPerson.assignments.get(i);
				if (assignment.name.equalsIgnoreCase(a.name) && assignment.type.equalsIgnoreCase(a.type) && assignment.startDate == a.startDate) {
					return assignment;
				}
			}
		}
		return null;
	}
	
	private void addClassToMaster(String className) {
		if (!classList.contains(className))
			classList.add(className);
	}
	private void addRotationToMaster(String rotationName) {
		
		//TODO: Should I be parsing it like this?
		if (rotationName.contains(" "))
			rotationName = rotationName.substring(0, rotationName.indexOf(" "));
		if (rotationName.contains(":"))
			rotationName = rotationName.substring(0, rotationName.indexOf(":"));
		if (rotationName.contains("-"))
			rotationName = rotationName.substring(0, rotationName.indexOf("-"));
		if (rotationName.contains("+"))
			rotationName = rotationName.substring(0, rotationName.indexOf("+"));
		
		if (!rotationList.contains(rotationName))
			rotationList.add(rotationName);
	}
	
	//Get the list of people who are working on a specific date.
	//  optionally filter by class and/or rotation
	public ArrayList<AMIONPerson> getPeople(Calendar c, String className, String rotationName) {

		ArrayList people = new ArrayList<AMIONPerson>();
		AMIONPerson p;
		Iterator it = masterPersonIndex.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        p = (AMIONPerson)pairs.getValue();
	        
	        ArrayList<String> classes = new ArrayList<String>();
	        if (className.length() > 0) 
	        	classes.add(className);
	        
	        ArrayList<String> rotations = new ArrayList<String>();
	        if (rotationName.length() > 0) 
	        	rotations.add(rotationName);
	        
	        if (meetsClassCriteria(p, classes) && meetsRotationCriteria(p, rotations, c))
	        	people.add(p);
	    }
		return people;
	}
	
	private boolean meetsClassCriteria(AMIONPerson p, ArrayList<String> classes) {
		boolean meetsCriteria = false;
		
		String className;
		if (classes.size() > 0) {
			for (int i=0; i<classes.size(); i++) {
				className = classes.get(i); 
				if (className.length() > 0) {
			    	if (p.className.equalsIgnoreCase(className))
			    		return true;
			    }
			}
		} else {
			return true;
		}
		
		return meetsCriteria;
	}
	private boolean meetsRotationCriteria(AMIONPerson p, ArrayList<String> rotations, Calendar c) {
		boolean meetsCriteria = false;
		
		String rotationName;
		if (rotations.size() > 0) {
			for (int i=0; i<rotations.size(); i++) {
				rotationName = rotations.get(i); 
				if (rotationName.length() > 0) {
			    	AMIONAssignment a;
			    	for (int j=0; j<p.assignments.size(); j++) {
			    		a = p.assignments.get(j);
			    		if (Helper.isDateInRange(Helper.getFormattedDate(c), a)) {
				    		if (a.name.startsWith(rotationName) && a.type.equalsIgnoreCase("r"))
					    		return true;
			    		}
			    	}
			    }
			}
		} else {
			return true;
		}
		
		return meetsCriteria;
	}
}
