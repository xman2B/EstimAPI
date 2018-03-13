package estimAPI;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import devices.TwoB.TwoBChannel;
import devices.TwoB.TwoBMode;

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
        
        boolean error = false;
    	String answer = "";
        Map<String, String[]> parameters  = request.getParameterMap();
        
        System.out.println(parameters);
        System.out.println(parameters.keySet());
        System.out.println(parameters.values().iterator().next().toString());
        for (String parameter: parameters.keySet()) {
        	String[] values = parameters.get(parameter);
        	switch (parameter) {
			case "initDevice":
				if ("true".equals(values[0]))
					api.initDevice(true);
				else {
					api.initDevice(false);
				}
				break;
			case "disconnectDevice":
				api.disconnectDevice();
				break;
			case "setMode":
				error = true;
				for (Mode mode: TwoBMode.values()) {
					if (mode.toString().equals(values[0])) {
						api.setMode(mode);
						error = false;
						break;
					}
				}
				break;
			case "setChannelOutputA":
				api.setChannelOutPut(TwoBChannel.A, Integer.parseInt(values[0]));
				break;
			case "setChannelOutputB":
				api.setChannelOutPut(TwoBChannel.B, Integer.parseInt(values[0]));
				break;
			case "setChannelOutputC":
				api.setChannelOutPut(TwoBChannel.C, Integer.parseInt(values[0]));
				break;
			case "setChannelOutputD":
				api.setChannelOutPut(TwoBChannel.D, Integer.parseInt(values[0]));
				break;
			case "linkChannels":
				api.linkChannels();
				break;
			case "unlinkChannels":
				api.unlinkChannels();
				break;
			case "setPowerHIGH":
				api.setPowerHIGH();
				break;
			case "setPowerLOW":
				api.setPowerLOW();
				break;
			case "kill":
				api.kill();
				break;
			case "reset":
				api.reset();
				break;
			case "getState":
				answer += api.getState().toString();
				break;
			case "getMode":
				answer += api.getMode().toString();
				break;
			case "getChannels":
				answer += api.getChannels().toString();
				break;
			case "getAvailableModes":
				answer += api.getAvailableModes().toString();
				break;
			default:
				error = true;
				break;
			}
        }

        
        

        out.println("<title>" + TITLE + "</title>");
        out.println("<b>");
        if (error) {
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
