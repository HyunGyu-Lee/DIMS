package com.hst.dims.clients.customcontrols;

import java.util.Date;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import com.hst.dims.tools.Toolbox;

public class ScheduleObject extends AnchorPane{

	private Label title;
	private Label date;
	private TextArea content;
	private Label category;
	private ScheduleObject me;
	private int scheduleID;
	private String backgroundInfo, titleValue, categoryValue, dateText, contentValue;
	private Date dateValue;
	private double width = 295, height = 200;
	private boolean isSelected = false;

	public static final String VIEWTYPE_GRID = "grid";
	public static final String VIEWTYPE_LIST = "list";
	public static final String VIEWTYPE_CELL = "cell";

	public ScheduleObject(){}

	public ScheduleObject(String titleValue, Date dateValue, String categoryValue, String contentValue, String viewtype)
	{
		me = this;
		switch(categoryValue)
		{
			case "일반" : backgroundInfo = "-fx-background-color: cyan;"; break;
			case "중요" : backgroundInfo = "-fx-background-color: red;"; break;
			case "행사" : backgroundInfo = "-fx-background-color: green;"; break;
		}
		this.titleValue = titleValue;
		this.categoryValue = categoryValue;
		this.dateValue = dateValue;
		this.dateText = Toolbox.getCurrentTimeFormat(dateValue, "yyyy-MM-dd HH:mm");
		this.contentValue = contentValue;
		if(viewtype.equals(VIEWTYPE_GRID))
		{
			objectType_GRID(titleValue, dateValue, categoryValue, contentValue);
		}
		else if(viewtype.equals(VIEWTYPE_LIST))
		{
			objectType_LIST(titleValue, dateValue, categoryValue, contentValue);
		}
	}

	public void objectType_GRID(String titleValue, Date dateValue, String categoryValue, String contentValue)
	{
		DropShadow s = new DropShadow();
		s.setWidth(21);
		s.setHeight(21);
		s.setRadius(10);

		this.setPrefSize(width, height);
		this.setEffect(s);
		this.setLayoutX(10);
		this.setLayoutY(10);

		switch(categoryValue)
		{
			case "일반" : backgroundInfo = "-fx-background-color: cyan;"; break;
			case "중요" : backgroundInfo = "-fx-background-color: red;"; break;
			case "행사" : backgroundInfo = "-fx-background-color: green;"; break;
		}
		this.setStyle(backgroundInfo);
		title = createTitleLabel_GRID(titleValue);
		date = createDateLabel_GRID(Toolbox.getCurrentTimeFormat(dateValue, "yyyy-MM-dd HH:mm"));
		category = createCategoryLabel_GRID(categoryValue);
		content = createContentArea_GRID(contentValue);



		this.setOnMouseDragged(new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			public void handle(javafx.scene.input.MouseEvent event) {
				me.setLayoutX(event.getSceneX());
				me.setLayoutY(event.getSceneY()-105);
			}
		});

		this.getChildren().addAll(title, category, date, content);
	}

	public void objectType_LIST(String titleValue, Date dateValue, String categoryValue, String contentValue)
	{
		DropShadow s = new DropShadow();
		s.setWidth(21);
		s.setHeight(21);
		s.setRadius(10);

		this.setPrefSize(581, 136);
		this.setEffect(s);
		this.setLayoutX(10);
		this.setLayoutY(10);

		switch(categoryValue)
		{
			case "일반" : backgroundInfo = "-fx-background-color: cyan;"; break;
			case "중요" : backgroundInfo = "-fx-background-color: red;"; break;
			case "행사" : backgroundInfo = "-fx-background-color: green;"; break;
		}
		this.setStyle(backgroundInfo);
		title = createTitleLabel_LIST(titleValue);
		date = createDateLabel_LIST(Toolbox.getCurrentTimeFormat(dateValue, "yyyy-MM-dd HH:mm"));
		category = createCategoryLabel_LIST(categoryValue);
		content = createContentArea_LIST(contentValue);

		this.setOnMouseDragged(new EventHandler<javafx.scene.input.MouseEvent>() {
			@Override
			public void handle(javafx.scene.input.MouseEvent event) {
				me.setLayoutX(event.getSceneX());
				me.setLayoutY(event.getSceneY()-105);
			}
		});

		this.getChildren().addAll(title, category, date, content);
	}

	public void setScheduleID(int id)
	{
		this.scheduleID = id;
	}

	public int getScheduleID()
	{
		return scheduleID;
	}

	public String getTitle()
	{
		return titleValue;
	}

	public String getDateText()
	{
		return dateText;
	}

	public String getCategory()
	{
		return categoryValue;
	}

	public String getContent()
	{
		return contentValue;
	}

	public ScheduleObject bindingCellObject(double witdh, double height)
	{
		this.setPrefSize(witdh, height);
		DropShadow s = new DropShadow();
		s.setWidth(10);
		s.setHeight(10);
		s.setRadius(10);
		this.setEffect(s);

		this.setStyle(backgroundInfo);

		title = new Label(titleValue);
		title.setStyle("-fx-background-color: linear-gradient(to bottom, #4c4c4c 0%,#595959 12%,#666666 25%,#474747 39%,#2c2c2c 50%,#000000 51%,#111111 60%,#2b2b2b 76%,#1c1c1c 91%,#131313 100%);");
		title.setTextFill(Paint.valueOf("white"));
		title.setFont(Font.font("HYwulM",20));
		title.setAlignment(Pos.CENTER);
		title.setLayoutX(15);
		title.setLayoutY(14);
		title.setPrefSize(118, 17);

		date = new Label(new java.text.SimpleDateFormat("d").format(dateValue));
		date.setTextFill(Paint.valueOf("white"));
		date.setLayoutX(58);
		date.setLayoutY(48);
		date.setFont(Font.font("HYwulM",20));
		date.setPrefSize(26, 25);

		this.getChildren().addAll(title,date);

		return this;
	}

	private Label createTitleLabel_GRID(String value)
	{
		Label temp = new Label(value);
		temp.setPrefWidth(width-95);
		temp.setPrefHeight(40);
		temp.setAlignment(Pos.CENTER);
		temp.setLayoutX(0);
		temp.setLayoutY(0);

		if(value.length()>6)
		{
			temp.setFont(Font.font("HYwulM",20));
		}
		else if(value.length()>10)
		{
			temp.setFont(Font.font("HYwulM",10));
		}
		else
		{
			temp.setFont(Font.font("HYwulM",30));
		}

		temp.setStyle("-fx-background-color: linear-gradient(to bottom, #4c4c4c 0%,#595959 12%,#666666 25%,#474747 39%,#2c2c2c 50%,#000000 51%,#111111 60%,#2b2b2b 76%,#1c1c1c 91%,#131313 100%);");
		temp.setTextFill(Paint.valueOf("white"));
		return temp;
	}
	private Label createDateLabel_GRID(String value)
	{
		Label temp = new Label(value);
		temp.setPrefSize(256, 29);
		temp.setAlignment(Pos.CENTER);
		temp.setLayoutX(14);
		temp.setLayoutY(42);
		temp.setFont(Font.font("HYwulM",19));
		return temp;
	}
	private Label createCategoryLabel_GRID(String value)
	{
		Label temp = new Label(value);
		temp.setPrefSize(95, 40);
		temp.setAlignment(Pos.CENTER);
		temp.setLayoutX(201);
		temp.setLayoutY(0);
		temp.setFont(Font.font("HYwulM",23));
		return temp;
	}
	private TextArea createContentArea_GRID(String content)
	{
		StringBuilder b = new StringBuilder();

		for(int i = 0;i<content.length();i++)
		{
			b.append(content.charAt(i));
			if(i%12==0&&i!=0)
			{
				b.append("\n");
			}
		}

		TextArea temp = new TextArea();
		temp.setText(b.toString());
		temp.setFont(Font.font("HYwulM",20));
		temp.setLayoutX(13);
		temp.setLayoutY(71);
		temp.setPrefSize(271, 120);
		temp.setEditable(false);
		return temp;
	}

	private Label createTitleLabel_LIST(String value)
	{
		Label temp = new Label(value);
		temp.setPrefWidth(221);
		temp.setPrefHeight(29);
		temp.setAlignment(Pos.CENTER);
		temp.setLayoutX(14);
		temp.setLayoutY(14);

		if(value.length()>6)
		{
			temp.setFont(Font.font("HYwulM",20));
		}
		else if(value.length()>10)
		{
			temp.setFont(Font.font("HYwulM",10));
		}
		else
		{
			temp.setFont(Font.font("HYwulM",30));
		}

		return temp;
	}
	private Label createDateLabel_LIST(String value)
	{
		Label temp = new Label(value);
		temp.setPrefSize(229, 29);
		temp.setAlignment(Pos.CENTER);
		temp.setLayoutX(14);
		temp.setLayoutY(54);
		temp.setFont(Font.font("HYwulM",22));
		return temp;
	}
	private Label createCategoryLabel_LIST(String value)
	{
		Label temp = new Label(value);
		temp.setPrefSize(221, 29);
		temp.setAlignment(Pos.CENTER);
		temp.setLayoutX(14);
		temp.setLayoutY(93);
		temp.setFont(Font.font("HYwulM",22));
		return temp;
	}
	private TextArea createContentArea_LIST(String content)
	{
		StringBuilder b = new StringBuilder();

		for(int i = 0;i<content.length();i++)
		{
			b.append(content.charAt(i));
			if(i%12==0&&i!=0)
			{
				b.append("\n");
			}
		}

		TextArea temp = new TextArea();
		temp.setText(b.toString());
		temp.setFont(Font.font("HYwulM",20));
		temp.setLayoutX(256);
		temp.setLayoutY(14);
		temp.setPrefSize(315, 115);
		temp.setEditable(false);
		return temp;
	}

	public void selected()
	{
		isSelected = true;
		this.setStyle(backgroundInfo+"-fx-border-color : black;"+"-fx-border-width : 5px;");
	}

	public void unSelect()
	{
		isSelected = false;
		this.setStyle(backgroundInfo);
	}

	public boolean isSelected()
	{
		return isSelected;
	}
}