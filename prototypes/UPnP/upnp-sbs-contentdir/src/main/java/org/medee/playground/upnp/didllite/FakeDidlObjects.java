package org.medee.playground.upnp.didllite;


import java.io.StringWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URLConnection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


import org.medee.playground.upnp.didllite.jaxb.*;

public class FakeDidlObjects {

	JAXBContext jaxbContext;
	Marshaller marshaller;
	ObjectFactory factory;
	StringWriter mySw;
	
	public FakeDidlObjects() {
		super();
		try {
			factory = new ObjectFactory();
			jaxbContext = JAXBContext.newInstance("org.medee.playground.upnp.didllite.jaxb");
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			mySw = new StringWriter();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized String getSingleContainer() {
		RootType root = factory.createRootType();
		ContainerType container = factory.createContainerType();
//		ClassType classType = factory.createClassType();
//		ElementType eltType = factory.createElementType();
//
//		root.getAllowedUnderDIDLLite().add(container);
//		classType.setValue("object.container");
//		eltType.setValue("Fake container - "+System.currentTimeMillis());
//		container.setId("1");
//		container.setParentID("0");
//		container.setRestricted(true);
//		container.setClazz(classType);
//		container.setTitle(eltType);


		root.getAllowedUnderDIDLLite().add(container);
		container.setClazz(factory.createClassType());
		container.getClazz().setValue("object.container");
		container.setTitle(factory.createElementType());
		container.getTitle().setValue("Fake container - "+System.currentTimeMillis());
		container.setId("1");
		container.setParentID("0");
		container.setRestricted(true);

		try {
			mySw.getBuffer().setLength(0);
			marshaller.marshal(factory.createDIDLLite(root), mySw);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return(mySw.toString());
	}

}
