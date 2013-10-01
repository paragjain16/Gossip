package org.ds.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.ds.member.Member;

public class NodeClient {
    public static void main(String[] args){
    	ArrayList<Member> memberList = null;
    	
    	System.out.println("Enter the ip address of the machine which would leave the network:");
    	
    	//Get the machine ip from user input.  
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));

        String machineIp = null;

        
        try {
        	machineIp = bReader.readLine();
        } catch (IOException ioe) {
           System.out.println("IO error");
           ioe.printStackTrace();
           System.exit(1);
        }
        DatagramSocket cSocket =null;
        try {
			 cSocket=new DatagramSocket();
		} catch (SocketException e) {
			System.exit(1);
			e.printStackTrace();
		}
        InetAddress IPAddress =null;
        try {
			 IPAddress = InetAddress.getByName(machineIp);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        memberList.add(new Member(IPAddress, -1, 3456)); // Add ip address of member to leave and set heartbeat to -1
              

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos.writeObject(memberList);               //Write membership list containing the member to leave.
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
        DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, IPAddress, 3456);
        try {
			cSocket.send(sendPacket);
			cSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
