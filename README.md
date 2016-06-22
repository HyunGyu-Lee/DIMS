# DIMS
기숙사 정보 관리 시스템 - DIMS(Domitory Infomation Management System)

일반적으로 행해지는 기숙사의 업무를 수행하는 시스템입니다.

# 개발 환경
  Java SE Development Kit 8, Eclipse, MySQL

# 필요 조건
  MySQL, 아래 링크를 통해 XAMPP를 설치하면서 MySQL을 설치할 수 있습니다.
  https://www.apachefriends.org/download.html 

# 시스템 구성

  - 기본 설정
    tools.Statics.java        : 서버 IP주소, 포트번호, DB 커넥터, 사용할 DB, DB에 로그인할 계정 등 각종 설정사항 관리
  
  - 통신
    tools.NetworkProtocols.java : 서버, 클라이언트의 동작을 결정하는 "타입"들 정의, 관리
    
  - 서버
    servers.ServerControlPanel.java : 서버 컨트롤 패널 실행
    servers.DIMS_Server.java        : 클라이언트에서 보내는 NetworkProtocols의 지정된 타입에 따라 요청처리 수행, 응답  
      
  - 관리자 클라이언트
    clients.MainApplication.java : 메인 클라이언트 프로그램 (공통)
    clients.controllers.AdministratorMainController.java : 관리자 메인페이지 컨트롤 수행

  - 학  생 클라이언트
    clients.MainApplication.java : 메인 클라이언트 프로그램 (공통)
    clients.controllers.StudentMain.java     : 학생 메인페이지 컨트롤 수행
  
  - 데이터베이스
    databases.DatabaseHandler.java : 서버와 서버로컬의 DB와 연결, 쿼리 실행, 결과 서버로 전달
  
  - UI
    clients.SceneManager.java : 메인 클라이언트 프로그램이 실행되는 동안 화면전환 담당, 대화상자 외 전환할 화면이 등록되어있음
    clients.ui, servers.ui, resources 패키지엔 FXML파일, CSS, 사용되는 아이콘 파일들 있음
    
      
      
# 실행 방법
  1) 데이터베이스 실행 => XAMPP 실행 후 mysql start 버튼 클릭
  2) 서버 실행         => ServerControlPanel 실행 후 GUI화면에 start버튼 클릭, 성공적으로 실행되면 버튼 옆 라벨이 초록색으로 바뀜
  3) Tester클래스      => Tester를 실행하면 MainApplication이 launch되면서 메인 클라이언트 프로그램 실행, 서버 IP주소 설정화면 노출
  4) ip주소를 설정하면 서버와 연결하고 연결 성공하면 로그인화면이 노출, 후에 로그인하는 계정에 따라 관리자, 학생 클라이언트로 분기
