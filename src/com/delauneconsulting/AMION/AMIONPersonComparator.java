package com.delauneconsulting.AMION;

import java.util.Comparator;

public class AMIONPersonComparator implements Comparator<AMIONPerson> {
    public int compare(AMIONPerson p1, AMIONPerson p2) {

        // parameter are of type Object, so we have to downcast it to Employee
        // objects
        String p1Name = p1.toString();
        String p2Name = p2.toString();

        // uses compareTo method of String class to compare names of the
        // employee
        return p1Name.compareTo(p2Name);
    }
}
