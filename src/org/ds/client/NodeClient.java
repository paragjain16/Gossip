package org.ds.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
/**
 * 
 * @author pjain11,mallapu2
 * Class responsible for the client to send leave message to the node server instructing it to
 * leave the network.
 */
public class NodeClient {
    public static void main(String[] args){
    	
    	DatagramSocket cSocket =null;
        try {
			 cSocket=new DatagramSocket();
		} catch (SocketException e) {
			System.exit(1);
			e.printStackTrace();
		}
        InetAddress IPAddress =null;
        try {
			 IPAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        String message="leave";            //Send a message of "leave" to the port 3457 in localhost.
        byte[] dataToSend = new byte[1024];
        dataToSend=message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(dataToSend, dataToSend.length, IPAddress, 3457);
        try {
			cSocket.send(sendPacket);
			cSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
