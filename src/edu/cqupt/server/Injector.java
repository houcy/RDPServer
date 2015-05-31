package edu.cqupt.server;

import java.io.DataOutputStream;

public class Injector {
	

	private static final int Home = 600;
	private static final int Menu =350;
	private static final int Back = 850;

	public void EventInject(String code) {
			
		 Process su = null;        
          DataOutputStream outputStream = null;
          
  
          try {
              su = Runtime.getRuntime().exec("su");
               	              
              outputStream = new DataOutputStream(su.getOutputStream());
              outputStream.writeBytes(code);
              outputStream.flush();
	          outputStream.writeBytes("exit\n");
	          outputStream.flush();
	          su.waitFor();		 
	          outputStream.close();

          } catch (Exception e) {
              e.printStackTrace();
          }
          
	}
	
	public String getTpdCmd(int id){
		
		String cmd =  
				"sendevent /dev/input/event3 3 48 100\n" +
		   		 "sendevent /dev/input/event3 1 330 1\n" +					 
		   		 "sendevent /dev/input/event3 3 53 " +  id + "\n" +
		   		 "sendevent /dev/input/event3 3 54 2080\n" +
		   		 "sendevent /dev/input/event3 0 2 0\n" +
		   		 "sendevent /dev/input/event3 0 0 0\n" +
		   		 "sendevent /dev/input/event3 1 330 0\n" +
		   		 "sendevent /dev/input/event3 0 2 0\n" +
		   		 "sendevent /dev/input/event3 0 0 0\n" +
		   		 "sendevent /dev/input/event3 0 0 0\n";
		
		return cmd;
	}
	
	public String getHomeCmd(){		
		return getTpdCmd(Home);
	}
	
	public String getMenuCmd(){		
		return getTpdCmd(Menu);
	}
	
	public String getBackCmd(){		
		return getTpdCmd(Back);
	}
	
		
	private  final int VolumeUp = 115;
	private  final int VolumeDown = 114;
	private  final int Switch = 116;
		
	public String getKpdCmd(int id){
		
		String cmd =
			"sendevent /dev/input/event2 1 " + id +" 1\n" +
			"sendevent /dev/input/event2 0 0 0\n" +
			"sendevent /dev/input/event2 0 0 0\n" +
			"sendevent /dev/input/event2 1 " + id +" 0\n" +
			 "sendevent /dev/input/event2 0 0 0\n" +
			 "sendevent /dev/input/event2 0 0 0\n";
		return cmd;
	}

	public String getVolumeUpCmd(){		
		return getKpdCmd(VolumeUp);
	}
	
	public String getVolumeDownCmd(){		
		return getKpdCmd(VolumeDown);
	}
	
	public String getSwitchCmd(){		
		return getKpdCmd(Switch);
	}
	
}

