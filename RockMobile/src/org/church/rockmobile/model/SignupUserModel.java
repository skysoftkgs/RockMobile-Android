package org.church.rockmobile.model;

public class SignupUserModel {
	String email;
	String password;
	String firstName;
	String lastName;
	
	public void setEmail(String email){
		this.email = email;
	}
	
	public String getEmail(){
		return this.email;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	public String getFirstName(){
		return this.firstName;
	}
	
	public void setLastName(String lastName){
		this.lastName = lastName;
	}
	
	public String getLastName(){
		return this.lastName;
	}
}
