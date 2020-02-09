package com.app.db.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class User extends BaseModelObject
{
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- PRIVATE VARIABLES                          --- **/
	/** -------------------------------------------------- **/
    
	private String firstName;
	private String lastName;
	private String email;
	private Boolean isActive;
	
	private Instant lastLoggedIn;
	private Instant lastActivity;
	
	@JsonSerialize(using = com.app.db.serializer.ObjectIdJsonSerializer.class)
	private ObjectId ref_companyId;
	private Company company;
	
	private Boolean isCompanyAdmin;
	private Boolean isSystemAdmin;
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- CONSTRUCTOR                                --- **/
	/** -------------------------------------------------- **/
    
	public User()
	{
		
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- GETTERS & SETTERS                          --- **/
	/** -------------------------------------------------- **/
    
	public String getFirstName()
	{
		return firstName;
	}
	
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public Boolean getIsActive()
	{
		return isActive;
	}
	
	public void setIsActive(Boolean isActive)
	{
		this.isActive = isActive;
	}
	
	public Instant getLastLoggedIn()
	{
		return lastLoggedIn;
	}
	
	public void setLastLoggedIn(Instant lastLoggedIn)
	{
		this.lastLoggedIn = lastLoggedIn;
	}
	
	public Instant getLastActivity()
	{
		return lastActivity;
	}
	
	public void setLastActivity(Instant lastActivity)
	{
		this.lastActivity = lastActivity;
	}
	
	public ObjectId getRef_companyId()
	{
		return ref_companyId;
	}
	
	public void setRef_companyId(ObjectId ref_companyId)
	{
		this.ref_companyId = ref_companyId;
	}
	
	@BsonIgnore
	public Company getCompany()
	{
		return company;
	}
	
	public void setCompany(Company company)
	{
		this.company = company;
	}
	
	public Boolean getIsCompanyAdmin()
	{
		return isCompanyAdmin;
	}
	
	public void setIsCompanyAdmin(Boolean isCompanyAdmin)
	{
		this.isCompanyAdmin = isCompanyAdmin;
	}
	
	public Boolean getIsSystemAdmin()
	{
		return isSystemAdmin;
	}
	
	public void setIsSystemAdmin(Boolean isSystemAdmin)
	{
		this.isSystemAdmin = isSystemAdmin;
	}
}