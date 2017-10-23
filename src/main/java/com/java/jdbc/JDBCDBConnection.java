package com.java.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCDBConnection {
	
	public static Connection getRemoteConnection() {
	    
	      try {
	      Class.forName("com.mysql.jdbc.Driver");
	      String dbName = "innodb";
	    
	      String userName = "Raji";
	      String password = "Cloudproject";
	      String hostname = "cloudproject.cbkzu87oal4c.us-west-1.rds.amazonaws.com";
	    
	/*      String userName = "guest";
	      String password = "guest123";
	      String hostname = "aa1422qs2y1137y.cbkzu87oal4c.us-west-1.rds.amazonaws.com";
	*/      
	      
	      String port = "3306";
	      String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
	     // logger.trace("Getting remote connection with connection string from environment variables.");
	      Connection con = DriverManager.getConnection(jdbcUrl);
	     // logger.info("Remote connection successful.");
	      return con;
	    }
	    catch (ClassNotFoundException e) { e.toString();}
	    catch (SQLException e) { e.toString();}
	    
	    return null;
	  }

}
