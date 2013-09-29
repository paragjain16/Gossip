package org.ds.member;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;

public class Member implements Serializable{
	private InetAddress address;
	private int heartBeat;
	private String identifier;
	private long timeStamp;
	private int port;

	public Member(InetAddress address, String id, int port){
		this.address = address;
		this.heartBeat = 0;
		this.identifier = id+":"+address.getHostAddress();
		this.timeStamp = new Date().getTime();
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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
	
	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(InetAddress address) {
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
	public boolean checkTimeOut(){
		if(new Date().getTime() - this.timeStamp >3){
			return true;
		}
		return false;
	}
	
}
