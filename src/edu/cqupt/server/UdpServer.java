package edu.cqupt.server;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class UdpServer  extends Thread{
	
	static DatagramSocket udpSocket = null;
	static DatagramPacket udpPacket = null;
	
	public static final int DEFAULT_PORT = 2011;
	
	String ip = null;
	
	Thread socketThread;
	
	public  UdpServer(Thread SocketThread){
		socketThread =  SocketThread;
	}

	public void run() {
		
		byte[] data = new byte[256];
		try {
			udpSocket = new DatagramSocket(DEFAULT_PORT);
			udpPacket = new DatagramPacket(data, data.length);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		while (true) {
			
			try {
				udpSocket.receive(udpPacket);
			} catch (Exception e) {
			}
			
			if (null != udpPacket.getAddress()) {			
				 ip = udpPacket.getAddress().toString().substring(1);
				 udpSocket.close();
				 break ;
			}
		}

	}
}
