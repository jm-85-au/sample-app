package com.app.account.apicontroller;

import org.bson.Document;

import com.app.account.security.PasswordEncryptionModule;
import com.app.db.dao.EmailInviteDAO;
import com.app.db.dao.UserDAO;
import com.app.db.dao.UserPasswordDAO;
import com.app.db.model.EmailInvite;
import com.app.db.model.UserPassword;

public class SignupAPIController
{
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- CONSTRUCTOR                                --- **/
	/** -------------------------------------------------- **/
    
	public SignupAPIController()
	{
		
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- PUBLIC METHODS                             --- **/
	/** -------------------------------------------------- **/
    
	public EmailInvite processGetRequest(String token)
	{
		//Find and return an EmailInvite object with the supplied token (if one exists).
		return EmailInviteDAO.getOne(new Document("token", token));
	}
	
	public boolean processPostRequest(String token, String passwordPlainText)
	{
		//Get EmailInvite object from token.
		EmailInvite emailInvite = EmailInviteDAO.getOne(new Document("token", token));
		
		//Return false if token is invalid.
		if (emailInvite == null)
		{
			return false;
		}
		
		//Create the UserPassword object in the database.
		UserPassword userPassword = new UserPassword();
		String passwordHash = PasswordEncryptionModule.encrypt(passwordPlainText);
		userPassword.setPasswordHash(passwordHash);
		userPassword.setRef_userId(emailInvite.getRef_userId());
		UserPasswordDAO.createOne(userPassword);
		
		//Update the status of the user account so it is now active.
		Document userQuery = new Document("_id", emailInvite.getRef_userId());
		Document userUpdate = new Document("$set", new Document("isActive", true));
		UserDAO.updateFromQuery(userQuery, userUpdate);
		
		//Remove the EmailInvite object from the database (the token has been used and must be removed).
		EmailInviteDAO.deleteFromQuery(new Document("_id", emailInvite.getId()));
		
		return true;
	}
}