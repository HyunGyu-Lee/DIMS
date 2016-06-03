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
	    
	    public static final String STUDENT_MAIN_FXML = "./ui/StudentMain.fxml";
		public static final String STUDENT_REAUTH_DIALOG_FXML = "../ui/ReAuthDialog.fxml";
	    
		
		/* UI ������ â �̸� ��� */
		public static final String LOGIN_WINDOW_TITLE = "�� �� ��";					
		public static final String PASSWORD_FIND_TITLE = "�� �� �� ȣ ã ��";			
		public static final String ADMIN_MAIN_TITLE = "�� �� �� �� �� �� �� ��";
		public static final String REGISTER_TITLE = "ȸ �� �� ��";  // ������ ���� ������ ȭ��
		public static final String CHECK_MESSAGE_TITLE = "�� �� �� Ȯ ��";
		public static final String ALERT_DIALOG_TITLE = "�� ��";
		public static final String SCHEDULE_CREATE_DIALOG_TITLE = "�� ����";
		public static final String STUDENT_TITLE = "�л� �Ż� ���� ������";
	    public static final String WEABAK_TITLE = "�ܹ� ���� ���� ������";
	    public static final String PLUS_MINUS_ASSIGN_TITLE = "����� �ο� ���� ������";
		
		public static final String STUDENT_MAIN_TITLE = "�� �� �� �� �� �� ��";
		public static final String STUDENT_REAUTH_DIALOG_TITLE = "������ ��û";		
		
	/* Network ���� */
	public static final String SERVER_ID_ADDRESS = "localhost";				// ���� IP �ּ�
	
		/* Networking - error */
		public static final int CONNECT_ERROR = -1;							// ���� ���� ����
		public static final int UNKNOWN_HOST_ERROR = -3;					// ȣ��Ʈ �ּ� ��ȸ ����
		
	/* Database */
	public static final String DASEBASE_DRIVER = "org.gjt.mm.mysql.Driver";	// �⺻ �����ͺ��̽� ����̹� ��Ű��
	public static final String DEFAULT_USE_DATABASE = "db2";				// �⺻ ��� �����ͺ��̽�
	public static final String DEFAULT_DATABASE_HOST_ID = "root";			// �⺻ �����ͺ��̽� ���� ���̵�
	public static final String DEFAULT_DATABASE_HOST_PASSWORD = "1234";		// �⺻ �����ͺ��̽� ���� ��й�ȣ
	
	/* User Data */
	public static final String DEFALUE_USER_DATA_DIRECTORY = "C:\\\\DIMS\\\\Userdata\\\\";
}
