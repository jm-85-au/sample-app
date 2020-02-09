package com.app.account.apicontroller;

import java.time.Instant;

import javax.servlet.http.HttpSession;

import org.bson.Document;

import com.app.account.security.PasswordEncryptionModule;
import com.app.account.session.UserSessionService;
import com.app.db.dao.UserDAO;
import com.app.db.dao.UserPasswordDAO;
import com.app.db.model.User;
import com.app.db.model.UserPassword;

public class LoginAPIController
{
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- CONSTRUCTOR                                --- **/
	/** -------------------------------------------------- **/
    
	public LoginAPIController()
	{
		
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- PUBLIC METHODS                             --- **/
	/** -------------------------------------------------- **/
    
	public User processPostRequest(HttpSession session, String email, String password, String deviceManufacturer, String deviceProduct, String browserName, String browserVersion, String operationalSystem)
	{
		//Initial validation.
		if (
				email == null || "".equals(email) ||
				password == null || "".equals(password)
			)
		{
			return null;
		}
		
		//Check if the email exists in the database for an active user.
		Document queryUser = new Document();
		queryUser.append("email", email.toLowerCase());
		queryUser.append("isActive", true);
		User user = UserDAO.getOne(queryUser);
		
		//The email is correct if a user object is returned from the database.
		if (user != null)
		{
			Document queryUserPassword = new Document();
			queryUserPassword.append("ref_userId", user.getId());
			UserPassword userPassword = UserPasswordDAO.getOne(queryUserPassword);
			
			boolean match = PasswordEncryptionModule.isMatching(password, userPassword.getPasswordHash());
			
			//Check if the password is correct.
			if (match)
			{
				//Log the session.
				UserSessionService.startLoggedInSession(session, user, deviceManufacturer, deviceProduct, browserName, browserVersion, operationalSystem);
				
				//Update the lastLoggedIn value of the user.
				Document query = new Document();
				query.append("_id", user.getId());
				Document update = new Document();
				update.append("$set", new Document("lastLoggedIn", Instant.now()));
				UserDAO.updateFromQuery(query, update);
				
				//Return the updated user object.
				user = UserDAO.getOne(queryUser);
				return user;
			}
		}
		
		//User not found, incorrect email and/or password or account is inactive.
		return null;
	}
}