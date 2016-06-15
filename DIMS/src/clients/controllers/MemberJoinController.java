package clients.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;

import com.orsoncharts.util.json.JSONObject;

import clients.SceneManager;
import clients.customcontrols.CustomDialog;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tools.NetworkProtocols;
import tools.Statics;
import tools.Toolbox;

public class MemberJoinController implements Initializable  {

   //ȸ������ UI����
   @FXML TextField idField;         // �й��ʵ�
   @FXML TextField pwdField;         // ��й�ȣ �ʵ�
   @FXML TextField RepwdField;         // ��й�ȣ Ȯ�� �ʵ�
   @FXML TextField qusanwerField;       // ���� �亯 �ʵ�
   @FXML TextArea addressField;      // ���� �ؽ�Ʈ �ʵ�
   @FXML TextField phone1Field;      // �� �߰� �ڸ�
   @FXML TextField phone2Field;      // �� ���ڸ�
   @FXML TextField privacynumField;   // �ֹι�ȣ
   @FXML TextField home1Field;         // �� �߰� ��ȣ
   @FXML TextField home2Field;         // �� �� ��ȣ
   @FXML TextField nameField;         // �̸� �ʵ�
   
   
   @FXML ComboBox<String> qusComboBox;      //�����޺��ڽ�
   @FXML ComboBox<String> phoneComboBox;   //�� ��Ż� �޺��ڽ�
   @FXML ComboBox<String> homeComboBox;     //�� �� ��ȣ �޺��ڽ�
   @FXML ComboBox<String> sexComboBox;      //���� �޺��ڽ�
   @FXML ComboBox<String> classComboBox;   //�Ҽ��а� �޺��ڽ�
   @FXML ComboBox<String> classnumComboBox;   //�г� �޺��ڽ�
   
   //�޺��ڽ� ������
   ObservableList<String> qusoption = FXCollections.observableArrayList(
         "�¾ ���� ����Դϱ�?","��Ӵ��� ������ �����Դϱ�?","�ƹ����� ������ �����Դϱ�?","���� ����־��� �������� ����ϱ�?");
   ObservableList<String> phoneoption = FXCollections.observableArrayList("010","011","016","017","019");
   ObservableList<String> homeoption = FXCollections.observableArrayList("02","031","051","032","062","044","042","052","033","043","041","063","061","054","055","064");
   ObservableList<String> sexoption = FXCollections.observableArrayList("��","��");
   ObservableList<String> classoption = FXCollections.observableArrayList("��ǻ���������а�"
         ,"ö���濵�����а�","ö���ü����а�","ö�������ý��۰��а�","ö���������ڰ��а�","ö�������ý��۰��а�");
   
   ObservableList<String> classnumoption = FXCollections.observableArrayList("1","2","3","4");
   boolean ID_CHECK = false; 
   
   private SceneManager sManager;
   private ObjectInputStream fromServer;
   private ObjectOutputStream toServer;
   
   @Override
   public void initialize(URL arg0, ResourceBundle arg1)
   {
      
   }
   
   public void startListener()
   {
      new Listener().start();
   }
   
   public void INIT_CONTROLLER(SceneManager manager, ObjectInputStream fromServer, ObjectOutputStream toServer)
   {
      this.sManager = manager;
      this.fromServer = fromServer;
      this.toServer = toServer;
      qusComboBox.setItems(qusoption);
      phoneComboBox.setItems(phoneoption);
      homeComboBox.setItems(homeoption);
      sexComboBox.setItems(sexoption);
      classComboBox.setItems(classoption);
      classnumComboBox.setItems(classnumoption);

   }
   
   //�ߺ��˻��ư
   @SuppressWarnings("unchecked")
   @FXML
   private void onDuplicate()
   {
      if(!idField.getText().equals(""))
      {
         if(idField.getText().length() == 7)
         {
            JSONObject request = new JSONObject();
            request.put("type", NetworkProtocols.ID_DUP_CHECK_REQUEST);
            request.put("id", idField.getText());
            sendProtocol(request);
         }
         else
         {
            CustomDialog.showConfirmDialog("�й� ����� �߸� �Ǿ����ϴ�.", sManager.getStage());
         }
      }
      else
      {
         CustomDialog.showConfirmDialog("���̵� �Է��� �ּ���.", sManager.getStage());
      }
   }
   
   @FXML
   private void onCancle()
   {
      System.out.println("��ҹ�ư Ŭ��");
      sManager.changeListenController("MEMBER_JOIN");
      sManager.changeScene(Statics.LOGIN_WINDOW_FXML);
   }
   //Ȯ�ι�ư
   @SuppressWarnings("unchecked")
   @FXML
   private void onSuccess()
   {
      int check=0;
      while(true)
      {
         if(!ID_CHECK)
         {
            CustomDialog.showConfirmDialog("�й� �ߺ�Ȯ���� ���ּ���.", sManager.getStage());
            break;
         }
         String id = idField.getText();
         String password = pwdField.getText();
         String rpassword = RepwdField.getText();
         if(password.equals(""))
         {
            CustomDialog.showConfirmDialog("��й�ȣ�� �Է� �� �ּ���.", sManager.getStage());
            break;
         }
         else if(!password.equals(rpassword) || rpassword.equals(""))
         {
            CustomDialog.showConfirmDialog("��й�ȣ Ȯ���� ���� �ʽ��ϴ�.", sManager.getStage());
            break;
         }
         
         int classcheck = classComboBox.getSelectionModel().getSelectedIndex()+1;
         if(classcheck == 0)
         {
            CustomDialog.showConfirmDialog("�Ҽ��а��� �����ϼ���", sManager.getStage());
            break;
         }
         
         
         
         int selectqus = qusComboBox.getSelectionModel().getSelectedIndex()+1;
         if(selectqus == 0)
         {
            CustomDialog.showConfirmDialog("���� ����� �����ϼ���", sManager.getStage());
            break;
         }
         String qusanwer = qusanwerField.getText();
         if(qusanwer.equals(""))
         {
            CustomDialog.showConfirmDialog("������ �亯 ���ּ���.", sManager.getStage());
            break;
         }

         String name = nameField.getText();
         if(name.equals(""))
         {
            CustomDialog.showConfirmDialog("������ �Է� �� �ּ���.", sManager.getStage());
            break;
         }
         
         String address = addressField.getText();
         if(address.equals(""))
         {
            CustomDialog.showConfirmDialog("�ּҷ� �Է� �ϼ���.", sManager.getStage());
            break;
         }
         
         int phonecheck = phoneComboBox.getSelectionModel().getSelectedIndex()+1;
         String phone1 = phone1Field.getText();
         String phone2 = phone2Field.getText();
         if(phonecheck == 0 || phone1.equals("") || phone2.equals(""))
         {
            CustomDialog.showConfirmDialog("�޴��� ��ȣ�� �ùٸ��� �Է� �ϼ���.", sManager.getStage());
            break;
         }
         
         String phonenum = phoneComboBox.getSelectionModel().getSelectedItem()+"-"+phone1+"-"+phone2;
         
         int homecheck = homeComboBox.getSelectionModel().getSelectedIndex()+1;
         String homenum1 = home1Field.getText();
         String homenum2 = home2Field.getText();
         
         if(homecheck == 0 || homenum1.equals("") || homenum2.equals(""))
         {
            CustomDialog.showConfirmDialog("������ȭ��ȣ�� �ùٸ��� �Է� �ϼ���.", sManager.getStage());
            break;
         }
         
         String homenum = homeComboBox.getSelectionModel().getSelectedItem()+"-"+homenum1+"-"+homenum2;
         
         String privacy = privacynumField.getText();
         
         if(privacy.equals(""))
         {
            CustomDialog.showConfirmDialog("�ֹε�Ϲ�ȣ�� �Է��Ͽ� �ּ���.", sManager.getStage());
            break;
         }
         else if(privacy.length() < 14)
         {
            CustomDialog.showConfirmDialog("�ֹε�Ϲ�ȣ ����� �ùٸ��� �Է��Ͽ� �ּ���.[�Էº���]", sManager.getStage());
            break;
         }
         for(int i = 0 ; i < privacy.length() ; i++)
         {
            
            char a = privacy.charAt(i);
            System.out.println(a);
            if((a == '-'))
            {
               check++;
            }
         }
         if(check == 0)
         {
            CustomDialog.showConfirmDialog("�ֹε�Ϲ�ȣ ����� �ùٸ��� �Է��Ͽ� �ּ���.[-����]", sManager.getStage());
            break;
         }
         
         int sex = sexComboBox.getSelectionModel().getSelectedIndex()+1;
         if(sex == 0)
         {
            CustomDialog.showConfirmDialog("������ �����Ͽ� �ּ���.", sManager.getStage());
            break;
         }
         String Strsex = sexComboBox.getSelectionModel().getSelectedItem();
         
         int classnum = classnumComboBox.getSelectionModel().getSelectedIndex()+1;
         if(classnum == 0)
         {
            CustomDialog.showConfirmDialog("�г��� �����Ͽ� �ּ���.", sManager.getStage());
            break;
         }
         
         
         System.out.println("------");
         JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MEMBER_JOIN_REQUEST);
         json.put("�й�", id);    json.put("��й�ȣ", password);   json.put("����", selectqus);
         json.put("�亯", qusanwer);   json.put("�Ҽ��а�", classcheck);   json.put("�̸�", name);
         json.put("�ּ�", address);   json.put("�ڵ�����ȣ", phonenum);   json.put("������ȭ��ȣ", homenum);
         json.put("�ֹε�Ϲ�ȣ", privacy);   json.put("����", Strsex);      json.put("���ȣ", "�̹���");
         json.put("��", "�̹���"); json.put("�г�", classnum);
         
         sendProtocol(json);
         
         break;
      }
      
      
      
   }
   
   
   public void sendProtocol(JSONObject protocol)
   {
      try
      {
         toServer.writeObject(protocol);
         toServer.flush();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

   }
   
   class Listener extends Thread
   {

      @SuppressWarnings("unchecked")
	@Override
      
      public void run()
      {
         
         try
         {
            while(true)
            {
               JSONObject respond = null;
               try
               {
                  respond = (JSONObject)fromServer.readObject();
               }
               catch (ClassNotFoundException e)
               {
                  e.printStackTrace();
               }
               
               String type = respond.get("type").toString();
               
               if(type.equals(NetworkProtocols.ID_DUP_RESPOND_OK))
               {
                  Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                        CustomDialog.showConfirmDialog("������ ������ �й� �Դϴ�.", sManager.getStage());
                        ID_CHECK = true;
                     }
                  });
               }
               else if(type.equals(NetworkProtocols.ID_DUP_RESPOND_DENY))
               {
                  Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                        CustomDialog.showConfirmDialog("�̹� ����� �Ǿ� �ִ� �й� �Դϴ�.", sManager.getStage());
                     }
                  });
               }
               else if(type.equals(NetworkProtocols.EXIT_RESPOND))
               {
                  break;
               }
               else if(type.equals(NetworkProtocols.RECIEVE_READY))
               {
                  JSONObject protocol = new JSONObject();
                  protocol.put("type", NetworkProtocols.RECIEVE_READY_OK);
                  sendProtocol(protocol);
               }
               else if(type.equals(NetworkProtocols.MEMBER_JOIN_RESPOND))
               {   
                  Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                        CustomDialog.showConfirmDialog("����� �Ϸ� �Ǿ����ϴ�.", sManager.getStage());
                        sManager.changeListenController("MEMBER_JOIN");
                        sManager.changeScene(Statics.LOGIN_WINDOW_FXML);
                     }
                  });
               }
               
               
            }
         }
         catch(IOException e)
         {
            e.printStackTrace();
         }
         
         System.out.println("MemberJoin ������ ������ ����");
      }

   }
}