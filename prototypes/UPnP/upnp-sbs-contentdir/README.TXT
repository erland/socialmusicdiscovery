This project is a prototype made to test UPnP. It depends upon cybergarage upnp
stack version >2.0. As a recente cybergarage upnp lib maven modulr is hard to 
find, the easier is to get it from svn:
https://cgupnpjava.svn.sourceforge.net/svnroot/cgupnpjava/trunk/cyberlink/upnp-stack/src/main/java/

To use it, you have tu run org.medee.playground.upnp.TestApi which will create 
a fake upnp device named "UPNP RV TEST DEVICE" which provide discovery service
and a very limited "Browse" service. 
This "Browse" service is connected to SBS database (db address is hardcoded in:
org.medee.playground.sbs.DatabaseConnector). Beware, some params are also 
hardcoded and designed to be run from eclipse like the path to the device
description xml file (descriptionFileName in TestApi).

