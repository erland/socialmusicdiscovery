package org.medee.playground.upnp;

import org.cybergarage.upnp.*;
import org.cybergarage.upnp.device.*;

public class TestApi {

	/**
	 * @param args
	 */
	String descriptionFileName = "src/main/resources/description/MediaServer1.xml";
	//String descriptionFileName2 = "src/main/resources/ContentDirectory1.xml";
	Device upnpDev;

	void startDevice() {
		try {
			upnpDev = new TestDevice(descriptionFileName);
			// upnpDev.loadDescription(descriptionFileName2);
			upnpDev.setFriendlyName("UPNP RV TEST DEVICE");

			//upnpDev.setUDN("uuid:abababab-4429-4e95-8725-bbbbbbbbbbbb");
			//upnpDev.getRootNode().addAttribute("aze", "QSD");
			upnpDev.getRootNode().addAttribute("xmlns", "urn:schemas-upnp-org:device-1-0");
//			Service toto = upnpDev.getService("urn:schemas-upnp-org:service:ConnectionManager:1");
//			Object tutu = toto.getActionList();
//			System.err.println(tutu);
			upnpDev.start();
		} catch (InvalidDescriptionException e) {
			String errMsg = e.getMessage();
			System.out.println("InvalidDescriptionException = " + errMsg);
		}
	}

	public void printDevice(Device dev) {
		String devName = dev.getFriendlyName();
		System.out.println(devName);
		DeviceList childDevList = dev.getDeviceList();
		int nChildDevs = childDevList.size();
		if(nChildDevs == 0) {
			System.out.println("device has no child devices");
		}
		for (int n = 0; n < nChildDevs; n++) {
			Device childDev = childDevList.getDevice(n);
			printDevice(childDev);
		}
	}

	public void printServices(Device dev) {
		ServiceList serviceList = dev.getServiceList();
		int serviceCnt = serviceList.size();
		for (int n = 0; n < serviceCnt; n++) {
			Service service = serviceList.getService(n);
			System.out.println("Service type: " + service.getServiceType());
			ActionList actionList = service.getActionList();
			int actionCnt = actionList.size();
			for (int i = 0; i < actionCnt; i++) {
				Action action = actionList.getAction(i);
				System.out.println("action [" + i + "] = " + action.getName());
			}
//			ServiceStateTable stateTable = service.getServiceStateTable();
//			int varCnt = stateTable.size();
//			for (int i = 0; i < actionCnt; i++) {
//				StateVariable stateVar = stateTable.getServiceStateVariable(i);
//				System.out.println("stateVar [" + i + "] = "
//						+ stateVar.getName());
//			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestApi ta = new TestApi();
		org.cybergarage.util.Debug.off();
		org.cybergarage.net.HostInterface.setInterface("192.168.1.2");
		//org.cybergarage.net.HostInterface.setInterface("127.0.0.1");
		ta.startDevice();
		ta.printDevice(ta.upnpDev);
		ta.printServices(ta.upnpDev);
	}

}
