package org.medee.playground.upnp.didllite;

import java.io.StringWriter;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.medee.playground.upnp.didllite.jaxb.ObjectFactory;
import org.medee.playground.upnp.didllite.jaxb.RootType;

public class DidlLiteDocument {
	private static JAXBContext jaxbContext;
	

	private static Marshaller marshaller;
	private static ObjectFactory factory;
	private static StringWriter mySw;
	
	
	private RootType root;
	private String id;
	private Vector<AllowedUnderDIDLLite> elements;
	
	protected static ObjectFactory getObjectFactory() {
		return factory;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	static {
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

	public DidlLiteDocument() {
		super();
		root = factory.createRootType();
		elements = new Vector<AllowedUnderDIDLLite>();
		this.setId("0");
	}

	public DidlLiteDocument addElement(AllowedUnderDIDLLite element) {
		elements.add(element);
		element.setParentId(this.getId());
		root.getAllowedUnderDIDLLite().add(element.getElement());
		return this;
	}
	
	public Container addNewContainer() {
		Container c = new Container();
		addElement(c);
		return c;
	}
	
	public Item addNewItem() {
		Item i = new Item();
		addElement(i);
		return i;
	}
	
	synchronized public String toString() {
		try {
			mySw.getBuffer().setLength(0);
			marshaller.marshal(factory.createDIDLLite(root), mySw);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
		return(mySw.toString());
	}
}
