package com.ef;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;

import db.HibernateUtil;
import entities.BannedIpEntry;
import entities.LogEntry;

public class Parser {
	
	private static void readLogs(String pathToLogFile) {
		FileInputStream inputStream = null;
		BufferedReader reader = null;
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		Session session = null;
		
		try {
			session = HibernateUtil.openSession();
			inputStream = new FileInputStream(pathToLogFile);
			reader = new BufferedReader(new InputStreamReader(inputStream));
			
			session.beginTransaction();
	         
	        String line = reader.readLine();
	        while(line != null){
	             String elements[] = line.split("\\|");
	             
	             LogEntry logEntry = new LogEntry();
	             
	             logEntry.setRequestTime(formatter.parse(elements[0]));
	             logEntry.setIp(elements[1]);
	             logEntry.setRequestType(elements[2]);
	             logEntry.setHttp_code(Integer.parseInt(elements[3]));
	             logEntry.setDeviceAndBrowser(elements[4]);
	             
	             session.save(logEntry);
	             
	             line = reader.readLine();
	        }      
	        
	        session.getTransaction().commit();

		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	private static void findAbusingIps(String startDate, String timeFrame, String sThreshold) {
		Session session = null;
		
		try {
			int threshold = Integer.parseInt(sThreshold);
			session = HibernateUtil.openSession();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");
			
			Calendar endDateCal = Calendar.getInstance();
			endDateCal.setTime(formatter.parse(startDate));
			
			switch(timeFrame) {
				case "daily":
					endDateCal.add(Calendar.DAY_OF_MONTH, 1);
					break;
				case "hourly":
					endDateCal.add(Calendar.HOUR_OF_DAY, 1);
					break;
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> results  = session.createNativeQuery("select count(1), ip from LogEntry where requestTime>=:startDate and requestTime<:endDate group by ip")
				.setParameter("startDate", formatter.parse(startDate))
				.setParameter("endDate", endDateCal.getTime())
				.getResultList();
			
			for (Object[] result : results) {
				if(Integer.parseInt(result[0].toString())>threshold) {
					String ipWhoExceededTheThreshold = result[1].toString();
					System.out.println(ipWhoExceededTheThreshold);
					
					addIpToBanList(ipWhoExceededTheThreshold,"Exceeded threshold of " + sThreshold + " from " + startDate + " to " + formatter.format(endDateCal.getTime()));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	public static void addIpToBanList(String ip, String comment) {
		Session session = null;
		
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			
			BannedIpEntry banedIpEntry = new BannedIpEntry();
			banedIpEntry.setIp(ip);
			banedIpEntry.setComment(comment);
			
			session.save(banedIpEntry);
			
			session.getTransaction().commit();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	public static void main(String[] args) {
		String startDate=null,duration=null,threshold=null;
		
		for(String arg: args) {
			arg = arg.replace("--", "");
			String params[] = arg.split("=");
			switch(params[0]) {
				case "startDate":
					startDate = params[1];
					break;
				case "duration":
					duration = params[1];
					break;
				case "threshold":
					threshold = params[1];
					break;
				case "accesslog":
					readLogs(params[1]);
					break;
			}
		}
		
		findAbusingIps(startDate,duration,threshold);
		System.exit(1);
	}

}
