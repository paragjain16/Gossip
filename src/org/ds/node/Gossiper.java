package org.ds.node;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.ds.member.Member;

public class Gossiper implements Runnable{
	private HashMap<String, Member> aliveMembers;
	private HashMap<String, Member> deadMembers;
	private Object lockUpdateMember;
	private Member itself;
	private ArrayList<Member> memberList;
	private DatagramSocket socket;
	
	public Gossiper(HashMap<String, Member> aliveMembers, HashMap<String, Member> deadMembers, Object lockUpdateMember, Member itself, DatagramSocket socket){
		this.aliveMembers = aliveMembers;
		this.deadMembers = deadMembers;
		this.lockUpdateMember = lockUpdateMember;
		this.itself = itself;
		this.socket = socket;
	}
	
	public void run(){
		synchronized(lockUpdateMember){
			itself.incrementHB();
			itself.setTimeStamp(new Date().getTime());
			aliveMembers.put(itself.getIdentifier(), itself);
			Set<String> keys = aliveMembers.keySet();;
			for(String key: keys){
				
			}
			memberList = new ArrayList<Member>(aliveMembers.values());
			
		}
	}
	public void updateMemberList(){
		
	}
}
