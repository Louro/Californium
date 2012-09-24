package ch.ethz.inf.vs.californium.coap;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.Message.messageType;
import ch.ethz.inf.vs.californium.endpoint.Resource;
import ch.ethz.inf.vs.californium.util.Properties;

public class Exchange {

// Logging /////////////////////////////////////////////////////////////////////
	
	protected static final Logger LOG = Logger.getLogger(Message.class.getName());
	
	/**
	 * The protocol used for an exchange.
	 * This does not contain possible target schemes in the Proxy-Uri.
	 */
	public enum Scheme {
		COAP,
		COAPS,
		HTTP,
		HTTPS
	}

// Members /////////////////////////////////////////////////////////////////////
	
	private InetAddress localAddress = null;
	private int localPort = -1;
	
	private InetAddress remoteAddress = null;
	private int remotePort = -1;
	
	private Scheme scheme = null;
	
	private Resource resource = null;
	
	private int currentMID = -1;
	private int numRetrans = 0;
	private int numReplied = 0;
	
	private long timestamp = -1;
	private Timer timer = new Timer(true); // run as daemon
	
	private Request request = null;
	private Response response = null;
	
	private boolean isSeparate = false;
	
	private byte[] token = null;
	
	private BlockOption block1 = null;
	private BlockOption block2 = null;

// Constructors ////////////////////////////////////////////////////////////////
	
	public Exchange(InetAddress rAddress, int rPort) {
		this.remoteAddress = rAddress;
		this.remotePort = rPort;
	}

// Methods /////////////////////////////////////////////////////////////////////
	
	public void setRequest(Request req) {
		this.request = req;
		
		this.currentMID = this.request.getMID();
		this.setToken(this.request.getToken());
		
		this.block1 = (BlockOption) this.request.getFirstOption(OptionNumberRegistry.BLOCK1);
		this.block2 = (BlockOption) this.request.getFirstOption(OptionNumberRegistry.BLOCK2);
		
		this.request.setExchange(this);
	}
	
	public void setResponse(Response res) {
		this.response = res;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}
}
