package server;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;

/**
 * Supports making requests to the server.
 * 
 * @author SE2
 * @version Spring 2024
 */
public class Server {

	private static final String CONNECTION_STRING = "tcp://127.0.0.1:5555";

	/**
	 * Creates a server object to send requests to the server.
	 * 
	 * @precondition request != null && request is valid (see README)
	 * @postondition none
	 * 
	 * @param request the request string to send to the server
	 * 
	 * @return the response string from the server
	 */
	public static String sendRequest(String request) {

		Context context = ZMQ.context(1);
		Socket socket = context.socket(ZMQ.REQ);
		socket.connect(Server.CONNECTION_STRING);
		socket.send(request.getBytes(ZMQ.CHARSET), 0);
		byte[] reply = socket.recv(0);
		String response = new String(reply, ZMQ.CHARSET);
		if (response.equals("bad format")) {
			throw new IllegalArgumentException("Must provide a valid request type (see README)");
		}
		return response;
	}

}
