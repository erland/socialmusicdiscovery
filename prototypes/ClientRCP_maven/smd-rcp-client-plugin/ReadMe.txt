Some VERY brief notes on how to setup and run the RCP prototype client. 

To Build and Run: 
1. Install Eclipse 3.6 for RCP developers: http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/heliossr1
2. Check out and build SMD code as described elsewhere
3. Copy the generated .jar into "imported.lib" in the RCP client source code structure 
   Example: copy <your build output directory>\smd-server-1.0-SNAPSHOT.jar <your project root>\smd-rcp\lib.imported
4. Launch client: right-click 'smd-rcp.product.launch (<your OS>)' - 'Run As ...' - 'smd-rcp.product'

To update the server code:
Ideally, update the server code and install the new .jar (step 3 above). 
In practice, you may have some problems getting Eclipse to understand that you have updated the server jar. 
If "refresh" and "clean build" doesn't help:
	- use Eclipse to physically delete the file
    - copy the jar again (step 3 above)
    - do a refresh
      
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
    
Optional:
- Install Eclipse Integration for Apache Maven (IAM) from http://www.eclipse.org/iam 
  Update site: http://q4e.googlecode.com/svn/trunk/updatesite-iam

=== NOTE === 

These instructions are preliminary and temporary for the prototype.
 
Since we don't have a checked-in "target platform", everyone needs to run the the same version of the Eclipse IDE, and install all the required features directly into the IDE. 
Some general notes and design guidelines are available on the wiki: 
http://code.google.com/p/socialmusicdiscovery/wiki/PrototypeRCPClient      

  