package com.usnschool;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class Server {
	ServerSocket serversocket;
	ServerReceiver receiverstart;
	static String[][] liststr ;
	static int[][] rightint;
	static ArrayList arrlist ;
	static ArrayList list;
	public Server() {
		
		try{
		serversocket = new ServerSocket(7778);
		arrlist = new ArrayList();
		list = new ArrayList();
		
			while(true){
					System.out.println("���Ӵ����");
					Socket socket = serversocket.accept();
					System.out.println("�Ѹ����");
					
					receiverstart = new ServerReceiver(socket);
					receiverstart.start();
					arrlist.add(receiverstart);

				


			}
			
		}catch(IOException ie){
			ie.printStackTrace();
			System.out.println("serverIO�߻�");
		}catch(ClassNotFoundException cne){
			System.out.println("servercne�߻�");
		}
		
	} //end of server()
	
	public static void main(String[] args) {
		new Server();
	}
	
}

class ServerReceiver extends Thread{
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos ;
	ArrayList arrlist ;
	String moonja;
	
	int count = 0;
	public ServerReceiver(Socket socket) throws IOException, ClassNotFoundException {
		this.socket = socket;
		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());
		System.out.println("������ ����");
	}
	@Override
	public void run(){
		while(ois!=null){
			try {
				moonja = ois.readUTF();
				System.out.println("utf�Ϸ�");
				
				arrlist = (ArrayList)ois.readObject();
				System.out.println("������Ʈ�Ϸ�");

				
			
				int sqrtsize = (int)Math.sqrt(arrlist.size());
				Server.list.add(moonja);
				Iterator it = arrlist.iterator();
				Server.liststr = new String[sqrtsize][sqrtsize]; //Ŭ���̾�Ʈ�� ���� ���� ���� ���� ��������
				Server.rightint = new int[sqrtsize][sqrtsize];
				for (int i = 0; i < sqrtsize; i++) { //list = ���� ���� �Ѱ� �� �Է��ϴ°�
					for (int j = 0; j < sqrtsize; j++) { 
						Server.liststr[i][j] = (String) it.next();
						System.out.println(Server.liststr[i][j]);
						for(int k = 0;k < Server.list.size(); k++){
							if(Server.liststr[i][j].equals(Server.list.get(k))){ //���� Server.list�� ��� ���ڿ� Ŭ���̾�Ʈ�κ��� �޾ƿ� ���ڰ� ������ �ִ��� Ȯ��
								Server.rightint[i][j] = 1;
							}else{
								Server.rightint[i][j] = 0;
							}
						}
					}
				}
				int temp = bingoCheck();
				if(temp>=3){
					
					for (int i = 0; i < Server.arrlist.size(); i++) {//��ü  �������ù����� ���ڸ� ������.
						((ServerReceiver)Server.arrlist.get(i)).oos.writeInt(1); // �����Ѱ��� ������.
						((ServerReceiver)Server.arrlist.get(i)).oos.flush();
						System.out.println("�����Ǻ���");
					}
					
				}else{
					for (int i = 0; i < Server.arrlist.size(); i++) {
						System.out.println("temp : "+temp);
						
						((ServerReceiver)Server.arrlist.get(i)).oos.writeInt(0); // ���� �����ʾ����� ������.
						((ServerReceiver)Server.arrlist.get(i)).oos.flush();
						((ServerReceiver)Server.arrlist.get(i)).oos.writeUTF(moonja);
						((ServerReceiver)Server.arrlist.get(i)).oos.flush();
						System.out.println("����ȹ��ڸ� �����ϴ�");
						System.out.println("���� ����������");
						//((ServerReceiver)Server.arrlist.get(i)).oos.writeObject(0);
						
					}
				}
			} catch (ClassNotFoundException e) {
				System.out.println("receivercne�߻�");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("receiverIO�߻�");
				e.printStackTrace();
			}
		}
		try {
		ois.close();
		oos.close();
		} catch(IOException e){
			e.printStackTrace();
			System.out.println("close���� io �߻�");
		}
	}
	public void Send(String s){
		try {
			oos.writeUTF(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int bingoCheck(){
		int countline = 0;
		int countcell = 0;
		boolean flag = false;
		//������ ����
		for(int i = 0;i < Server.rightint.length; i++){
			for (int j = 0; j < Server.rightint[i].length; j++) {
				if(Server.rightint[i][j]==1){
					countcell++;
				}		
			}
			if(countcell==5){
				countline++;
			}
			countcell = 0;
		}
		//������ ����
		for(int i = 0;i < Server.rightint.length; i++){
			for (int j = 0; j < Server.rightint[i].length; j++) {
				if(Server.rightint[j][i]==1){
					countcell++;
				}		
			}
			if(countcell==5){
				countline++;
			}
			countcell = 0;
		}
		//�밢�� ���� 1�� - > 7��
		int j = 0;

		for(int i = 0 ; i < Server.rightint.length ; i++){
			
			if(Server.rightint[i][i]==1){
				countcell++;
			}
		}
		if(countcell==5){
			countline++;
		}
		countcell = 0;
		j = 0;
		
		
		for(int i = 0 ; i < Server.rightint.length ; i++){
			if(Server.rightint[j][i]==1){
				countcell++;
			}
			j++;
		}
		if(countcell==5){
			countline++;
		}
		
		return countline;
	}
}
