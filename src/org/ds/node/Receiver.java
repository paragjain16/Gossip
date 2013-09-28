package org.ds.node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.List;

import org.ds.member.Member;

public class Receiver implements Runnable {

	private List<Member> aliveList;
	private List<Member> deadList;
	private DatagramSocket nodeSocket;
	
	public Receiver(List<Member> aliveList, List<Member> deadList,
			DatagramSocket nodeSocket) {
		super();
		this.aliveList = aliveList;
		this.deadList = deadList;
		this.nodeSocket = nodeSocket;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		byte[] msgBuffer=new byte[2048];
		DatagramPacket msgPacket=new DatagramPacket(msgBuffer,msgBuffer.length);
		try {
			nodeSocket.receive(msgPacket);
			 ByteArrayInputStream bis = new ByteArrayInputStream(msgPacket.getData());
             ObjectInputStream ois = new ObjectInputStream(bis);
             
             Object memberList=ois.readObject();
             if(memberList instanceof List<?>){
            	 List<Member> memList=( List<Member>)memberList;
            	 for(Member member:memList){
            		 if(aliveList.contains(memList)){
            			 
            		 }
            	 }
             }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
