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

package org.socialmusicdiscovery.server.plugins.mediaimport.filesystem;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.FileUtils;
import org.socialmusicdiscovery.server.business.logic.InjectHelper;
import org.socialmusicdiscovery.server.business.logic.config.MappedConfigurationContext;
import org.socialmusicdiscovery.server.business.logic.config.MemoryConfigurationManager;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.plugins.mediaimport.TrackData;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileSystemTest {
    FileSystem fileSystem;

    @Inject
    @Named("default-value")
    MemoryConfigurationManager defaultValueConfigurationManager;

    @BeforeClass
    public void setUp() {
        InjectHelper.injectMembers(this);
        fileSystem = new FileSystem();

        String pluginConfigurationPath = "org.socialmusicdiscovery.server.plugins.mediaimport." + fileSystem.getId() + ".";
        Set<ConfigurationParameter> defaultConfiguration = new HashSet<ConfigurationParameter>();
        for (ConfigurationParameter parameter : fileSystem.getDefaultConfiguration()) {
            ConfigurationParameterEntity entity = new ConfigurationParameterEntity(parameter);
            if (!entity.getId().startsWith(pluginConfigurationPath)) {
                entity.setId(pluginConfigurationPath + entity.getId());
            }
            entity.setDefaultValue(true);
            defaultConfiguration.add(entity);
        }
        defaultValueConfigurationManager.setParametersForPath(pluginConfigurationPath, defaultConfiguration);
        fileSystem.setConfiguration(new MappedConfigurationContext(pluginConfigurationPath, defaultValueConfigurationManager));
        fileSystem.init(null);
    }

    @Test
    public void testScanFileFlac() throws IOException {
        TrackData trackData = fileSystem.scanFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.flac"));
        assert trackData != null;
    }

    @Test
    public void testScanFileMp3() throws IOException {
        TrackData trackData = fileSystem.scanFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"));
        assert trackData != null;
    }

    @Test
    public void testScanDirectoryWithFiles() throws IOException {
        if (new File(BaseTestCase.getOutputDiretory() + "/scan-test").exists()) {
            FileUtils.deleteDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        }
        new File(BaseTestCase.getOutputDiretory() + "/scan-test").mkdir();

        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/testfile1.mp3"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.flac"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/testfile1.flac"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/testfile1.txt"));

        List<File> files = new FileSystem().scanDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        assert files != null;
        assert files.size() == 2;
    }

    @Test
    public void testScanDirectoryWithFilesAndSubDirectories() throws IOException {
        if (new File(BaseTestCase.getOutputDiretory() + "/scan-test").exists()) {
            FileUtils.deleteDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        }
        new File(BaseTestCase.getOutputDiretory() + "/scan-test").mkdir();
        new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir1").mkdir();
        new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir11").mkdir();
        new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2").mkdir();
        new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2/subdir21").mkdir();

        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir1/testfile1.mp3"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.flac"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir1/testfile1.flac"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir1/testfile1.txt"));

        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2/testfile1.mp3"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.flac"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2/testfile1.flac"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2/testfile1.txt"));

        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2/subdir21/testfile1.mp3"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.flac"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2/subdir21/testfile1.flac"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2/subdir21/testfile1.txt"));

        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/testfile1.mp3"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.flac"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/testfile1.flac"));
        FileUtils.copyFile(new File(BaseTestCase.getTestResourceDiretory() + "/org/socialmusicdiscovery/server/plugins/mediaimport/filesystem/testfile1.mp3"), new File(BaseTestCase.getOutputDiretory() + "/scan-test/testfile1.txt"));

        List<File> files = new FileSystem().scanDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        assert files != null;
        assert files.size() == 8;
    }

    @Test
    public void testScanDirectoryEmpty() throws IOException {
        if (new File(BaseTestCase.getOutputDiretory() + "/scan-test").exists()) {
            FileUtils.deleteDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        }
        new File(BaseTestCase.getOutputDiretory() + "/scan-test").mkdir();

        List<File> files = new FileSystem().scanDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        assert files != null;
        assert files.size() == 0;
    }

    @Test
    public void testScanDirectoryEmptyWithSubDirectories() throws IOException {
        if (new File(BaseTestCase.getOutputDiretory() + "/scan-test").exists()) {
            FileUtils.deleteDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        }
        new File(BaseTestCase.getOutputDiretory() + "/scan-test").mkdir();
        new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir1").mkdir();
        new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir11").mkdir();
        new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2").mkdir();
        new File(BaseTestCase.getOutputDiretory() + "/scan-test/subdir2/subdir21").mkdir();

        List<File> files = new FileSystem().scanDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        assert files != null;
        assert files.size() == 0;
    }

    @Test
    public void testScanDirectoryNonExisting() throws IOException {
        if (new File(BaseTestCase.getOutputDiretory() + "/scan-test").exists()) {
            FileUtils.deleteDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        }

        List<File> files = new FileSystem().scanDirectory(new File(BaseTestCase.getOutputDiretory() + "/scan-test"));
        assert files != null;
        assert files.size() == 0;
    }
}
