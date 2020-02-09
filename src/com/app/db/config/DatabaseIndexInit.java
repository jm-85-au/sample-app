package com.app.db.config;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.app.db.dao.CompanyDAO;
import com.app.db.dao.EmailInviteDAO;
import com.app.db.dao.ProjectDAO;
import com.app.db.dao.ProjectTaskDAO;
import com.app.db.dao.UserDAO;
import com.app.db.dao.UserPasswordDAO;

public class DatabaseIndexInit implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		//Set up all indexes in the database.
		CompanyDAO.setupIndexes();
		EmailInviteDAO.setupIndexes();
		ProjectDAO.setupIndexes();
		ProjectTaskDAO.setupIndexes();
		UserDAO.setupIndexes();
		UserPasswordDAO.setupIndexes();
	}
}