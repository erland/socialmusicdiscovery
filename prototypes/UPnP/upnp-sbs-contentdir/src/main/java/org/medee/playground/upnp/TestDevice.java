package org.medee.playground.upnp;





import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.Device;
import org.cybergarage.upnp.Service;
import org.cybergarage.upnp.ServiceList;
import org.cybergarage.upnp.StateVariable;
import org.cybergarage.upnp.control.ActionListener;
import org.cybergarage.upnp.control.QueryListener;
import org.cybergarage.upnp.device.InvalidDescriptionException;
import org.medee.playground.sbs.Contributor;
import org.medee.playground.sbs.DatabaseConnector;
import org.medee.playground.upnp.didllite.AllowedUnderDIDLLite;
import org.medee.playground.upnp.didllite.DidlLiteDocument;
import org.medee.playground.upnp.didllite.FakeDidlObjects;

public class TestDevice extends Device implements ActionListener, QueryListener {

	private static final String ARTIST = "Artist";

	// Browse Action       
	public final static String BROWSE = "Browse";
	
	public final static String SEARCH = "Search";
	
	public final static String GET_SEARCH_CAPABILITIES = "GetSearchCapabilities";
	public final static String SEARCH_CAPS = "SearchCaps";
	
	public final static String GET_SORT_CAPABILITIES = "GetSortCapabilities";
	public final static String SORT_CAPS = "SortCaps";

	public final static String GET_PROTOCOL_INFO= "GetProtocolInfo";
	public final static String SOURCE = "Source";

	
	public final static String GET_SYSTEM_UPDATE_ID = "GetSystemUpdateID";
	public final static String ID = "Id";
	
	public final static String SYSTEM_UPDATE_ID = "SystemUpdateID";
	
	public final static String CONTENT_EXPORT_URI = "/ExportContent";
	public final static String CONTENT_IMPORT_URI = "/ImportContent";
	public final static String CONTENT_ID = "id";

	private FakeDidlObjects fdo;
	private DatabaseConnector dc;
	
	private String createUUID() {
		// Create device uuid unique for computer/user
		String hostname = System.getenv().get("HOSTNAME");
		if (hostname==null || hostname.trim().equals(""))
			hostname = System.getenv("COMPUTERNAME");

		String username = System.getenv().get("USER");
		if (username==null || username.trim().equals(""))
			username = System.getenv("USERNAME");	

		return("uuid:" + 
			java.util.UUID.nameUUIDFromBytes(
				(hostname+username).getBytes() ));
	}

	public TestDevice(String descriptionFileName)
			throws InvalidDescriptionException {
		super(descriptionFileName);
		fdo = new FakeDidlObjects();
		dc = new DatabaseConnector();
		this.setUDN(this.createUUID());
		
		setActionListener(this);
		setQueryListener(this);
		
//		Action getSearchCapabilitiesAction = getAction(GET_SEARCH_CAPABILITIES);
//		getSearchCapabilitiesAction.setActionListener(this);
//		StateVariable searchCriteria = getStateVariable("A_ARG_TYPE_SearchCriteria");
//		searchCriteria.setQueryListener(this);
		
		ServiceList serviceList = this.getServiceList();
		int serviceCnt = serviceList.size();
		for (int n = 0; n < serviceCnt; n++) {
			Service service = serviceList.getService(n);
			System.out.println("Registering listener for service type: " + service.getServiceType());
			service.setActionListener(this);
			service.setQueryListener(this);
		}
	}

	public boolean queryControlReceived(StateVariable stateVar) {
		System.out.println("XXXXXX: queryControlReceived");
		return true;
	}

	
	private boolean handleGetProtocolInfo(Action gpiAction) {
		Argument sourceArg = gpiAction.getArgument(SOURCE);
		sourceArg.setValue("http-get:*:audio/mpeg:DLNA.ORG_PN=MP3,	http-get:*:audio/mpeg:DLNA.ORG_PN=MP2,	http-get:*:audio/x-ms-wma:DLNA.ORG_PN=WMABASE,	http-get:*:audio/mp4:DLNA.ORG_PN=AAC_ISO,	http-get:*:audio/x-flac:*,	http-get:*:audio/x-aiff:*,	http-get:*:audio/x-ogg:*,	http-get:*:audio/wav:*,	http-get:*:audio/x-ape:*,	http-get:*:audio/x-m4a:*,	http-get:*:audio/x-m4b:*,	http-get:*:audio/x-wavpack:*,	http-get:*:audio/x-musepack:*,	http-get:*:audio/basic:*,	http-get:*:audio/L16;rate=11025;channels=2:DLNA.ORG_PN=LPCM,	http-get:*:audio/L16;rate=22050;channels=2:DLNA.ORG_PN=LPCM,	http-get:*:audio/L16;rate=44100;channels=2:DLNA.ORG_PN=LPCM,	http-get:*:audio/L16;rate=48000;channels=2:DLNA.ORG_PN=LPCM");
		return(true);
	}
	
	private boolean handleGetSearchCapabilities(Action gscAction) {
		Argument searchCapsArg = gscAction.getArgument(SEARCH_CAPS);
		searchCapsArg.setValue("@id,@refID,dc:title,upnp:class,upnp:genre,upnp:artist,upnp:author,upnp:author@role,upnp:album,dc:creator,res@size,res@duration,res@protocolInfo,res@protection,dc:publisher,dc:language,upnp:originalTrackNumber,dc:date,upnp:producer,upnp:rating,upnp:actor,upnp:director,upnp:toc,dc:description,microsoft:userRatingInStars,microsoft:userEffectiveRatingInStars,microsoft:userRating,microsoft:userEffectiveRating,microsoft:serviceProvider,microsoft:artistAlbumArtist,microsoft:artistPerformer,microsoft:artistConductor,microsoft:authorComposer,microsoft:authorOriginalLyricist,microsoft:authorWriter,upnp:userAnnotation,upnp:channelName,upnp:longDescription,upnp:programTitle");
		return(true);
	}

	private boolean handleGetSortCapabilities(Action gscAction) {
		Argument sortCapsArg = gscAction.getArgument(SORT_CAPS);
		sortCapsArg.setValue("dc:title,upnp:genre,upnp:album,dc:creator,res@size,res@duration,res@bitrate,dc:publisher,dc:language,upnp:originalTrackNumber,dc:date,upnp:producer,upnp:rating,upnp:actor,upnp:director,upnp:toc,dc:description,microsoft:year,microsoft:userRatingInStars,microsoft:userEffectiveRatingInStars,microsoft:userRating,microsoft:userEffectiveRating,microsoft:serviceProvider,microsoft:artistAlbumArtist,microsoft:artistPerformer,microsoft:artistConductor,microsoft:authorComposer,microsoft:authorOriginalLyricist,microsoft:authorWriter,microsoft:sourceUrl,upnp:userAnnotation,upnp:channelName,upnp:longDescription,upnp:programTitle");
		return(true);
	}
	
	private boolean handleBrowse(BrowseAction browseAction) {
		if (browseAction.isMetadata() == true)
			return browseMetadataActionReceived(browseAction);
		if (browseAction.isDirectChildren() == true)
			return browseDirectChildrenActionReceived(browseAction);
		return false;
	}
	
	private boolean browseMetadataActionReceived(BrowseAction action) {
		String objID =action.getObjectID();
		
		System.out.println("BrowseMD action on object: "+objID);
		if("0".equals(objID)) {
			action.setResult(this.getRootMD());
			action.setNumberReturned(1);
			action.setTotalMaches(1);
		}
		action.setUpdateID(0);
		return true;
	}

	private boolean browseDirectChildrenActionReceived(BrowseAction action) {
		String objID =action.getObjectID();
		int startingIndex = action.getStartingIndex();
		int requestedCount = action.getRequestedCount();
		
		System.out.println("BrowseDC action on object: "+objID);
		action.setNumberReturned(1);
		action.setTotalMaches(1);
		action.setUpdateID(0);
		
		if("0".equals(objID)) {
			action.setResult(this.getRootDC());
			action.setTotalMaches(4);
			action.setNumberReturned(4);
		} else if (ARTIST.equals(objID)) {
			action.setResult(this.getArtistDC());
			action.setTotalMaches(getArtistDCCount());
			action.setNumberReturned(getArtistDCCount());
		} else {
			action.setResult(fdo.getSingleContainer());
		}
		return true;
	}

	private String getRootMD(){
		DidlLiteDocument dld = new DidlLiteDocument();
		AllowedUnderDIDLLite c;

		c = dld.addNewContainer();
		c.setClazz("object.container");
		c.setTitle("Root");
		c.setId("0");
		c.setParentId("-1");
		
		System.out.println(dld.toString());
		return dld.toString();
	}
	
	private int getArtistDCCount() {
		return dc.getContributorsVector().size();
	}
	
	private String getArtistDC() {
		DidlLiteDocument dld = new DidlLiteDocument();
		AllowedUnderDIDLLite a;
		
		for(Contributor c: dc.getContributorsVector()) {
			a = dld.addNewContainer();
			a.setClazz("object.container.person.musicArtist");
			a.setTitle(c.getName());
			a.setId(Integer.toString(c.getId()));
			a.setParentId(ARTIST);
		}
		System.out.println(dld.toString());
		return dld.toString();
	}
	
	private String getRootDC() {
		DidlLiteDocument dld = new DidlLiteDocument();
		String[] rootNodesText = {ARTIST, "Album", "Genre", "Years" };
		//int[][] twoDimArray = { {1,2,3}, {4,5,6}, {7,8,9} };

		AllowedUnderDIDLLite c;

		for(String nodeText: rootNodesText) {
			c = dld.addNewContainer();
			c.setClazz("object.container.musicContainer");
			c.setTitle(nodeText);
			c.setId(nodeText);
		}
		
		System.out.println(dld.toString());
		return dld.toString();
	}
	
	public boolean actionControlReceived(Action action) {
		boolean ret=false;
		String actionName = action.getName(); 

		System.out.println("XXXXXX: actionControlReceived  ActionName: "+ actionName);

		if (actionName.equals(BROWSE) == true) {
			ret = handleBrowse(new BrowseAction(action));
		} 
		if (actionName.equals(GET_SORT_CAPABILITIES) == true) {
			ret = handleGetSortCapabilities(action);
		} 
		if (actionName.equals(GET_SEARCH_CAPABILITIES) == true) {
			ret = handleGetSearchCapabilities(action);
		}
		if (actionName.equals(GET_PROTOCOL_INFO) == true) {
			ret = handleGetProtocolInfo(action);
		} 
		

		//al.
		//action.setOutArgumentValues(new ArgumentList().)
		return ret;
	}

}
