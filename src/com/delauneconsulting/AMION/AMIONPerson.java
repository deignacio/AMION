package com.delauneconsulting.AMION;

public class AMIONPerson {

	public String firstName = "";
	public String lastName = "";
	public String currentJob = "";
	public String comment = "";
	public boolean existsInContacts = false;
	public String phoneNumber = "";
	
	public AMIONPerson() {
	}
	
	public String toString() {
		if (firstName.length() > 0)
			return lastName + ", " + firstName;
		else
			return lastName;
	}
}
