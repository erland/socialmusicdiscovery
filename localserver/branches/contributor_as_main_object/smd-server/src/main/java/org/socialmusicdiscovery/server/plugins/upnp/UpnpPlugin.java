/*
 *  Copyright 2010-2011, Social Music Discovery project
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of Social Music Discovery project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL SOCIAL MUSIC DISCOVERY PROJECT BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.socialmusicdiscovery.server.plugins.upnp;

import org.socialmusicdiscovery.server.api.plugin.AbstractPlugin;
import org.socialmusicdiscovery.server.api.plugin.PluginException;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.binding.LocalServiceBindingException;
import org.teleal.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.teleal.cling.model.DefaultServiceManager;
import org.teleal.cling.model.ValidationException;
import org.teleal.cling.model.meta.*;
import org.teleal.cling.model.types.DeviceType;
import org.teleal.cling.model.types.UDADeviceType;
import org.teleal.cling.model.types.UDN;
import org.teleal.cling.support.connectionmanager.ConnectionManagerService;

import java.io.IOException;

public class UpnpPlugin extends AbstractPlugin implements Runnable {
	
	UpnpService upnpService;
	
	@Override
	public boolean start() throws PluginException {

		System.err.println("UPnP plugin: starting");
        upnpService = new UpnpServiceImpl();
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
