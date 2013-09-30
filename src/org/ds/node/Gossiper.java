package org.ds.node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.ds.logger.DSLogger;
import org.ds.member.Member;

public class Gossiper implements Runnable{
	private HashMap<String, Member> aliveMembers;
	private HashMap<String, Member> deadMembers;
	private Object lockUpdateMember;
	private Member itself;
	private ArrayList<Member> memberList;
	private DatagramSocket socket;
	private ArrayList<String> keysToRemove;
	
	public Gossiper(HashMap<String, Member> aliveMembers, HashMap<String, Member> deadMembers, Object lockUpdateMember, Member itself){
		this.aliveMembers = aliveMembers;
		this.deadMembers = deadMembers;
		this.lockUpdateMember = lockUpdateMember;
		this.itself = itself;
		//this.socket = socket;
	}
	
	public void run(){	
		DSLogger.log("Gossiper", "run", "Entered");
		
		synchronized(lockUpdateMember){
			DSLogger.log("Gossiper", "run", "Lock Acquired by gossiper");
			itself.incrementHB();
			itself.setTimeStamp(new Date().getTime());
			aliveMembers.put(itself.getIdentifier(), itself);
			DSLogger.log("Gossiper", "run", itself.getIdentifier()+" added to member list");
			Set<String> keys = aliveMembers.keySet();
			Member aMember;
			keysToRemove = new ArrayList<String>();
			for(String key: keys){
				aMember =aliveMembers.get(key);
				if(aMember.checkTimeOut()){
					keysToRemove.add(aMember.getIdentifier());
					deadMembers.put(aMember.getIdentifier(), aMember);
					DSLogger.log("Gossiper", "run", aMember.getIdentifier()+" added to dead list");
					//aliveMembers.remove(aMember.getIdentifier());
				}
			}
			for(String keytoRemove: keysToRemove){
				aliveMembers.remove(keytoRemove);
				DSLogger.log("Gossiper", "run", keytoRemove+" removed from alive list");
			}
			DSLogger.log("Gossiper", "run", "Alive and dead member list updated");
			memberList = new ArrayList<Member>(aliveMembers.values());
		}
		DSLogger.log("Gossiper", "run", "Lock released by gossiper");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			socket = new DatagramSocket();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(memberList);
			byte[] buf = baos.toByteArray();
			Member memberToGossip = chooseRandom();
			printGossip(memberToGossip);
			if(memberToGossip!=null){
				
				DatagramPacket packet = new DatagramPacket(buf, buf.length, memberToGossip.getAddress(), memberToGossip.getPort());
				socket.send(packet);
				
			}
			DSLogger.log("Gossiper", "run", "Exiting gossiper");
		}catch (IOException e) {
			DSLogger.log("Gossiper", "run", e.getMessage());
			e.printStackTrace();
		}finally{
			socket.close();
		}
		
	}
	
	public Member chooseRandom(){
		DSLogger.log("Gossiper", "chooseRandom", "Choosing a Random member");
		Random random = new Random();
		int tryAnother = 15;
		while(tryAnother-- >0){
			int i = random.nextInt(memberList.size());
			DSLogger.log("Gossiper", "chooseRandom", "Random "+memberList.get(i).getIdentifier());
			if(!(memberList.get(i) == itself)){
				DSLogger.log("Gossiper", "chooseRandom", "Member "+memberList.get(i).getIdentifier()+" chosen");
				return memberList.get(i);
			}
		}
		DSLogger.log("Gossiper", "chooseRandom", "No members to choose");
		return null;
		
	}
	/*Print Gossip method*/
	public void printGossip(Member mem){
		if(mem!=null){
			System.out.println("Gossiping to "+mem.getIdentifier());
		}
		System.out.println("Alive Members ");
		Set<String> keys = aliveMembers.keySet();;
		Member aMember;
		for(String key: keys){
			aMember =aliveMembers.get(key);
			System.out.println(aMember.getIdentifier());
		}
		System.out.println("Dead Members ");
		keys = deadMembers.keySet();;
		for(String key: keys){
			aMember =deadMembers.get(key);
			System.out.println(aMember.getIdentifier());
		}
	}
}
