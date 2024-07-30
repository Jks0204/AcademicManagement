import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class BookRent extends JPanel{
	DefaultTableModel model=null;
	JTable table=null;
	
	String query=null;
	
	Connection con=null;
	Statement stmt=null;
	ResultSet rs = null;  // select결과를 fetch하는 객체
	
	JButton btnReset=null;
	JButton btnRent=null;
	JButton btnReturn=null;
	
	String clickedID=null;
	String clickedTitle=null;
	String clickedDate=null;
	
	public BookRent() {
		query="select student.id, student.name, book.title, rentbook.rdate "
				+ " from student,rentbook,book"
				+ " where student.id=rentbook.id"
				+ " and rentbook.bid=book.bid order by rentbook.rdate";
		
		// Layout default: FlowLayout
		this.setLayout(null); // layout사용안함. 컴포넌트의 위치.크기 직접 설정
		
		JLabel lblDepartment=new JLabel("학과");
		lblDepartment.setBounds(10, 10, 30, 20); // "학과"lable의 위치와 크기 지정
		this.add(lblDepartment);
		
		//ComboBox만들기
		String[] dept={"전체","국문학과","물리학과","컴퓨터공학과"}; // 목록
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
					query+=" and student.dept='국문학과' order by rentbook.rdate";
				}else if(cb.getSelectedIndex()==2) { //물리학과
					query+=" and student.dept='물리학과' order by rentbook.rdate";
				}else if(cb.getSelectedIndex()==3) { //컴퓨터공학과
					query+=" and student.dept='컴퓨터공학과' order by rentbook.rdate";
				}	
//				System.out.println(query);
				
				list(); //목록출력
				
			}});
		
		cbDept.setBounds(45, 10, 100, 20);
		this.add(cbDept);
		
		String colName[]={"학번","이름","도서명","대출기록"};
		model=new DefaultTableModel(colName,0);
		table=new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(760,350));
//		this.add(table);
		JScrollPane sp=new JScrollPane(table);
		sp.setBounds(10, 40, 760, 350);
		this.add(sp);
		
		//전체
		list();	
		
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				table=(JTable) e.getComponent(); // 컴포넌트를 JTabel로 변환
				model=(DefaultTableModel) table.getModel();
				clickedID=(String)model.getValueAt(table.getSelectedRow(), 0); // 선택한 행의 id구하기, 열 위치
				clickedTitle=(String)model.getValueAt(table.getSelectedRow(), 2);
				clickedDate=(String)model.getValueAt(table.getSelectedRow(), 3);
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
					
		JLabel lblRent = new JLabel("대출");
		lblRent.setBounds(50,400,120,20);
		this.add(lblRent);
//		ImageIcon imgRent = new ImageIcon("img/rent.png");
		ImageIcon imgRent = new ImageIcon(getClass().getClassLoader().getResource("rent.png"));
		btnRent=new JButton(imgRent);
		btnRent.setVerticalTextPosition(JButton.BOTTOM);
		btnRent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JDialog dialog=new RentReturn();
				dialog.setModal(true);
				dialog.setVisible(true);
			}
		});
		btnRent.setBounds(10,420,120,70);
		this.add(btnRent);
		
		
		JLabel lblReturn = new JLabel("반납");
		lblReturn.setBounds(180,400,120,20);
		this.add(lblReturn);
//		ImageIcon imgReturn = new ImageIcon("img/return.png");
		ImageIcon imgReturn = new ImageIcon(getClass().getClassLoader().getResource("return.png"));
		btnReturn=new JButton(imgReturn);
		btnReturn.setVerticalTextPosition(JButton.BOTTOM);
		btnReturn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//경고문 반납하시겠습니까 Y/N Y면 실행 N이면 취소
				int confirm = JOptionPane.showConfirmDialog(null, "반납하시겠습니까?","반납 확인",JOptionPane.YES_NO_OPTION);
				if(confirm==JOptionPane.YES_OPTION) {
					Connection conn=null;
					try {
						//oracle jdbc driver load
						Class.forName("oracle.jdbc.driver.OracleDriver");
						//Connection
						conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
						
						Statement stmt=conn.createStatement(); // statement객체 생성
						
						//Application에서 sql실행시 기본적으로 auto commit.

						//update
						//클릭한값 읽어오기
						stmt.executeUpdate("delete from rentbook where id= '"+clickedID+"' and rdate='"+clickedDate+"'");
						stmt.executeUpdate("update book set inventory=inventory+1 where title='"+clickedTitle+"'");

						//select
						ResultSet rs=stmt.executeQuery(query);//select문 실행.
						// rs는 cursor역할. 한행씩 while문으로 fetch
						
						//목록 초기화
						model.setRowCount(0);
						while(rs.next()) {
							String[] row=new String[4];
							row[0]=rs.getString("id");
							row[1]=rs.getString("name");
							row[2]=rs.getString("title");
							row[3]=rs.getString("rdate");
							model.addRow(row);
						}
						JOptionPane.showConfirmDialog(null, "ID: "+clickedID+", "+clickedTitle+" 반납되었습니다","반납 확인",JOptionPane.PLAIN_MESSAGE);
					}catch(Exception e1) {
						e1.printStackTrace();
					}finally {
						try {
							if(rs!=null) {rs.close();} 
							if(stmt!=null) {stmt.close();}
							if(con!=null) {con.close();}
						}catch(Exception e2) {
							e2.printStackTrace();
						}
					}
				} 
				
			}});
		btnReturn.setBounds(140,420,120,70);
		this.add(btnReturn);
		
		JLabel lblRefresh = new JLabel("새로고침");
		lblRefresh.setBounds(680,400,120,20);
		this.add(lblRefresh);
//		ImageIcon imgRefresh = new ImageIcon("img/refresh.png");
		ImageIcon imgRefresh = new ImageIcon(getClass().getClassLoader().getResource("refresh.png"));
		btnReset=new JButton(imgRefresh);
		btnReset.setVerticalTextPosition(JButton.BOTTOM);
		btnReset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				list();
			}
		});
		btnReset.setBounds(650,420,120,70);
		this.add(btnReset);
		
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
				row[3]=rs.getString("rdate");
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
	 
	// 대출 버튼이 있는 다이얼로그
	class RentReturn extends JDialog {
		JLabel lblID1=null;
		JTextField tfID1=null;
		JLabel lblTitle1=null;
		JTextField tfTitle1=null;
		JLabel lblInventory1=null;
		JTextField tfInventory1=null;
		
		DefaultTableModel model1=null;
		JTable table1=null;
		
		public RentReturn () {
			
			this.setTitle("대출반납");
			this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			this.setLayout(new FlowLayout());
			
			lblID1= new JLabel("학번");
			add(lblID1);
			tfID1=new JTextField(13);
			add(tfID1);
			lblTitle1=new JLabel("도서명");
			add(lblTitle1);
			tfTitle1=new JTextField(13);
			add(tfTitle1);
			
			String[] colName={"책 제목","재고량",};
			model1=new DefaultTableModel(colName,0); // model:데이터
			table1=new JTable(model1);// table과 model 바인딩(binding)
			table1.setPreferredScrollableViewportSize(new Dimension (530,300)); // 가로, 세로크기
			JScrollPane tSP = new JScrollPane(table1); // 스크롤바 구현
			this.add(tSP);
			
			table1.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					table1=(JTable) e.getComponent(); // 컴포넌트를 JTabel로 변환
					model1=(DefaultTableModel) table1.getModel();
					String title=(String)model1.getValueAt(table1.getSelectedRow(), 0);
					tfTitle1.setText(title);
				}
				@Override
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});

			//초기 목록	
			String query = "select title, inventory from book";
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");//oracle driver 로드
				// oracle xe연결. enterprise는 xe대신 orcl
				con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
				// statement객체 생성.
				stmt=con.createStatement();			
				rs=stmt.executeQuery(query);
				
				//목록 초기화
				model1.setRowCount(0); // model의 행의 수를 0으로 변경				
				// fetch. 한행씩 읽어오기
				while(rs.next()) {
					String[] row=new String[2];
					row[0]=rs.getString("title");
					row[1]=rs.getString("inventory");
					model1.addRow(row);
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

			// 대출 book: inventory-1(inventory가 0이면 안되고 경고메세지 뜨게), rentBook: 추가
			JButton btnRent=new JButton("대출");
			btnRent.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Connection conn=null;
					try {
						//oracle jdbc driver load
						Class.forName("oracle.jdbc.driver.OracleDriver");
						//Connection
						conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
						
						Statement stmt=conn.createStatement(); // statement객체 생성
						
						ResultSet rs=stmt.executeQuery("select inventory from book where title= '"+tfTitle1.getText()+"'" );
						String result="";
						while(rs.next()) {
							result=rs.getString("inventory");
						}
						
						if(Integer.parseInt(result)<=0) {
							JOptionPane.showMessageDialog(table1, "책의 재고가 없어 대출하실 수 없습니다.", "재고 부족",JOptionPane.WARNING_MESSAGE);
						} else {						
							// 대출 시간 년.월.일
							Date now=new Date();
							SimpleDateFormat f= new SimpleDateFormat("yyyy/MM/dd/HHmmssSSS");
							//update                                                                      
							stmt.executeUpdate("insert into rentbook values (rbook_seq_num.nextval, '"+tfID1.getText()+"', "
									+ "(select bid from book where title='"+tfTitle1.getText()+"'), '"+f.format(now)+"')");
							stmt.executeUpdate("update book set inventory=inventory-1 where title='"+tfTitle1.getText()+"'");
	
							//select
							ResultSet rs1=stmt.executeQuery("select * from book");//select문 실행.
							// rs는 cursor역할. 한행씩 while문으로 fetch
							
							//목록 초기화
							model1.setRowCount(0);
							while(rs1.next()) {
								String[] row=new String[2];
								row[0]=rs1.getString("title");
								row[1]=rs1.getString("inventory");
								model1.addRow(row);
							}
							JOptionPane.showConfirmDialog(null, "ID: "+tfID1.getText()+", "+tfTitle1.getText()+" 대출되었습니다","대출 확인",JOptionPane.PLAIN_MESSAGE);
						}
						
					}catch(Exception e1) {
						e1.printStackTrace();
					}finally {
						try {
							if(rs!=null) {rs.close();}
							if(stmt!=null) {stmt.close();}
							if(con!=null) {con.close();}
						}catch(Exception e2) {
							e2.printStackTrace();
						}
					}
				}});
			add(btnRent);
			
			this.setSize(600,450);
			setLocationRelativeTo(null);
		}

	}

	public static void main(String[] args) {
		new BookRent();
	}

}


