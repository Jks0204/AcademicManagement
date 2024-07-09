import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

class MyDialog extends JDialog{
	JLabel lblID=null;
	JTextField tfID=null;
	JLabel lblPW=null;
	JPasswordField tfPW=null;
	JButton btnLogin=null;
	JButton btnExit=null;
	Connection conn=null;
	
	public MyDialog(JFrame frame, String title) {
		super(frame, title);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		// Frame Layout default: BorderLayout
		this.setLayout(new FlowLayout()); // FlowLayout으로 변경
		
		lblID=new JLabel("ID");
		this.add(lblID);
		tfID=new JTextField(10);
		this.add(tfID);
		
		lblPW=new JLabel("PW");
		this.add(lblPW);
		tfPW=new JPasswordField(10);
		this.add(tfPW);
		
		btnLogin=new JButton("Login");
		this.add(btnLogin);
		btnLogin.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					//oracle jdbc driver load
					Class.forName("oracle.jdbc.driver.OracleDriver");
					//Connection
					conn=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","ora_user","hong");
					
					Statement stmt=conn.createStatement(); // statement객체 생성
					
					//select
					ResultSet rs=stmt.executeQuery("select * from login");//select문 실행.
					// rs는 cursor역할. 한행씩 while문으로 fetch
					
					//목록 초기화
					HashMap<String,String> map2 = new HashMap<>();
					while(rs.next()) {
						map2.put(rs.getString("id"),rs.getString("pw"));
					}
					
					// ID, PW 확인
					if(map2.containsKey(tfID.getText()) && map2.get(tfID.getText()).equals(tfPW.getText())) {
						dispose();
					}else {
						JOptionPane.showMessageDialog(btnLogin, "잘못된 id 혹은 비밀번호 입니다.", "로그인 실패",JOptionPane.WARNING_MESSAGE);
					}
					
					rs.close();
					stmt.close();
					conn.close();//연결해제
				}catch(Exception e1) {
					e1.printStackTrace();
				}
				
			}

			private Object map2(String key) {
				// TODO Auto-generated method stub
				return null;
			}
		});
		this.setSize(170, 140);
		
		btnExit=new JButton("Exit");
		this.add(btnExit);
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(1);
			}
			
		});
		this.setSize(170, 140);
	}
}


public class Haksa extends JFrame {
	JPanel panel; //메뉴별 화면이 출력되는 패널
	MyDialog dialog=null;
	public Haksa() {
		this.setTitle("학사관리");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Menu Bar
		JMenuBar bar = new JMenuBar();
		// Menu
		JMenu mStudent = new JMenu("학생관리"); // 메뉴생성
		bar.add(mStudent); // 메뉴바에 추가
		JMenu mBookRent = new JMenu("도서관리");
		bar.add(mBookRent);
		
		// Menu Item (서브메뉴)
		JMenuItem miStudent = new JMenuItem("학생정보");
		miStudent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.removeAll(); //모든컴포넌트 삭제
			    panel.revalidate(); //다시 활성화
			    panel.repaint();    //다시 그리기
			    panel.add(new Student()); //화면 생성.
			    panel.setLayout(null);//레이아웃적용안함
			}
		});
		mStudent.add(miStudent); // 학생정보 menu에 학생정보 item추가
		
		JMenuItem miBookRent = new JMenuItem("대출목록");
		miBookRent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.removeAll();
				panel.revalidate();
				panel.repaint();
				panel.add(new BookRent());
				panel.setLayout(null);
			}
		});
		mBookRent.add(miBookRent);
		
		this.setJMenuBar(bar); // menu bar를 frame에 추가
		
		//panel 생성, 추가
		this.panel=new JPanel();
		this.panel.setBackground(Color.LIGHT_GRAY);
		this.add(panel);
		
		ImageIcon icon = new ImageIcon("img/library.jpg");
		Image img=icon.getImage();
		Image fixImg = img.getScaledInstance(800, 600, img.SCALE_SMOOTH);
		ImageIcon fixIcon = new ImageIcon(fixImg);
		JLabel lbl = new JLabel(fixIcon);
		panel.add(lbl);
		
		this.setSize(800, 600);
		this.setVisible(true);
		
		//로그인 다이얼로그 띄우기
		dialog = new MyDialog(this,"로그인");
		dialog.setModal(true); // modal dialog로 설정
		dialog.setVisible(true);// dialog 보이게
	}
	
	public static void main(String[] args) {
		new Haksa();
	}

}

// 사용기술: Java, JDBC, Swing, Oracle
