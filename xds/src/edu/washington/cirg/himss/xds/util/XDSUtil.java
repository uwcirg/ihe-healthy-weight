/*
 * Copyright 2004-2005 (C) University of Washington. All Rights Reserved.
 * Created on Dec 29, 2004
 * University of Washington, CIRG
 * $Id: XDSUtil.java,v 1.4 2005/01/26 19:35:15 ddrozd Exp $
 */
package edu.washington.cirg.himss.xds.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import edu.washington.cirg.himss.xds.ccr.Name;

/** Static utility class, contains a variety of string formatting methods
 * as well as some DOM4J to w3c DOM conversions and a method for retrieving
 * a file via HTTP.  All methods are related to XDS transactions.
 * @author <a href="mailto:ddrozd@u.washington.edu">Dan Drozd</a>
 * @version $Revision: 1.4 $
 *
 */
public class XDSUtil {
	
	private static Log log = LogFactory.getLog(XDSUtil.class);
	public static final int CCR_FORMAT = 1;
	public static final int HL7_FORMAT = 2;
	public static final int XDS_FORMAT = 3;
	
	private static final int BUF_SIZE = 1024;

	
	public static String currentXdsDate(int format) {
		
		TimeZone tz = TimeZone.getTimeZone("GMT");
		Calendar cal = new GregorianCalendar(tz);

		
		return getXdsDate(cal, format);
	}
	
	public static String dateToXdsDateString(Date date, int format) {

		TimeZone tz = TimeZone.getTimeZone("GMT");
		Calendar cal = new GregorianCalendar(tz);
		cal.setTime(date);

		
		return getXdsDate(cal, format);
	}
	
	private static String getXdsDate(Calendar cal, int format) {

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		int dom = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		String monthStr, domStr, hourStr, minStr, secStr;
		
		if ( month < 10 ) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}
		
		if ( dom < 10 ) {
			domStr = "0" + dom;
		} else {
			domStr = String.valueOf(dom);
		}

		if ( hour < 10 ) {
			hourStr = "0" + hour;
		} else {
			hourStr = String.valueOf(hour);
		}
		
		if ( min < 10 ) {
			minStr = "0" + min;
		} else {
			minStr = String.valueOf(min);
		}
		
		if ( sec < 10 ) {
			secStr = "0" + sec;
		} else {
			secStr = String.valueOf(sec);
		}

		String xdsDateString = null;
		switch ( format ) {
			case CCR_FORMAT:
				xdsDateString = year + "-" + monthStr + "-" + domStr  
				+ "T" + hourStr + ":" + minStr + ":" + secStr + "Z";
				break;
			case HL7_FORMAT:
				xdsDateString = year + monthStr + domStr  
				+ "T" + hourStr + ":" + minStr + ":" + secStr + "Z";
				break;
			case XDS_FORMAT:
				xdsDateString = year + monthStr + domStr + hourStr + minStr + secStr;
				break;
			default:
				break;
		}
		return xdsDateString;
	}
	
	
	
	public static String getHL7Name(Name name) {
		return XDSUtil.getHL7Name(name.getFamilyName(),
				name.getGivenName(),
				name.getMiddleName(),
				null,
				name.getTitle(),
				name.getSuffix());
	}
	
	public static String getHL7Name(String familyName, 
			String givenName, 
			String middleName, 
			String suffix, 
			String prefix,
			String degree) {
		StringBuffer sb = new StringBuffer();
		sb.append("^");
		if ( familyName != null ) {
			sb.append(familyName);
		}
		sb.append("^");
		if ( givenName != null ) {
			sb.append(givenName);
		}
		sb.append("^");
		if ( middleName != null ) {
			sb.append(middleName);
		}
		sb.append("^");
		if ( suffix != null ) {
			sb.append(suffix);
		}
		sb.append("^");
		if ( prefix != null ) {
			sb.append(prefix);
		}
		sb.append("^");
		if ( degree != null ) {
			sb.append(degree);
		}
		return sb.toString();
	}

	public static void httpGetFile(String urlString, String localFileURI) 
		throws IOException, MalformedURLException {
		
		URL url;
		URLConnection conn;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			url = new URL(urlString);
		} catch ( MalformedURLException mue ) {
			log.error(mue);
			throw mue;
		}

		try {
			conn = url.openConnection();
			File outFile = new File(localFileURI);
			FileOutputStream out = new FileOutputStream(outFile);
			bis = new BufferedInputStream(conn.getInputStream(), BUF_SIZE);
			bos = new BufferedOutputStream(out);
			byte[] buff = new byte[BUF_SIZE];
			int bytesRead;
			
			if ( log.isDebugEnabled() ) {
				log.debug("Getting " + urlString);
			}
			
			while ( -1 != (bytesRead = bis.read(buff, 0, buff.length)) ) {
				bos.write(buff, 0, bytesRead);
			}
			bos.flush();
			
			if ( log.isDebugEnabled() ) {
				log.debug("Retrieved file from " + urlString + " and saved to " + localFileURI);
			}
		} catch ( IOException ioe ) {
			log.error(ioe);
			throw ioe;
		} finally {
			if ( bis != null ) {
				bis.close();
			}
			if ( bos != null ) {
				bos.close();
			}
		}
	}
	
	
	public static void printDOM(PrintStream out, org.w3c.dom.Document doc ) 
		throws UnsupportedEncodingException, IOException
	{
		DOMReader reader = new DOMReader();
		org.dom4j.Document jdomDoc = reader.read(doc);
		printDOM(out, jdomDoc);
	}
	
	public static void printDOM(PrintStream out, org.dom4j.Document doc ) 
		throws UnsupportedEncodingException, IOException
	{
		OutputFormat outformat = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(out, outformat);
		writer.write(doc);
		writer.flush();
	}
	
	public static org.w3c.dom.Document dom4jtoDOM(Document dom4jDocument) 
		throws DocumentException {
		DOMWriter writer = new DOMWriter();
		return writer.write(dom4jDocument);
	}
	
	public static String capitalize(String src ) {

		StringBuffer buffer = new StringBuffer(src.length());

		char c = src.charAt(0);
		buffer.append(Character.toTitleCase(c));
		for ( int i = 1; i < src.length(); i++ ) {
			buffer.append(src.charAt(i));
		}
		return buffer.toString();		
	}

	
}