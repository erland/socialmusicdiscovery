package org.medee.playground.upnp.didllite;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.medee.playground.upnp.didllite.jaxb.*;
import org.junit.Test;


public class DidlLiteJaxbTest {
	
	@Test
	public void testSetInterface() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance("org.medee.playground.upnp.didllite.jaxb");
		Marshaller marshaller = jaxbContext.createMarshaller();
		
		// remove following to create xml header
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		ObjectFactory factory = new ObjectFactory();
		RootType root = factory.createRootType();
		ItemType item = factory.createItemType();
		

		item.setClazz(factory.createClassType());
		item.getClazz().setName("maClasseName");
		item.getClazz().setValue("maClasseValue");
		root.setLang("test");
		root.getAllowedUnderDIDLLite().add(item);
		item.setRestricted(true);
		item.setTitle(factory.createElementType());
		item.getTitle().setValue("My object");
		DescType dt = new DescType();
		dt.setId("8");
		dt.setType("AZEZEZEZEZ");
		dt.setAny(  new JAXBElement(
				new QName("urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/","privateObject"),
					String.class, "TURLUTUTU")) ;
		item.getAllowedUnderItem().add( new JAXBElement<DescType> (
				new QName("urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/","desc"),
						DescType.class, dt));
		item.getAllowedUnderItemBis().add( new JAXBElement(
				new QName("urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/","desc"),String.class, "QWERTY"));

		root.getAllowedUnderDIDLLite().add(item);
		root.getAllowedUnderDIDLLite().add("Au secours");
		List tata = root.getAllowedUnderDIDLLite();
		StringWriter mySw = new StringWriter();
		

		
		marshaller.marshal(factory.createDIDLLite(root), mySw);
		System.out.println(mySw);
		assertTrue(true);
	}
	
	@Test
	public void testGetSingleContainer() throws JAXBException {
		FakeDidlObjects fdo = new FakeDidlObjects();
		System.out.println(fdo.getSingleContainer());
	}
}
