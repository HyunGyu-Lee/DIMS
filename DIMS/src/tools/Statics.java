package tools;

public class Statics {

	/* Application 설정 */
	public static final int COMMIT = 0;	
	public static final int ERROR = -2;
		
	/* UI 설정 */
		/* UI fxml 파일이름 목록 */
		public static final String LOGIN_WINDOW_FXML = "./ui/LoginUI.fxml";		// 가장 첫화면, 로그인화면
		public static final String PASSWORD_FIND_FXML = "./ui/PasswordFind.fxml";	// 비밀번호 찾기 화면
		public static final String ADMIN_MAIN_FXML = "./ui/AdministratorMain.fxml";  // 관리자 메인 페이지 화면
		public static final String REGISTER_FXML = "./ui/MemberJoin.fxml";  // 회원가입 화면
		public static final String CHECK_MESSAGE_FXML = "../ui/CheckMessageDialog.fxml";	// 메세지확인 다이얼로그
		public static final String ALERT_DIALOG = "../ui/MessageDialogUI.fxml";	// 알림창
		public static final String SCHEDULE_CREATE_DIALOG = "../ui/ScheduleCreateDialog.fxml";	// 새일정
		public static final String CHECK_STUDENT_FXML = "../ui/StudentInfoDialog.fxml"; //신상정보 상세내용 화면
	    public static final String CHECK_WEABAK_FXML = "../ui/CheckWeabakDialog.fxml"; //외박 정보 상세내용 화면
		public static final String PLUS_MINUS_ASSIGN_FXML ="../ui/PlusMinusAssignDialog.fxml"; //신상정보 부여 화면
	    
	    public static final String STUDENT_MAIN_FXML = "./ui/StudentMain.fxml";
		public static final String STUDENT_REAUTH_DIALOG_FXML = "../ui/ReAuthDialog.fxml";
	    
		
		/* UI 윈도우 창 이름 목록 */
		public static final String LOGIN_WINDOW_TITLE = "로 그 인";					
		public static final String PASSWORD_FIND_TITLE = "비 밀 번 호 찾 기";			
		public static final String ADMIN_MAIN_TITLE = "관 리 자 메 인 페 이 지";
		public static final String REGISTER_TITLE = "회 원 가 입";  // 관리자 메인 페이지 화면
		public static final String CHECK_MESSAGE_TITLE = "메 세 지 확 인";
		public static final String ALERT_DIALOG_TITLE = "알 림";
		public static final String SCHEDULE_CREATE_DIALOG_TITLE = "새 일정";
		public static final String STUDENT_TITLE = "학생 신상 정보 페이지";
	    public static final String WEABAK_TITLE = "외박 승인 정보 페이지";
	    public static final String PLUS_MINUS_ASSIGN_TITLE = "상벌점 부여 정보 페이지";
		
		public static final String STUDENT_MAIN_TITLE = "학 생 메 인 페 이 지";
		public static final String STUDENT_REAUTH_DIALOG_TITLE = "재인증 요청";		
		
	/* Network 설정 */
	public static final String SERVER_ID_ADDRESS = "localhost";				// 서버 IP 주소
	
		/* Networking - error */
		public static final int CONNECT_ERROR = -1;							// 서버 접속 실패
		public static final int UNKNOWN_HOST_ERROR = -3;					// 호스트 주소 조회 실패
		
	/* Database */
	public static final String DASEBASE_DRIVER = "org.gjt.mm.mysql.Driver";	// 기본 데이터베이스 드라이버 패키지
	public static final String DEFAULT_USE_DATABASE = "db2";				// 기본 사용 데이터베이스
	public static final String DEFAULT_DATABASE_HOST_ID = "root";			// 기본 데이터베이스 접속 아이디
	public static final String DEFAULT_DATABASE_HOST_PASSWORD = "1234";		// 기본 데이터베이스 접속 비밀번호
	
	/* User Data */
	public static final String DEFALUE_USER_DATA_DIRECTORY = "C:\\\\DIMS\\\\Userdata\\\\";
}
