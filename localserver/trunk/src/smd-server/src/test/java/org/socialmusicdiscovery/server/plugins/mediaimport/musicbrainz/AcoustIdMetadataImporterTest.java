package org.socialmusicdiscovery.server.plugins.mediaimport.musicbrainz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MemoryConfigurationManager;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.plugins.mediaimport.TrackData;
import org.socialmusicdiscovery.server.plugins.mediaimport.filesystem.FileSystem;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AcoustIdMetadataImporterTest {

	AcoustIdMetadataImporter ami;

	@Inject
	@Named("default-value")
	MemoryConfigurationManager defaultValueConfigurationManager;

	@Test
	public void executeImport() throws FileNotFoundException {
		if(!ami.fpCalcFound()) {
			System.out.println("AcoustId fpcalc binary not found, skipping tests");
			return;
		}
		ami.executeImport(null);

	}

	@Test(dataProvider = "Media-Files-With-Fingerprints")
	public void computeAcoustIdfingerprintOnFilename(String filename, String fingerprint) throws IOException {
		if(!ami.fpCalcFound()) {
			System.out.println("AcoustId fpcalc binary not found, skipping tests");
			return;
		}
        String basedir = BaseTestCase.getTestResourceDiretory()+"/org/socialmusicdiscovery/server/plugins/mediaimport/musicbrainz/";
        assert ami.computeAcoustIdfingerprintOnFilename(basedir.concat(filename)).getFingerprint().equals(fingerprint);

//		ami.computeAcoustIdfingerprintOnFilename("J:\\documents\\Projects\\perso\\workspace\\smd\\localserver\\trunk\\src\\smd-server\\src\\test\\resources\\org\\socialmusicdiscovery\\server\\plugins\\mediaimport\\filesystem\\testfile1.flac");
//		ami.computeAcoustIdfingerprintOnFilename("J:\\documents\\Projects\\perso\\workspace\\smd\\localserver\\trunk\\src\\smd-server\\src\\test\\resources\\org\\socialmusicdiscovery\\server\\plugins\\mediaimport\\filesystem\\testfile2separated.flac");
	}

	@DataProvider(name = "Media-Files-With-Fingerprints")
	public Object[][] parameterIntTestProvider() {
		return new Object[][]{
				{"440hz-125sec.flac", "AQADtEmUaEkSZSoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"},
				{"440hz-125sec.mp3", "AQADtEmUaEkSZSoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"},
				{"440hz-125sec.ogg", "AQADtEmUaEkSZSoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"},
		};
	}
	
	@Test
	public void getId() throws FileNotFoundException {
		assert ami.getId().equals("acoustid");
	}

	@BeforeClass
	public void setUp() {
		InjectHelper.injectMembers(this);
		ami = new AcoustIdMetadataImporter();

		String pluginConfigurationPath = "org.socialmusicdiscovery.server.plugins.mediaimport."
				+ ami.getId() + ".";
		Set<ConfigurationParameter> defaultConfiguration = new HashSet<ConfigurationParameter>();
		for (ConfigurationParameter parameter : ami.getDefaultConfiguration()) {
			ConfigurationParameterEntity entity = new ConfigurationParameterEntity(
					parameter);
			if (!entity.getId().startsWith(pluginConfigurationPath)) {
				entity.setId(pluginConfigurationPath + entity.getId());
			}
			entity.setDefaultValue(true);
			defaultConfiguration.add(entity);
		}

		defaultValueConfigurationManager.setParametersForPath(
				pluginConfigurationPath, defaultConfiguration);
		ami.setConfiguration(new MappedConfigurationContext(
				pluginConfigurationPath, defaultValueConfigurationManager));
		Map<String, String> parameters = new HashMap<String, String>();
		ami.init(parameters);
	}

	@BeforeTest
	public void beforeTest() {
	}

}