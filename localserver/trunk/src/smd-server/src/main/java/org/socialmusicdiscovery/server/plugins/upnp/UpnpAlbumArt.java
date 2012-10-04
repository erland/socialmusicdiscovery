package org.socialmusicdiscovery.server.plugins.upnp;


import org.w3c.dom.Element;

public class UpnpAlbumArt {
	
    private String uri;
    
    static public class ARTIST extends org.teleal.cling.support.model.DIDLObject.Property.PropertyPersonWithRole 
    	implements org.teleal.cling.support.model.DIDLObject.Property.UPNP.NAMESPACE {
        public ARTIST() {
        }

//        public ARTIST(UpnpAlbumArt value) {
//            super(value, null);
//        }
    }

    public UpnpAlbumArt(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpnpAlbumArt albumArt = (UpnpAlbumArt) o;

        if (!uri.equals(albumArt.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Override
    public String toString() {
        return getUri();
    }
    

    public void setOnElement(Element element) {
        element.setTextContent(toString());
        element.setAttribute("upnp:", getUri());
    }
}
