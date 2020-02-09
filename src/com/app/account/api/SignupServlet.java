package com.app.account.api;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.account.apicontroller.SignupAPIController;
import com.app.common.json.JSONParser;
import com.app.db.model.EmailInvite;

@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
    public SignupServlet()
    {
        super();
    }
    
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- GET                                        --- **/
	/** -------------------------------------------------- **/
    
	//Retrieves the information of an email invite (user's information, time invite sent, etc.) based on a supplied token.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//Get the parameters from the request.
		String token = request.getParameter("token");
		
		//Process the request.
		SignupAPIController controller = new SignupAPIController();
		EmailInvite emailInvite = controller.processGetRequest(token);
		
		//Prepare the response.
		if (emailInvite != null)
		{
			//An invite exists for the supplied token.
			//Returns the email invite object and status code 200.
			response.getWriter().write(JSONParser.getObjectAsJson(emailInvite));
			response.setStatus(HttpServletResponse.SC_OK);
		}
		else
		{
			//Incorrect token.
			//Returns status code 400.
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect token!");
		}
	}
	
	
	
	
	
	/** -------------------------------------------------- **/
	/** --- POST                                       --- **/
	/** -------------------------------------------------- **/
    
	//Completes the signup process from an email invite, attaches the supplied password to the user's account and sets it to an active status.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//Get the data from the request.
		HashMap<String, Object> map =	JSONParser.getJsonDataAsMap(request);
		String token =					JSONParser.getValueAsString(map, "token");
		String password =				JSONParser.getValueAsString(map, "password");
		
		//Process the request.
		SignupAPIController controller = new SignupAPIController();
		boolean result = controller.processPostRequest(token, password);
		
		//Prepare the response.
		if (result)
		{
			//User's account has been set up properly.
			//Returns status code 201.
			response.setStatus(HttpServletResponse.SC_CREATED);
		}
		else
		{
			//Incorrect token.
			//Returns status code 400.
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect token!");
		}
	}
}