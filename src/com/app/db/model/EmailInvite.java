package com.app.db.model;

import java.time.Instant;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class EmailInvite extends BaseModelObject
{
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- PRIVATE VARIABLES                          --- **/
	/** -------------------------------------------------- **/
    
	private String token;
	private Instant dateTimeSent;
	private Integer emailInvitesSent;
	
	@JsonSerialize(using = com.app.db.serializer.ObjectIdJsonSerializer.class)
	private ObjectId ref_userId;
	private User user;
	
	@JsonSerialize(using = com.app.db.serializer.ObjectIdJsonSerializer.class)
	private ObjectId ref_userSentById;
	private User userSentBy;
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- CONSTRUCTOR                                --- **/
	/** -------------------------------------------------- **/
    
	public EmailInvite()
	{
		
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- GETTERS & SETTERS                          --- **/
	/** -------------------------------------------------- **/
    
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Instant getDateTimeSent() {
		return dateTimeSent;
	}

	public void setDateTimeSent(Instant dateTimeSent) {
		this.dateTimeSent = dateTimeSent;
	}

	public Integer getEmailInvitesSent() {
		return emailInvitesSent;
	}
	
	public void setEmailInvitesSent(Integer emailInvitesSent) {
		this.emailInvitesSent = emailInvitesSent;
	}
	
	public ObjectId getRef_userId() {
		return ref_userId;
	}

	public void setRef_userId(ObjectId ref_userId) {
		this.ref_userId = ref_userId;
	}

	@BsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ObjectId getRef_userSentById() {
		return ref_userSentById;
	}

	public void setRef_userSentById(ObjectId ref_userSentById) {
		this.ref_userSentById = ref_userSentById;
	}

	@BsonIgnore
	public User getUserSentBy() {
		return userSentBy;
	}

	public void setUserSentBy(User userSentBy) {
		this.userSentBy = userSentBy;
	}
}