package edu.cqupt.server;

import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;


public class Server extends Activity {
	
	// private static final String     TAG = Server.class.getName();
	private static final int        REQUEST_CODE= 100;
    private static MediaProjection  MEDIA_PROJECTION;

    
    private MediaProjectionManager  mProjectionManager;
	private ImageReader             mImageReader;
	private Handler                 mHandler;
    private int                     mWidth = 360;
    private int                     mHeight =480;
    
    ImageView imageView;	
    
    static SocketThread  socketThread;
    
    public static String ip;

    static TextView state;
    static  Bitmap bitmap = null;
   
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
	    
	    imageView = (ImageView)findViewById(R.id.imageView);
	    state = (TextView) findViewById(R.id.status);
	    
	    new UdpServer().start();
	       
	  
	    // start capture handling thread
	    new Thread() {
	    	@Override
	    	public void run() {
	    		Looper.prepare();
	    		mHandler = new Handler();
	    		Looper.loop();
	    	}
	    }.start();
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
        	
            Image image = null;        
            bitmap = null;

            try {
                image = mImageReader.acquireLatestImage();
                
                
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    // create bitmap
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                              
                      imageView.setImageBitmap(bitmap);
                              
                      ByteArrayOutputStream baos = new ByteArrayOutputStream();
                      bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                      
                      byte[]  bytes =  baos.toByteArray(); 
                   
                    if(!socketThread.isBusy()){
                    	  socketThread.send(bytes);
                      }

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                          
                if (bitmap!=null) {
                    bitmap.recycle();
                }
                if (image!=null) {
                    image.close();
                }
                
              
            }
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == REQUEST_CODE) {
    		MEDIA_PROJECTION = mProjectionManager.getMediaProjection(resultCode, data);
    	
				DisplayMetrics metrics = getResources().getDisplayMetrics();
				int density = metrics.densityDpi;
				int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
				//Display display = getWindowManager().getDefaultDisplay();
			//	Point size = new Point();
			//	display.getSize(size);
			//	mWidth = size.x;
			//	mHeight = size.y;

				mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
                MEDIA_PROJECTION.createVirtualDisplay("screencap", mWidth, mHeight, density, flags, mImageReader.getSurface(), null, mHandler);
 
				mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    		}
    	}
   // }
    
    
    public class UdpServer extends Thread{
    	
    	 DatagramSocket udpSocket = null;
    	 DatagramPacket udpPacket = null;
    	
    	public static final int UDP_PORT = 2011;
    	
    	String ip = null;
    	
    	public void run() {
    		
    		byte[] data = new byte[256];
    		try {
    			udpSocket = new DatagramSocket(UDP_PORT);
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
    				 
    				 state.post( new Runnable() {							
							@Override
							public void run() {
								state.append("Receive UDP BroadCast From IP:"+ip+"\n");
								
							}
						});
    				 
    				 socketThread = new SocketThread(ip);
    				 socketThread.start();   	   				 
    				 startProjection();
    			
    				 udpSocket.close();
    				 break ;
    			}
    		}

    	}
    }
    
   
    
    private void startProjection() {
    	startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
    }
    
    private void stopProjection() {
    	mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(MEDIA_PROJECTION != null) MEDIA_PROJECTION.stop();
            }
    	});
    }
}

