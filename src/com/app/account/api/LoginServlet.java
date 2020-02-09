package com.app.account.api;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.app.account.apicontroller.LoginAPIController;
import com.app.common.json.JSONParser;
import com.app.db.model.User;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
    public LoginServlet()
    {
        super();
    }
    
    
    
    
    
	/** -------------------------------------------------- **/
	/** --- POST                                       --- **/
	/** -------------------------------------------------- **/
    
	//Handles all login requests, requires user to provide email and password (all other variables are automated via platform.js).
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		//Get the data from the request.
		HashMap<String, Object> map =	JSONParser.getJsonDataAsMap(request);
		String email =					JSONParser.getValueAsString(map, "email");
		String password =				JSONParser.getValueAsString(map, "password");
		String deviceManufacturer =		JSONParser.getValueAsString(map, "deviceManufacturer");
		String deviceProduct =			JSONParser.getValueAsString(map, "deviceProduct");
		String browserName =			JSONParser.getValueAsString(map, "browserName");
		String browserVersion =			JSONParser.getValueAsString(map, "browserVersion");
		String operationalSystem =		JSONParser.getValueAsString(map, "operationalSystem");
		
		//Process the request.
		LoginAPIController controller = new LoginAPIController();
		User user = controller.processPostRequest(request.getSession(), email, password, deviceManufacturer, deviceProduct, browserName, browserVersion, operationalSystem);
		
		//Prepare the response.
		if (user != null)
		{
			//Valid email and password.
			//Returns the user object and status code 200.
			response.getWriter().write(JSONParser.getObjectAsJson(user));
			response.setStatus(HttpServletResponse.SC_OK);
		}
		else
		{
			//Incorrect email and/or password or account is inactive.
			//Returns status code 401.
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Incorrect email and/or password or account is inactive.");
		}
	}
}