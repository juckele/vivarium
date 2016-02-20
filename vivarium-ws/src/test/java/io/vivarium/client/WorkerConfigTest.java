package io.vivarium.client;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;

import com.johnuckele.vtest.Tester;

import io.vivarium.serialization.FileIO;
import io.vivarium.test.FastTest;
import io.vivarium.test.SystemTest;
import io.vivarium.util.UserFacingError;

public class WorkerConfigTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public String path;
    private String defaultConfigFile;

    @Before
    public void setupPath() throws IOException
    {
        path = folder.getRoot().getCanonicalPath() + File.separator;
        defaultConfigFile = path + WorkerConfig.DEFAULT_PATH;
    }

    @Test(expected = UserFacingError.class)
    @Category({ FastTest.class, SystemTest.class })
    public void testMissingFile()
    {
        WorkerConfig.loadWorkerConfig(new File(path + "missing_file.json"), false);
    }

    @Test
    @Category({ FastTest.class, SystemTest.class })
    public void testCreateOnMissing()
    {
        File configFile = new File(defaultConfigFile);
        Tester.isFalse("Config file should not yet exist", configFile.exists());
        WorkerConfig config = WorkerConfig.loadWorkerConfig(configFile, true);
        Tester.isNotNull("config should not be null", config);
        Tester.isNotNull("config workerID should not be null", config.workerID);
        Tester.isNotNull("config throughputs should not be null", config.throughputs);
        Tester.isTrue("Config file should now exist", configFile.exists());
    }

    @Test
    @Category({ FastTest.class, SystemTest.class })
    public void testCreateAndReload()
    {
        File configFile = new File(defaultConfigFile);
        WorkerConfig.loadWorkerConfig(configFile, true);
        WorkerConfig config = WorkerConfig.loadWorkerConfig(configFile, false);
        Tester.isNotNull("config should not be null", config);
        Tester.isNotNull("config workerID should not be null", config.workerID);
        Tester.isNotNull("config throughputs should not be null", config.throughputs);
    }

    @Test(expected = UserFacingError.class)
    @Category({ FastTest.class, SystemTest.class })
    public void testBadInput()
    {
        FileIO.saveStringToFile("malformed config file", defaultConfigFile);
        WorkerConfig.loadWorkerConfig(new File(defaultConfigFile), true);
    }
}
