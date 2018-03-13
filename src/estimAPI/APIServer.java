package estimAPI;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.internal.runners.InitializationError;

import devices.TwoB.TwoB;



public class APIServer {

	private final static String DEVICE = "/dev/ttyUSB0";
	private static EstimAPI api = new TwoB(DEVICE);
	
	public static void main(String[] args) throws Exception {
		// Init Device
		if (!api.initDevice(true)) {
			throw new Exception("Can't detect a device");
		}
		
		 // The Server
        Server server = new Server();

        // HTTP connector
        ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(8080);
        http.setIdleTimeout(30000);

        // Set the connector
        server.addConnector(http);

        // Set a handler
        server.setHandler(new ConnectionHandler(api));

        // Start the server
        try {
			server.start();
	        server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
