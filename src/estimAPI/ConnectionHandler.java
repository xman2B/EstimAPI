package estimAPI;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


public class ConnectionHandler extends AbstractHandler {

	private EstimAPI api = null;
	
	private static final String TITLE= "EstimAPI";
	private static final String BODY_OK = "OK";
	private static final String BODY_ERROR = "ERROR";

	
	public ConnectionHandler(EstimAPI api) {
		this.api = api;
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        
        PrintWriter out = response.getWriter();
        
    	String answer = "";
        Map<String, String[]> parameters  = request.getParameterMap();
        
        System.out.println(parameters);
        
        Boolean success = api.execute(parameters);


        out.println("<title>" + TITLE + "</title>");
        out.println("<b>");
        if (!success) {
            out.println(BODY_ERROR);
        }
        else {
            out.println(BODY_OK);
        }
        out.println(answer);
        out.println("</b>");        
        
        
        baseRequest.setHandled(true);
		
	}

	

}
