package org.ds.node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ds.logger.DSLogger;
import org.ds.member.Member;

public class Receiver implements Runnable {

	private Map<String, Member> aliveMap;
	private Map<String, Member> deadMap;
	private DatagramSocket nodeSocket;
	private Object nodeLockObject;

	public Receiver(Map<String,Member> aliveMap, Map<String,Member> deadMap,
			DatagramSocket nodeSocket, Object nodeLockObject) {
		super();
		this.nodeSocket = nodeSocket;
		this.aliveMap = aliveMap;
		this.deadMap = deadMap;
		this.nodeLockObject = nodeLockObject;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		DSLogger.log("Receiver", "run", "Entered Run") ;
		byte[] msgBuffer = new byte[2048];
		DatagramPacket msgPacket = new DatagramPacket(msgBuffer,
				msgBuffer.length);
		while(true){
		try {
			nodeSocket.receive(msgPacket);
			DSLogger.log("Receiver", "run", "Received data over UDP socket") ;
            
			ByteArrayInputStream bis = new ByteArrayInputStream(
					msgPacket.getData());
			ObjectInputStream ois = new ObjectInputStream(bis);

			Object memberList = ois.readObject();

			if (memberList instanceof List<?>) {
				List<Member> memList = (List<Member>) memberList;
                DSLogger.log("Receiver", "run", "Received member list of size: "+memList.size()) ;
                for(Member mem:memList){
                	DSLogger.log("Receiver", "run", "Received member:  "+mem.getIdentifier()+" with heartbeat:"+mem.getHeartBeat()) ;
                }
				synchronized (nodeLockObject) {
					DSLogger.log("Receiver", "run", "Lock Acquired by receiver") ;
					for (Member member : memList) { // Iterate over the member
													// list
													// received over the network

						String memAddress = member.getIdentifier();

						if (aliveMap.containsKey(memAddress)) { // Found a match
							DSLogger.log("Receiver", "run", "Found match in alive map for: "+memAddress); 
							Member localMemberObj = aliveMap.get(memAddress);
							if (localMemberObj.getHeartBeat() >= member.getHeartBeat()) {
								// Ignore, as the local member's heartbeat is
								// greater than incoming member's heartbeat.

							} else { // Update the local member's heartbeat with
										// the
										// received heartbeat.
								Member localObj = aliveMap.get(memAddress);
								localObj.setHeartBeat(member.getHeartBeat());
								localObj.setTimeStamp(new Date().getTime());
							}
						}

						// else if the member was not found in the alive map,
						// either
						// it is a new member or an old update of an already
						// dead
						// member
						else {
							if (deadMap.containsKey(memAddress)) {
								DSLogger.log("Receiver", "run", "Found match in dead map for: "+memAddress); 

								// Check if the local member present in the dead
								// Map
								// has a heartbeat greater than the heartbeat of
								// the
								// received member.
								Member localMemberObj = deadMap.get(memAddress);
								if (localMemberObj.getHeartBeat() >= member
										.getHeartBeat()) {
									// Ignore, as the local member's heartbeat
									// is
									// greater than incoming member's heartbeat.

								} else { // Reincarnation of a dead member,
											// remove
											// it from dead member list and add
											// it
											// to alive member list.
									DSLogger.log("Receiver", "run", "Reincarnation for "+memAddress); 

									Member localObj = deadMap.get(memAddress);
									localObj.setHeartBeat(member.getHeartBeat());
									localObj.setTimeStamp(new Date().getTime());
									deadMap.remove(memAddress);
									aliveMap.put(memAddress, localObj);
								}
							}

							else { // A new member is being added to the list.
								DSLogger.log("Receiver", "run", "New member added with "+memAddress); 
								aliveMap.put(memAddress, member);
							}
						}
					}
				}
				DSLogger.log("Receiver", "run", "********Alive members after update*******") ;
				printMemberMap(aliveMap);
				DSLogger.log("Receiver", "run", "**********Dead members after update*******") ;
				printMemberMap(deadMap);
				DSLogger.log("Receiver", "run", "Lock released by receiver") ;
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
	
	/*Print Gossip method*/
	public void printMemberMap(Map<String,Member> memberMap){
		
		Set<String> keys = memberMap.keySet();;
		Member aMember;
		for(String key: keys){
			aMember =memberMap.get(key);
			DSLogger.log("Receiver", "printMemberMap ", aMember.getIdentifier());
		}
	}
}
