package com.hst.dims.tools;

public class NetworkProtocols {
		
	public static final String LOGIN_REQUEST = "logreq";
	public static final String LOGIN_ACCEPT = "logaccept";
	public static final String LOGIN_DENY = "logdeny";
	
	public static final String ID_DUP_CHECK_REQUEST = "iddupcheckreq";
	public static final String ID_DUP_RESPOND_OK = "dupok";
	public static final String ID_DUP_RESPOND_DENY = "dupdeny";
	
	public static final String EXIT_REQUEST = "exitreq";
	public static final String EXIT_RESPOND = "exitres";
	
	public static final String INVALID_REQUEST_ERROR = "invalidreqerr";
	
	public static final String WINDOW_INIT_PROPERTY = "wininitproperty";
	
	public static final String RECIEVE_READY = "isready?";
	public static final String RECIEVE_READY_OK = "readyok";
	
	public static final String PLZ_REQUEST = "gogo";
	
	public static final String VIDIO_REQUEST = "vreq";
	public static final String VIDIO_RESPOND = "vres";
	
	// �޼����� ����
	public static final String MESSAGE_SHOW_MESSAGE_TAP_REQUEST = "msmtreq";
	public static final String MESSAGE_SHOW_MESSAGE_TAP_RESPOND = "msmtres";
	
	// ���� �޼����� ��û
	public static final String MESSAGE_RECIEVE_LIST_REQUEST = "mrlreq";
	public static final String MESSAGE_RECIEVE_LIST_RESPOND = "mrlres";
	
	// ���� �޼����� ��û
	public static final String MESSAGE_SEND_LIST_REQUEST = "mslreq";
	public static final String MESSAGE_SEND_LIST_RESPOND = "mslres";
	
	// ����� ��� ��û
	public static final String MESSAGE_USER_LIST_REQUEST = "mulreq";
	public static final String MESSAGE_USER_LIST_RESPOND = "mulres";
	
	// �޼��� ������ ��û
	public static final String MESSAGE_SEND_REQUEST = "msreq";
	public static final String MESSAGE_SEND_RESPOND = "msacc";
	
	// �޼����� ���� Ŭ����
	public static final String MESSAGE_CONTENT_REQUEST = "mcreq";
	public static final String MESSAGE_CONTENT_RESPOND = "mcres";
	
	// �޼��� �˾� â ����
	public static final String SHOW_MESSAGE_DIALOG = "smdlg";
	
	// �Խ��� �� Ŭ����
	public static final String BOARD_MAIN_REQUEST = "bmreq";
	// '��������'ī�װ��� �Խñ۵��� Ŭ���̾�Ʈ�� ����
	public static final String BOARD_MAIN_RESPOND = "bmres";
		
		// �Խñ� �ۼ�ȭ�鿡�� ���ۼ� Ŭ����
		public static final String ENROLL_BOARD_REQUEST = "ebreq";
		// �Խñ��� ��� ����ϰ� Ŭ���̾�Ʈ���� �˸�
		public static final String ENROLL_BOARD_RESPOND = "ebrespond";
		// �Խñ� ��� ���� ��
		public static final String ENROLL_BOARD_ERROR = "eberr";
		
		// �� ī�װ� Ŭ����
		public static final String BOARD_LIST_REQUEST = "blistreq";
		// �� ī�װ��� �ִ� �Խñ۸� Ŭ���̾�Ʈ�� ����
		public static final String BOARD_LIST_RESPOND = "blistres";
	
		// �Խñ� ���� Ŭ����
		public static final String BOARD_CONTENT_REQUEST = "bcoreq";
		// �ش� �Խñ��� ������ Ŭ���̾�Ʈ�� ����
		public static final String BOARD_CONTENT_RESPOND = "bcores";
		
		// �Խñ� �˻� ��
		public static final String BOARD_SEARCH_REQUEST ="bsreq";
		// �˻���� ����
		public static final String BOARD_SEARCH_RESPOND ="bsres";
		// �˻���� ������
		public static final String BOARD_NO_SEARCH_RESULT = "nsres";

		// �Ż�������ȸ �� Ŭ�� ��
		public static final String SHOW_USER_INFO_TAB_REQUEST = "suitreq";
		public static final String SHOW_USER_INFO_TAB_RESPOND = "suitres";
		
		//�������� �� Ŭ����
		public static final String SHOW_SCHEDULE_MANAGER_TAB_REQUEST = "ssmtreq";
		public static final String SHOW_SCHEDULE_MANAGER_TAB_RESPOND = "ssmtres";
		
		//���� ���� ��û ��
		public static final String MODIFY_SCHEDULE_REQUEST = "msreq1";
		
		//���� �߰� ��û ��
		public static final String ADD_SCHEDULE_REQUEST = "asreq";
		
		//���� ���� ��û ��
		public static final String DELETE_SCHEDULE_REQUEST = "dsreq";
		
		//��� �˻� ��û ��
		public static final String SCHEDULE_PROFESSIONAL_SEARCH_REQUEST = "spsreq";
		
		//���� �޷¸��� ��ȸ��
		public static final String MONTHLY_SCHEDULE_VIEW_REQUEST = "msvreq";
		public static final String MONTHLY_SCHEDULE_VIEW_RESPOND = "msvres";
		
		//�Ż� ���� ��ȸ �� Ŭ����
	    public static final String SHOW_USER_INFO_TAP_REQUEST ="suitreq";
	    public static final String SHOW_USER_INFO_TAP_RESPOND ="suitres";
		
		//�Ż����� ��ȸ ���� ����Ŭ����
	    public static final String USER_CONTENT_REQUEST = "uccreq";
	    public static final String USER_CONTENT_RESPOND = "ucres"; 
	      
	    //�ܹ� ��ȸ ���� �� Ŭ����
	    public static final String WEABAK_INFO_TAP_REQUEST = "witreq";
	    public static final String WEABAK_INFO_TAP_RESPOND = "witres";
	      
	    //����� �ο� ���� �� Ŭ����
	    public static final String PLUS_MINUS_TAP_REQUEST = "pmtreq";
	    public static final String PLUS_MINUS_TAP_RESPOND = "pmtres";
	      
	    //����� ��ȸ ���� �� Ŭ����
	    public static final String PLUS_MINUS_TAP_INFO_REQUEST = "pmtireq";
	    public static final String PLUS_MINUS_TAP_INFO_RESPOND = "pmtires";
	     
	    //�ܹ� ��ȸ ���� ���� Ŭ����
	    public static final String WEABAK_CONTENT_REQUEST = "wcreq";
	    public static final String WEQBAK_CONTENT_RESPOND = "wcres";
		
	    //�ܹ� ���� 
	    public static final String WEABAK_PROCESS_REQUEST = "wbpreq";
	    public static final String WEABAK_PROCESS_RESPOND = "wbpres";
	    
	    //�л� ���� ����� ��ȸ ���� Ŭ����
	    public static final String PLUS_MINUS_ASSIGN_REQUEST = "pmcreq";	    
	    public static final String PLUS_MINUS_ASSGIN_RESPOND = "pmcreS";	
	    
	    public static final String PLUS_MINUS_OVER_REQUEST = "pmoreq";
	    public static final String PLUS_MINUS_OVER_RESPOND = "pmores";
	    
	    public static final String MESSAGE_RICIEVER_SELECT_DELETE_REQUEST = "mrsdreq";
	    public static final String MESSAGE_RICIEVER_SELECT_DELETE_RESPOND = "mrsdres";
	    
	    public static final String MESSAGE_RICIEVER_ALL_DELETE_REQUEST = "mradreq";
	    public static final String MESSAGE_RICIEVER_ALL_DELETE_RESPOND = "mradreq";
	    
	    // �Ż����� �а� �޺��ڽ� ���ý�
	    public static final String STUDENT_CLASS_SELECT_COMBOBOX_REQUEST = "sascreq";
	    public static final String STUDENT_CLASS_SELECT_COMBOBOX_RESPOND = "sascres";
	    
	    // �Ż����� �� �� �޺��ڽ� ���ý�
	    public static final String STUDENT_LEVEL_SELECT_COMBOBOX_REQUEST = "slscreq";
	    
	    // ������ �Խñ� ����
	    public static final String ADMIN_BOARD_DELETE_REQUEST = "abdreq";
	    public static final String ADMIN_BOARD_DELETE_RESPOND = "abdres";
	    
	    // ������ ī�װ� ���� ���ʿ�û
	    public static final String ADMIN_CATEGORY_DELETE_REQUEST = "acdreq";
	    public static final String ADMIN_CATEGORY_DELETE_RESPOND = "acdres";	    
	    
	    // ������ ī�װ� ������ ī�װ� Ʃ�ü� ��û
	    public static final String ADMIN_DELETE_CATEGORY_COUNT_REQUEST = "adccreq";
	    public static final String ADMIN_DELETE_CATEGORY_COUNT_RESPOND = "adccres";
	    
	    // ������ ī�װ� ���� ���� ����
	    public static final String ADMIN_DELETE_FINAL_REQUEST = "adfreq";
	    public static final String ADMIN_DELETE_FINAL_RESPOND = "adfres";
	    
	    /*-----------------------------------------------------------------*/	
		
		// �л�-�ܹ� ���� ����Ʈ ��û
		public static final String MY_OVERNIGHT_LIST_REQUEST = "myolreq";
		public static final String MY_OVERNIGHT_LIST_RESPOND = "myolres";		
		
		// �л�-�ܹ� ��û ��û
		public static final String ENROLL_OVERNIGHT_REQUEST = "eoreq";
		public static final String ENROLL_OVERNIGHT_RESPOND = "eores";
		
		// �л� - ���� �޼��� ����Ʈ ��û
		public static final String STUDENT_RECIEVE_MESSAGE_REQUEST = "srmreq";
		public static final String STUDENT_RECIEVE_MESSAGE_RESPOND = "srmres";
		
		// �л� - ���� �޼��� ����Ʈ ��û
		public static final String STUDENT_SEND_MESSAGE_REQUEST = "ssmreq";
		public static final String STUDENT_SEND_MESSAGE_RESPOND = "ssmres";
		
		public static final String ADMIN_ADD_TAP_REQUEST = "aatreq";
		public static final String ADMIN_ADD_TAP_RESPOND = "aatres";		
		
		// �л� - �Ż����� ��ȸ ���� ��û
		public static final String STUDENT_USER_INFO_REQUEST = "suireq";
		public static final String STUDENT_USER_INFO_RESPOND = "suires";
		
		// �л� - �̹��� ���ε� ��û
		public static final String STUDENT_UPLOAD_PROFILE_IMAGE_REQUEST = "supireq";
		public static final String STUDENT_UPLOAD_PROFILE_IMAGE_RESPOND = "supires";
		
		// �л� - �Ż����� ���� ��û
		public static final String STUDENT_MODIFY_USER_INFO_REQUEST = "smuireq";
		public static final String STUDENT_MODIFY_USER_INFO_RESPOND = "smuires";		
		
		// �л� - �Ż����� ��й�ȣ ������ ��û
		public static final String STUDENT_REAUTH_REQUEST = "srreq";
		public static final String STUDENT_REAUTH_RESPOND = "srres";

		// �л� - �Ż����� - ��й�ȣ �缳�� ��û
		public static final String STUDENT_PASSWORD_SETUP_REQUEST = "spsreq2";
		public static final String STUDENT_PASSWORD_SETUP_RESPOND = "spsres2";
		
		// �л� - ������ ��� ��û 
		public static final String STUDENT_MEDIA_LIST_REQUEST = "smlreq";
		public static final String STUDENT_MEDIA_LIST_RESPOND = "smlres";
		
		// �л� - ������ ������ ��û
		public static final String STUDENT_MEDIA_CONTENT_REQUEST = "smcreq";
		public static final String STUDENT_MEDIA_CONTENT_RESPOND = "smcres";
		
		// �л� - ������ ���� ���� �˸�
		public static final String STUDENT_SEND_MEDIA_NOTIFICATION = "ssmn";
		
		public static final String LOGOUT_REQUEST = "logout";
		
		public static final String MESSAGE_REPLY_REQUEST = "mrreq";
		public static final String MESSAGE_REPLY_RESPOND = "mrres";
		
		// �л� - ī�װ� ����Ʈ ��û
		public static final String STUDENT_CATEGORY_LIST_REQUEST = "sclreq";
		public static final String STUDENT_CATEGORY_LIST_RESPOND = "sclres";
		
		// �л� - �Խ��� �˻� ��û
		public static final String STUDENT_BOARD_SEARCH_REQUEST = "sbsreq";
		public static final String STUDENT_BOARD_SEARCH_RESPOND = "sbsres";		
		
		// ������ - ���⼭�� ���������� ��û
		public static final String ADMIN_SUBMIN_MAIN_REQUEST = "asmreq";
		public static final String ADMIN_SUBMIN_MAIN_RESPOND = "asmres";
		
		// ������ - ���⼭�� ��� ��û
		public static final String ADMIN_SUBMIT_ENROLL_REQUEST = "asereq";
		public static final String ADMIN_SUBMIT_ENROLL_RESPOND = "aseres";
		
		// ������ - ���⼭�� ���� �� Ȯ�� ��û
		public static final String ADMIN_SUBMIT_DISPOSE_ASK_REQEUST = "asdareq";
		public static final String ADMIN_SUBMIT_DISPOSE_ASK_RESPOND = "asdares";

		// ������ - ���⼭�� ���� ��û
		public static final String ADMIN_SUBMIT_DISPOSE_REQEUST = "asdreq";
		public static final String ADMIN_SUBMIT_DISPOSE_RESPOND = "asdres";
		
		// �л� - ���� ���� ��û
		public static final String STUDENT_SUBMIT_REQUEST = "ssreq";
		public static final String STUDENT_SUBMIT_RESPOND = "ssres";		
		
		// ������ �������� ��û
		public static final String ADMIN_LOCAL_SAVE_REQUEST = "alsreq";
		public static final String ADMIN_LOCAL_SAVE_RESPOND = "alsres";	
		
		// ������ - ���������غ� �˸�
		public static final String ADMIN_LOCAL_SAVE_NOTIFICATION = "alsnoti";
		
		// ������ - ���� �߼� ��û
		public static final String EMAIL_SEND_REQUEST = "esreq";
		public static final String EMAIL_SEND_RESPOND = "esres";
		
		// ���� - ����÷�ΰ��� �ʰ�
		public static final String EMAIL_MAX_COUNT_EXCEED = "emcexc";
		// ���� - ����÷�ο뷮 �ʰ�
		public static final String EMAIL_FILE_SIZE_LIMIT = "efsl";
		
		// ��й�ȣã�� - �������
		public static final String PASSWORD_FIND_QUESTION_LIST = "pfql";
		
		// ��й�ȣã�� - ��������
		public static final String PASSWORD_FIND_IDENTIFY_REQUEST = "pfireq";
		public static final String PASSWORD_FIND_IDENTIFY_RESPOND = "pfires";		
		
		// ��й�ȣã�� - �� ��й�ȣ ����
		public static final String PASSWORD_FIND_MODIFY_REQUEST = "pfmreq";
		public static final String PASSWORD_FIND_MODIFY_RESPOND = "pfmres";
		
		
		public static final String MESSAGE_SEND_ALL_DELETE_RESPOND = "msadres";
		public static final String MESSAGE_SEND_SELECT_DELETE_RESPOND = "mssdres";
		
		public static final String STUDENT_SORT_OVERLAP_REQUEST = "ssoreq";
		public static final String STUDENT_SORT_OVERLAP_RESPOND = "ssores";
		
		public static final String MEMBER_JOIN_REQUEST = "mjreq";
		public static final String MEMBER_JOIN_RESPOND = "mjres";
		
		public static final String ADMIN_VIDEO_UPLOAD_REQUEST = "avureq";
		public static final String VIDEO_DATA_SEND_NOTIFICATION = "vdsnoti";
		
}