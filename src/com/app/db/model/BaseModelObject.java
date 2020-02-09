package com.app.db.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class BaseModelObject
{
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- PRIVATE VARIABLES                          --- **/
	/** -------------------------------------------------- **/
	
	@BsonId
	@JsonSerialize(using = com.app.db.serializer.ObjectIdJsonSerializer.class)
	private ObjectId id;
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- CONSTRUCTOR                                --- **/
	/** -------------------------------------------------- **/
    
	public BaseModelObject()
	{
		
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- VALIDATION                                 --- **/
	/** -------------------------------------------------- **/
    
	public boolean checkIfObjectIsValid()
	{
		if (this.id == null)
		{
			return false;
		}
		return true;
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- GETTERS & SETTERS                          --- **/
	/** -------------------------------------------------- **/
    
	public ObjectId getId()
	{
		return id;
	}
	
	public void setId(ObjectId id)
	{
		this.id = id;
	}
}