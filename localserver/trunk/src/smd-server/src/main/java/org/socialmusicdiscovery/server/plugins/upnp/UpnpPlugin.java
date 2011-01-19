package org.socialmusicdiscovery.server.plugins.upnp;

import java.io.IOException;


import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.DeviceIdentity;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.LocalService;
import org.teleal.cling.model.meta.ManufacturerDetails;
import org.teleal.cling.model.meta.ModelDetails;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.support.connectionmanager.ConnectionManagerService;

public class UpnpPlugin extends AbstractPlugin implements Runnable {
	
	final UpnpService upnpService =  new UpnpServiceImpl();
	
	@Override
	public boolean start() throws PluginException {

		System.err.println("UPnP plugin: starting");

		Thread serverThread = new Thread(this);
		serverThread.setDaemon(false);
		serverThread.start();

		System.err.println("UPnP plugin started");
		return true;
	}

	@Override
	public void stop() throws PluginException {
		upnpService.shutdown();
		System.err.println("UPnP plugin was stopped");
		super.stop();
	}

	public void run() {
		// Logger.getLogger(org.teleal.cling.transport.spi.DatagramIO.class.getPackage().getName()).setLevel(Level.FINEST);
		try {

//			Runtime.getRuntime().addShutdownHook(new Thread() {
//				@Override
//				public void run() {
//					upnpService.shutdown();
//				}
//			});

			// Add the bound local device to the registry
			LocalDevice ld = createDevice();
			System.err.println("XXXXXXXXXXXXXXXXX Local device Udn: "
					+ ld.getRoot().getIdentity().getUdn());
			upnpService.getRegistry().addDevice(ld);
		} catch (Exception ex) {
			System.err.println("Exception occured: " + ex);
			ex.printStackTrace(System.err);
			System.exit(1);
		}
	}

	LocalDevice createDevice() throws ValidationException,
			LocalServiceBindingException, IOException {

		DeviceIdentity identity = new DeviceIdentity(
				UDN.uniqueSystemIdentifier("SMD UPnP Plugin"));

		DeviceType type = new UDADeviceType("MediaServer", 1);

		DeviceDetails details = new DeviceDetails("SMD Media Server",
				new ManufacturerDetails("SMD Project - vrobin"), new ModelDetails(
						"SMD-UPnP-CD-Proto", "Prototype of UPnP ContentDirectory for SocialMusicDirectory Server",
						"v0.0.1alpha"));

		// Icon icon = new Icon("image/png", 48, 48, 8, getClass().getResource(
		// "icon.png"));

		LocalService<ContentDirectory> testClingContentDirectory = new AnnotationLocalServiceBinder()
				.read(ContentDirectory.class);

		testClingContentDirectory.setManager(new DefaultServiceManager(
				testClingContentDirectory, ContentDirectory.class));

		LocalService<ConnectionManagerService> service = new AnnotationLocalServiceBinder()
				.read(ConnectionManagerService.class);

		service.setManager(new DefaultServiceManager<ConnectionManagerService>(
				service, ConnectionManagerService.class));

		LocalService[] lsa = new LocalService[2];
		lsa[0] = testClingContentDirectory;
		lsa[1] = service;
		return new LocalDevice(identity, type, details, // icon,
				lsa);
	}
}
