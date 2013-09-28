package org.ds.node;

import java.util.Date;
import java.util.HashMap;

import org.ds.member.Member;

public class Gossiper implements Runnable{
	private HashMap<String, Member> aliveMembers;
	private HashMap<String, Member> deadMembers;
	private Object lockUpdateMember;
	private Member itself;
	
	public Gossiper(HashMap<String, Member> aliveMembers, HashMap<String, Member> deadMembers, Object lockUpdateMember, Member itself){
		this.aliveMembers = aliveMembers;
		this.deadMembers = deadMembers;
		this.lockUpdateMember = lockUpdateMember;
		this.itself = itself;
	}
	
	public void run(){
		synchronized(lockUpdateMember){
			itself.incrementHB();
			itself.setTimeStamp(new Date());
			aliveMembers.put(itself.getIdentifier(), itself);
			
		}
	}
	public void updateMemberList(){
		
	}
}
