package tools;

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
	
	// 메세지탭 오픈
	public static final String MESSAGE_SHOW_MESSAGE_TAP_REQUEST = "msmtreq";
	public static final String MESSAGE_SHOW_MESSAGE_TAP_RESPOND = "msmtres";
	
	// 받은 메세지함 요청
	public static final String MESSAGE_RECIEVE_LIST_REQUEST = "mrlreq";
	public static final String MESSAGE_RECIEVE_LIST_RESPOND = "mrlres";
	
	// 보낸 메세지함 요청
	public static final String MESSAGE_SEND_LIST_REQUEST = "mslreq";
	public static final String MESSAGE_SEND_LIST_RESPOND = "mslres";
	
	// 사용자 목록 요청
	public static final String MESSAGE_USER_LIST_REQUEST = "mulreq";
	public static final String MESSAGE_USER_LIST_RESPOND = "mulres";
	
	// 메세지 보내기 요청
	public static final String MESSAGE_SEND_REQUEST = "msreq";
	public static final String MESSAGE_SEND_RESPOND = "msacc";
	
	// 메세지함 더블 클릭시
	public static final String MESSAGE_CONTENT_REQUEST = "mcreq";
	public static final String MESSAGE_CONTENT_RESPOND = "mcres";
	
	// 메세지 팝업 창 생성
	public static final String SHOW_MESSAGE_DIALOG = "smdlg";
	
	// 게시판 탭 클릭시
	public static final String BOARD_MAIN_REQUEST = "bmreq";
	// '공지사항'카테고리의 게시글들을 클라이언트로 보냄
	public static final String BOARD_MAIN_RESPOND = "bmres";
		
		// 게시글 작성화면에서 글작성 클릭시
		public static final String ENROLL_BOARD_REQUEST = "ebreq";
		// 게시글을 디비에 등록하고 클라이언트에게 알림
		public static final String ENROLL_BOARD_RESPOND = "ebrespond";
		// 게시글 등록 못할 때
		public static final String ENROLL_BOARD_ERROR = "eberr";
		
		// 각 카테고리 클릭시
		public static final String BOARD_LIST_REQUEST = "blistreq";
		// 그 카테고리에 있는 게시글만 클라이언트로 보냄
		public static final String BOARD_LIST_RESPOND = "blistres";
	
		// 게시글 더블 클릭시
		public static final String BOARD_CONTENT_REQUEST = "bcoreq";
		// 해당 게시글의 본문을 클라이언트로 보냄
		public static final String BOARD_CONTENT_RESPOND = "bcores";
		
		// 게시글 검색 시
		public static final String BOARD_SEARCH_REQUEST ="bsreq";
		// 검색결과 응답
		public static final String BOARD_SEARCH_RESPOND ="bsres";
		// 검색결과 없을때
		public static final String BOARD_NO_SEARCH_RESULT = "nsres";

		// 신상정보조회 탭 클릭 시
		public static final String SHOW_USER_INFO_TAB_REQUEST = "suitreq";
		public static final String SHOW_USER_INFO_TAB_RESPOND = "suitres";
		
		//일정관리 탭 클릭시
		public static final String SHOW_SCHEDULE_MANAGER_TAB_REQUEST = "ssmtreq";
		public static final String SHOW_SCHEDULE_MANAGER_TAB_RESPOND = "ssmtres";
		
		//일정 수정 요청 시
		public static final String MODIFY_SCHEDULE_REQUEST = "msreq1";
		
		//일정 추가 요청 시
		public static final String ADD_SCHEDULE_REQUEST = "asreq";
		
		//일정 삭제 요청 시
		public static final String DELETE_SCHEDULE_REQUEST = "dsreq";
		
		//고급 검색 요청 시
		public static final String SCHEDULE_PROFESSIONAL_SEARCH_REQUEST = "spsreq";
		
		//일정 달력모드로 조회시
		public static final String MONTHLY_SCHEDULE_VIEW_REQUEST = "msvreq";
		public static final String MONTHLY_SCHEDULE_VIEW_RESPOND = "msvres";
		
		//신상 정보 조회 탭 클릭시
	    public static final String SHOW_USER_INFO_TAP_REQUEST ="suitreq";
	    public static final String SHOW_USER_INFO_TAP_RESPOND ="suitres";
		
		//신상정보 조회 내용 더블클릭시
	    public static final String USER_CONTENT_REQUEST = "uccreq";
	    public static final String USER_CONTENT_RESPOND = "ucres"; 
	      
	    //외박 조회 내용 탭 클릭시
	    public static final String WEABAK_INFO_TAP_REQUEST = "witreq";
	    public static final String WEABAK_INFO_TAP_RESPOND = "witres";
	      
	    //상벌점 부여 내용 탭 클릭시
	    public static final String PLUS_MINUS_TAP_REQUEST = "pmtreq";
	    public static final String PLUS_MINUS_TAP_RESPOND = "pmtres";
	      
	    //상벌점 조회 내용 탭 클릭시
	    public static final String PLUS_MINUS_TAP_INFO_REQUEST = "pmtireq";
	    public static final String PLUS_MINUS_TAP_INFO_RESPOND = "pmtires";
	     
	    //외박 조회 내용 더블 클릭시
	    public static final String WEABAK_CONTENT_REQUEST = "wcreq";
	    public static final String WEQBAK_CONTENT_RESPOND = "wcres";
		
	    //외박 승인 
	    public static final String WEABAK_PROCESS_REQUEST = "wbpreq";
	    public static final String WEABAK_PROCESS_RESPOND = "wbpres";
	    
	    //학생 개인 상벌점 조회 더블 클릭시
	    public static final String PLUS_MINUS_ASSIGN_REQUEST = "pmcreq";	    
	    public static final String PLUS_MINUS_ASSGIN_RESPOND = "pmcreS";	
	    
	    public static final String PLUS_MINUS_OVER_REQUEST = "pmoreq";
	    public static final String PLUS_MINUS_OVER_RESPOND = "pmores";
	    
	    public static final String MESSAGE_RICIEVER_SELECT_DELETE_REQUEST = "mrsdreq";
	    public static final String MESSAGE_RICIEVER_SELECT_DELETE_RESPOND = "mrsdres";
	    
	    public static final String MESSAGE_RICIEVER_ALL_DELETE_REQUEST = "mradreq";
	    public static final String MESSAGE_RICIEVER_ALL_DELETE_RESPOND = "mradreq";
	    
	    // 신상정보 학과 콤보박스 선택시
	    public static final String STUDENT_CLASS_SELECT_COMBOBOX_REQUEST = "sascreq";
	    public static final String STUDENT_CLASS_SELECT_COMBOBOX_RESPOND = "sascres";
	    
	    // 신상정보 층 별 콤보박스 선택시
	    public static final String STUDENT_LEVEL_SELECT_COMBOBOX_REQUEST = "slscreq";
	    
	    // 관리자 게시글 삭제
	    public static final String ADMIN_BOARD_DELETE_REQUEST = "abdreq";
	    public static final String ADMIN_BOARD_DELETE_RESPOND = "abdres";
	    
	    // 관리자 카테고리 삭제 최초요청
	    public static final String ADMIN_CATEGORY_DELETE_REQUEST = "acdreq";
	    public static final String ADMIN_CATEGORY_DELETE_RESPOND = "acdres";	    
	    
	    // 관리자 카테고리 삭제될 카테고리 튜플수 요청
	    public static final String ADMIN_DELETE_CATEGORY_COUNT_REQUEST = "adccreq";
	    public static final String ADMIN_DELETE_CATEGORY_COUNT_RESPOND = "adccres";
	    
	    // 관리자 카테고리 삭제 최종 결정
	    public static final String ADMIN_DELETE_FINAL_REQUEST = "adfreq";
	    public static final String ADMIN_DELETE_FINAL_RESPOND = "adfres";
	    
	    /*-----------------------------------------------------------------*/	
		
		// 학생-외박 내역 리스트 요청
		public static final String MY_OVERNIGHT_LIST_REQUEST = "myolreq";
		public static final String MY_OVERNIGHT_LIST_RESPOND = "myolres";		
		
		// 학생-외박 신청 요청
		public static final String ENROLL_OVERNIGHT_REQUEST = "eoreq";
		public static final String ENROLL_OVERNIGHT_RESPOND = "eores";
		
		// 학생 - 받은 메세지 리스트 요청
		public static final String STUDENT_RECIEVE_MESSAGE_REQUEST = "srmreq";
		public static final String STUDENT_RECIEVE_MESSAGE_RESPOND = "srmres";
		
		// 학생 - 보낸 메세지 리스트 요청
		public static final String STUDENT_SEND_MESSAGE_REQUEST = "ssmreq";
		public static final String STUDENT_SEND_MESSAGE_RESPOND = "ssmres";
		
		public static final String ADMIN_ADD_TAP_REQUEST = "aatreq";
		public static final String ADMIN_ADD_TAP_RESPOND = "aatres";		
		
		// 학생 - 신상정보 조회 정보 오청
		public static final String STUDENT_USER_INFO_REQUEST = "suireq";
		public static final String STUDENT_USER_INFO_RESPOND = "suires";
		
		// 학생 - 이미지 업로드 요청
		public static final String STUDENT_UPLOAD_PROFILE_IMAGE_REQUEST = "supireq";
		public static final String STUDENT_UPLOAD_PROFILE_IMAGE_RESPOND = "supires";
		
		// 학생 - 신상정보 수정 요청
		public static final String STUDENT_MODIFY_USER_INFO_REQUEST = "smuireq";
		public static final String STUDENT_MODIFY_USER_INFO_RESPOND = "smuires";		
		
		// 학생 - 신상정보 비밀번호 재인증 요청
		public static final String STUDENT_REAUTH_REQUEST = "srreq";
		public static final String STUDENT_REAUTH_RESPOND = "srres";

		// 학생 - 신상정보 - 비밀번호 재설정 요청
		public static final String STUDENT_PASSWORD_SETUP_REQUEST = "spsreq2";
		public static final String STUDENT_PASSWORD_SETUP_RESPOND = "spsres2";
		
		// 학생 - 동영상 목록 요청 
		public static final String STUDENT_MEDIA_LIST_REQUEST = "smlreq";
		public static final String STUDENT_MEDIA_LIST_RESPOND = "smlres";
		
		// 학생 - 동영상 데이터 요청
		public static final String STUDENT_MEDIA_CONTENT_REQUEST = "smcreq";
		public static final String STUDENT_MEDIA_CONTENT_RESPOND = "smcres";
		
		// 학생 - 동영상 전송 시작 알림
		public static final String STUDENT_SEND_MEDIA_NOTIFICATION = "ssmn";
		
		public static final String LOGOUT_REQUEST = "logout";
		
		public static final String MESSAGE_REPLY_REQUEST = "mrreq";
		public static final String MESSAGE_REPLY_RESPOND = "mrres";
		
		// 학생 - 카테고리 리스트 요청
		public static final String STUDENT_CATEGORY_LIST_REQUEST = "sclreq";
		public static final String STUDENT_CATEGORY_LIST_RESPOND = "sclres";
		
		// 학생 - 게시판 검색 요청
		public static final String STUDENT_BOARD_SEARCH_REQUEST = "sbsreq";
		public static final String STUDENT_BOARD_SEARCH_RESPOND = "sbsres";		
		
		// 관리자 - 제출서류 관리페이지 요청
		public static final String ADMIN_SUBMIN_MAIN_REQUEST = "asmreq";
		public static final String ADMIN_SUBMIN_MAIN_RESPOND = "asmres";
		
		// 관리자 - 제출서류 등록 요청
		public static final String ADMIN_SUBMIT_ENROLL_REQUEST = "asereq";
		public static final String ADMIN_SUBMIT_ENROLL_RESPOND = "aseres";
		
		// 관리자 - 제출서류 마감 전 확인 요청
		public static final String ADMIN_SUBMIT_DISPOSE_ASK_REQEUST = "asdareq";
		public static final String ADMIN_SUBMIT_DISPOSE_ASK_RESPOND = "asdares";

		// 관리자 - 제출서류 마감 요청
		public static final String ADMIN_SUBMIT_DISPOSE_REQEUST = "asdreq";
		public static final String ADMIN_SUBMIT_DISPOSE_RESPOND = "asdres";
		
		// 학생 - 서류 제출 요청
		public static final String STUDENT_SUBMIT_REQUEST = "ssreq";
		public static final String STUDENT_SUBMIT_RESPOND = "ssres";		
		
		// 관리자 로컬저장 요청
		public static final String ADMIN_LOCAL_SAVE_REQUEST = "alsreq";
		public static final String ADMIN_LOCAL_SAVE_RESPOND = "alsres";	
		
		// 관리자 - 로컬저장준비 알림
		public static final String ADMIN_LOCAL_SAVE_NOTIFICATION = "alsnoti";
		
		// 관리자 - 메일 발송 요청
		public static final String EMAIL_SEND_REQUEST = "esreq";
		public static final String EMAIL_SEND_RESPOND = "esres";
		
		// 메일 - 파일첨부갯수 초과
		public static final String EMAIL_MAX_COUNT_EXCEED = "emcexc";
		// 메일 - 파일첨부용량 초과
		public static final String EMAIL_FILE_SIZE_LIMIT = "efsl";
		
		// 비밀번호찾기 - 질문목록
		public static final String PASSWORD_FIND_QUESTION_LIST = "pfql";
		
		// 비밀번호찾기 - 본인인증
		public static final String PASSWORD_FIND_IDENTIFY_REQUEST = "pfireq";
		public static final String PASSWORD_FIND_IDENTIFY_RESPOND = "pfires";		
		
		// 비밀번호찾기 - 새 비밀번호 설정
		public static final String PASSWORD_FIND_MODIFY_REQUEST = "pfmreq";
		public static final String PASSWORD_FIND_MODIFY_RESPOND = "pfmres";		
}