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
		
		// 학생 - 메세지 보내기 새로운거
		
		
		
		
}