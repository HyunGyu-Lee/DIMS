package databases;

import java.sql.*;

import tools.Statics;

public class DatabaseHandler {
   
   public static final int INVALID_QUERY_ERROR = -3;
   public static final int LOGIN_FAIL_ERROR = -2;
   public static final int DRIVER_INIT_ERROR = -1;
   public static final int COMPLETE = 0;
   
   private final int PORT = 3306;

   private Connection connection;
   
   public DatabaseHandler()
   {
	   
   }
   
   public int connect()
   {
      try
      {
    	  Class.forName(Statics.DASEBASE_DRIVER);
          try
          {
             connection = DriverManager.getConnection("jdbc:mysql://localhost:"+PORT+"/"+Statics.DEFAULT_USE_DATABASE, Statics.DEFAULT_DATABASE_HOST_ID, Statics.DEFAULT_DATABASE_HOST_PASSWORD);
          }
          catch (SQLException e)
          {
            return LOGIN_FAIL_ERROR;
          }
      }
      catch (ClassNotFoundException e)
      {
         return DRIVER_INIT_ERROR;
      }
      
      return COMPLETE;
   }

   
   public Connection getConnection()
   {
	   return connection;
   }
   
   public ResultSet excuteQuery(String query)
   {
      /* 여기서 쿼리문 통해서 실제 디비에 전달된
       * addValue를 디비에 삽입시켜야함
       *  */
      ResultSet rs=null;
      try
      {
         Statement stmt = connection.createStatement();
         rs = stmt.executeQuery(query);
      }
      catch (SQLException e)
      {
    	  e.printStackTrace();
      }
      
      return rs;
   }
   
   public void excuteUpdate(String query)
   {
      int rs;
      try
      {
         Statement stmt = connection.createStatement();
         rs = stmt.executeUpdate(query);
         stmt.close();         
      }
      catch (SQLException e)
      {
    	  e.printStackTrace();
      }
      
   }

}
