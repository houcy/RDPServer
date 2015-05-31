package edu.cqupt.server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class SocketThread  extends Thread{
	
	private static final int DEFAULT_PORT = 2015;
	String ipAddress;
	
	byte[] sendbytes;
	
	 Socket socket;
	 DataOutputStream dos;
	 
	 DataInputStream dis;
	 
	 private static boolean  busy= false;
	 
	 private static boolean  sendSwicth = false;
	 
	 private static boolean  socketSwicth = true ;
	 
	public SocketThread(String ip){	
		ipAddress = ip;
	}
	
	public void send(byte[] bytes){
		sendSwicth = true;
		sendbytes = bytes;
	}
	
	public boolean isBusy(){
		return busy;
	}
	
	public void close( ){
		socketSwicth = false ;		
		try {
			dos.close();
			dis.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public void run() {
			// TODO Auto-generated method stub
			
		while(socketSwicth){
			  			
			if(socket == null)
			{
				try {
					
					socket = new Socket(InetAddress.getByName(ipAddress),DEFAULT_PORT);
					dos = new DataOutputStream(socket.getOutputStream());	
					dis = new DataInputStream(socket.getInputStream());
					
					 // new ReceiveThread().start();
												
					} catch (IOException e) {				
						e.printStackTrace();
					}
				
			}else{
				
				if(sendSwicth){
					
					if(!busy){
						
						busy = true;
						try {
							dos.write(sendbytes);
							dos.flush();
							sendbytes = null;													
							busy = false;
							sendSwicth = false;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}   
				}				
			}		
		}
		
		
			
		
			
		}
			
	
	static class ReceiveThread extends Thread {
    	@Override
    	public void run() {
    		
    		while(socketSwicth){
    			
    			
    		}
    		
    	}
    }
  	  
}


