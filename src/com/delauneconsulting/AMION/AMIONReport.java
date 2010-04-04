package com.delauneconsulting.AMION;

import java.util.ArrayList;
import java.util.Calendar;

public interface AMIONReport {

	public abstract String getTitle();

    public abstract ArrayList<AMIONPerson> getPeople(String className, String assignmentName);
    public abstract ArrayList<AMIONAssignment> getScheduleForPerson(String personId);
    
    public abstract ArrayList<String> getClasses();
    public abstract ArrayList<String> getAssignments();
    
    public abstract Calendar getDate();

    public abstract String getUrl();
}
