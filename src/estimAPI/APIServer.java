package estimAPI;



import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

import devices.TwoB.TwoB;

public class APIServer {


	public static void main(String[] args) throws IllegalStateException {

		Options options = new Options();

		// Command Line Options
		options.addOption("p", "port", true, "The port to listen on");
		options.addOption("host", true, "The host adress");
		options.addOption("d", "device", true, "The path to the device");
		options.addOption("f", "fast", false, "Use the HighSpeed Mode");
		options.addOption("h", "help", false, "Display the help message");
		options.addOption("v", "version", false, "Show the version number");

		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();

		int port = 8080;
		String device = null;
		String host = "localhost";
		boolean HighSpeedMode = false;

		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			if (line.hasOption("help")) {
				formatter.printHelp("APIServer", options);
				System.exit(0);
			}
			if (line.hasOption("version")) {
				System.out.println("Bleeding Edge !");
				System.out.println("Don't expect anything to work, but be happy if it works :-)");
				System.exit(0);
			}
			if (line.hasOption("port")) {
				port = Integer.parseInt(line.getOptionValue("port"));
			}
			if (line.hasOption("device")) {
				device = line.getOptionValue("device");
			}
			if (line.hasOption("host")) {
				host = line.getOptionValue("host");
			}
			if (line.hasOption("fast")) {
				HighSpeedMode = true;
			}
		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
			System.exit(1);
		}

		//Create the TwoB instance
		EstimAPI api = null;
		if (device == null) {
			api = new TwoB();

		}
		else {
			api = new TwoB(device);
		}

		// Try Init Device
		try {
			if (!api.initDevice(HighSpeedMode)) {
				throw new IllegalStateException("Can't detect a device");
			}
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		}

		// The Server
		Server server = new Server();

		// HTTP connector
		ServerConnector http = new ServerConnector(server);
		http.setHost(host);
		http.setPort(port);
		http.setIdleTimeout(30000);

		// Set the connector
		server.addConnector(http);

		// Set a handler
		server.setHandler(new ConnectionHandler(api));

		// Start the server
		try {
			server.start();
			System.out.println("Server is running !");
			System.out.println("Use http://" + host + ":" + port + "/ to connect");
			server.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
