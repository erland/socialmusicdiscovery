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

import com.google.inject.Inject;

import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.model.classification.ClassificationEntity;
import org.socialmusicdiscovery.server.business.model.core.*;
import org.socialmusicdiscovery.server.business.service.browse.ArtistBrowseService;
import org.socialmusicdiscovery.server.business.service.browse.BrowseServiceManager;
import org.socialmusicdiscovery.server.business.service.browse.ClassificationBrowseService;
import org.socialmusicdiscovery.server.business.service.browse.LibraryBrowseService;
import org.socialmusicdiscovery.server.business.service.browse.Result;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem.ResultItemImage;
import org.socialmusicdiscovery.server.business.service.browse.TrackBrowseService;
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
import org.teleal.cling.support.model.DIDLObject.Property;
import org.teleal.cling.support.model.DIDLObject.Property.UPNP;
import org.teleal.cling.support.model.container.Container;
import org.teleal.cling.support.model.container.MusicAlbum;
import org.teleal.cling.support.model.item.Item;
import org.teleal.cling.support.model.item.MusicTrack;
import org.teleal.cling.support.model.item.Photo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
//TODO: investigate problem with Folder:styles/Classification.style:5e7484e8-8cea-4a42-b10e-f4e07c1ff027/Artist:980d5ab6-2932-4158-ba1c-8df59404c177/Release:007019a0-f58e-4df0-9977-40dee95fa9a3

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

	@Inject
	BrowseServiceManager browseServiceManager;
	
	ArtistBrowseService artistBrowseService;
	ClassificationBrowseService classificationBrowseService;
	TrackBrowseService trackBrowseService;
	
	public ContentDirectory() {

		super(	// search caps
				Arrays.asList("dc:title", "upnp:class", "upnp:genre"),
				// sort caps
				Arrays.asList("dc:title", "upnp:author"));
		InjectHelper.injectMembers(this);
		artistBrowseService = browseServiceManager.getBrowseService("Artist");
		classificationBrowseService = browseServiceManager.getBrowseService("Classification");
		trackBrowseService = browseServiceManager.getBrowseService("Track");		
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
    		//                                Microsoft-Windows/6.1 UPnP/1.0 Windows-Media-Player/12.0.7601.17514_DLNADOC
        	// Foobar: 
        	String UA = null;
        	try {
        		UA = ReceivingAction.getRequestMessage().getHeaders().getFirstHeader(UpnpHeader.Type.USER_AGENT).getString();
        		//UA = ReceivingAction.getExtraResponseHeaders().getFirstHeader(UpnpHeader.Type.USER_AGENT).getString();
        		System.err.println(">>>>>>>>>> RequestFrom agent: " + UA);
        	} catch (NullPointerException ex) {
        		System.err.println(">>>>>>>>>> RequestFrom agent: Anonymous");
        	}


        	if(browseFlag == BrowseFlag.DIRECT_CHILDREN ) {
        		return getSmdDirectChildrens(objectID, filter, firstResult, maxResults, orderby);

        	} else if(browseFlag == BrowseFlag.METADATA) {
        		return getSmdMetadata(objectID, filter, firstResult, maxResults, orderby);
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
            
            // childCount is optional according to UPnP spec
            // for performance reason, childCount isn't set by plugin
            // cling didn't permit it before, now it's null by default
            //setChildCount(null);
            setRestricted(true);

            // for "metaFolder", getItem is null
            // could be smd model change or bug in Plugin code
            // TODO: check if dc:title works for both meta folder and real folders
            //setTitle(folderItem.getItem()); 
            setTitle(folderItem.getName());
            setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container"));
        }
    }

    @SuppressWarnings("unchecked")
	private BrowseResult getSmdMetadata (String objectID,
                               String filter,
                               long firstResult, long maxResults,
                               SortCriterion[] orderby) throws Exception {

    	String smdObjectID = null;
    	
    	DIDLContent didl = new DIDLContent();
		// if objectID is 0, the client asks for root menu (null value for SMD)
		if(objectID.equals("0")) {
			smdObjectID = null; 
		}else {
			smdObjectID = objectID;
		}

		Container upnpContainer = new Container();	
		upnpContainer.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container"));
		upnpContainer.setRestricted(true);
		didl.addContainer(upnpContainer);

		if(smdObjectID == null) {
			upnpContainer.setTitle("Root Container");
			upnpContainer.setParentID("-1");
			upnpContainer.setId("0");
		} else if (smdObjectID.matches("^Folder:([^/]+)$") ) {
			java.util.regex.Pattern folderRx = java.util.regex.Pattern.compile("^Folder:([^/]+)$");
			java.util.regex.Matcher matcher = folderRx.matcher(smdObjectID);
			matcher.find();
			upnpContainer.setTitle(matcher.group(1));
			upnpContainer.setParentID("0");
			upnpContainer.setId(smdObjectID);
			
		}
		
		return new BrowseResult(new DIDLParser().generate(didl), 1, 1);
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
		
		Result<Object> browsedContent = browseService.findChildren("upnp", smdObjectID, (int)firstResult, smdMaxItems, false);
		
		// Memo: DIDL object mandatory elements:
		// id, parentID, title, class, restricted (for 'object' base class)
		
		Container upnpContainer = null;
		Item upnpItem = null;
		
		for(ResultItem<?> browsedItem: browsedContent.getItems()) {
			
			// nullify container to prevent a bug if browsedItem type is unknown 
			// unless nullified, the same object will be added twice in didl document
			upnpContainer = null;
			
			if(browsedItem.getType().equals("Folder") || browsedItem.getType().equals("ImageFolder") ) {
				
				
				// found item is a Folder (could be root elements, LastFM Image, etc.
				upnpContainer = new SmdFolder( (ResultItem<String>)browsedItem);

				// create object ID (parentId/itemID)
				if(smdObjectID != null) {
					upnpContainer.setId(smdObjectID+"/"+browsedItem.getId());
				}
				// before r839: upnpContainer.setChildCount(browseService.findChildren(browsedItem.getId(), null, null, false).getCount().intValue());
                // after  r839: upnpContainer.setChildCount(browseService.findChildren("upnp", browsedItem.getId(), null, null, false).getCount().intValue());

			} else if (browsedItem.getType().equals("Artist") ) {
				
				// SMD Item is considered as an artist, create corresponding UPnP container
				
				ResultItem<ArtistEntity> artistItem = (ResultItem<ArtistEntity>) browsedItem;
				
				org.teleal.cling.support.model.container.MusicArtist upnpMusicArtist = new org.teleal.cling.support.model.container.MusicArtist();
				
				upnpContainer = upnpMusicArtist;
				upnpMusicArtist.setId(objectID+"/"+artistItem.getId())
					.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container.person.musicArtist"))
					.setTitle(artistItem.getItem().getName());
				System.err.println("CurrentArtist: "+upnpMusicArtist.getTitle());
				// TODO: Artist can have "artistDiscographyURI" 
				// upnpMusicArtist.setGenres( new String[] {"Unknown"} );
				
				// findChildren seems to require parent path concatenation now
				//upnpContainer.setChildCount(browseService.findChildren(artistItem.getId(), null, null, false).getCount().intValue());
				// upnpContainer.setChildCount(browseService.findChildren(objectID+"/"+artistItem.getId(), null, null, false).getCount().intValue());
				
				// search for artist genre
				// TODO: activate again when performance problem is gone
//				List<String> artistGenres = new ArrayList<String>();
//				for(ResultItem<ClassificationEntity> classificationResult: classificationBrowseService.findChildren(Arrays.asList(artistItem.getId()), null, 0, null, false).getItems() ) {
//					ClassificationEntity classification = classificationResult.getItem();
//					if(classification.getType().equals("genre")) {
//						artistGenres.add(classification.getName());
//					}
//				}
//				if(artistGenres.size()>0) {
//					upnpMusicArtist.setGenres(artistGenres.toArray(new String[0]));
//				}

			} else if (browsedItem.getType().equals("Release") ) {
				ResultItem<ReleaseEntity> releaseItem = (ResultItem<ReleaseEntity>) browsedItem;
				MusicAlbum upnpAlbum = new MusicAlbum();
				upnpContainer = upnpAlbum;
				List<PersonWithRole> contributors =  new ArrayList<PersonWithRole>();
				
//				try {

				Collection<ResultItem<ArtistEntity>> artists = 
					artistBrowseService.findChildren(Arrays.asList(releaseItem.getId()), null, 0, null, false).getItems(); 
				
				for(ResultItem<ArtistEntity> artist : artists) {
					// TODO: replace hard coded "performer" by SMD role
					 contributors.add(new PersonWithRole(artist.getItem().getName(), "Performer"));
				}
				
				upnpAlbum.setArtists(contributors.toArray(new PersonWithRole[0] ));
// TODO: check what happens if there's no artist (should be handled below by contributors.size test)
//				} catch(java.util.NoSuchElementException Ex) {
//					// ignore this exception, it means there's no artist for that album
//				}
				
				upnpAlbum.setId(objectID+"/"+releaseItem.getId());
				upnpAlbum.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container.album.musicAlbum"));
				
				if(contributors.size()>0) {
					upnpAlbum.setCreator(contributors.get(0).getName());
				} else {
					upnpAlbum.setCreator("unknown");
				}
				
				upnpAlbum.setTitle(releaseItem.getItem().getName());
				upnpAlbum.addProperty(new DIDLObject.Property.UPNP.ALBUM(upnpAlbum.getTitle()) );
				upnpAlbum.setDescription("aze");
				// <albumArtUri dlna:profileID="PNG_TN">
				// <albumArtUri dlna:profileID="JPEG_TN">
				
				upnpAlbum.setAlbumArtURIs(new URI[] { new URI(releaseItem.getImage().getUrl())} );
				Property<URI>[] albumArtUriProperties = upnpAlbum.getProperties(UPNP.ALBUM_ART_URI.class);
				for(Property<URI> uriProperty : albumArtUriProperties) {
					//DIDLObject.Property.DLNA.PROFILE_ID toto = new DIDLObject.Property.DLNA.PROFILE_ID();
					//toto.getValue().
					//DIDLAttribute tutu = new DIDLAttribute(DIDLObject.Property.DLNA, "dlna", "zeazé")
					
					// value for dlna:profileID attribute of upnp:albumArtURI element
					String upnpProfileID;
					
			    	if(uriProperty.getValue().toString().endsWith(".png")) {
			    		upnpProfileID = "PNG_TN";
			    	} else {
			    		// if extension isn't png, suppose it's JPEG (may be a bad guess)
			    		upnpProfileID = "JPEG_TN";
			    		// TODO: improve image entity to contain image type or enhance this code
			    		// to detect image format 
			    	}
			    	
					uriProperty.addAttribute( 
							new DIDLObject.Property.DLNA.PROFILE_ID(
									new DIDLAttribute(
									DIDLObject.Property.DLNA.NAMESPACE.URI, 
									"dlna", 
									upnpProfileID)
							)	
					);					
				}
				
				// search for album genre
// commented for performance reason, should be uncommented or changed
// when performance problem is solved
//				List<String> albumGenres = new ArrayList<String>();
//				for(ResultItem<ClassificationEntity> classificationResult: classificationBrowseService.findChildren(Arrays.asList(releaseItem.getId()), null, 0, null, false).getItems() ) {
//					ClassificationEntity classification = classificationResult.getItem();
//					if(classification.getType().equals("genre")) {
//						albumGenres.add(classification.getName());
//					}
//				}

				
				// construct album genres from genre, style and moods classifications
				List<String> albumGenres = new ArrayList<String>();
				for(ResultItem<ClassificationEntity> classificationResult: 
								classificationBrowseService.findChildren(
									Arrays.asList(releaseItem.getId(),"Classification.genre"), 
										null, 0, null, false).getItems() 
				) {
					StringTokenizer sk = new StringTokenizer(classificationResult.getItem().getName(), ",");
					while(sk.hasMoreTokens() ) {
						String genre = sk.nextToken().trim();
						if(!albumGenres.contains(genre))
							albumGenres.add(genre);
					}
				}

				for(ResultItem<ClassificationEntity> classificationResult: 
					classificationBrowseService.findChildren(
						Arrays.asList(releaseItem.getId(),"Classification.style"), 
							null, 0, null, false).getItems() 
				) {
//						albumGenres.addAll( Arrays.asList(classificationResult.getItem().getName().split(" ")) );
					StringTokenizer sk = new StringTokenizer(classificationResult.getItem().getName(), ",");
					while(sk.hasMoreTokens() ) {
						String genre = sk.nextToken().trim();
						if(!albumGenres.contains(genre))
							albumGenres.add(genre);
					}
				}

				for(ResultItem<ClassificationEntity> classificationResult: 
					classificationBrowseService.findChildren(
						Arrays.asList(releaseItem.getId(),"Classification.mood"), 
							null, 0, null, false).getItems() 
				) {
//						albumGenres.add(classificationResult.getItem().getName());
						StringTokenizer sk = new StringTokenizer(classificationResult.getItem().getName(), ",");
						while(sk.hasMoreTokens() ) {
							String genre = sk.nextToken().trim();
							if(!albumGenres.contains(genre))
								albumGenres.add(genre);
						}						
				}
					
				if(albumGenres.size()>0) {
					upnpAlbum.setGenres(albumGenres.toArray(new String[0]));
				}
				
				// TODO: add childcount is asked for by the client parameters
				// before r839: upnpContainer.setChildCount(browseService.findChildren(objectID+"/"+releaseItem.getId(), null, null, false).getCount().intValue());
				// after  r839: upnpContainer.setChildCount(browseService.findChildren("upnp", artistItem.getId(), null, null, false).getCount().intValue());

			} else if (browsedItem.getType().equals("Classification") ) {
				ResultItem<ClassificationEntity> classificationItem = (ResultItem<ClassificationEntity>) browsedItem;
				upnpContainer = new org.teleal.cling.support.model.container.MusicArtist();
				upnpContainer.setId(objectID+"/"+classificationItem.getId())
					.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.container.genre.musicGenre"))
					.setTitle(classificationItem.getItem().getName());
				upnpContainer.setChildCount(browseService.findChildren("upnp", classificationItem.getId(), null, null, false).getCount().intValue());
				
			} else if (browsedItem.getType().equals("Track") ) {
				
				upnpItem = getUpnpMusicTrackFromSmdTrackEntity((ResultItem<TrackEntity>)browsedItem, objectID, filters);
				
			} else if (browsedItem.getType().equals("LastFMImage") ) {
				ResultItemImage smdImage = browsedItem.getImage(); 		
				Photo upnpPhoto = new Photo();
		    	upnpItem = upnpPhoto;
		    	
				// Mandatory elements:
		    	upnpPhoto.setTitle("unknown");
		    	upnpPhoto.setAlbum("unknown");
		    	upnpPhoto.setWriteStatus(WriteStatus.NOT_WRITABLE);
		    	upnpPhoto.setCreator("LastFM");
		    	upnpPhoto.setId(objectID+"/"+browsedItem.getId());
		    	upnpPhoto.setClazz(new org.teleal.cling.support.model.DIDLObject.Class("object.item.imageItem.photo"));
		    	Res url = new Res();
		    	upnpPhoto.addResource(url);
		    	url.setValue(smdImage.getUrl());
		    	if(url.getValue().endsWith(".png")) {
		    		url.setProtocolInfo(new ProtocolInfo("http-get:*:image/png:DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS="));
		    	} else if(url.getValue().endsWith(".jpg")) {
		    		url.setProtocolInfo(new ProtocolInfo("http-get:*:image/jpeg:DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS="));
		    	} 

			} else {
				System.err.println("TYPE: "+browsedItem.getType());
			}
			
			if(upnpContainer != null) {
				upnpContainer.setRestricted(true);
				upnpContainer.setParentID(objectID);
				
				if(upnpContainer.getCreator() == null) {
					upnpContainer.setCreator("SMD");
				}
				
				didl.addContainer(upnpContainer);
			}
			if(upnpItem != null) {
				upnpItem.setRestricted(true);
				upnpItem.setParentID(objectID);
				didl.addItem(upnpItem);
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
			try {
				upnpTrack.setCreator(trackItem.getRecording().getContributors().iterator().next().getArtist().getName());
			}catch (Exception NoSuchElementException) {
				upnpTrack.setCreator("unknown");
			}
		}
		if(wantedProperty("upnp:album", filters)) {		
			upnpTrack.setAlbum(trackItem.getRelease().getName());
		}
		if(wantedProperty("upnp:originalTrackNumber", filters)) {
			upnpTrack.setOriginalTrackNumber(trackItem.getNumber());
		}
		if(wantedProperty("dc:date", filters)) {
			// TODO: verify date nullity 
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

// NOTE: search request made by WMP11 (
//	Received: Search objectid=0 searchCriteria=upnp:class derivedfrom "object.container.playlistContainer" and @refID exists false filter=dc:title,microsoft:folderPath indice=0 nbresults=200
//	Received: Search objectid=0 searchCriteria=upnp:class derivedfrom "object.item.audioItem" and @refID exists false filter=* indice=0 nbresults=200

	// NOTE: search request made by WMP11 ( at startup )
//	sort cap called
//	Received: Search objectid=0 searchCriteria=upnp:class derivedfrom "object.container.playlistContainer" and @refID exists false filter=dc:title,microsoft:folderPath indice=0 nbresults=200
//	sort cap called
//	sort cap called
//	sort cap called
//	Received: Search objectid=0 searchCriteria=upnp:class derivedfrom "object.item.audioItem" and @refID exists false filter=* indice=0 nbresults=200

	
	// NOTE: search request made by WMP11 ( after a manual "refresh" )	
//	Received: Browse  DC objectid=0 filter=* indice=0 nbresults=200
//	>>>>>>>>>> RequestFrom agent: Microsoft-Windows/6.1 UPnP/1.0 Windows-Media-Player/12.0.7601.17514_DLNADOC
//	Received: Browse  DC objectid=Folder:styles filter=* indice=0 nbresults=200
//	Received: Browse  DC objectid=Folder:styles/Classification.style:607728fa-2f2c-433d-9625-da82e581c331 filter=* indice=0 nbresults=200
//	CurrentArtist: Jimmy Scott
//	Received: Browse  DC objectid=Folder:styles/Classification.style:607728fa-2f2c-433d-9625-da82e581c331/Artist:08216615-06da-4b31-83d6-b10177a10a14 filter=* indice=0 nbresults=200
//	Received: Browse  DC objectid=Folder:styles/Classification.style:607728fa-2f2c-433d-9625-da82e581c331/Artist:08216615-06da-4b31-83d6-b10177a10a14/Release:72a61174-0f71-4b4d-8c00-0835abbcd4b1 filter=* indice=0 nbresults=200
//	[cling-28        ] WARNING - 13:15:55,86  - te.hql.ast.QueryTranslatorImpl#list: firstResult/maxResults specified with collection fetch; applying in memory!
//	Received: Browse  DC objectid=Folder:styles/Classification.style:2a4ad998-2c7c-406c-afc7-9be805f3ea84 filter=* indice=0 nbresults=200
//	CurrentArtist: The Beach Boys
//	Received: Browse  DC objectid=Folder:styles/Classification.style:2a4ad998-2c7c-406c-afc7-9be805f3ea84/Artist:a1c75c5d-db2b-427f-abb1-a1d76b688ddb filter=* indice=0 nbresults=200
//	Received: Browse  DC objectid=Folder:styles/Classification.style:2a4ad998-2c7c-406c-afc7-9be805f3ea84/Artist:a1c75c5d-db2b-427f-abb1-a1d76b688ddb/Release:bdbe4fd1-dcf6-4e1e-b8e0-6a1aa217db41 filter=* indice=0 nbresults=200
//	[cling-28        ] WARNING - 13:15:58,245 - te.hql.ast.QueryTranslatorImpl#list: firstResult/maxResults specified with collection fetch; applying in memory!
//	Received: Browse  DC objectid=Folder:styles/Classification.style:df943ee4-d547-4f47-8f85-a92ece1b3b30 filter=* indice=0 nbresults=200
//	CurrentArtist: Mirwais
//	Received: Browse  DC objectid=Folder:styles/Classification.style:df943ee4-d547-4f47-8f85-a92ece1b3b30/Artist:edc903f0-af64-4104-b5ee-96b338840b6a filter=* indice=0 nbresults=200
//	Received: Browse  DC objectid=Folder:styles/Classification.style:df943ee4-d547-4f47-8f85-a92ece1b3b30/Artist:edc903f0-af64-4104-b5ee-96b338840b6a/Release:b5c085f9-63e9-42a7-b7ff-8b3795603e79 filter=* indice=0 nbresults=200
//	[cling-28        ] WARNING - 13:16:00,156 - te.hql.ast.QueryTranslatorImpl#list: firstResult/maxResults specified with collection fetch; applying in memory!
//	Received: Browse  DC objectid=Folder:styles/Classification.style:2f499758-54fa-4c3f-95c4-602c36acdd51 filter=* indice=0 nbresults=200

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
		
		System.err.println("XXXXXXXX ARG2!!!!");
		// if upnp maxResults is 0, there's no limit 
		if(maxResults==0) {
			smdMaxItems = null;
		} else { // items to return is limited
			smdMaxItems = new Integer((int)maxResults);
		}


		// quick and dirty hack for WMP search tracks
		if( (searchCriteria != null) && searchCriteria.startsWith("upnp:class derivedfrom \"object.item.audioItem\"") ) {
			
			// Result<TrackEntity> searchResult = new org.socialmusicdiscovery.server.business.service.browse.TrackBrowseService().findChildren(new ArrayList<String>(), new ArrayList<String>(), (int)firstResult, (int)smdMaxItems, true);
			Result<TrackEntity> searchResult = trackBrowseService.findChildren(new ArrayList<String>(), new ArrayList<String>(), (int)firstResult, (int)smdMaxItems, true);

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
//    public SmdArtist(Item<String> artistItem) {
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