package tools;

public class Statics {

	/* Application ���� */
	public static final int COMMIT = 0;	
	public static final int ERROR = -2;
		
	/* UI ���� */
		/* UI fxml �����̸� ��� */
		public static final String LOGIN_WINDOW_FXML = "./ui/LoginUI.fxml";		// ���� ùȭ��, �α���ȭ��
		public static final String PASSWORD_FIND_FXML = "./ui/PasswordFind.fxml";	// ��й�ȣ ã�� ȭ��
		public static final String ADMIN_MAIN_FXML = "./ui/AdministratorMain.fxml";  // ������ ���� ������ ȭ��
		public static final String REGISTER_FXML = "./ui/MemberJoin.fxml";  // ȸ������ ȭ��
		public static final String CHECK_MESSAGE_FXML = "../ui/CheckMessageDialog.fxml";	// �޼���Ȯ�� ���̾�α�
		public static final String ALERT_DIALOG = "../ui/MessageDialogUI.fxml";	// �˸�â
		public static final String SCHEDULE_CREATE_DIALOG = "../ui/ScheduleCreateDialog.fxml";	// ������
		public static final String CHECK_STUDENT_FXML = "../ui/StudentInfoDialog.fxml"; //�Ż����� �󼼳��� ȭ��
	    public static final String CHECK_WEABAK_FXML = "../ui/CheckWeabakDialog.fxml"; //�ܹ� ���� �󼼳��� ȭ��
		public static final String PLUS_MINUS_ASSIGN_FXML ="../ui/PlusMinusAssignDialog.fxml"; //�Ż����� �ο� ȭ��
	    public static final String OK_CANCLE_DIALOG = "../ui/OK_CANCEL_DialogUI.fxml";
		
	    public static final String STUDENT_MAIN_FXML = "./ui/StudentMain.fxml";
		public static final String STUDENT_REAUTH_DIALOG_FXML = "../ui/ReAuthDialog.fxml";
	    public static final String LOADING_DIALOG_FXML = "../ui/LoadingDialog.fxml";
		
		/* UI ������ â �̸� ��� */
		public static final String LOGIN_WINDOW_TITLE = "DIMS - �α���";					
		public static final String PASSWORD_FIND_TITLE = "DIMS - ��й�ȣ ã��";			
		public static final String ADMIN_MAIN_TITLE = "DIMS - �����ڿ� Ŭ���̾�Ʈ";
		public static final String REGISTER_TITLE = "DIMS - ȸ������";  // ������ ���� ������ ȭ��
		public static final String CHECK_MESSAGE_TITLE = "DIMS - �޼��� Ȯ��";
		public static final String ALERT_DIALOG_TITLE = "DIMS - �˸�";
		public static final String SCHEDULE_CREATE_DIALOG_TITLE = "DIMS - �� ����";
		public static final String STUDENT_TITLE = "DIMS - �л� �Ż� ���� Ȯ��";
	    public static final String WEABAK_TITLE = "DIMS - �ܹ� ���� ���� Ȯ��";
	    public static final String PLUS_MINUS_ASSIGN_TITLE = "DIMS - ����� �ο� ���� Ȯ��";
		public static final String OK_CANCEL_DAILOG_TITLE = "DIMS - Ȯ��";
	    
		public static final String STUDENT_MAIN_TITLE = "DIMS - �л��� Ŭ���̾�Ʈ";
		public static final String STUDENT_REAUTH_DIALOG_TITLE = "DIMS - ������ ��û";
		public static final String LOADING_DIALOG_TITLE = "DIMS - ���� �ٿ�ε� ������...";
		
	/* Network ���� */
	public static final String DIMS_SERVER_IP_ADDRESS = "localhost";				// ���� IP �ּ�
	public static final int DIMS_SERVER_PORT_NUMBER = 8080;
	
	public static final String DIMS_FILE_SERVER_IP_ADDRESS = "localhost";				// ���� IP �ּ�
	public static final int DIMS_FILE_SERVER_PORT_NUMBER = 9090;
	
		/* Networking - error */
		public static final int CONNECT_ERROR = -1;							// ���� ���� ����
		public static final int UNKNOWN_HOST_ERROR = -3;					// ȣ��Ʈ �ּ� ��ȸ ����
		
	/* Database */
	public static final String DASEBASE_DRIVER = "org.gjt.mm.mysql.Driver";	// �⺻ �����ͺ��̽� ����̹� ��Ű��
	public static final String DEFAULT_USE_DATABASE = "db2";				// �⺻ ��� �����ͺ��̽�
	public static final String DEFAULT_DATABASE_HOST_ID = "root";			// �⺻ �����ͺ��̽� ���� ���̵�
	public static final String DEFAULT_DATABASE_HOST_PASSWORD = "1234";		// �⺻ �����ͺ��̽� ���� ��й�ȣ
	
	/* User Data */
	public static final String DEFALUE_USER_DATA_DIRECTORY = "C:\\\\DIMS\\\\Userdata\\\\";	// �������̹���
	public static final String DEFALUE_MOVIE_DATA_DIRECTORY = "C:\\\\DIMS\\\\Movie\\\\";	// ����
}
