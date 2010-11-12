package org.medee.playground.upnp.didllite;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class DidlLiteDocumentTest {

	private static final String ITEM_AUDIO_TRACK = "item.audio.track";
	private static final String CONTAINER_ALBUM = "container.album";
	DidlLiteDocument dld;
	
	@Before
	public void init() {
		dld = new DidlLiteDocument();
	}
	
	@After
	public void clean() {
		dld = null;
	}
	
	@Test
	public void serializeWorks() {
		AllowedUnderDIDLLite i = dld.addNewItem();
		AllowedUnderDIDLLite c = dld.addNewContainer();

		i.setClazz(ITEM_AUDIO_TRACK);
		i.setTitle("this is track title");
		c.setClazz(CONTAINER_ALBUM);
		c.setTitle("this is album title");
		System.out.println(dld.toString());
	}
	
	@Test
	public void dldFactoryNotNull() {
		assertNotNull(DidlLiteDocument.getObjectFactory());
	}
	
	@Test
	public void dldNotNull() {
		assertNotNull(dld);
	}
	
	@Test
	public void addNewContainerReturnsContainer() {
		Container c = dld.addNewContainer();
		assertEquals(c.getClass(), org.medee.playground.upnp.didllite.Container.class);
		AllowedUnderDIDLLite a = dld.addNewContainer();
		assertEquals(a.getClass(), org.medee.playground.upnp.didllite.Container.class);
	}


	@Test
	public void addNewItemReturnsItem() {
		Item c = dld.addNewItem();
		assertEquals(c.getClass(), org.medee.playground.upnp.didllite.Item.class);
		AllowedUnderDIDLLite a = dld.addNewItem();
		assertEquals(a.getClass(), org.medee.playground.upnp.didllite.Item.class);

	}

	
	@Test
	public void itemClazzAccessors() {
		AllowedUnderDIDLLite c = dld.addNewItem();
		c.setClazz(ITEM_AUDIO_TRACK);
		assertEquals(ITEM_AUDIO_TRACK, c.getClazz());
		Item i = dld.addNewItem();
		i.setClazz(ITEM_AUDIO_TRACK);
		assertEquals(ITEM_AUDIO_TRACK, i.getClazz());
	}
	
	@Test
	public void containerClazzAccessors() {
		AllowedUnderDIDLLite a = dld.addNewContainer();
		a.setClazz(CONTAINER_ALBUM);
		assertEquals(CONTAINER_ALBUM, a.getClazz());
		
		Container c = dld.addNewContainer();
		c.setClazz(CONTAINER_ALBUM);
		assertEquals(CONTAINER_ALBUM, c.getClazz());
	}
	
	@Test
	public void itemTitleAccessors() {
		AllowedUnderDIDLLite c = dld.addNewItem();
		c.setTitle(ITEM_AUDIO_TRACK);
		assertEquals(ITEM_AUDIO_TRACK, c.getTitle());
		Item i = dld.addNewItem();
		i.setTitle(ITEM_AUDIO_TRACK);
		assertEquals(ITEM_AUDIO_TRACK, i.getTitle());
	}
	
	@Test
	public void containerTitleAccessors() {
		AllowedUnderDIDLLite a = dld.addNewContainer();
		a.setTitle(CONTAINER_ALBUM);
		assertEquals(CONTAINER_ALBUM, a.getTitle());
		
		Container c = dld.addNewContainer();
		c.setTitle(CONTAINER_ALBUM);
		assertEquals(CONTAINER_ALBUM, c.getTitle());
	}
}
