package com.hst.dims.clients.controllers;

import com.hst.dims.clients.SceneManager;
import com.hst.dims.clients.customcontrols.CustomDialog;
import com.hst.dims.tools.NetworkProtocols;
import com.hst.dims.tools.Statics;
import com.hst.dims.tools.Toolbox;
import com.orsoncharts.util.json.JSONObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class MemberJoinController implements Initializable  {

   //회원가입 UI구성
   @FXML TextField idField;         // STUDENT_NO필드
   @FXML TextField pwdField;         // 비밀번호 필드
   @FXML TextField RepwdField;         // 비밀번호 확인 필드
   @FXML TextField qusanwerField;       // 질문 답변 필드
   @FXML TextArea addressField;      // 질문 텍스트 필드
   @FXML TextField phone1Field;      // 폰 중간 자리
   @FXML TextField phone2Field;      // 폰 끝자리
   @FXML TextField privacynumField;   // 주민번호
   @FXML TextField home1Field;         // 집 중간 번호
   @FXML TextField home2Field;         // 집 끝 번호
   @FXML TextField nameField;         // 이름 필드


   @FXML ComboBox<String> qusComboBox;      //질문콤보박스
   @FXML ComboBox<String> phoneComboBox;   //폰 통신사 콤보박스
   @FXML ComboBox<String> homeComboBox;     //집 앞 번호 콤보박스
   @FXML ComboBox<String> sexComboBox;      //성별 콤보박스
   @FXML ComboBox<String> classComboBox;   //소속학과 콤보박스
   @FXML ComboBox<String> classnumComboBox;   //학년 콤보박스

   //콤보박스 아이템
   ObservableList<String> qusoption = FXCollections.observableArrayList(
           "태어난 곳은 어디입니까?","어머니의 성함은 무엇입니까?","아버지의 성함은 무엇입니까?","가장 재미있었던 여행지는 어딥니까?");
   ObservableList<String> phoneoption = FXCollections.observableArrayList("010","011","016","017","019");
   ObservableList<String> homeoption = FXCollections.observableArrayList("02","031","051","032","062","044","042","052","033","043","041","063","061","054","055","064");
   ObservableList<String> sexoption = FXCollections.observableArrayList("남","여");
   ObservableList<String> classoption = FXCollections.observableArrayList("컴퓨터정보공학과"
           ,"철도경영물류학과","철도시설공학과","철도운전시스템공학과","철도전기전자공학과","철도차량시스템공학과");

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

   //중복검사버튼
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
            CustomDialog.showConfirmDialog("STUDENT_NO 양식이 잘못 되었습니다.", sManager.getStage());
         }
      }
      else
      {
         CustomDialog.showConfirmDialog("아이디를 입력해 주세요.", sManager.getStage());
      }
   }

   @FXML
   private void onCancle()
   {
      System.out.println("취소버튼 클릭");
      sManager.changeListenController("MEMBER_JOIN");
      sManager.changeScene(Statics.LOGIN_WINDOW_FXML);
   }
   //확인버튼
   @SuppressWarnings("unchecked")
   @FXML
   private void onSuccess()
   {
      int check=0;
      while(true)
      {
         if(!ID_CHECK)
         {
            CustomDialog.showConfirmDialog("STUDENT_NO 중복확인을 해주세요.", sManager.getStage());
            break;
         }
         String id = idField.getText();
         String password = pwdField.getText();
         String rpassword = RepwdField.getText();
         if(password.equals(""))
         {
            CustomDialog.showConfirmDialog("비밀번호를 입력 해 주세요.", sManager.getStage());
            break;
         }
         else if(!password.equals(rpassword) || rpassword.equals(""))
         {
            CustomDialog.showConfirmDialog("비밀번호 확인이 맞지 않습니다.", sManager.getStage());
            break;
         }

         int classcheck = classComboBox.getSelectionModel().getSelectedIndex()+1;
         if(classcheck == 0)
         {
            CustomDialog.showConfirmDialog("소속학과를 선택하세요", sManager.getStage());
            break;
         }



         int selectqus = qusComboBox.getSelectionModel().getSelectedIndex()+1;
         if(selectqus == 0)
         {
            CustomDialog.showConfirmDialog("질문 양식을 선택하세요", sManager.getStage());
            break;
         }
         String qusanwer = qusanwerField.getText();
         if(qusanwer.equals(""))
         {
            CustomDialog.showConfirmDialog("질문에 답변 해주세요.", sManager.getStage());
            break;
         }

         String name = nameField.getText();
         if(name.equals(""))
         {
            CustomDialog.showConfirmDialog("성함을 입력 해 주세요.", sManager.getStage());
            break;
         }

         String address = addressField.getText();
         if(address.equals(""))
         {
            CustomDialog.showConfirmDialog("주소룰 입력 하세요.", sManager.getStage());
            break;
         }

         int phonecheck = phoneComboBox.getSelectionModel().getSelectedIndex()+1;
         String phone1 = phone1Field.getText();
         String phone2 = phone2Field.getText();
         if(phonecheck == 0 || phone1.equals("") || phone2.equals(""))
         {
            CustomDialog.showConfirmDialog("휴대폰 번호를 올바르게 입력 하세요.", sManager.getStage());
            break;
         }

         String phonenum = phoneComboBox.getSelectionModel().getSelectedItem()+"-"+phone1+"-"+phone2;

         int homecheck = homeComboBox.getSelectionModel().getSelectedIndex()+1;
         String homenum1 = home1Field.getText();
         String homenum2 = home2Field.getText();

         if(homecheck == 0 || homenum1.equals("") || homenum2.equals(""))
         {
            CustomDialog.showConfirmDialog("자택전화번호를 올바르게 입력 하세요.", sManager.getStage());
            break;
         }

         String homenum = homeComboBox.getSelectionModel().getSelectedItem()+"-"+homenum1+"-"+homenum2;

         String privacy = privacynumField.getText();

         if(privacy.equals(""))
         {
            CustomDialog.showConfirmDialog("주민등록번호를 입력하여 주세요.", sManager.getStage());
            break;
         }
         else if(privacy.length() < 14)
         {
            CustomDialog.showConfirmDialog("주민등록번호 양식을 올바르게 입력하여 주세요.[입력부족]", sManager.getStage());
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
            CustomDialog.showConfirmDialog("주민등록번호 양식을 올바르게 입력하여 주세요.[-없음]", sManager.getStage());
            break;
         }

         int sex = sexComboBox.getSelectionModel().getSelectedIndex()+1;
         if(sex == 0)
         {
            CustomDialog.showConfirmDialog("성별을 선택하여 주세요.", sManager.getStage());
            break;
         }
         String Strsex = sexComboBox.getSelectionModel().getSelectedItem();

         int classnum = classnumComboBox.getSelectionModel().getSelectedIndex()+1;
         if(classnum == 0)
         {
            CustomDialog.showConfirmDialog("학년을 선택하여 주세요.", sManager.getStage());
            break;
         }


         System.out.println("------");
         JSONObject json = Toolbox.createJSONProtocol(NetworkProtocols.MEMBER_JOIN_REQUEST);
         json.put("STUDENT_NO", id);    json.put("비밀번호", password);   json.put("질문", selectqus);
         json.put("답변", qusanwer);   json.put("DEPARTMENT", classcheck);   json.put("이름", name);
         json.put("주소", address);   json.put("핸드폰번호", phonenum);   json.put("자택전화번호", homenum);
         json.put("주민등록번호", privacy);   json.put("성별", Strsex);      json.put("방번호", "미배정");
         json.put("층", "미배정"); json.put("학년", classnum);

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
                        CustomDialog.showConfirmDialog("생성이 가능한 STUDENT_NO 입니다.", sManager.getStage());
                        ID_CHECK = true;
                     }
                  });
               }
               else if(type.equals(NetworkProtocols.ID_DUP_RESPOND_DENY))
               {
                  Platform.runLater(new Runnable() {
                     @Override
                     public void run() {
                        CustomDialog.showConfirmDialog("이미 등록이 되어 있는 STUDENT_NO 입니다.", sManager.getStage());
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
                        CustomDialog.showConfirmDialog("등록이 완료 되었습니다.", sManager.getStage());
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

         System.out.println("MemberJoin 리스너 스레드 종료");
      }

   }
}