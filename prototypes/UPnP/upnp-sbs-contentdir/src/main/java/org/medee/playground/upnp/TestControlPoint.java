package org.medee.playground.upnp;

import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.DeviceList;
import org.cybergarage.upnp.UPnPStatus;

public class TestControlPoint {
	
	ControlPoint ctrlPoint = null;

	class Periodic implements Runnable {

//		    	ControlPoint ctrlPoint=null;
//		    	
//		    	public void setCtrlPoint(ControlPoint  ctrlPoint) {
//		    		this.ctrlPoint=ctrlPoint;
//		    	}
		    	
		    	public void periodic() {
		    		DeviceList rootDevList = ctrlPoint.getDeviceList();
		    		int nRootDevs = rootDevList.size();
		    		System.out.println("===========================================");
		    		for (int n=0; n<nRootDevs; n++) {
		    			Device dev = rootDevList.getDevice(n);
		    			String devName = dev.getFriendlyName();
		    			System.out.println("[" + n + "] = " + devName +" " + dev.getUDN() 
		    					+ " " + dev.getDeviceType());
		    			// if we find a lamp
		    			if("urn:schemas-upnp-org:device:BinaryLight:1".equals(dev.getDeviceType())) {
		    				this.toggleLampStatus(dev);
		    			}
		    		}		    		
		    	}
		    	
		    	public void toggleLampStatus(Device dev) {
    				
    				System.out.println("Found the light! "+ dev.getUDN());
    				Action llsAct = dev.getAction("GetStatus");
    				//llsAct.setArgumentValue(“time”, newTime);
    				
    				// get its status
    				if (llsAct.postControlAction() == true) {
    					ArgumentList outArgList = llsAct.getOutputArgumentList();
    					int nOutArgs = outArgList.size();
    					boolean isLightOn = false;
    					
    					// find correct answer
    					for (int i=0; i<nOutArgs; i++) {
	    					Argument outArg = outArgList.getArgument(i);
	    					String name = outArg.getName();
	    					String value = outArg.getValue();
	    					System.out.println("Name: " + name +" Value: "+value);
    					}

    					isLightOn = ("1".equals(outArgList.getArgument("ResultStatus").getValue()));
    					System.out.println("Light is " + (isLightOn?"on":"off") );
						Action setTargetAct = dev.getAction("SetTarget");
						setTargetAct.setArgumentValue("newTargetValue", (isLightOn?"0":"1"));
						setTargetAct.postControlAction();
    				} else {
    					UPnPStatus err = llsAct.getStatus();
    					System.err.println("Error Code = " + err.getCode());
    					System.err.println("Error Desc = " + err.getDescription());
    					}
		    		
		    	}
		    	
		    	public void run()
			    {
		    		System.out.println("Thread started");
		    		ctrlPoint.search();
		    		while(true) {
		    			try {
		    				if(ctrlPoint!=null) {
		    					this.periodic();
		    				}
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		    		}
		    		
			    }
		    }


//	class periodicJob {
//	( new Thread() {
//
//		public void run() {
//
//		for(;;) System.out.println("Stop the world!");
//
//		}
//
//		}
//
//		).start();
//	}
//	
	public TestControlPoint() {
		super();
		this.ctrlPoint = new ControlPoint();
		this.ctrlPoint.start();
		Periodic p = new Periodic();
//		p.setCtrlPoint(ctrlPoint);
		new Thread(p).start();
	}


	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		//org.cybergarage.net.HostInterface.setInterface("127.0.0.1");
//		org.cybergarage.net.HostInterface.setInterface("192.168.1.2");
		TestControlPoint cp = new TestControlPoint();
		Thread.sleep(10000);

	}

}
