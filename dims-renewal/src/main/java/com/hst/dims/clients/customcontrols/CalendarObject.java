package com.hst.dims.clients.customcontrols;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import com.hst.dims.tools.Toolbox;

public class CalendarObject extends AnchorPane{

	// �޷� ��ü ũ��
	private double CALENDAR_WIDTH = 1024, CALENDAR_HEIGHT = 768;
	
	// ������Ʈ
	public GridPane calendar;	
	public Label DateHeader;
	public DatePicker pick;
	
	// ������
	CalendarDataManager cDataManager = new CalendarDataManager();
	ArrayList<ScheduleObject> sObjList;
	
	public CalendarObject()
	{
		
	}
	
	public CalendarObject(ArrayList<ScheduleObject> sObjList)
	{
		// �޷°�ü ����
		this.setPrefWidth(CALENDAR_WIDTH);
		this.setPrefHeight(CALENDAR_HEIGHT+200);
		
		calendar = new GridPane();
		DateHeader = new Label();
		
		// �޷� ������ ������ �������� �ʱ�ȭ
		this.sObjList = sObjList;
		calendar.setLayoutX(0);
		calendar.setLayoutY(70);
		initCalendarHeader();
		initDate();

		//�޷°�ü ���
		DateHeader.setText(new SimpleDateFormat("yyyy�� M��").format(new Date(System.currentTimeMillis())));
		DateHeader.setStyle("-fx-background-color: linear-gradient(to bottom, #4c4c4c 0%,#595959 12%,#666666 25%,#474747 39%,#2c2c2c 50%,#000000 51%,#111111 60%,#2b2b2b 76%,#1c1c1c 91%,#131313 100%);");
		DateHeader.setAlignment(Pos.CENTER);
		DateHeader.setTextFill(Paint.valueOf("white"));
		DateHeader.setFont(Font.font("HYwulM",50));
		DateHeader.setLayoutX(1024/4);
		DateHeader.setLayoutY(0);
		DateHeader.setPrefSize(512, 70);
		
		//�޷°�ü ��¥����
		pick = new DatePicker();
		pick.setPrefSize(188, 32);
		pick.setLayoutX(36);
		pick.setLayoutY(19);
		
		
		
		this.getChildren().addAll(pick, DateHeader, calendar);
	}
	
	public CalendarDataManager getDataInstance()
	{
		return cDataManager;
	}
	
	public void initCalendarComponent()
	{
		calendar.setHgap(3);
		calendar.setVgap(3);
		calendar.setPrefSize(CALENDAR_WIDTH, CALENDAR_HEIGHT);
		
	}
	
	public void initCalendarHeader()
	{
		calendar.add(new Cell("��", "gray"), 0, 0);
		calendar.add(new Cell("��", "gray"), 1, 0);
		calendar.add(new Cell("ȭ", "gray"), 2, 0);
		calendar.add(new Cell("��", "gray"), 3, 0);
		calendar.add(new Cell("��", "gray"), 4, 0);
		calendar.add(new Cell("��", "gray"), 5, 0);
		calendar.add(new Cell("��", "gray"), 6, 0);
	}
	
	public void setScheduleData(ArrayList<ScheduleObject> sObjList)
	{
		this.sObjList = sObjList;
	}
	
	public void initDate()
	{
		cDataManager.setFirstDayOfMonth();
		
		calendar.getChildren().clear();
		initCalendarHeader();
		int processMonth = cDataManager.getMonth();
		for(int weekIdx = 0 ; weekIdx<6 ; weekIdx++)
		{
			if(weekIdx==0)
			{
				int pc = cDataManager.getDayOfWeekIndex();
				for(int i = 0; i<pc;i++)
				{
					cDataManager.prevDay();
					Cell targetCell = new Cell(cDataManager.getDayInt()+"", "white");
					targetCell.setFont(Font.font("HYwulM", 20));
					targetCell.setTextFill(Paint.valueOf("black"));
					targetCell.setDisable(true);
					calendar.add(targetCell, cDataManager.getDayOfWeekIndex()-1, 1);
				}
				for(int i = 0; i<pc;i++)
				{
					cDataManager.nextDay();
				}
			}
			
			for(int dayIdx = cDataManager.getDayOfWeekIndex() ; dayIdx <= cDataManager.getDayOfWeekCount() ; dayIdx++)
			{
				Cell targetCell = new Cell(cDataManager.getDayInt()+"", "white");
				targetCell.setFont(Font.font("HYwulM", 20));
				
				if(cDataManager.getDayOfWeekIndex()==1)
				{
					targetCell.setTextFill(Paint.valueOf("red"));
				}
				else if(cDataManager.getDayOfWeekIndex()==7)
				{
					targetCell.setTextFill(Paint.valueOf("blue"));
				}
				else
				{
					targetCell.setTextFill(Paint.valueOf("black"));					
				}
				
				if(cDataManager.getMonth()!=processMonth)
				{
//					targetCell.setStyle("-fx-background-color : #e1e1ea");
//					targetCell.setTextFill(Paint.valueOf("white"));
					targetCell.setDisable(true);
				}

				calendar.add(targetCell, cDataManager.getDayOfWeekIndex()-1, weekIdx+1);
				
				for(ScheduleObject oo : sObjList)
				{
					if(oo.getDateText().contains(cDataManager.toString()))
					{
						calendar.add(oo.bindingCellObject(CALENDAR_WIDTH/7, CALENDAR_HEIGHT/6), cDataManager.getDayOfWeekIndex()-1, weekIdx+1);
					}
				}
				cDataManager.nextDay();
			}
			
			
			
		}
		
		
	}
	
	class Cell extends Label
	{
		Cell()
		{
			init();
		}
		
		Cell(String title, String cellColor)
		{
			super.setText(title);
			init(cellColor);
		}
		
		private void init()
		{
			this.setFont(Font.font("HYwulM",25));
			this.setPrefWidth(CALENDAR_WIDTH/7);
			this.setPrefHeight(CALENDAR_HEIGHT/6);
			this.setAlignment(Pos.CENTER);
		}
		
		private void init(String cellColor)
		{
			this.setFont(Font.font("HYwulM",25));
			this.setStyle("-fx-background-color : "+cellColor+";");
			this.setPrefWidth(CALENDAR_WIDTH/7);
			this.setPrefHeight(CALENDAR_HEIGHT/6);
			this.setAlignment(Pos.CENTER);
		}
		
	}
	
	public class CalendarDataManager
	{
		private String[] DAY_STRING = {"","��","��","ȭ","��","��","��","��"};
		
		private Calendar calendarData = Calendar.getInstance();
		
		public CalendarDataManager()
		{
			//setFirstDayOfMonth();
		}
		
		public int getYear()
		{
			return calendarData.get(Calendar.YEAR);
		}
		
		public int getMonth()
		{
			return (calendarData.get(Calendar.MONTH)+1);
		}
		
		public int getDayInt()
		{
			return calendarData.get(Calendar.DATE);
		}
		
		public int getWeekOfMonthCount()
		{
			return calendarData.getActualMaximum(Calendar.WEEK_OF_MONTH);
		}
		
		public int getDayOfWeekCount()
		{
			return calendarData.getActualMaximum(Calendar.DAY_OF_WEEK);
		}
		
		public int getDayOfWeekIndex()
		{
			return calendarData.get(Calendar.DAY_OF_WEEK);
		}
		
		public int getEndDayIntMonth()
		{
			return calendarData.getActualMaximum(Calendar.DATE);
		}
		
		public int getWeekOfMonth()
		{
			return calendarData.get(Calendar.WEEK_OF_MONTH);
		}
		
		public String getDayString()
		{
			return DAY_STRING[getDayOfWeekIndex()];
		}
		
		public String getStartDateOfMonth()
		{
			String str = getYear()+"-"+getMonth()+"-"+calendarData.getActualMinimum(Calendar.DAY_OF_MONTH);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
			Date d = null;
			try
			{
				d = sdf.parse(str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new SimpleDateFormat("yyyy-MM-dd").format(d);
		}
		
		public String getEndDateOfMonth()
		{
			String str = getYear()+"-"+getMonth()+"-"+calendarData.getActualMaximum(Calendar.DAY_OF_MONTH);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
			Date d = null;
			try
			{
				d = sdf.parse(str);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new SimpleDateFormat("yyyy-MM-dd").format(d);
		}
		
		public Calendar getCalander()
		{
			return calendarData;
		}
		
		@Override
		public String toString()
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			return sdf.format(calendarData.getTime());
		}
		
		public void setFirstDayOfMonth()
		{
			calendarData.add(Calendar.DATE, -getDayInt()+1);
		}
		
		public void nextDay()
		{
			calendarData.add(Calendar.DATE, 1);
		}
		
		public void prevDay()
		{
			calendarData.add(Calendar.DATE, -1);
		}
		
		public void prevMonth()
		{
			calendarData.add(Calendar.MONTH, -1);
		}
		
		public void nextMonth()
		{
			calendarData.add(Calendar.MONTH, 1);
		}
		
		public void prevYear()
		{
			calendarData.add(Calendar.YEAR, -1);
		}
		
		public void nextYear()
		{
			calendarData.add(Calendar.YEAR, 1);
		}
		
	}
	
}
