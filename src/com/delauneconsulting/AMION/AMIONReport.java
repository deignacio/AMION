package com.delauneconsulting.AMION;

import java.util.ArrayList;
import java.util.Calendar;

public interface AMIONReport {

	public abstract String getTitle(String filter);

    public abstract ArrayList<AMIONPerson> getPeople(String filter);

    public abstract String getDefaultTitle();

    public abstract ArrayList<String> getFilters();
    
    public abstract Calendar getDate();

    public abstract String getUrl();
}
