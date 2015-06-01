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
	 
	 onReceiveCallback  receiveCallback;
	 
	 private static boolean  busy= false;	 
	 private static boolean  sendSwicth = false; 
	 private static boolean  socketSwicth = true ;
	 
	public SocketThread(String ip, onReceiveCallback  receiveCallback){	
		ipAddress = ip;
		this.receiveCallback = receiveCallback;
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
			e.printStackTrace();
		}
			
	}
	
	 public static interface onReceiveCallback {
	        public void onRequsetCallback(String cmd);
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
				
	 class ReceiveThread extends Thread {
    	@Override
    	public void run() {
    		
    		while(socketSwicth){	
    			
				try {
					int size = dis.available();
					byte[] data = new byte[size];  
					dis.read(data);
					String cmd = data.toString();
	    			receiveCallback.onRequsetCallback(cmd) ;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
				
    		}
    		
    	}
    }
  	  
}


