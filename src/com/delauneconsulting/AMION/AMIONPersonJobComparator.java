package com.delauneconsulting.AMION;

import java.util.Comparator;

public class AMIONPersonJobComparator implements Comparator<AMIONPerson> {
    public int compare(AMIONPerson p1, AMIONPerson p2) {

        // parameter are of type Object, so we have to downcast it to Employee
        // objects
        String p1Job = p1.currentJob;
        String p2Job = p2.currentJob;

        // uses compareTo method of String class to compare names of the
        // employee
        return p1Job.compareTo(p2Job);
    }
}
