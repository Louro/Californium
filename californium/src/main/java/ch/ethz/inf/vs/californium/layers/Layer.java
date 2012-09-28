/**
 * 
 */
package ch.ethz.inf.vs.californium.layers;

import java.io.IOException;

import ch.ethz.inf.vs.californium.coap.Exchange;
import ch.ethz.inf.vs.californium.coap.MessageReceiver;

/**
 * @author Francesco Corazza
 * 
 */
public interface Layer extends MessageReceiver {
	
	void send(Exchange exchange) throws IOException;
	
    void registerReceiver(MessageReceiver receiver);
    void unregisterReceiver(MessageReceiver receiver);
}
