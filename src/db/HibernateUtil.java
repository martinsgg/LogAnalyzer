package db;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import entities.BannedIpEntry;
import entities.LogEntry;


public class HibernateUtil {
	private static SessionFactory DEFAULT_SESSION_FACTORY;
	
	private static Session generalSession;
	
    public static SessionFactory getDefaultSessionFactory() {
    	
        if (DEFAULT_SESSION_FACTORY == null) {
        	try {
	            Configuration configuration = new Configuration().configure(new File("hibernate.xml"));
	            configuration.addAnnotatedClass(LogEntry.class);
	            configuration.addAnnotatedClass(BannedIpEntry.class);
	            DEFAULT_SESSION_FACTORY = configuration.buildSessionFactory();      
        	} catch (Exception e){
        		e.printStackTrace();
        	}
        }
         
        return DEFAULT_SESSION_FACTORY;
    }
    
    public static Session getSession(){
    	if(generalSession == null){
    		generalSession = DEFAULT_SESSION_FACTORY.getCurrentSession();
    	}
    	return generalSession;
    }
    
    public static Session openSession(){
    	return getDefaultSessionFactory().openSession();
    }
}
