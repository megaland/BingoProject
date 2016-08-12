package com.usnschool;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.event.ChangeListener;

public class Client extends JFrame implements Runnable, ActionListener {
	JFrame jf;
	ZButton jbt[][];
	Panel mainp1;
	Panel mainp2;
	int plnumx = 5;
	int plnumy = 5;
	Button submitbt;
	Button readybt;
	Button exitbt;
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	String laststring = " ";
	static Color c;
	int done=0;
	Random r = new Random();

	Client() {
		jf = new JFrame();
		jf.setLayout(new BorderLayout());
		jf.setSize(500, 500);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		jbt = new ZButton[plnumx][plnumy];
		
		mainp1 = new Panel();
		mainp1.setLayout(new GridLayout(plnumx, plnumy));
		mainp2 = new Panel();
		mainp2.setLayout(new GridLayout(3, 1));

		jf.add(mainp1, BorderLayout.CENTER);
		jf.add(mainp2, BorderLayout.EAST);

		for (int i = 0; i < jbt.length; i++) {
			for (int j = 0; j < jbt[i].length; j++) {
				jbt[i][j] = new ZButton();

//				jbt[i][j].setBackground(rColor()); �⺻ ����
				jbt[i][j].addActionListener(this);
				mainp1.add(jbt[i][j]);

			}
		}

		// �غ�
		readybt = new Button("��     ��");
		readybt.addActionListener(this);
		mainp2.add(readybt);

		// �����ϱ�
		submitbt = new Button("��     ��");
		submitbt.addActionListener(this);
		mainp2.add(submitbt);
		// ������
		exitbt = new Button("�� �� ��");
		exitbt.addActionListener(this);
		mainp2.add(exitbt);

		jf.setVisible(true);
		
		//���Ϻκ�
		try{
		String serverip = "127.0.0.1";
		socket = new Socket(serverip, 7778);
		
		oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (ConnectException ce){
			
		} catch (IOException ie){
			
		}
	}

	Color rColor() {
		c = new Color(r.nextInt(255) + 1, r.nextInt(255) + 1, r.nextInt(255) + 1);
		return c;
	}

	static Color oColor() {
		c = new Color(100, 100, 100);
		return c;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == readybt) {
			done = 1;
		} else if (e.getSource() == submitbt) {
			String str;
			try{
				ArrayList arrlist = new ArrayList();
				for (int i = 0; i < jbt.length; i++) {
					for (int j = 0; j < jbt[i].length; j++) {
						str=jbt[i][j].getText();
						jbt[i][j].str = str;
						arrlist.add(str);
					}
				}
				System.out.println(arrlist.get(4));
				oos.writeUTF(laststring);
				System.out.println("utf����");
				oos.writeObject(arrlist);
				System.out.println("������Ʈ����");
			
				
			}catch(IOException ie){
				
			}
			
		} else if (e.getSource() == exitbt) {
			try{
				
				oos.close();
				ois.close();
			}catch(IOException ie){
				
			}
			System.exit(0);

		} else {
			if(done==0){
				if(e.getSource() instanceof ZButton){
					JFrame jfr = new JFrame("�ܾ �Է��Ͻÿ�");
					jfr.setLayout(new FlowLayout());
					jfr.setSize(200,100);
					TextField tf = new TextField("", 15);
					Button btn1 = new Button("Ȯ��");
					jfr.add(tf);
					jfr.add(btn1);
					
					btn1.addActionListener( new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e1) {
							((ZButton)e.getSource()).setText(tf.getText());
							jfr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							jfr.dispose();
						}
					});
					jfr.setVisible(true);
	//				((ZButton)e.getSource()).setText();
					
				}
			} else if (done==1){
				((ZButton)e.getSource()).setContentAreaFilled(false);
				laststring = ((ZButton)e.getSource()).getText();
			}
		}

	}

	public static void main(String[] args) {
		Client c = new Client();
		Thread t1 = new Thread(c);
		t1.start();
	}

	public void run()  {
		String str;
		try{
			Thread.sleep(1000);
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("readint�� ��ٸ����ս��ϴ�." + ois!= null);
			while(ois!=null){
				

				switch(ois.readInt()){
				case 0: 
					System.out.println("���� ���� �ʾҽ��ϴ�.");
					str = ois.readUTF();
					System.out.println(str);
					for (int i = 0; i < jbt.length; i++) {
						for (int j = 0; j < jbt[i].length; j++) {
							if(jbt[i][j].str.equals(str)){
								System.out.println("����������"+i +""+j);
								jbt[i][j].setContentAreaFilled(true);
								jbt[i][j].setBackground(oColor());
							}
						}
					}
					break;
				case 1:
					System.out.println("����!!!!!!!!!!!");
					
					break;
				case 3:

					
					break;

				}
			}
		} catch(IOException ie) {
			ie.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class ZButton extends JButton implements Serializable{
	String str = "";
	public ZButton() {
		
	}
}

class sender extends Thread{
	Socket socket;
	ObjectOutputStream oos;
	
	public sender() {
		try{
		String serverip = "127.0.0.1";
		socket = new Socket(serverip, 7777);
		
		oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (ConnectException ce){
			
		} catch (IOException ie){
			
		}
	}
	public void run(){
		
	}
}

class receiver extends Thread{
	
	public receiver() {

	}
	public void run(){
		
	}
}

class ActionListen implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		e.getSource();
	}
}