package org.ds.node;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.ds.logger.DSLogger;
import org.ds.member.Member;
import org.ds.networkConf.XmlParseUtility;

public class Node {
	private HashMap<String, Member> aliveMembers;
	private HashMap<String, Member> deadMembers;
	final private Object lockUpdateMember;
	private DatagramSocket socket;
	private Gossiper gossiper;
	private Receiver receiver;
	private Member itself;

	private ScheduledFuture<?> gossip = null;

	public Node( int port,String id) {
		aliveMembers = new HashMap<String, Member>();
		deadMembers = new HashMap<String, Member>();
		lockUpdateMember = new Object();
		try {
			socket = new DatagramSocket(port);
			itself = new Member(InetAddress.getByName(getLocalIP()),id,port);
			aliveMembers.put(itself.getIdentifier(), itself);
			DSLogger.log("Node", "Node", "Member with id "+itself.getIdentifier()+" joined");
//			gossiper = new Gossiper(aliveMembers, deadMembers, lockUpdateMember, itself, socket);
//			final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
//			gossip = scheduler.scheduleAtFixedRate(gossiper, 0, 1, TimeUnit.SECONDS);
//				
//			DSLogger.log("Node", "Node",
//					"Member with id " + itself.getIdentifier() + " joined");

		} catch (SocketException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (gossip != null)
				gossip.cancel(true);
		}

	}

	public static void main(String[] args) {
		String contactMachineIP;
		String contactMachinePort;
		String contactMachineId;
		int port = 0;
		String id = null;
		Member contactMember = null;
		if(args.length < 1){
			System.out.println("Please pass id  as a parameter");
			System.exit(0);
		}
		else{
			port=Integer.parseInt(args[0]);
			id=args[1];
		}
		Node node = new Node(port,id);
		System.out.println("Node Id"+id+"started with port: "+port);
	
		String contactMachineAddr = XmlParseUtility.getContactMachineAddr();
		contactMachineIP = contactMachineAddr.split(":")[0];
		contactMachinePort = contactMachineAddr.split(":")[1];
		contactMachineId = contactMachineAddr.split(":")[2];

		try {
			contactMember = new Member(InetAddress.getByName(contactMachineIP),
					contactMachineId, Integer.parseInt(contactMachinePort));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		node.aliveMembers.put(contactMember.getIdentifier(), contactMember);
		node.gossiper = new Gossiper(node.aliveMembers, node.deadMembers,
				node.lockUpdateMember, node.itself, node.socket);
		final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(
				1);
		node.gossip = scheduler.scheduleAtFixedRate(node.gossiper, 0, 1,
				TimeUnit.SECONDS);
		node.receiver=new Receiver(node.aliveMembers, node.deadMembers, node.socket, node.lockUpdateMember);
		Thread receiveThread=new Thread(node.receiver);
		receiveThread.start();

	}
	
	public static String getLocalIP(){
		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (interfaces.hasMoreElements()){
		    NetworkInterface current = interfaces.nextElement();
		    try {

				if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    Enumeration<InetAddress> addresses = current.getInetAddresses();
		    while (addresses.hasMoreElements()){
		        InetAddress current_addr = addresses.nextElement();
		        if (current_addr.isLoopbackAddress()) continue;
		        if(current_addr instanceof Inet4Address){
		        String addr=current_addr.getHostAddress();
		        if(addr.contains(".")){
		        	return addr;
		        }
		    }
		}
	}
		return null;
	}
	
}
