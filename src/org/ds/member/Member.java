package org.ds.member;

import java.io.Serializable;
import java.util.Date;

public class Member implements Serializable{
	private String address;
	private int heartBeat;
	private String identifier;
	private long timeStamp;

	public Member(String address, String id){
		this.address = address;
		this.heartBeat = 0;
		this.identifier = id+address;
		this.timeStamp = new Date().getTime();
	}
	
	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public void incrementHB(){
		this.setHeartBeat(getHeartBeat()+1);
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getHeartBeat() {
		return heartBeat;
	}
	public void setHeartBeat(int heartBeat) {
		this.heartBeat = heartBeat;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	
}
