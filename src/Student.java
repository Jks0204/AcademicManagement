import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Student extends JPanel{
	JLabel lblID=null; // lbl: Label
	JTextField tfID=null; // tf: TextField
	JLabel lblName=null; 
	JTextField tfName=null;
	JLabel lblDept=null; 
	JTextField tfDept=null;
	JLabel lblAddress=null;
	JTextField tfAddress=null;
	
	JButton btnInsert = null;
	JButton btnSelect = null;
	JButton btnUpdate = null;
	JButton btnDelete = null;
	JButton btnSearch = null;
	
	DefaultTableModel model=null;
	JTable table=null;
	
	public Student() {
		// Layout default: FlowLayout
		
		this.setLayout(null);
		
		lblID= new JLabel("학번");
		add(lblID);
		lblID.setBounds(10,10,30,20);
		tfID= new JTextField(13); // 가로크기 지정
		add(tfID);
		tfID.setBounds(40,10,200,20);
		btnSearch= new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Connection conn=null;
				try {
					//oracle jdbc driver load
					Class.forName("oracle.jdbc.driver.OracleDriver");
					//Connection
					conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
					
					Statement stmt=conn.createStatement(); // statement객체 생성

					//select
					ResultSet rs=stmt.executeQuery("select * from student where id='"+tfID.getText()+"'");//select문 실행.
					// rs는 cursor역할. 한행씩 while문으로 fetch
					
					//목록 초기화
					model.setRowCount(0); // model의 행의 수를 0으로 설정
					
					while(rs.next()) {
						String[] row=new String[4];
						row[0]=rs.getString("id");
						row[1]=rs.getString("name");
						row[2]=rs.getString("dept");
						row[3]=rs.getString("address");
						model.addRow(row);
						
						tfID.setText(rs.getString("id"));
						tfName.setText(rs.getString("name"));
						tfDept.setText(rs.getString("dept"));
						tfAddress.setText(rs.getString("address"));
					}
						
					rs.close();
					stmt.close();
					conn.close();//연결해제
				}catch(Exception e1) {
					e1.printStackTrace();
				}
			}});
		add(btnSearch);
		btnSearch.setBounds(250,10,100,20);
		lblName= new JLabel("이름");
		add(lblName);
		lblName.setBounds(10,40,30,20);
		tfName= new JTextField(20);
		add(tfName);
		tfName.setBounds(40,40,200,20);
		lblDept= new JLabel("학과");
		add(lblDept);
		lblDept.setBounds(10,70,30,20);
		tfDept= new JTextField(20);
		add(tfDept);
		tfDept.setBounds(40,70,200,20);
		lblAddress= new JLabel("주소");
		add(lblAddress);
		lblAddress.setBounds(10,100,30,20);
		tfAddress= new JTextField(20);
		add(tfAddress);
		tfAddress.setBounds(40,100,200,20);
		
		String[] colName={"학번","이름","학과","주소"};
		model=new DefaultTableModel(colName,0); // model:데이터
		table=new JTable(model);// table과 model 바인딩(binding)
		table.setPreferredScrollableViewportSize(new Dimension (720,330)); // 가로, 세로크기
		JScrollPane tSP = new JScrollPane(table); // 스크롤바 구현
		this.add(tSP);
		tSP.setBounds(20,130,750,330);
		
		// 테이블을 클릭했을 때 해당 row 값들을 입력창에 출력
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				table=(JTable) e.getComponent(); // 컴포넌트를 JTabel로 변환
				model=(DefaultTableModel) table.getModel();
				String id=(String)model.getValueAt(table.getSelectedRow(), 0); // 선택한 행의 id구하기, 열 위치
				tfID.setText(id);
				String name=(String)model.getValueAt(table.getSelectedRow(), 1);
				tfName.setText(name);
				String dept=(String)model.getValueAt(table.getSelectedRow(), 2);
				tfDept.setText(dept);
				String address=(String)model.getValueAt(table.getSelectedRow(), 3);
				tfAddress.setText(address);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		btnInsert=new JButton("등록");
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Connection conn=null;
				try {
					//oracle jdbc driver load
					Class.forName("oracle.jdbc.driver.OracleDriver");
					//Connection
					conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
					
					Statement stmt=conn.createStatement(); // statement객체 생성
					
					//Application에서 sql실행시 기본적으로 auto commit.
					
					//insert
					stmt.executeUpdate("insert into student values('"+tfID.getText()+"','"+tfName.getText()+"','"+tfDept.getText()+"','"+tfAddress.getText()+"')"); 
					
					//select
					ResultSet rs=stmt.executeQuery("select * from student");//select문 실행.
					// rs는 cursor역할. 한행씩 while문으로 fetch
					
					//목록 초기화
					model.setRowCount(0);
					while(rs.next()) {
						String[] row=new String[4];
						row[0]=rs.getString("id");
						row[1]=rs.getString("name");
						row[2]=rs.getString("dept");
						row[3]=rs.getString("address");
						model.addRow(row);
					}
					
					rs.close();
					stmt.close();
					conn.close();//연결해제
				}catch(Exception e1) {
					e1.printStackTrace();
				}
				
			}});
		add(btnInsert);
		btnInsert.setBounds(270,470,100,40);
		
		btnSelect=new JButton("목록");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Connection conn=null;
				try {
					//oracle jdbc driver load
					Class.forName("oracle.jdbc.driver.OracleDriver");
					//Connection
					conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
					
					Statement stmt=conn.createStatement(); // statement객체 생성

					//select
					ResultSet rs=stmt.executeQuery("select * from student");//select문 실행.
					// rs는 cursor역할. 한행씩 while문으로 fetch
					
					//목록 초기화
					model.setRowCount(0);
					while(rs.next()) {
						String[] row=new String[4];
						row[0]=rs.getString("id");
						row[1]=rs.getString("name");
						row[2]=rs.getString("dept");
						row[3]=rs.getString("address");
						model.addRow(row);
					}
					
					rs.close();
					stmt.close();
					conn.close();//연결해제
				}catch(Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		add(btnSelect);
		btnSelect.setBounds(150,470,100,40);
		
		btnUpdate=new JButton("수정");
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Connection conn=null;
				try {
					//oracle jdbc driver load
					Class.forName("oracle.jdbc.driver.OracleDriver");
					//Connection
					conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
					
					Statement stmt=conn.createStatement(); // statement객체 생성
					
					//Application에서 sql실행시 기본적으로 auto commit.

					//update
					stmt.executeUpdate("update student set name='"+tfName.getText()+"', dept='"+tfDept.getText()+"' where id='"+tfID.getText()+"'");

					//select
					ResultSet rs=stmt.executeQuery("select * from student");//select문 실행.
					// rs는 cursor역할. 한행씩 while문으로 fetch
					
					//목록 초기화
					model.setRowCount(0);
					while(rs.next()) {
						String[] row=new String[4];
						row[0]=rs.getString("id");
						row[1]=rs.getString("name");
						row[2]=rs.getString("dept");
						row[3]=rs.getString("address");
						model.addRow(row);
					}
					
					rs.close();
					stmt.close();
					conn.close();//연결해제
				}catch(Exception e1) {
					e1.printStackTrace();
				}
			}});
		add(btnUpdate);
		btnUpdate.setBounds(390,470,100,40);
		
		btnDelete=new JButton("삭제");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(null, "정말로 삭제 하시겠습니까?","삭제",JOptionPane.YES_NO_OPTION);
				// 결과값이 정수로 나옴, yes:0, no:1

				if (result==JOptionPane.YES_OPTION) {
					Connection conn=null;
					try {
						//oracle jdbc driver load
						Class.forName("oracle.jdbc.driver.OracleDriver");
						//Connection
						conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
						System.out.println("연결완료");
						
						Statement stmt=conn.createStatement(); // statement객체 생성
						
						//Application에서 sql실행시 기본적으로 auto commit.
						
						//delete
						stmt.executeUpdate("delete from student where id='"+tfID.getText()+"'");
						
						//select
						ResultSet rs=stmt.executeQuery("select * from student");//select문 실행.
						// rs는 cursor역할. 한행씩 while문으로 fetch
						
						//목록 초기화
						model.setRowCount(0);
						while(rs.next()) {
							String[] row=new String[4];
							row[0]=rs.getString("id");
							row[1]=rs.getString("name");
							row[2]=rs.getString("dept");
							row[3]=rs.getString("address");
							model.addRow(row);
						}
						
						//입력항목 초기화
						tfID.setText(null);
						tfName.setText(null);
						tfDept.setText(null);
						tfAddress.setText(null);
						
						rs.close();
						stmt.close();
						conn.close();//연결해제
					}catch(Exception e1) {
						e1.printStackTrace();
					}			
				}
			}});
		add(btnDelete);
		btnDelete.setBounds(510,470,100,40);
		
		this.setSize(790,540);
		this.setVisible(true);
	}

}
