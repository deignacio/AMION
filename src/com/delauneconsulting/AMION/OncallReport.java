package com.delauneconsulting.AMION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.util.Log;

public class OncallReport implements AMIONReport {
    final private HashMap<String, ArrayList<AMIONPerson>> peopleLists = new HashMap<String, ArrayList<AMIONPerson>>();
    final private HashMap<String, String> titles = new HashMap<String, String>();
    final private String urlPattern = "http://www.amion.com/cgi-bin/ocs?Lo=%s&Rpt=619";

    private String passwd = null;
    private String response = null;

    public OncallReport(String pwd) {
        this.passwd = pwd;
        this.response = Helper.getHttpResponseAsString(String.format(this.urlPattern, this.passwd));
    }

    /* (non-Javadoc)
     * @see com.delauneconsulting.AMION.AMIONReport#getDefaultTitle()
     */
    public String getDefaultTitle() {
        return "AMION | " + this.passwd;
    }

    /* (non-Javadoc)
     * @see com.delauneconsulting.AMION.AMIONReport#getTitle(java.lang.String)
     */
    public String getTitle(String filter) {
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
        if (this.peopleLists.containsKey(filter)) {
            return this.peopleLists.get(filter);
        }

        ArrayList<AMIONPerson> peeps = new ArrayList<AMIONPerson>();

        try {
            // split the result set into lines
            String[] lines = null;
            lines = this.response.split("\r\n|\r|\n");

            AMIONPerson p;
            for (int i = 0; i < lines.length; i++) {

                if (lines[i].length() > 0 && lines[i].startsWith("\"")) {

                    p = new AMIONPerson();
                    p.comment = lines[i];

                    int index = lines[i].indexOf("\"", 2);
                    String personName = lines[i].substring(1, index);

                    // TODO: Split first and last names out
                    p.lastName = personName;

                    // get rid of the name field, since we already have it, then
                    // clean up everything else
                    lines[i] = lines[i].replace("\"" + personName + "\",", "");
                    String[] temp = lines[i].split(",");
                    for (int j = 0; j < temp.length; j++) {
                        temp[j] = temp[j].trim();
                        if (temp[j].contains("\"")) {
                            temp[j] = temp[j].replace("\"", "").trim();
                        }
                    }

                    // this is just the "job"
                    p.currentJob = temp[2];

                    if (filter.equalsIgnoreCase("OnCall") && !p.currentJob.contains("Bpr Coverage")) {
                        Log.e("HI MOM", "adding "+p.toString());
                        peeps.add(p);
                    } else if (filter.equalsIgnoreCase("BprCoverage")
                            && p.currentJob.contains("Bpr Coverage")) {
                        peeps.add(p);
                    }
                }

                // sort the custom ArrayList of AMIONPerson objects
                Collections.sort(peeps, new AMIONPersonComparator());
            }
        } catch (Exception e) {
        }

        this.peopleLists.put(filter, peeps);
        return peeps;
    }
}
