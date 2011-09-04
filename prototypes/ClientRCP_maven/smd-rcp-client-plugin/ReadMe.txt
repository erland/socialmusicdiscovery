Some VERY brief notes on how to setup and run the RCP prototype client. 

These instructions are available both on the wiki and in Eclipse (wiki is linked to the actual page in SVN):
* wiki: http://socialmusicdiscovery.googlecode.com/svn/prototypes/ClientRCP_maven/smd-rcp-client-plugin/ReadMe.txt
* Eclipse: /smd-rcp-client-plugin/ReadMe.txt 
 
=== DISCLAIMER ===

These instructions are preliminary and temporary for the prototype. 
Please feel free to correct errors or add missing info!

=== To Build and Run from command line ===
 
1. Use subversion to checkout server and client code (see wiki for details)
2. Use Maven to build server code
	a. cd .../localserver
	b. mvn install  
2. Use Maven to build and run a standalone client
	a. cd .../ClientRCP_maven
	b. svn update
	c. mvn install
3. Run client
	a. cd ClientRCP_maven/smd-rcp-client/target/win32.win32.x86/eclipse
	   (adjust path for your platform)
	b. launcher.exe
	
=== To Build and Run in Eclipse === 

1. Install Eclipse 3.7 for RCP developers: http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/indigor
2. Check out and build SMD code as described on the wiki: http://code.google.com/p/socialmusicdiscovery/wiki/BuildInstructions
3. Import all client projects into Eclipse: File - Import - Team - Team Project Set - ..../ClientRCP_maven/projectSet.psf  
4. Open the .target file that fits your platform (e.g. /smd-rcp-client/smd-rcp-client.win32.x86.target). 
   In the upper right hand corner, click "set as target platform" 
5. Launch client: right-click 'smd-rcp.product.launch (<your OS>)' - 'Run As ...' - 'smd-rcp.product'.
   Example: /smd-rcp-client-plugin/smd-rcp.product.launch (WIN32)
   
=== To work on the client === 

1. Install and setup Eclipse for RCP developers as described above
2. Install SWT Designer from update site: http://download.eclipse.org/windowbuilder/WB/release/R201106211200/3.7
   You need at least the SWT designer and the WindowBuilder engine, but might just as well install it all.
   - As of Eclipse 3.7, WindowBuilder is part of Eclipse (see more on http://www.eclipse.org/windowbuilder).
     Older versions were part of Google's WindowBuilder PRO: 
     - http://code.google.com/javadevtools/wbpro/installation/updatesite_3.6.html
     - update site: http://dl.google.com/eclipse/inst/d2wbpro/latest/3.6
3. Optional: install or refer to Eclipse examples:
   - RCP:
	 http://www.vogella.de/articles/EclipseRCP/article.html
   - JFace:
  	 http://wiki.eclipse.org/JFace_Data_Binding/Snippets#Running_the_Snippets
  	 http://wiki.eclipse.org/index.php/JFaceSnippets
   - SWT:
     http://www.eclipse.org/swt/snippets/
     http://www.eclipse.org/swt/examples.php
    
Some general notes and design guidelines are available on the wiki: 
http://code.google.com/p/socialmusicdiscovery/wiki/PrototypeRCPClient     