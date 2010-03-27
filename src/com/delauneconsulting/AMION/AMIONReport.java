package com.delauneconsulting.AMION;

import java.util.ArrayList;

public interface AMIONReport {

    public abstract String getTitle(String filter);

    public abstract ArrayList<AMIONPerson> getPeople(String filter);

    public abstract String getDefaultTitle();

}
