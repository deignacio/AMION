package com.delauneconsulting.AMION;

import java.util.Comparator;
import java.util.Date;

public class AMIONAssignment {

	public String id = "";
	public String backupId = "";
	public String personId = "";
	public String name = "";
	public String type = "";
	public Date startDate = null;
	public Date endDate = null;
	
	public AMIONAssignment() {
		
	}
	
	//Default sorter is by name
    public static class AMIONAssignmentComparator implements Comparator<AMIONAssignment> {
        public int compare(AMIONAssignment p1, AMIONAssignment p2) {

            String p1Name = p1.name;
            String p2Name = p2.name;

            return p1Name.compareTo(p2Name);
        }
    }
    public static class AMIONAssignmentDateComparator implements Comparator<AMIONAssignment> {
        public int compare(AMIONAssignment one, AMIONAssignment two){
        	Date d1 = one.startDate;
        	Date d2 = two.startDate;
        	
        	return d1.compareTo(d2);
        }
    }
}
