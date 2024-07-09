import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class BookRent extends JPanel{
	DefaultTableModel model=null;
	JTable table=null;
	
	String query=null;
	
	Connection con=null;
	Statement stmt=null;
	ResultSet rs = null;  // select결과를 fetch하는 객체
	
	SimpleDateFormat df=new SimpleDateFormat("yyyy/MM/dd");
	
	public BookRent() {
		query="select student.id, student.name, book.title, rentbook.rdate"
				+ " from student,rentbook,book"
				+ " where student.id=rentbook.id"
				+ " and rentbook.bid=book.bid order by student.id";
		
		// Layout default: FlowLayout
		this.setLayout(null); // layout사용안함. 컴포넌트의 위치.크기 직접 설정
		
		JLabel lblDepartment=new JLabel("학과");
		lblDepartment.setBounds(10, 10, 30, 20); // "학과"lable의 위치와 크기 지정
		this.add(lblDepartment);
		
		//ComboBox만들기
		String[] dept={"전체","국문학과","영문학과","컴퓨터공학"}; // 목록
		JComboBox cbDept=new JComboBox(dept);
		cbDept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//기본쿼리
				query ="select student.id,student.name,book.title,rentbook.rdate"
						+ " from student,book,rentbook"
						+ " where student.id = rentbook.id"
						+ " and book.bid = rentbook.bid";
				JComboBox cb=(JComboBox)e.getSource();
				
				// 동적쿼리작성, 동적쿼리: 실행시에 쿼리가 바뀌는 것
				if(cb.getSelectedIndex()==0) { //전체
					query+=" order by student.id";
				}else if(cb.getSelectedIndex()==1) { //국문학과
					query+=" and student.dept='국문학과' order by student.id";
				}else if(cb.getSelectedIndex()==2) { //영문학과
					query+=" and student.dept='영문학과' order by student.id";
				}else if(cb.getSelectedIndex()==3) { //컴퓨터공학
					query+=" and student.dept='컴퓨터공학' order by student.id";
				}	
				System.out.println(query);
				
				list(); //목록출력
				
			}});
		
		cbDept.setBounds(45, 10, 100, 20);
		this.add(cbDept);
		
		String colName[]={"학번","이름","도서명","대출일"};
		model=new DefaultTableModel(colName,0);
		table=new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(760,450));
//		this.add(table);
		JScrollPane sp=new JScrollPane(table);
		sp.setBounds(10, 40, 760, 450);
		this.add(sp);
		
		//전체
		list();		
		
		this.setSize(780, 530);
		this.setVisible(true);
	}
	
	//select 실행 , list 메소드
	public void list() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");//oracle driver 로드
			// oracle xe연결. enterprise는 xe대신 orcl
			con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
			// statement객체 생성.
			stmt=con.createStatement();			
			rs=stmt.executeQuery(query);
			//목록 초기화
			model.setRowCount(0); // medel의 행의 수를 0으로 변경
			
			// fetch. 한행씩 읽어오기
			while(rs.next()) {
				String[] row=new String[4];
				row[0]=rs.getString("id");
				row[1]=rs.getString("name");
				row[2]=rs.getString("title");
				Date date=rs.getDate("rdate");
				row[3]=df.format(date);
				model.addRow(row);
			}
		}catch(Exception e1) {
			e1.printStackTrace();
		}finally {
			try {
				if(rs!=null) {rs.close();}  // rs==null: 레퍼런스, 인스턴스가 없다
				if(stmt!=null) {stmt.close();}
				if(con!=null) {con.close();}
			}catch(Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	

	public static void main(String[] args) {
		new BookRent();
	}

}
