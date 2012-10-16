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

package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.socialmusicdiscovery.server.api.ConfigurationContext;
import org.socialmusicdiscovery.server.api.mediaimport.MediaImporter;
import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;

import org.socialmusicdiscovery.server.business.model.core.PlayableElement;
import org.socialmusicdiscovery.server.business.repository.GlobalIdentityRepository;
import org.socialmusicdiscovery.server.plugins.mediaimport.AbstractTagImporter;


/**
 * @author Robin
 * Main class for musicbrainz meta data importer, triggered by executeImport
 * For each files, it computes fingerprint and supervise metadata retrieving
 * and storage into SMD DB 
 */
public class AcoustIdMetadataImporter extends AbstractTagImporter implements MediaImporter {

	@Inject
    @Named("musicbrainz.acoustid.fpcalcFilename")
	private String fpcalcFilename;

	@Inject
    @Named("org.socialmusicdiscovery.path.binaries")
    private String binariesPath;

	/**
	 * Path were fpcalc.exe can be found
	 * - should be absolute or relative to program working directory
	 * - include both path and executable filename
	 * - is constructed from previous injected parameter (separated with "arch/<arch>" subdirectory)
	 */
	private String fpcalcPath;

    /**
     * ProcessBuilder used to create fpcalc process. 
     * It contains media file absolute filename as first argument and error stream redirected to standard output
     * @see AcoustIdMetadataImporter#init(Map) for initialization
     */
    private ProcessBuilder pb;
    
    /**
     * @author Robin
     * Simple class to hold length (in seconds) and acoustid fingerprint of a media file 
     */
    protected class DurationAndFingerprint {
		int length;
		String fingerprint;

		protected int getLength() {
			return length;
		}
		protected void setLength(int length) {
			this.length = length;
		}
		protected String getFingerprint() {
			return fingerprint;
		}
		protected void setFingerprint(String fingerprint) {
			this.fingerprint = fingerprint;
		}

    }
    
    /**
     * Nothing done in constructor yet, @see {@link AcoustIdMetadataImporter#init(Map)}
     */
    public AcoustIdMetadataImporter() {
    	//InjectHelper.injectMembers(this);
    }
    
    /**
     * @throws FileNotFoundException 
     * @inherit
     * Init of the plugin. Construct fpcalc binary path, check its existence and prepare process builder object
     */
    @Override
    public void init(Map<String, String> executionParameters)  {
        super.init(executionParameters);

        // TODO: replace static "arch/win32" with something smarter
        // inspired by http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
        try {
        	// Strange hack to concatenate path (how come that Java doesn't include such tools?
			this.fpcalcPath = new File(new File(binariesPath, "arch/win32").getCanonicalPath(), this.fpcalcFilename).getCanonicalPath();
		} catch (IOException e) {
			// TODO: change init interface method to throw exception?
			e.printStackTrace();
		}
    	if(this.fpcalcPath == null  || (new File(this.fpcalcPath).isFile() == false) ) {
    		//throw new FileNotFoundException("fpcalc executable path invalid or not set: "+ this.fpcalcPath);
    		// TODO: Throw InitFailedException in parent class?
    		System.err.println("fpcalc executable path invalid or not set: "+ this.fpcalcPath);
    		return;
    	}
    	// second argument is media filename and will be completed later in the process for each file
    	this.pb = new ProcessBuilder(this.fpcalcPath, null);
    	// we want stderr and stdout as a single stream 
    	this.pb.redirectErrorStream(true);
    }

    
    /**
     * @return true if fpcalc binary is available (used to skip unittests)
     */
    public boolean fpCalcFound() {
    	if(this.pb != null)
    		return true;
    	else
    		return false;
    }
    
    @Override
    public String getId() {
        return "acoustid";
    }

    @Override
    protected void executeImport(ProcessingStatusCallback progressHandler) {
    	// TODO: implement core logic
    	System.out.println("executeImport of acoustid plugin has been called");
    	// WIP this.entityManager
    	ConfigurationContext conf = this.getConfiguration();

    	for(PlayableElement playableElement: this.playableElementRepository.findAll()) {
    		System.out.println(playableElement.getUri());
    	}
    	// for PlayableElement playableElement:
    }
    
    /**
     * @param filename
     * @return Duration and Fingerprint of the song as computed by fpcalc executable
     * @throws IOException
     */
    protected DurationAndFingerprint computeAcoustIdfingerprintOnFilename(String filename) throws IOException {
    	//Process fpcalcProc = Runtime.getRuntime().exec(this.fpcalcPath);
    	
    	String line;
    	File file = new File(filename);
    	DurationAndFingerprint durationAndFingerprint = new DurationAndFingerprint();
    	
    	if(!file.isFile()) {
    		throw new FileNotFoundException("unable to find media file: "+ filename);
    	}
    	
    	pb.command().set(1, file.getAbsolutePath());
    	Process fpcalcProc = pb.start();
    	BufferedReader br = new BufferedReader( 
    							new InputStreamReader( fpcalcProc.getInputStream()) );
    	
    	// TODO: better error handling, throwing exception in case something goes wrong
    	// bad media file, bad fpcalc executable, etc.
    	while ((line = br.readLine()) != null) {
    		if(line.startsWith("FINGERPRINT=")) {
    			durationAndFingerprint.setFingerprint( line.substring("FINGERPRINT=".length()) );
    		}
    		
    		if(line.startsWith("DURATION=")) {
    			durationAndFingerprint.setLength( Integer.parseInt( line.substring("DURATION=".length()) ) );
    		}

    	}
    	
    	// System.out.println("fpcalc result: "+durationAndFingerprint.getLength()+':'+durationAndFingerprint.getFingerprint());
    	return durationAndFingerprint;
    }

}
