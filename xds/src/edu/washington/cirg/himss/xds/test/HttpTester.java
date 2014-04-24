/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Jan 20, 2005
 * University of Washington, CIRG
 * $Id: HttpTester.java,v 1.1 2005/01/26 19:35:15 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Test class used for performance/load testing of web applications.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.1 $
 *
 */

public class HttpTester {

	private static int numThreads = 1;
	private static String url = null;
	
	private static Log log = LogFactory.getLog(HttpTester.class);

	/**
	 * 
	 */
	public HttpTester() {
		super();
	}

    public static void main(String[] args) {
        
    	if ( args.length >= 2 ) {
    		numThreads = Integer.parseInt(args[0]);
    		url = args[1];
    	} else {
    		log.fatal("Usage: java edu.washington.cirg.himss.xds.test.HttpTester numThreads URL args");
    		System.exit(1);
    	}
    	
        // Create an HttpClient with the MultiThreadedHttpConnectionManager.
        // This connection manager must be used if more than one thread will
        // be using the HttpClient.
    	
    	MultiThreadedHttpConnectionManager manager =
    		new MultiThreadedHttpConnectionManager();
    	manager.setMaxConnectionsPerHost(numThreads);
        HttpClient httpClient = 
        	new HttpClient(manager);
        // Set the default host/protocol for the methods to connect to.
        // This value will only be used if the methods are not given an absolute URI
        //httpClient.getHostConfiguration().setHost("jakarta.apache.org", 80, "http");
        
        StringBuffer sb = new StringBuffer();
        if ( args.length > 2 ) {
        	sb.append("?");
        }
        
        for ( int j = 2; j < args.length; j++ ) {
        	sb.append(encodeParam(args[j]));
        	if ( j < args.length - 1 ) {
        		sb.append("&");
        	}
        }
        
        url += sb.toString();
        // create a thread for each URI
        GetThread[] threads = new GetThread[numThreads];
        for (int i = 0; i < threads.length; i++) {
            GetMethod get = new GetMethod(url);
        	//get.setFollowRedirects(true);
            threads[i] = new GetThread(httpClient, get, i + 1);
        }
        
        // start the threads
        for (int j = 0; j < threads.length; j++) {
            threads[j].start();
        }
        
    }
    
    /**
     * A thread that performs a GET.
	 */
    static class GetThread extends Thread {
        
        private HttpClient httpClient;
        private GetMethod method;
        private int id;
        
        public GetThread(HttpClient httpClient, GetMethod method, int id) {
            this.httpClient = httpClient;
            this.method = method;
            this.id = id;
        }
        
        /**
         * Executes the PostMethod and prints some satus information.
         */
        public void run() {
            
            try {
                
            	long start = System.currentTimeMillis();
            	log.debug(id + " - about to get something from " + method.getURI());
                // execute the method
                int status = httpClient.executeMethod(method);
                
                
                log.debug(id + " - get executed");
                // get the response body as an array of bytes
                byte[] bytes = method.getResponseBody();
                long end = System.currentTimeMillis();
                
                String s = new String(bytes);
                /*
                String status = "Failure";
                if ( s.indexOf("Successfully") != -1 ) {
                	status = "Success";
                }
                */
                String statusMsg = "Failure";
                if ( status == HttpStatus.SC_OK ) {
                	statusMsg = "Success";
                } else {
                	log.error(s);
                }
                log.info(id + " - " + statusMsg + " - " + (end-start) + " ms");
                
                
                
            } catch (Exception e) {
            	log.error(id + " - error: " + e);
            } finally {
                // always release the connection after we're done 
                method.releaseConnection();
                log.debug(id + " - connection released");
            }
        }
       
    }
    
    public static String encodeParam(String param) {
    	Map mCodes = new HashMap();
    	mCodes.put("\\^", "%5E");
    	mCodes.put("&", "%26");
    	Set keys = mCodes.keySet();
    	Iterator iterator = keys.iterator();
    	String ascii;
     	while ( iterator.hasNext() ) {
    		ascii = (String)iterator.next();
    		param = param.replaceAll(ascii, (String)mCodes.get(ascii));
    	}
    	return param;
    }
}