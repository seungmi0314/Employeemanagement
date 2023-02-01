package edu.kh.emp.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.kh.emp.model.vo.Employee;
import edu.kh.emp.view.EmployeeView;

//DAO(Data Access Object, 데이터 접근 객체)
//-> 데이터베이스에 접근(연결)하는 객체
//--> JDBC 코드 작성

/**
 * @author user1
 *
 */
/**
 * @author user1
 *
 */
/**
 * @author user1
 *
 */
public class EmployeeDAO {
	
	// JDBC 객체 참조 변수 필드 선언 (class 내부에 공통 사용) -> 필드에다 선언해서 -> Heap 영역에 생김
	// 메소드 안에다 선언한건? -> Stack영역에 생성
	
	private Connection conn;  // null값으로 초기화 안해줌 왜? 초기값으로 conn이 null을 가지고 있음 왜? Heap영역에 생성돼서
	// Heap 영역에 생성된건 비어있을 수 없다   초기화 안해주면 JVM이 지정한 기본값으로 초기화 해줌
	private Statement stmt;
	private ResultSet rs = null; // -> 참조형의 초기값은 null
								// 별도 초기화 안해도 된다
	
	private PreparedStatement pstmt;
	// Statement의 자식으로 향상된 기능 제공
	// -> ? 기호 (placeholder / 위치홀더)를 이용해서
	// SQL에 작성되어지는 리터럴을 동적으로 제어함
	
	// SQL ? 기호에 추가되는 값은
	// 숫자인 경우 ''없이 대입
	// 문자열인 경우 '' 가 자동으로 추가되어 대입
	
	
	/* public void method() {
		Connection conn2; // 지역변수(Stack 영역에 만들어짐, 변수가 비어있을 수 있음)
	} */
	
	
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String url = "jdbc:oracle:thin:@localhost:1521:XE";
	private String user = "kh";
	private String pw = "kh1234";
	
	
	
	
	
	
	/** 전체 사원 정보 조회 DAO
	 * @return empList
	 */
	public List<Employee> selectAll() {
		// 1. 결과 저장용 변수 선언
		List<Employee> empList = new ArrayList<>();
		
		try {
			// 2. JDBC 참조 변수에 객체 대입
			// -> conn, stmt, rs에 객체 대입
			Class.forName(driver); // 오라클 jdbc 드라이버 객체 메모리 로드
			
			conn = DriverManager.getConnection(url, user, pw);
			// 오라클 jdbc 드라이버 객체를 이용하여 DB 접속 방법 생성
			
			String sql = "SELECT EMP_ID, EMP_NAME, EMP_NO, EMAIL, PHONE,\r\n"
					+ "NVL(DEPT_TITLE, '부서없음') DEPT_TITLE,\r\n"
					+ "JOB_NAME, SALARY\r\n"
					+ "FROM EMPLOYEE\r\n"
					+ "LEFT JOIN DEPARTMENT ON(DEPT_ID = DEPT_CODE)\r\n"
					+ "JOIN JOB USING(JOB_CODE)";
			
			// Statement 객체 생성
			stmt = conn.createStatement();
			
			// SQL을 수행 후 결과(ResultSet) 반환 받음
			rs = stmt.executeQuery(sql);
			
			// 3. 조회 결과를 얻어와 한 행씩 접근하여
			// Employee 객체 생성 후 컬럼값 옮겨 닮기
			// -> List 추가
			while(rs.next()) {
				
				int empId = rs.getInt("EMP_ID");
				// EMP_ID 컬럼은 문자열 컬럼이지만
				// 저장 된 값들이 모두 숫자혙태
				// -> DB에서 자동으로 형변환을 진행해서 얻어오게됨
				
				String empName = rs.getString("EMP_NAME");
				String empNo = rs.getString("EMP_NO");
				String email = rs.getString("EMAIL");
				String phone = rs.getString("PHONE");
				String departmentTitle = rs.getString("DEPT_TITLE");
				String jobName = rs.getString("JOB_NAME");
				int salary = rs.getInt("SALARY");
				
				// Employee 객체화 해서 emp 안에 넣어줌
				Employee emp = new Employee(empId, empName, empNo, email, phone, departmentTitle
						, jobName, salary);
				
				empList.add(emp); // List 담기
				
				
			} // while 종료
			// 전체사원 조회 dao는 여기서 끝
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 4. JDBC 객체 자원 반환
		} try {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
			if(conn != null) conn.close();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
	
	// 결과 반환
		return empList;
		
	}
		
	
	/** 주민등록번호가 일치하는 사원 정보 조회 DAO
	 * @param empNo
	 * @return empNo
	 */
	public Employee selectEmpNo(String empNo) {
		
		// 결과 저장용 변수 선언
		Employee emp = null;
		
		try {
			// Connection 생성
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			
			// SQL 작성
			String sql = "SELECT EMP_ID, EMP_NAME, EMP_NO, EMAIL, PHONE,\r\n"
					+ "NVL(DEPT_TITLE, '부서없음') DEPT_TITLE,\r\n"
					+ "JOB_NAME, SALARY\r\n"
					+ "FROM EMPLOYEE\r\n"
					+ "LEFT JOIN DEPARTMENT ON(DEPT_ID = DEPT_CODE)\r\n"
					+ "JOIN JOB USING(JOB_CODE)\r\n"
					+ "WHERE EMP_NO = ?";
									// placeholder
			
			// Statement 객체 사용 시 순서
			// SQL 작성 -> Statement 생성 -> SQL 수행 후 결과 반환
			
			// PreparedStatement 객체 사용 시 순서
			// SQL 작성 ( 여기까진 똑같음)
			// -> PreparedStatement 객체 생성 ( ? 가 포함된 SQL을 매개변수로 사용)
			// -> ? 에 알맞은 값 대입
			// -> SQL 수행 후 결과 반환
			
			// PreparedStatement 객체 생성
			// 객체 만들어질 때 SQL문은 이미 타고 있음 
			
			pstmt = conn.prepareStatement(sql);
			
			// 위에 대로 보내면 db에 ?도 같이 보내겠단 소리 그래서 ?에 값을 넣어줘야 함
			// ?에 알맞은 값 대입
			pstmt.setString(1, empNo);
						// ?는 여러개 작성 가능한데 그 중 첫번째 ? 에 넣을거라서 1이라고 적은거
			
			// SQL 수행 후 결과 반환
			// 이미 sql 승객이 타고 있기 때문에 더 태울 필요 없이 이대로 db에 보내면 됨
			rs = pstmt.executeQuery();
			// PreparedStatment는 
			// 객체 생성 시 이미 SQL이 담겨져 있는 상태이므로
			// SQL 수행(executeQuery()) 시 매개변수로 전달 할 필요가 없다!
			
			// pstmt.executeQuery(sql);
			// -> ? 에 작성되어있던 값이 모두 사라져 수행 시 오류 발생
			
			
			// 값이 하나라 계속 돌 필요 없어서 if
			if(rs.next()) {
				int empId = rs.getInt("EMP_ID");
							
				String empName = rs.getString("EMP_NAME");
				//String empNo = rs.getString("EMP_NO"); // -> 파라미터와 같은 값이므로 불필요
				String email = rs.getString("EMAIL");
				String phone = rs.getString("PHONE");
				String departmentTitle = rs.getString("DEPT_TITLE");
				String jobName = rs.getString("JOB_NAME");
				int salary = rs.getInt("SALARY");
				
				emp = new Employee(empId, empName, empNo, email, phone, departmentTitle
						, jobName, salary);
			}
			// 있으면 가져오고 아니면 말아라
			
			
			
			
			
			
			
			
		} catch(Exception e){
			e.printStackTrace();
			
		} finally {
			
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return emp;
	}
	
	
	/** 
	 * 사원 정보 추가
	 * @param emp
	 * @return empNo
	 */
	public int insertEmployee(Employee emp) {
		
		// 결과 저장용 변수 선언
		int result = 0;
		
		try {
			
			// 커넥션 생성
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			
			
			// ** DML 수행할 예정 **
			// 트랜잭션 DML 구문이 임시 저장
			// --> 정상적인 DML인지를 판별해서 개발자가 직접 commit, rollback을 수행
			
			// Connection 객체 생성 시
			// AutoCommit이 활서오하 되어 있는 상태이기 때문에
			// 이를 해제하는 코드를 추가
			conn.setAutoCommit(false); // AutoCommit 비활성화
			
			// AutoCommit 비활성화 해도
			// conn.close(); 구문이 수행되면 자동으로 Commit이 수행 됨
			// -> close() 수행 전에 트랜잭션 제어 코드를 작성해야 한다
			
			// SQL 작성
			String sql = "INSERT INTO EMPLOYEE VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, NULL, DEFAULT)";
			// 퇴사 여부 컬럼의 DEFAULT == 'N'
			
			// PreparedStatement 객체 생성(매개변수에 SQL 추가)
			pstmt = conn.prepareStatement(sql);
			
			// ?(placeholder)에 알맞은 값 대입
			pstmt.setInt(1, emp.getEmpId());
			pstmt.setString(2, emp.getEmpName());
			pstmt.setString(3, emp.getEmpNo());
			pstmt.setString(4, emp.getEmail());
			pstmt.setString(5, emp.getPhone());
			pstmt.setString(6, emp.getDeptCode());
			pstmt.setString(7, emp.getJobCode());
			pstmt.setString(8, emp.getSalLevel());
			pstmt.setInt(9, emp.getSalary());
			pstmt.setDouble(10, emp.getBonus());
			pstmt.setInt(11, emp.getManagerId());
			
			
			// SQL 수행 후 결과 반환 받기
			// DML 은 executeUpdate 사용
			result = pstmt.executeUpdate();
			// executeQuery() : SELECT 수행 후 ResultSet 반환
			// executeUpdate() : DML(INSERT, UPDATE, DELETE) 수행 후 결과 행 개수 반환
			
			
			
			
			
			
			
			
			// *** 트랜잭션 제어 처리 ***
			// -> DML 성공 여부에 따라서 commit, rollback 제어
			
			if(result > 0) conn.commit(); // DML 성공 시 commit
			else		conn.rollback(); // DML 실패 시 rollback
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	

		
		
		
		
		return result;
	}
	


	/** 
	 * 사번이 일치하는 사원 정보 수정 DAO
// 뭔가 이상함 급여에서부터 오류남
 * 
	 * @param emp
	 * @return empNo
	 */

	public int updateEmployee(Employee emp) {
		int result = 0;
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			conn.setAutoCommit(false); // AutoCommit 비활성화
			
			String sql = "UPDATE EMPLOYEE SET"
					+ "EMAIL = ?, PHONE = ?, SALARY = ?"
					+ "WHERE EMP_ID = ?";
			
			// PreparedStatement 생성
			pstmt = conn.prepareStatement(sql);
			
			// ?에 알맞은 값 세팅
			pstmt.setString(1,  emp.getEmail());
			pstmt.setString(2,  emp.getPhone());
			pstmt.setInt(3,  emp.getSalary());
			pstmt.setInt(4,  emp.getEmpId());
			
			result = pstmt.executeUpdate();
			
			
			// 트랜잭션 제어 처리
			if(result == 0) conn.rollback();
			else			conn.commit();
			
		} catch(Exception e) {
			e.printStackTrace();
			
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	
	
	/** 
	 * 사번이 일치하는 사원 정보 삭제 DAO
	 * @param emp
	 * @return empNo
	 */
	public int deleteEmployee(int empId) {
		
		int result = 0; // 결과 저장용 변수
		
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pw);
			conn.setAutoCommit(false); // AutoCommit 비활성화
			
			String sql = "DELETE FROM EMPLOYEE WHERE EMP_ID = ?";
			
			pstmt = conn.prepareStatement(sql);
					
			pstmt.setInt(1, empId);
			
			result = pstmt.executeUpdate();
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		
		}
		return result;
		
	}
	
	/** 
	 * 입력 받은 부서와 일치하는 모든 사원 정보 조회
	 * @param departmentTilte
	 * @return 
	 */
		
		
		
			
		
	
	/** 
	 * 입력 받은 부서와 일치하는 모든 사원 정보 조회 DAO
	 * @param departmentTilte
	 * @return 
	 */
		public List<Employee> selectDeptEmp(String departmentTitle) {
			
			try {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, user, pw);
				conn.setAutoCommit(false);
				
				String sql = "SELECT EMP_ID, EMP_NAME, EMP_NO, EMAIL, PHONE,\r\n"
						+ "NVL(DEPT_TITLE, '부서없음') DEPT_TITLE,\r\n"
						+ "JOB_NAME, SALARY\r\n"
						+ "FROM EMPLOYEE\r\n"
						+ "LEFT JOIN DEPARTMENT ON(DEPT_ID = DEPT_CODE)\r\n"
						+ "JOIN JOB USING(JOB_CODE)\r\n"
						+ "WHERE DEPT_TITLE = ?";
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(pstmt != null) pstmt.close();
					if(conn != null) conn.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			return null;  // 일단 null 넣어놈 나머지 다 채우기
		}
		
	
	
	
	}