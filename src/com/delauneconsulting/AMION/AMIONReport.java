package com.delauneconsulting.AMION;

import java.util.ArrayList;
import java.util.Calendar;

public interface AMIONReport {

	public abstract String getTitle(String filter);

    public abstract ArrayList<AMIONPerson> getPeople(ArrayList<String> filters);

    public abstract String getDefaultTitle();

    public abstract ArrayList<String> getFilters();
    public abstract String[] getFiltersAsStringArray();
    
    public abstract Calendar getDate();

    public abstract String getUrl();
}
