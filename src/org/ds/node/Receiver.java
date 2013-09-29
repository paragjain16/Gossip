package org.ds.node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ds.member.Member;

public class Receiver implements Runnable {

	private List<Member> aliveList;
	private List<Member> deadList;
	private Map<String, Member> aliveMap;
	private Map<String, Member> deadMap;
	private DatagramSocket nodeSocket;
	private Object nodeLockObject;

	public Receiver(List<Member> aliveList, List<Member> deadList,
			DatagramSocket nodeSocket, Object nodeLockObject) {
		super();
		this.aliveList = aliveList;
		this.deadList = deadList;
		this.nodeSocket = nodeSocket;
		aliveMap = new HashMap<String, Member>();
		for (Member aliveMember : aliveList) {
			aliveMap.put(aliveMember.getIdentifier(), aliveMember);
		}
		deadMap = new HashMap<String, Member>();
		for (Member deadMember : deadList) {
			deadMap.put(deadMember.getIdentifier(), deadMember);
		}
		this.nodeLockObject=nodeLockObject;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		byte[] msgBuffer = new byte[2048];
		DatagramPacket msgPacket = new DatagramPacket(msgBuffer,
				msgBuffer.length);
		try {
			nodeSocket.receive(msgPacket);
			ByteArrayInputStream bis = new ByteArrayInputStream(
					msgPacket.getData());
			ObjectInputStream ois = new ObjectInputStream(bis);

			Object memberList = ois.readObject();
			
			if (memberList instanceof List<?>) {
				List<Member> memList = (List<Member>) memberList;
				for (Member member : memList) { // Iterate over the member list
												// received over the network

					String memAddress = member.getIdentifier();

					if (aliveMap.containsKey(memAddress)) { // Found a match
						Member localMemberObj = aliveMap.get(memAddress);
						if (localMemberObj.getHeartBeat() >= member
								.getHeartBeat()) {
							// Ignore, as the local member's heartbeat is
							// greater than incoming member's heartbeat.

						} else { // Update the local member's heartbeat with the
									// received heartbeat.
							Member localObj = aliveMap.get(memAddress);
							localObj.setHeartBeat(member.getHeartBeat());
							localObj.setTimeStamp(new Date().getTime());
						}
					}

					// else if the member was not found in the alive map, either
					// it is a new member or an old update of an already dead
					// member
					else {
						if (deadMap.containsKey(memAddress)) {
							// Check if the local member present in the dead Map
							// has a heartbeat greater than the heartbeat of the
							// received member.
							Member localMemberObj = deadMap.get(memAddress);
							if (localMemberObj.getHeartBeat() >= member
									.getHeartBeat()) {
								// Ignore, as the local member's heartbeat is
								// greater than incoming member's heartbeat.

							} else { // Reincarnation of a dead member, remove
										// it from dead member list and add it
										// to alive member list.
								Member localObj = deadMap.get(memAddress);
								localObj.setHeartBeat(member.getHeartBeat());
								localObj.setTimeStamp(new Date().getTime());
								deadMap.remove(memAddress);
								aliveMap.put(memAddress, localObj);
							}
						}

						else { // A new member is being added to the list.
							aliveMap.put(memAddress, member);
						}
					}
				}
			}
			synchronized(nodeLockObject){
				//Update the alive list and dead member list back to the parent node.
				aliveList.clear();
				deadList.clear();
				for(String memAddress:aliveMap.keySet()){
					aliveList.add(aliveMap.get(memAddress));
				}
				for(String memAddress:deadMap.keySet()){
					deadList.add(deadMap.get(memAddress));
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
