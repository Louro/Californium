/*******************************************************************************
 * Copyright (c) 2012, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the Californium (Cf) CoAP framework.
 ******************************************************************************/
package ch.ethz.inf.vs.californium.layers;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import ch.ethz.inf.vs.californium.coap.Message;

/**
 * This class implements a queue for the handling of a filter for the incoming messages,
 * allowing only a certain transmission rate in a time unit.
 *
 * @author Francesco Corazza
 */
public class RateControlLayer extends UpperLayer {
	
	/** The message queue (lock free implementation). */
	private ConcurrentLinkedQueue<Message> messageQueue;
	
	/** The delay for the scheduling of the threads. */
	private long period;
	
	/** The timer for the dispatch of the messages. */
	private Timer timer;
	
	/** The queue max size. */
	private int queueMaxSize;
	
	/**
	 * Instantiates a new rate control layer with a specified threshold.
	 *
	 * @param requestPerSecond the request per second admitted
	 */
	public RateControlLayer(int requestPerSecond) {
		// if the rate is not positive, it means the layer must only forward messages.
		if (requestPerSecond > 0) {
			// create the queue
			this.messageQueue = new ConcurrentLinkedQueue<Message>();
			
			// using a fixed delay for a normal distribution in the message dispatch
			this.period = 1000 / requestPerSecond;
			
			this.timer = new Timer();
			this.timer.schedule(new QueueHandler(), 0, this.period);
			// TODO timer finalization?
			// TODO how to handle the aging of messages?
			// TODO how to handle an overflow?
		}
	}
	
	/**
	 * The Class QueueHandler.
	 *
	 * @author Francesco Corazza
	 */
	private class QueueHandler extends TimerTask {
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// get the head of the queue (the oldest message)
			Message message = RateControlLayer.this.messageQueue.poll();
			
			// if the queue is not empty, send the message
			if (message != null) {
				deliverMessage(message);
				
				// print info
				long now = Calendar.getInstance().getTimeInMillis();
				System.out.println("Message MID: " + message.getMID()
						+ " sent with " + (now - message.getTimestamp())
						+ " delay ");
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see ch.ethz.inf.vs.californium.layers.Layer#doSendMessage(ch.ethz.inf.vs.californium.coap.Message)
	 */
	@Override
	protected void doSendMessage(Message msg) throws IOException {
		// only forward the message to lower layer
		sendMessageOverLowerLayer(msg);
		
		// TODO in the proxy also this flow has to be limited with another queue
	}
	
	/* (non-Javadoc)
	 * @see ch.ethz.inf.vs.californium.layers.Layer#doReceiveMessage(ch.ethz.inf.vs.californium.coap.Message)
	 */
	@Override
	protected void doReceiveMessage(Message msg) {
		// if the queue wasn't created, the layer only forwards the message to the lower layer
		if (this.messageQueue != null) {
			// check duplicates
			//if (!this.messageQueue.contains(msg)) { // TODO otherwise I can control only MID??
			
			// add the element in the tail of the queue
			this.messageQueue.add(msg);
			
			if (this.messageQueue.size() > this.queueMaxSize) {
				this.queueMaxSize = this.messageQueue.size();
			}
			
			System.out.println(String.format(
					"Message MID: %d enqueued, queue size: %d (max %d)",
					msg.getMID(), this.messageQueue.size(), this.queueMaxSize));
		} else {
			// only forward the message to receivers
			deliverMessage(msg);
		}
	}
	
	/**
	 * Prints the stats.
	 */
	protected void printStats() {
		//TODO
	}
}
