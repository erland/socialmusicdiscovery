Some VERY brief notes on how to setup and run the RCP prototype client. 

To Build and Run: 
1. Install Eclipse 3.6 for RCP developers: http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/heliossr1
2. Check out and build SMD code as described on the wiki: http://code.google.com/p/socialmusicdiscovery/wiki/BuildInstructions
3. Import all client projects into Eclipse
4. Open the .target file that fits your platform (e.g. /smd-rcp-client/smd-rcp-client.win32.x86.target). 
   In the upper right hand corner, click "set as target platform" 
5. Launch client: right-click 'smd-rcp.product.launch (<your OS>)' - 'Run As ...' - 'smd-rcp.product'.
   Example: /smd-rcp-client-plugin/smd-rcp.product.launch (WIN32)
   
You can also use Maven to build and run a standalone client (see wiki). 

To work on UI: 
- Install SWT Designer (part of WindowBuilder PRO): http://code.google.com/javadevtools/wbpro/installation/updatesite_3.6.html
  (update site: http://dl.google.com/eclipse/inst/d2wbpro/latest/3.6) - you probably need to ignore one security warning.
- install or refer to Eclipse examples:
  -- RCP:
	 http://www.vogella.de/articles/EclipseRCP/article.html
  -- JFace:
  	 http://wiki.eclipse.org/JFace_Data_Binding/Snippets#Running_the_Snippets
  	 http://wiki.eclipse.org/index.php/JFaceSnippets
  -- SWT:
     http://www.eclipse.org/swt/snippets/
     http://www.eclipse.org/swt/examples.php
    
=== NOTE === 

These instructions are preliminary and temporary for the prototype.
 
Some general notes and design guidelines are available on the wiki: 
http://code.google.com/p/socialmusicdiscovery/wiki/PrototypeRCPClient     