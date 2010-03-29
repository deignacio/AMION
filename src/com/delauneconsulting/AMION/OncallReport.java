package com.delauneconsulting.AMION;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;

public class OncallReport implements AMIONReport {
    final private ArrayList<AMIONPerson> allPeople = new ArrayList<AMIONPerson>();
    final private HashMap<String, ArrayList<AMIONPerson>> tokenizedPeople = new HashMap<String, ArrayList<AMIONPerson>>();
    final private HashMap<String, String> titles = new HashMap<String, String>();
    final private String urlPattern = "http://www.amion.com/cgi-bin/ocs?Login=%s&Rpt=619&Day=%s&Month=%s&Year=%s";
    final private String ignoredToks = "\\W"; //!@#$%^&*()_-+={}[]\\|;:<>,./?`~";
    private String passwd = null;
    //private Date date;
    private Calendar calendar;
    private String response = null;

    public OncallReport(String pwd, Calendar calendar) {
        this.passwd = pwd;
        //this.date = date;        
        this.calendar = calendar;
        this.response = Helper.getHttpResponseAsString(String.format(this.urlPattern, this.passwd, calendar.getTime().getDate(), calendar.getTime().getMonth()+1, calendar.getTime().getYear()+1900));
        tokenizePeople();
    }

    private void tokenizePeople() {
        try {
            StringTokenizer responseTokens = new StringTokenizer(response, "\r\n|\r|\n");

            AMIONPerson p;
            String responseLine;
            while (responseTokens.hasMoreTokens()) {
                responseLine = responseTokens.nextToken();

                if (responseLine.length() > 0 && responseLine.startsWith("\"")) {
                    p = new AMIONPerson(responseLine);
                    
                    allPeople.add(p);

                    StringTokenizer tokenizer = new StringTokenizer(p.currentJob);
                    String tok;
                    while (tokenizer.hasMoreTokens()) {
                        tok = tokenizer.nextToken();
                        tok = tok.replaceAll(ignoredToks, "");
                        if (tok.length() == 0) {
                            continue;
                        }
                        registerPerson(tok, p);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void registerPerson(String key, AMIONPerson person) {
        if (tokenizedPeople.containsKey(key)) {
            tokenizedPeople.get(key).add(person);
            return;
        }
        ArrayList<AMIONPerson> people = new ArrayList<AMIONPerson>();
        people.add(person);
        tokenizedPeople.put(key, people);
    }

    /* (non-Javadoc)
     * @see com.delauneconsulting.AMION.AMIONReport#getDefaultTitle()
     */
    public String getDefaultTitle() {
        return "AMION | " + this.passwd;
    }

    public Calendar getDate() {
    	return this.calendar;
    }
    public String getUrl() {
    	return String.format(this.urlPattern, this.passwd, calendar.getTime().getDate(), calendar.getTime().getMonth()+1, calendar.getTime().getYear()+1900);
    }
    
    /* (non-Javadoc)
     * @see com.delauneconsulting.AMION.AMIONReport#getTitle(java.lang.String)
     */
    public String getTitle(String filter) {
        if (filter == null) {
            return getDefaultTitle();
        }

        if (this.titles.containsKey(filter)) {
            return this.titles.get(filter);
        }

        String title = null;
        try {
            // split the result set into lines
            String[] lines = null;
            lines = this.response.split("\r\n|\r|\n");

            String date = lines[0].replace("Assignments for", "").trim();
            title = this.passwd + " | " + filter + " | " + date;
        } catch (Exception e) {
            title = "error parsing title";
        }

        this.titles.put(filter, title);
        return title;
    }

    /* (non-Javadoc)
     * @see com.delauneconsulting.AMION.AMIONReport#getPeople(java.lang.String)
     */
    public ArrayList<AMIONPerson> getPeople(String filter) {
        if (filter == null) {
            return allPeople;
        }

        if (!this.tokenizedPeople.containsKey(filter)) {
            return null;
        }

        return this.tokenizedPeople.get(filter);
    }

    private class FilterComparator implements Comparator<String> {
        public int compare(String s1, String s2) {

            // parameter are of type Object, so we have to downcast it to Employee
            // objects
            Integer numPeople1 = tokenizedPeople.get(s1).size();
            Integer numPeople2 = tokenizedPeople.get(s2).size();


            // we want descending
            return numPeople2.compareTo(numPeople1);
        }
    }

    public ArrayList<String> getFilters() {
        ArrayList<String> filters = new ArrayList<String>(tokenizedPeople.keySet());
        Collections.sort(filters, new FilterComparator());
        return filters;
    }
}
