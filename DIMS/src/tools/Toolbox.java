package tools;



import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.orsoncharts.util.json.JSONObject;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Toolbox {

	public static String getWhereStringBetweenDate(java.util.Date begin, java.util.Date end, String format)
	{
		StringBuilder wStr = new StringBuilder();
		
		wStr.append("일시 between'").append(begin.toString()).append(" 00:00:00' and '").append(end.toString()).append(" 23:59:59'");
		
		return wStr.toString();
	}
	
	public static java.util.Date StringToDate(String date, String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		java.util.Date dt = null;
		
		try
		{
			dt = sdf.parse(date);
		}
		catch(ParseException e)
		{
			e.printStackTrace();
		}
		return dt;
	}
	
	public static String getWhereStringBetweenDate(String begin, String end)
	{
		StringBuilder wStr = new StringBuilder();
		
		wStr.append("일시 between'").append(begin).append(" 00:00:00' and '").append(end).append(" 23:59:59'");
		
		return wStr.toString();
	}
	
	public static String getCurrentTimeFormat(java.util.Date date, String formatter)
	{
		SimpleDateFormat format = new SimpleDateFormat(formatter);
		
		return format.format(date);
	}
	
	public static String getSQLDateToFormat(Date date, String formatter)
	{
		SimpleDateFormat format = new SimpleDateFormat(formatter);
		
		return format.format(date);
	}
	
	public static void showMessageDialog(Stage stage, int x, int y, String msg)
	{
		BorderPane border = new BorderPane();
		border.setCenter(new Label(msg));
		border.setPrefSize(x, y);
		stage.setScene(new Scene(border));
		stage.show();
	}
	
	public static void showDialog()
	{
		Stage stage = new Stage();
		stage.initStyle(StageStyle.DECORATED);
		
		stage.show();
	}
	
	public static <T> ArrayList<T> arrToList(T[] arr)
	{
		ArrayList<T> list = new ArrayList<T>();
		
		for(T t : arr)
		{
			list.add(t);
		}
		
		return list;
	}
	
	public static String formatTime(Duration elapsed, Duration duration)
	{
		   int intElapsed = (int)Math.floor(elapsed.toSeconds());
		   int elapsedHours = intElapsed / (60 * 60);
		   if (elapsedHours > 0) {
		       intElapsed -= elapsedHours * 60 * 60;
		   }
		   int elapsedMinutes = intElapsed / 60;
		   int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 
		                           - elapsedMinutes * 60;
		 
		   if (duration.greaterThan(Duration.ZERO)) {
		      int intDuration = (int)Math.floor(duration.toSeconds());
		      int durationHours = intDuration / (60 * 60);
		      if (durationHours > 0) {
		         intDuration -= durationHours * 60 * 60;
		      }
		      int durationMinutes = intDuration / 60;
		      int durationSeconds = intDuration - durationHours * 60 * 60 - 
		          durationMinutes * 60;
		      if (durationHours > 0) {
		         return String.format("%d:%02d:%02d/%d:%02d:%02d", 
		            elapsedHours, elapsedMinutes, elapsedSeconds,
		            durationHours, durationMinutes, durationSeconds);
		      } else {
		          return String.format("%02d:%02d/%02d:%02d",
		            elapsedMinutes, elapsedSeconds,durationMinutes, 
		                durationSeconds);
		      }
		      } else {
		          if (elapsedHours > 0) {
		             return String.format("%d:%02d:%02d", elapsedHours, 
		                    elapsedMinutes, elapsedSeconds);
		            } else {
		                return String.format("%02d:%02d",elapsedMinutes, 
		                    elapsedSeconds);
		            }
		        }
		    }

	@SuppressWarnings("unchecked")
	public static JSONObject createJSONProtocol(String type, String[] keys, Object[] values)
	{
		if(keys.length!=values.length)return null;
		
		JSONObject obj = new JSONObject();
		
		obj.put("type", type);
		
		for(int i=0; i<keys.length; i++)
		{
			obj.put(keys[i], values[i]);
		}
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject createJSONProtocol(String[] keys, Object[] values)
	{
		if(keys.length!=values.length)return null;
		
		JSONObject obj = new JSONObject();
				
		for(int i=0; i<keys.length; i++)
		{
			obj.put(keys[i], values[i]);
		}
		
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> JSONObject createJSONProtocol(String type, String[] keys, ArrayList<T> values)
	{
		if(keys.length!=values.size())return null;
		
		JSONObject obj = new JSONObject();
		
		obj.put("type", type);
		
		for(int i=0; i<keys.length; i++)
		{
			obj.put(keys[i], values.get(i));
		}
		
		return obj;
	}
	
	public static int getResultSetSize(ResultSet resultSet)
	{
	    int size = -1;

	    try {
	        resultSet.last(); 
	        size = resultSet.getRow();
	        resultSet.beforeFirst();
	    } catch(SQLException e) {
	        return size;
	    }

	    return size;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject createJSONProtocol(String type)
	{
		JSONObject obj = new JSONObject();
		
		obj.put("type", type);
				
		return obj;
	}
	
	public static Date translateMillTime(long millTime)
	{
		Date d = new Date(millTime);
		return d;
	}
	
	public static boolean isNumber(String str)
	{
		try
		{
			Integer.parseInt(str);
			return true;
		}
		catch(Exception e)
		{
			System.out.println("상벌점 점수 오류 : "+e);
			return false;
		}
	}
	
}
