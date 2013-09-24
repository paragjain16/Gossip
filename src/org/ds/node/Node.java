package org.ds.node;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;

import org.ds.logger.DSLogger;
import org.ds.member.Member;

public class Node {
	private HashMap<String, Member> aliveMemebers;
	private DatagramSocket socket; 
	private int port = 3456;
	private String id ;
	
	public Node(){
		aliveMemebers = new HashMap<String, Member>();
		try {
			socket = new DatagramSocket(port);
			Member itself = new Member(socket.getInetAddress().getHostAddress(), id);
			aliveMemebers.put(itself.getIdentifier(), itself);
			DSLogger.log("Node", "Node", "Memeber with id "+itself.getIdentifier()+" joined");
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
