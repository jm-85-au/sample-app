package com.app.db.constant;

import com.mongodb.client.MongoCollection;

import com.app.db.config.DatabaseConnection;
import com.app.db.model.Company;
import com.app.db.model.EmailInvite;
import com.app.db.model.PasswordReset;
import com.app.db.model.Project;
import com.app.db.model.ProjectTask;
import com.app.db.model.User;
import com.app.db.model.UserPassword;

public class DatabaseCollection
{
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- PUBLIC VARIABLES                           --- **/
	/** -------------------------------------------------- **/
    
	public static final MongoCollection<Company> COMPANY =					DatabaseConnection.MONGO_DATABASE.getCollection("Company", Company.class);
	public static final MongoCollection<EmailInvite> EMAIL_INVITE =			DatabaseConnection.MONGO_DATABASE.getCollection("EmailInvite", EmailInvite.class);
	public static final MongoCollection<PasswordReset> PASSWORD_RESET =		DatabaseConnection.MONGO_DATABASE.getCollection("PasswordReset", PasswordReset.class);
	public static final MongoCollection<Project> PROJECT =					DatabaseConnection.MONGO_DATABASE.getCollection("Project", Project.class);
	public static final MongoCollection<ProjectTask> PROJECT_TASK =			DatabaseConnection.MONGO_DATABASE.getCollection("ProjectTask", ProjectTask.class);
	public static final MongoCollection<User> USER =						DatabaseConnection.MONGO_DATABASE.getCollection("User", User.class);
	public static final MongoCollection<UserPassword> USER_PASSWORD =		DatabaseConnection.MONGO_DATABASE.getCollection("UserPassword", UserPassword.class);
}