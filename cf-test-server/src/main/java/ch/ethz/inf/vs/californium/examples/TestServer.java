/*******************************************************************************
 * Copyright (c) 2012, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
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

package ch.ethz.inf.vs.californium.examples;

import java.net.SocketException;
import java.util.logging.Level;

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;
import ch.ethz.inf.vs.californium.endpoint.ServerEndpoint;
import ch.ethz.inf.vs.californium.endpoint.resources.LocalResource;
import ch.ethz.inf.vs.californium.examples.resources.CarelessResource;
import ch.ethz.inf.vs.californium.examples.resources.HelloWorldResource;
import ch.ethz.inf.vs.californium.examples.resources.ImageResource;
import ch.ethz.inf.vs.californium.examples.resources.LargeResource;
import ch.ethz.inf.vs.californium.examples.resources.LongPath;
import ch.ethz.inf.vs.californium.examples.resources.Observe;
import ch.ethz.inf.vs.californium.examples.resources.Query;
import ch.ethz.inf.vs.californium.examples.resources.Separate;
import ch.ethz.inf.vs.californium.examples.resources.StorageResource;
import ch.ethz.inf.vs.californium.examples.resources.ToUpperResource;
import ch.ethz.inf.vs.californium.util.Log;

/**
 * The class ExampleServer shows how to implement a server by extending {@link LocalEndpoint}. In
 * the implementation class, use
 * {@link LocalEndpoint#addResource(ch.ethz.inf.vs.californium.endpoint.resources.LocalResource)} to
 * add
 * custom resources extending {@link LocalResource}.
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public class TestServer extends ServerEndpoint {
    // exit codes for runtime errors
    public static final int ERR_INIT_FAILED = 1;
    
    private static final int DEFAULT_PORT = 5683;
    
    public static void main(String[] args) {
        // set the level high
        Log.setLevel(Level.ALL);
        Log.init();
        
        int port;
        
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else {
            port = DEFAULT_PORT;
        }
        
        // create server
        try {
            
            LocalEndpoint server = new TestServer(port);
            server.start();
            
            System.out.printf("ExampleServer listening on port %d.\n", server.getPort());
            
        } catch (SocketException e) {
            System.err.printf("Failed to create SampleServer: %s\n", e.getMessage());
            System.exit(ERR_INIT_FAILED);
        }
        
    }
    
    /**
     * Constructor for a new ExampleServer. Call {@code super(...)} to configure
     * the port, etc. according to the {@link LocalEndpoint} constructors.
     * <p>
     * Add all initial {@link LocalResource}s here.
     */
    public TestServer(int port) throws SocketException {
        super(port);
        
        // add resources to the server
        addResource(new HelloWorldResource());
        addResource(new ToUpperResource());
        addResource(new StorageResource());
        // addResource(new SeparateResource());
        addResource(new LargeResource());
        addResource(new ImageResource());
        addResource(new CarelessResource());
        
        addResource(new LongPath());
        addResource(new Query());
        addResource(new Separate());
        addResource(new Observe());
    }
    
    @Override
    public final void handleRequest(Request request) {
        
        // Add additional handling like special logging here.
        // request.prettyPrint();
        
        // dispatch to requested resource
        super.handleRequest(request);
    }
    
}
