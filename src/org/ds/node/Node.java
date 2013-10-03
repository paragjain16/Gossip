package org.ds.node;

import java.io.IOException;
import java.net.DatagramPacket;
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

import javax.xml.crypto.NodeSetData;

import org.ds.logger.DSLogger;
import org.ds.member.Member;
import org.ds.networkConf.XmlParseUtility;

public class Node {
	private HashMap<String, Member> aliveMembers;
	private HashMap<String, Member> deadMembers;
	final private Object lockUpdateMember;
	private DatagramSocket receiveSocket;
	private Gossiper gossiper;
	private Receiver receiver;
	private Member itself;

	private ScheduledFuture<?> gossip = null;

	public Node(int port, String id) {
		aliveMembers = new HashMap<String, Member>();
		deadMembers = new HashMap<String, Member>();
		lockUpdateMember = new Object();
		try {
			receiveSocket = new DatagramSocket(port);
			DSLogger.log("Node", "run", "Receving socket boud to "
					+ receiveSocket.getInetAddress());
			itself = new Member(InetAddress.getByName(getLocalIP()), id, port);
			aliveMembers.put(itself.getIdentifier(), itself);
			DSLogger.log("Node", "Node",
					"Member with id " + itself.getIdentifier() + " joined");
			// gossiper = new Gossiper(aliveMembers, deadMembers,
			// lockUpdateMember, itself, socket);
			// final ScheduledExecutorService scheduler = new
			// ScheduledThreadPoolExecutor(1);
			// gossip = scheduler.scheduleAtFixedRate(gossiper, 0, 1,
			// TimeUnit.SECONDS);
			//
			// DSLogger.log("Node", "Node",
			// "Member with id " + itself.getIdentifier() + " joined");
			/*
			 * gossiper = new Gossiper(aliveMembers, deadMembers,
			 * lockUpdateMember, itself, socket); final ScheduledExecutorService
			 * scheduler = new ScheduledThreadPoolExecutor(1); gossip =
			 * scheduler.scheduleAtFixedRate(gossiper, 0, 1, TimeUnit.SECONDS);
			 * 
			 * DSLogger.log("Node", "Node", "Member with id " +
			 * itself.getIdentifier() + " joined");
			 */

		} catch (SocketException e) {

			e.printStackTrace();
		} catch (UnknownHostException e) {
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
		if (args.length < 1) {
			System.out.println("Please pass id  as a parameter");
			System.exit(0);
		} else {
			port = Integer.parseInt(args[0]);
			id = args[1];
		}
		Node node = new Node(port, id);
		System.out.println("Node Id" + id + "started with port: " + port);

		String contactMachineAddr = XmlParseUtility.getContactMachineAddr();
		contactMachineIP = contactMachineAddr.split(":")[0];
		contactMachinePort = contactMachineAddr.split(":")[1];
		contactMachineId = "";// contactMachineAddr.split(":")[2];
		if (!getLocalIP().equals(contactMachineIP)) {
			try {
				contactMember = new Member(
						InetAddress.getByName(contactMachineIP),
						contactMachineId, Integer.parseInt(contactMachinePort));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			node.aliveMembers.put(contactMember.getIdentifier(), contactMember);
			DSLogger.log("Node", "main", "Alive member list updated with "
					+ contactMember.getIdentifier());
		}
		node.gossiper = new Gossiper(node.aliveMembers, node.deadMembers,
				node.lockUpdateMember, node.itself);
		DSLogger.log("Node", "main", "Starting to gossip");
		final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(
				2);
		DSLogger.log("Node", "main", "Starting to gossip step 2");
		node.gossip = scheduler.scheduleAtFixedRate(node.gossiper, 0, 1,
				TimeUnit.SECONDS);
		DSLogger.log("Node", "main", "Starting receiver");
		node.receiver = new Receiver(node.aliveMembers, node.deadMembers,
				node.receiveSocket, node.lockUpdateMember);
		// scheduler.schedule(node.receiver, 0 , TimeUnit.SECONDS);
		scheduler.execute(node.receiver);
		try {
			DatagramSocket s = new DatagramSocket(3457);
			while (true) {
				DSLogger.log("Node", "main", "Started receiver");
				byte b[] = new byte[2048];
				DatagramPacket packet = new DatagramPacket(b, b.length);
				s.receive(packet);
				String cmd = new String(packet.getData(), 0, packet.getLength());
				if (cmd.equals("leave")) {
					scheduler.shutdown();
					node.receiver.shutDown();
					Thread gossipLeave = new Thread();
					gossipLeave.join();
					System.exit(0);
				}
			}
		} catch (SocketException e) {
			DSLogger.log("Node", "run", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			DSLogger.log("Node", "run", e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			DSLogger.log("Node", "run", e.getMessage());
			e.printStackTrace();
		}

		// while(true){}
	}

	public static String getLocalIP() {
		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (interfaces.hasMoreElements()) {
			NetworkInterface current = interfaces.nextElement();
			try {

				if (!current.isUp() || current.isLoopback()
						|| current.isVirtual())
					continue;
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Enumeration<InetAddress> addresses = current.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress current_addr = addresses.nextElement();
				if (current_addr.isLoopbackAddress())
					continue;
				if (current_addr instanceof Inet4Address) {
					String addr = current_addr.getHostAddress();
					if (addr.contains(".")) {
						return addr;
					}
				}
			}
		}
		return null;
	}

}
