package org.ds.logger;

import org.apache.log4j.Logger;

public class DSLogger {
		 static Logger log = Logger.getLogger(DSLogger.class.getName());
		 
		 public static void log(String className, String methodName, String msg){
			 log.debug(className+":"+methodName+"~"+msg);
		 }
		 public static void report(String key, String value){
			 log.info(key+" : "+value);
		 }
		 /* public static void main(String[] args)
		                throws IOException,SQLException{
		   
		     log.debug("Hello this is an debug message");
		     log.info("Hello this is an info message");
		  }*/

}
