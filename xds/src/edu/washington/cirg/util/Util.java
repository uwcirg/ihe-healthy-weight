/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 30, 2004
 * University of Washington, CIRG
 * $Id: Util.java,v 1.6 2005/01/26 19:35:15 ddrozd Exp $
 */
package edu.washington.cirg.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXWriter;
import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.SAXException;

 

/** Static utility class, contains database access methods using
 * JNDI and standard DriverManager methods, and in memory DOM validation 
 * using Sun MSV and iso relax verifiers
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.6 $
 *
 */
public class Util {
	
	private static Log log = LogFactory.getLog(Util.class);
	
	private static InitialContext initialContext;
	private static Context envContext;
	private static Map mDataSources;
	private static Object lock;
	
	static {
		lock = new Object();
		mDataSources = new HashMap();
	}
	
	public static Connection getConnection(String dataSourceName) 
		throws NamingException, SQLException {

		BasicDataSource ds = null;
		log.info("Trying: " + dataSourceName);
		if ( dataSourceName == null ) {
			throw new NamingException("Null dataSourceName passed to method");
		}
		try {
		
			if ( initialContext == null ) {
				log.info("initial context is null");
				initialContext = new InitialContext();
			} else {
				log.info("initial context is not null");
			}
			log.info("initial context: " + initialContext.getEnvironment().toString());
			
			if ( envContext == null ) {
				log.info("env context is null");
				envContext = 
					(Context)initialContext.lookup("java:/comp/env");
			} else {
				log.info("env context is not null");
			}
			log.info(envContext.lookupLink(dataSourceName).toString());
			log.info("envcontext: " + ((BasicDataSource) envContext.lookup(dataSourceName)).getDriverClassName());

			ds = (BasicDataSource)mDataSources.get(dataSourceName);

			if ( ds == null ) {
				log.info("envcontext: " + ((BasicDataSource) envContext.lookup(dataSourceName)).getDriverClassName());
				ds = (BasicDataSource)envContext.lookup(dataSourceName);

				if ( ds == null ) {
					throw new NamingException("Unable to obtain a DataSource for " + dataSourceName);
				}
				log.info("ds is '" + ds.getDriverClassName() + "', " + ds.getPassword() + ", " + ds.getUsername());
				synchronized (lock) {
					mDataSources.put(dataSourceName, ds);
				}
			}
		} catch (NamingException ne) {
			log.error(ne);
			throw ne;
		}
		
		if ( ds == null ) {
			throw new NamingException("Null DataSource");
		}
		
		return ds.getConnection();
	}
	
	public static Connection getConnection(String jdbcDriverClassName, 
			String host, 
			String db, 
			String user, 
			String password )
		throws SQLException {
		jdbcDriverClassName = "com.mysql.jdbc.Driver";
		host = "localhost";
		db = "ihe2014";
		user = "phr_rw";
		password = "";
		try {
			Class.forName(jdbcDriverClassName).newInstance();
		} catch (InstantiationException e) {
			log.error(e);
			throw new SQLException("InstantiationException: Unable to instantiate driver class");
		} catch (IllegalAccessException e) {
			log.error(e);
			throw new SQLException("IllegalAccessException: Unable to access driver class");
		} catch (ClassNotFoundException e) {
			log.error(e);
			throw new SQLException("ClassNotFoundException: Unable to locate driver class");
		}
		StringBuffer sb = new StringBuffer();
		
		sb.append("jdbc:mysql://");
		sb.append(host);
		sb.append("/");
		sb.append(db);
		sb.append("?user=");
		sb.append(user);
		sb.append("&password=");
		sb.append(password);
		
		Connection conn = 
			DriverManager.getConnection(sb.toString());
		
		return conn;
	}
	
	public static void closeConnection(Connection conn)
		throws SQLException {
		
		if ( conn != null ) {
			conn.close();
		}
	}
	
	
	public static void validate(Schema schema, Document doc) throws VerifierConfigurationException, SAXException {
        Verifier verifier = schema.newVerifier();
        verifier.setErrorHandler(new SAXErrorHandler());        
        VerifierHandler handler = verifier.getVerifierHandler();
        SAXWriter writer = new SAXWriter( handler );
        writer.write( doc );
	}
	

}
