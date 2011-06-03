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

import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.service.browse.LibraryBrowseService;
import org.socialmusicdiscovery.server.business.service.browse.Result;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem;
import org.teleal.cling.binding.annotations.*;
import org.teleal.cling.model.message.header.UpnpHeader;
import org.teleal.cling.model.types.ErrorCode;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.csv.CSV;
import org.teleal.cling.protocol.sync.ReceivingAction;
import org.teleal.cling.support.contentdirectory.AbstractContentDirectoryService;
import org.teleal.cling.support.contentdirectory.ContentDirectoryErrorCode;
import org.teleal.cling.support.contentdirectory.ContentDirectoryException;
import org.teleal.cling.support.contentdirectory.DIDLParser;
import org.teleal.cling.support.model.*;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.item.MusicTrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UpnpStateVariables({
    @UpnpStateVariable(
            name = "A_ARG_TYPE_ObjectID",
            sendEvents = false,
            datatype = "string"),
    @UpnpStateVariable(
            name = "A_ARG_TYPE_Result",
            sendEvents = false,
            datatype = "string"),
    @UpnpStateVariable(
            name = "A_ARG_TYPE_BrowseFlag",
            sendEvents = false,
            datatype = "string",
            allowedValuesEnum = BrowseFlag.class),
	@UpnpStateVariable(
            name = "A_ARG_TYPE_SearchCriteria",
            sendEvents = false,
            datatype = "string"),
    @UpnpStateVariable(
            name = "A_ARG_TYPE_Filter",
            sendEvents = false,
            datatype = "string"),
    @UpnpStateVariable(
            name = "A_ARG_TYPE_ContainerID",
            sendEvents = false,
            datatype = "string"),
    @UpnpStateVariable(
            name = "A_ARG_TYPE_SortCriteria",
            sendEvents = false,
            datatype = "string"),
    @UpnpStateVariable(
            name = "A_ARG_TYPE_Index",
            sendEvents = false,
            datatype = "ui4"),
    @UpnpStateVariable(
            name = "A_ARG_TYPE_Count",
            sendEvents = false,
            datatype = "ui4"),
    @UpnpStateVariable(
            name = "A_ARG_TYPE_UpdateID",
            sendEvents = false,
            datatype = "ui4"),
    @UpnpStateVariable(
            name = "A_ARG_TYPE_URI",
            sendEvents = false,
            datatype = "uri")
})
public class ContentDirectory extends AbstractContentDirectoryService  {



	public ContentDirectory() {

		super(	// search caps
				Arrays.asList("dc:title", "upnp:class", "upnp:genre"),
				// sort caps
				Arrays.asList("dc:title", "upnp:author"));
		// TODO Auto-generated constructor stub
	}

	
	@Override
    public BrowseResult browse(String objectID, BrowseFlag browseFlag,
                               String filter,
                               long firstResult, long maxResults,
                               SortCriterion[] orderby) throws ContentDirectoryException {
        try {
    		System.err.println("Received: Browse  "+((browseFlag == BrowseFlag.DIRECT_CHILDREN)?"DC ":"MD ")+"objectid="+objectID+" filter="+ filter
    				+ " indice="+firstResult + " nbresults="+maxResults);
   
        	// WMP 12 Windows 7 64 family fr: Microsoft-Windows/6.1 UPnP/1.0 Windows-Media-Player/12.0.7600.16667_DLNADOC
        	// Foobar: 
        	String UA = null;
        	try {
        		UA = ReceivingAction.getRequestMessage().getHeaders().getFirstHeader(UpnpHeader.Type.USER_AGENT).getString();
        		System.err.println(">>>>>>>>>> RequestFrom agent: " + UA);
        	} catch (NullPointerException ex) {
        		System.err.println(">>>>>>>>>> RequestFrom agent: Anonymous");
        	}


        	if(browseFlag == BrowseFlag.DIRECT_CHILDREN ) {
        		return getSmdDirectChildrens(objectID, filter, firstResult, maxResults, orderby);

        	} else {
        		System.err.println("XXXXXXXXXXXXXX>> Browse called wihout direct children flag");
        	}


        } catch (Exception ex) {
            throw new ContentDirectoryException(
                    ContentDirectoryErrorCode.CANNOT_PROCESS,
                    ex.toString()
            );
        }
        throw new ContentDirectoryException(
                ContentDirectoryErrorCode.CANNOT_PROCESS
        );
    }

    public class SmdFolder extends Container {

        public SmdFolder(ResultItem<String> folderItem) {
            setId(folderItem.getId());
            
            // for performance reason, childCount should be null
            // unfortunately, cling doesn't permit it (yet)
            // setChildCount(null);
            // TODO: fill a bug report or submit a patch for cling
            // problem is null pointer exception in DIDLParser.java (line 337)
            // containerElement.setAttribute("childCount", Integer.toString(container.getChildCount()));
            // yet check how UPnP clients behave with containers without childCount attribute
            setRestricted(true);
            // item contained in result item is of type String
            setTitle(folderItem.getItem()); 
            setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container"));
        }
    }

    
    @SuppressWarnings("unchecked")
	private BrowseResult getSmdDirectChildrens (String objectID,
                               String filter,
                               long firstResult, long maxResults,
                               SortCriterion[] orderby) throws Exception {


		DIDLContent didl = new DIDLContent();
		LibraryBrowseService browseService = new LibraryBrowseService();
		
		String smdObjectID = null;
		Integer smdMaxItems;
		List<String> filters = this.filterStringToList(filter);
		
		// if objectID is 0, the client asks for root menu (null value for SMD)
		if(objectID.equals("0")) {
			smdObjectID = null; 
		}else {
			smdObjectID = objectID;
		}
		
		// if upnp maxResults is 0, there's no limit 
		if(maxResults==0) {
			smdMaxItems = null;
		} else { // items to return is limited
			smdMaxItems = new Integer((int)maxResults);
		}
		
		Result<Object> browsedContent = browseService.findChildren(smdObjectID, (int)firstResult, smdMaxItems, false);
		
		// Memo: DIDL object mandatory elements:
		// id, parentID, title, class, restricted (for 'object' base class)
		
		Container upnpContainer = null;
		MusicTrack upnpTrack= null;
		
		for(ResultItem<?> browsedItem: browsedContent.getItems()) {
			
			if(browsedItem.getType().equals("Folder")) {
				upnpContainer = new SmdFolder( (ResultItem<String>)browsedItem);
				upnpContainer.setChildCount(browseService.findChildren(browsedItem.getId(), null, null, false).getCount().intValue());

			} else if (browsedItem.getType().equals("Artist") ) {
				ResultItem<ArtistEntity> artistItem = (ResultItem<ArtistEntity>) browsedItem;
				upnpContainer = new org.teleal.cling.support.model.container.MusicArtist();
				upnpContainer.setId(objectID+"/"+artistItem.getId())
					.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container.person.musicArtist"))
					.setTitle(artistItem.getItem().getName());
				upnpContainer.setChildCount(browseService.findChildren(artistItem.getId(), null, null, false).getCount().intValue());

			} else if (browsedItem.getType().equals("Release") ) {
				ResultItem<ReleaseEntity> releaseItem = (ResultItem<ReleaseEntity>) browsedItem;
				upnpContainer = new org.teleal.cling.support.model.container.MusicArtist();
				upnpContainer.setId(objectID+"/"+releaseItem.getId())
					.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container.album.musicAlbum"))
					.setCreator("TODO: Put ArtistName")
					.setTitle(releaseItem.getItem().getName());
				upnpContainer.setChildCount(browseService.findChildren(releaseItem.getId(), null, null, false).getCount().intValue());

			} else if (browsedItem.getType().equals("Classification") ) {
				ResultItem<ClassificationEntity> classificationItem = (ResultItem<ClassificationEntity>) browsedItem;
				upnpContainer = new org.teleal.cling.support.model.container.MusicArtist();
				upnpContainer.setId(objectID+"/"+classificationItem.getId())
					.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container.genre.musicGenre"))
					.setTitle(classificationItem.getItem().getName());
				upnpContainer.setChildCount(browseService.findChildren(classificationItem.getId(), null, null, false).getCount().intValue());
				
			} else if (browsedItem.getType().equals("Track") ) {
				upnpTrack = getUpnpMusicTrackFromSmdTrackEntity((ResultItem<TrackEntity>)browsedItem, objectID, filters);
				
			}else {
				System.err.println("TYPE: "+browsedItem.getType());
			}
			if(upnpContainer != null) {
				upnpContainer.setRestricted(true);
				upnpContainer.setParentID(objectID);
				didl.addContainer(upnpContainer);
			}
			if(upnpTrack != null) {
				upnpTrack.setRestricted(true);
				upnpTrack.setParentID(objectID);
				didl.addItem(upnpTrack);
			}
	        			
		}
		return new BrowseResult(new DIDLParser().generate(didl), browsedContent.getItems().size(), browsedContent.getCount().intValue());
	}
	
    public static List<String> filterStringToList(String s) {
    	
    	List<String> list = new ArrayList<String>();
        if (s == null || s.length() == 0) return list;
        
        String[] filters= s.split(",");
        for (String filter : filters) {
            list.add(filter.trim());
        }
        return list;
    }
 
    /**
     * wantedProperty tells whether or not a given property is to be returned
     * example 
     * 	wantedProperty("upnp:class", "*") => true
     * 	wantedProperty("dc:creator", "upnp:class,dc:creator") => true
     * 	wantedProperty("dc:creator", "upnp:class") => false
     *
     * @param property		Name of the property 
     * @param filter 		Filter string
     * @return Boolean that tells if the property has to be returned
     */
    private boolean wantedProperty(String property, List<String> filters ) {
    	if(filters.size() == 0 ) return false;
    	for(String filter: filters) {
    		if( filter.equals("*") || filter.equals(property) ) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Create a UPnP MusicTrack object from an SMD Track entity
     * @param trackResultItem	smd item object
     * @param parentId			parent object id
     * @param filters			list of elements to be returned
     * @return
     */
    private MusicTrack getUpnpMusicTrackFromSmdTrackEntity(ResultItem<TrackEntity> trackResultItem, String parentId, List<String> filters) {

    	MusicTrack upnpTrack = new MusicTrack();
    	TrackEntity trackItem = trackResultItem.getItem();
    	
		// Mandatory elements:
		upnpTrack.setId(parentId+"/"+trackResultItem.getId());
		upnpTrack.setRefID(trackResultItem.getId());
		upnpTrack.setTitle(trackResultItem.getName());


		if(wantedProperty("dc:creator", filters)) {
			upnpTrack.setCreator(trackItem.getRecording().getContributors().iterator().next().getArtist().getName());
		}
		if(wantedProperty("upnp:album", filters)) {		
			upnpTrack.setAlbum(trackItem.getRelease().getName());
		}
		if(wantedProperty("upnp:originalTrackNumber", filters)) {
			upnpTrack.setOriginalTrackNumber(trackItem.getNumber());
		}
		if(wantedProperty("dc:date", filters)) {
			// TODO: verigy date nullity 
			// upnpTrack.setDate(trackItem.getRelease().getDate().toString());
		}
		if(wantedProperty("upnp:artists", filters)) {
			//upnpTrack.setArtists()
			List<PersonWithRole> contributors = new ArrayList<PersonWithRole>();  		
		
			for(Contributor contributor: trackItem.getRecording().getContributors()) {
				contributors.add(new PersonWithRole(contributor.getArtist().getName(),
						contributor.getType()));
				
			}
			upnpTrack.setArtists(contributors.toArray(new PersonWithRole[0]));
		}
		
		if(wantedProperty("res", filters)) {		
			Res upnpResource = new Res();
			upnpResource.setProtocolInfo(new ProtocolInfo("*:*:*:*"));
			if(trackResultItem.getItem().getPlayableElements().size() != 1 ) {
				// TODO: handle track items with multiple playable elements
				System.err.println("Track "+trackResultItem.getName()+" ("+trackResultItem.getId()+") has "+
						trackItem.getPlayableElements().size()+
						" playable elements item, no support yet for ");
			}
			upnpResource.setValue(trackItem.getPlayableElements().iterator().next().getUri());
			upnpTrack.addResource(upnpResource);
		}
		return upnpTrack;

// unnecessary, initialized by constructor
//		upnpTrack.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.item.audioItem.musicTrack"));

//work name used instead of trackname... TODO: check which is better / always working (trackname returns url encoded string)  
//			.setTitle(trackItem.getItem().getRecording().getWork().getName());
    }

	
	@Override
	public CSV<String> getSortCapabilities() {
    	System.err.println("sort cap called");
		return super.getSortCapabilities();
	}
	
	@UpnpAction(out = {
			@UpnpOutputArgument(name = "Result", stateVariable = "A_ARG_TYPE_Result", getterName = "getResult"),
			@UpnpOutputArgument(name = "NumberReturned", stateVariable = "A_ARG_TYPE_Count", getterName = "getCount"),
			@UpnpOutputArgument(name = "TotalMatches", stateVariable = "A_ARG_TYPE_Count", getterName = "getTotalMatches"),
			@UpnpOutputArgument(name = "UpdateID", stateVariable = "A_ARG_TYPE_UpdateID", getterName = "getContainerUpdateID") })
	public BrowseResult search(
			@UpnpInputArgument(name = "ContainerID") String objectId,
			@UpnpInputArgument(name = "SearchCriteria") String searchCriteria,
			@UpnpInputArgument(name = "Filter") String filter,
			@UpnpInputArgument(name = "StartingIndex", stateVariable = "A_ARG_TYPE_Index") UnsignedIntegerFourBytes firstResult,
			@UpnpInputArgument(name = "RequestedCount", stateVariable = "A_ARG_TYPE_Count") UnsignedIntegerFourBytes maxResults,
			@UpnpInputArgument(name = "SortCriteria") String orderBy)
			throws ContentDirectoryException {

		SortCriterion[] orderByCriteria;
		try {
			orderByCriteria = SortCriterion.valueOf(orderBy);
		} catch (Exception ex) {
			throw new ContentDirectoryException(
					ContentDirectoryErrorCode.UNSUPPORTED_SORT_CRITERIA,
					ex.toString());
		}

		try {
			return search(objectId, searchCriteria,
					filter, firstResult.getValue(), maxResults.getValue(),
					orderByCriteria);
		} catch (Exception ex) {
			throw new ContentDirectoryException(ErrorCode.ACTION_FAILED,
					ex.toString());
		}
		
	}

// NOTE: search request made by WMP11
//	Received: Search objectid=0 searchCriteria=upnp:class derivedfrom "object.container.playlistContainer" and @refID exists false filter=dc:title,microsoft:folderPath indice=0 nbresults=200
//	Received: Search objectid=0 searchCriteria=upnp:class derivedfrom "object.item.audioItem" and @refID exists false filter=* indice=0 nbresults=200

    public BrowseResult search(String objectID, String searchCriteria,
            String filter,
            long firstResult, long maxResults,
            SortCriterion[] orderby)  {
    	
		System.err.println("Received: Search objectid="+objectID+" searchCriteria="+searchCriteria+" filter="+ filter
				+ " indice="+firstResult + " nbresults="+maxResults);
		
		DIDLContent didl = new DIDLContent();

		String smdObjectID = null;
		Integer smdMaxItems;
		
		// if objectID is 0, the client asks for root menu (null value for SMD)
		if(objectID.equals("0")) {
			smdObjectID = null; 
		}else {
			smdObjectID = objectID;
		}
		
		// if upnp maxResults is 0, there's no limit 
		if(maxResults==0) {
			smdMaxItems = null;
		} else { // items to return is limited
			smdMaxItems = new Integer((int)maxResults);
		}


		// quick and dirty hack for WMP search tracks
		if(searchCriteria.startsWith("upnp:class derivedfrom \"object.item.audioItem\"") ) { 
			Result<TrackEntity> searchResult = new org.socialmusicdiscovery.server.business.service.browse.TrackBrowseService().findChildren(new ArrayList<String>(), new ArrayList<String>(), (int)firstResult, (int)smdMaxItems, true);

			for( ResultItem<TrackEntity> resultItem: searchResult.getItems() ) {
				Track smdTrack = resultItem.getItem();
//				System.err.println(smdTrack.getId());
//				if("9709128e-b607-4069-a101-ce76171c06dd".equals(smdTrack.getId())) {
//					System.err.println("bad element");
//				}
					
				MusicTrack upnpTrack = new org.teleal.cling.support.model.item.MusicTrack();
				Res upnpResource = new Res();
				List<PersonWithRole> contributors = new ArrayList<PersonWithRole>();  

				upnpTrack.setId(smdTrack.getId());
				upnpTrack.setParentID(smdTrack.getRelease().getId());
				upnpTrack.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.item.audioItem.musicTrack"));
				upnpTrack.setTitle(smdTrack.getRecording().getWorks().iterator().next().getName());
				//work name used instead of trackname... TODO: check which is better / always working (trackname returns url encoded string)  
				//				.setTitle(trackItem.getName());

				if(smdTrack.getRecording().getContributors().size() > 0 ) {
					upnpTrack.setCreator(smdTrack.getRecording().getContributors().iterator().next().getArtist().getName());
				} else {
					upnpTrack.setCreator("no artist");
				}
				upnpTrack.setAlbum(smdTrack.getRelease().getName());
				
				for(Contributor contributor: smdTrack.getRecording().getContributors()) {
					contributors.add(new PersonWithRole(contributor.getArtist().getName(),
							contributor.getType()));
					
				}
				upnpTrack.setArtists(contributors.toArray(new PersonWithRole[0]));
				
				upnpResource.setProtocolInfo(new ProtocolInfo("*:*:*:*"));
				if(smdTrack.getPlayableElements().size() != 1 ) {
					// TODO: handle track items with multiple playable elements
					System.err.println("Track "+smdTrack+" ("+smdTrack.getId()+") has "+
							smdTrack.getPlayableElements().size()+
							" playable elements item, no support yet for ");
				}
				upnpResource.setValue(smdTrack.getPlayableElements().iterator().next().getUri());
				upnpTrack.addResource(upnpResource);
				didl.addItem(upnpTrack);
			}
			try {
				return new BrowseResult(new DIDLParser().generate(didl), searchResult.getItems().size(), searchResult.getCount().intValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Parse error in DIDL Ojbect");
			}			
		}
    	return new BrowseResult(null, 0L, 0L, 0L);
    }
    
}




///// Code graveyard
//@Override
//public CSV<String> getSearchCapabilities() {
//	CSVString sc = new CSVString();
//	sc.add("dc:title");
//	sc.add("upnp:class");
//	sc.add("upnp:genre");
//	sc.add("upnp:artist");
//	sc.add("upnp:author");
//	sc.add("upnp:author@role");
//	sc.add("upnp:album");
//	System.err.println("search cap called");
//	return sc;
////	return super.getSearchCapabilities();
//}

//UpnpHeaders headers = ReceivingAction.getRequestMessage().getHeaders();
//System.err.println(">>>>>>>>>> RequestFrom agent: " + headers.get(UpnpHeader.Type.values()));
//Object [] toto = headers.getAsArray((UpnpHeader.Type.values()); 

//This is just an example... you have to create the DIDL content dynamically!
//if("0".equals(objectID)) {
//return getHardcodedRoot();
//}else {
//return getFakeContent();
//}

//private BrowseResult getFakeContent() throws Exception {
//DIDLContent didl = new DIDLContent();
//
//String album = ("Black Gives Way To Blue");
//String creator = "Alice In Chains"; // Required
//PersonWithRole artist = new PersonWithRole(creator, "Performer");
//MimeType mimeType = new MimeType("audio", "mpeg");
//
//didl.addContainer(new Container(
//      "103", "3", // 103 is the Item ID, 3 is the parent Container ID
//      "Album container",  "aze",
//      new org.teleal.cling.support.model.DIDLObject.Class("object.container"), 5
//));
//
//didl.addItem(new MusicTrack(
//      "101", "3", // 101 is the Item ID, 3 is the parent Container ID
//      "All Secrets Known",
//      creator, album, artist,
//      new Res(mimeType, 123456l, "00:03:25", 8192l, "http://10.0.0.1/files/101.mp3")
//));
//
//didl.addItem(new MusicTrack(
//      "102", "3",
//      "Check My Brain",
//      creator, album, artist,
//      new Res(mimeType, 2222222l, "00:04:11", 8192l, "http://10.0.0.1/files/102.mp3")
//));
//
//// Create more tracks...
//
//// Count and total matches is 2
//return new BrowseResult(new DIDLParser().generate(didl), 3, 3);
//}

//container
//.setId(item.getId())
//.setParentID(objectID)
//.setTitle(((org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity)item.getItem()).getName())
//.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container"));

//didl.addContainer(new Container(
//    item.getId(), objectID, // 103 is the Item ID, 3 is the parent Container ID
//    //item.getItem().toString(),  "SMD",
//    ((org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity)item.getItem()).getName(),  
//    "SMD",
//    new org.teleal.cling.support.model.DIDLObject.Class("object.container"), 
//    browseService.findChildren(item.getId(), null, null, false).getCount().intValue()
//));
//private BrowseResult getHardcodedRoot() throws Exception {
//DIDLContent didl = new DIDLContent();
//
////didl.addContainer(new Container(
////        "0.Artists", "0", // 103 is the Item ID, 3 is the parent Container ID
////        "Artists",  "SMD",
////        new org.teleal.cling.support.model.DIDLObject.Class("object.container"), 5
////));
////
////didl.addContainer(new Container(
////        "0.Releases", "0", 
////        "Releases",  "SMD",
////        new org.teleal.cling.support.model.DIDLObject.Class("object.container"), 5
////));
////
//return new BrowseResult(new DIDLParser().generate(didl), 2, 2);
//}


//public class SmdArtist extends  org.teleal.cling.support.model.container.MusicArtist {
//
//    public SmdArtist(ResultItem<String> artistItem) {
//        setId(artistItem.getId());
//        
//        // for performance reason, childCount should be null
//        // unfortunately, cling doesn't permit it (yet)
//        // setChildCount(null);
//        // TODO: fill a bug report or submit a patch for cling
//        // problem is null pointer exception in DIDLParser.java (line 337)
//        // containerElement.setAttribute("childCount", Integer.toString(container.getChildCount()));
//        // item contained in result item is of type String
//        setTitle(artistItem.getItem()); 
//        setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container"));
//    }
//}