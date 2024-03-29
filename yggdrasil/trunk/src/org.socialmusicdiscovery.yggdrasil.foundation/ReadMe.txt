Some VERY brief notes on how to setup and run the "yggdrasil" RCP client. 

These instructions are available both on the wiki and in Eclipse (wiki is linked to the actual page in SVN):
* wiki: http://socialmusicdiscovery.googlecode.com/svn/yggdrasil/trunk/src/smd-rcp-client-plugin/ReadMe.txt
* Eclipse: /smd-rcp-client-plugin/ReadMe.txt 
 
Please feel free to correct errors or add missing info!

=== To Build and Run from command line ===

Prerequisite: you have Maven and Subversion command-line versions installed. 

There are of course many other ways to check out and build, the instructions 
below should be seen as guidelines. Instructions below assume that "mvn" runs 
Maven and "svn" runs subversion.

Please see wiki for exact, up-to-date details on how to check out and build server code.
  
1. Use subversion to checkout server and client code:
   a. cd to some empty directory, e.g. "/smd" or "D:\smd"
   b. svn checkout https://socialmusicdiscovery.googlecode.com/svn/localserver/trunk/src localserver
   c. svn checkout https://socialmusicdiscovery.googlecode.com/svn/yggdrasil/trunk/src yggdrasil
2. Use Maven to build server code
	a. cd .../localserver
	b. mvn install  
3. Use Maven to build and run a standalone client
	a. cd .../yggdrasil
	b. mvn install
4. Run client
   (with path and launcher name as generated for your platform)   
	a. cd .../yggdrasil/smd-rcp-client/target/win32.win32.x86/eclipse
	b. yggdrasil.exe (or launcher.exe) 
	
=== To Build and Run in Eclipse === 

1. Install Eclipse 3.7 for RCP/RAP developers: http://www.eclipse.org/downloads/packages/eclipse-rcp-and-rap-developers/indigor
2. Check out and build SMD code as described above
3. Import all client projects into Eclipse: 
   a. File - Import - General - Existing Projects into Workspace 
   b. browse to .../yggdrasil
   c. select all projects
4. In org.socialmusicdiscovery.yggdrasil-product, open the .target file that fits your platform (e.g. dev-win32.x86.target). 
   In the upper right hand corner, click "set as target platform" 
5. Launch client: 
   a. open /org.socialmusicdiscovery.yggdrasil-product/org.socialmusicdiscovery.yggdrasil-product.product
   b. Select the Overview tab
   c. Optional: if you change any product/application data, click "1. Synchronize"
   d. Under "2. Test the product", click "Launch an Eclipse application"

=== To work on the client === 

1. Install and setup Eclipse for RCP developers as described above
2. Install SWT Designer from update site: http://download.eclipse.org/windowbuilder/WB/release/R201106211200/3.7
   You need at least the SWT designer and the WindowBuilder engine, but might just as well install it all.
   - As of Eclipse 3.7, WindowBuilder is part of Eclipse (see more on http://www.eclipse.org/windowbuilder).
     Older versions were part of Google's WindowBuilder PRO: 
     - http://code.google.com/javadevtools/wbpro/installation/updatesite_3.6.html
     - update site: http://dl.google.com/eclipse/inst/d2wbpro/latest/3.6
3. Install plugin for Subversion
   - recommended: Subclipse plugin from update site http://subclipse.tigris.org/update_1.6.x
    (you probably have to ignore a warning during install)
   - alternative: Subversive plugin from http://download.eclipse.org/technology/subversive/0.7/update-site/
     On next restart, check to include the latest versions of SVNKit and JavaHL.
     NOTE: we've had some problems with the Subversive plug-in, the Subclipse is recommended (see notes below).
4. Optional: install or refer to Eclipse examples:
   - RCP:
	 http://www.vogella.de/articles/EclipseRCP/article.html
   - JFace:
  	 http://wiki.eclipse.org/JFace_Data_Binding/Snippets#Running_the_Snippets
  	 http://wiki.eclipse.org/index.php/JFaceSnippets
   - SWT:
     http://www.eclipse.org/swt/snippets/
     http://www.eclipse.org/swt/examples.php
    
Some general notes and design guidelines are available on the wiki: 
http://code.google.com/p/socialmusicdiscovery/wiki/Yggdrasil

=== NOTES ===

- Team Project Set 
  For some reason, an exported Team Project Set cannot be imported; local and SVN paths get mixed up. 
  Not investigated further.
- Subversion Plugin
  For some reason, the Eclipse/Polarion Subversive plugin has occasionally caused some problems, primarily 
  preventing import of projects that were checked out with command-line SVN. On recent experiments, the 
  Subversive plug seems to work OK. Meanwhile, The Subclipse plugin (available at tigris.org) worked 
  ok at first attempt and has not presented any problems.
  Since the Subversive plugin seems to be favored by Eclipse, this should be the recommended option.
  However, since Subclipse seems to work better, it is our recommended option. 
  Anyone who has the time an energy to sort out exactly how to setup and use Subversive with SMD yggdrasil 
  is more than welcome to share this knowledge! 
- Eclipse Development Target 
  For some reason, we can not make Maven/Tycho produce a target platform that also includes Eclipse source 
  and jUnit tests. Alas, we have to use a target platform that adds the online p2 repository to the platform.
  This is unfortunate but not a disaster since the target is only used in development and does not depend on 
  a local installation. We will have more features available thru this target platform than at runtime,
  offering the opportunity to add dependencies to feature not available at runtime. However, any mistakes 
  in this area will be rapidly detected when we try to build the client with Maven. 