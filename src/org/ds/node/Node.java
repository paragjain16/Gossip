package org.ds.node;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.ds.logger.DSLogger;
import org.ds.member.Member;

public class Node {
	private HashMap<String, Member> aliveMembers;
	private HashMap<String, Member> deadMembers;
	private Object lockUpdateMember;
	private DatagramSocket socket; 
	private Gossiper gossiper;
	private int port = 3456;
	private String id ;
	private Member itself;
	private ScheduledFuture<?> gossip = null;
	
	public Node(){
		aliveMembers = new HashMap<String, Member>();
		deadMembers = new HashMap<String, Member>();
		try {
			socket = new DatagramSocket(port);
			itself = new Member(socket.getInetAddress(), id, port);
			aliveMembers.put(itself.getIdentifier(), itself);
			DSLogger.log("Node", "Node", "Member with id "+itself.getIdentifier()+" joined");
			gossiper = new Gossiper(aliveMembers, deadMembers, lockUpdateMember, itself, socket);
			final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
			gossip = scheduler.scheduleAtFixedRate(gossiper, 0, 1, TimeUnit.SECONDS);
				
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}finally{
			if(gossip!=null)
				gossip.cancel(true);
		}
		
	}
}