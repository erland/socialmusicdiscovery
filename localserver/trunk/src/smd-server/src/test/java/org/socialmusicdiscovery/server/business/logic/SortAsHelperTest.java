package org.socialmusicdiscovery.server.business.logic;

import com.google.inject.Inject;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameter;
import org.socialmusicdiscovery.server.business.model.config.ConfigurationParameterEntity;
import org.socialmusicdiscovery.server.business.repository.config.ConfigurationParameterRepository;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

public class SortAsHelperTest extends BaseTestCase {
    @Inject
    ConfigurationParameterRepository configurationParameterRepository;

    String PREFIX = SortAsHelper.class.getName() + ".";

    @AfterMethod
    @BeforeMethod
    public void cleanUpConfiguration() {
        Collection<ConfigurationParameterEntity> entities = configurationParameterRepository.findByPath(PREFIX);
        for (ConfigurationParameterEntity entity : entities) {
            configurationParameterRepository.remove(entity);
        }

    }

    @Test
    public void testUpperCase() {

        assert SortAsHelper.getSortAsForValue("Artist", "äöåéíü").equals("ÄÖÅÉÍÜ");

        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Artist.uppercase", ConfigurationParameter.Type.BOOLEAN, "false"));

        assert SortAsHelper.getSortAsForValue("Artist", "äöåéíü").equals("äöåéíü");
    }

    @Test
    void testAccented() {
        assert SortAsHelper.getSortAsForValue("Artist", "äöåéíü").equals("ÄÖÅÉÍÜ");

        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Artist.removeAccents", ConfigurationParameter.Type.BOOLEAN, "true"));

        assert SortAsHelper.getSortAsForValue("Artist", "äöåéíü").equals("AOAEIU");
    }

    @Test
    public void testMatching() {
        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Artist.sortAsExpression", ConfigurationParameter.Type.STRING, "^(.*) ([^ ]*)$"));
        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Artist.sortAsValue", ConfigurationParameter.Type.STRING, "$2 $1"));

        assert SortAsHelper.getSortAsForValue("Artist", "Frank Zappa").equals("ZAPPA FRANK");

        assert SortAsHelper.getSortAsForValue("Artist", "Wolfgang Amadeus Mozart").equals("MOZART WOLFGANG AMADEUS");

        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Artist.sortAsExpression", ConfigurationParameter.Type.STRING, "^(.*) ([^ ]*)$"));
        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Artist.sortAsValue", ConfigurationParameter.Type.STRING, "$2"));

        assert SortAsHelper.getSortAsForValue("Artist", "Frank Zappa").equals("ZAPPA");

        assert SortAsHelper.getSortAsForValue("Artist", "Wolfgang Amadeus Mozart").equals("MOZART");

        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Artist.sortAsExpression", ConfigurationParameter.Type.STRING, ""));
        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Artist.sortAsValue", ConfigurationParameter.Type.STRING, ""));

        assert SortAsHelper.getSortAsForValue("Artist", "Frank Zappa").equals("FRANK ZAPPA");

        assert SortAsHelper.getSortAsForValue("Artist", "Wolfgang Amadeus Mozart").equals("WOLFGANG AMADEUS MOZART");
    }

    @Test
    public void testIgnoredArticles() {
        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Work.ignoredArticles", ConfigurationParameter.Type.STRING, "(?i)(The|El|La|Los|Las|Le|Les)"));

        assert SortAsHelper.getSortAsForValue("Work", "The Final Countdown").equals("FINAL COUNTDOWN");

        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Work.ignoredArticles", ConfigurationParameter.Type.STRING, ""));

        assert SortAsHelper.getSortAsForValue("Work", "The Final Countdown").equals("THE FINAL COUNTDOWN");
    }

    @Test
    public void testRemovePunctuation() {
        assert SortAsHelper.getSortAsForValue("Work", "...Baby One More Time").equals("BABY ONE MORE TIME");
        assert SortAsHelper.getSortAsForValue("Work", "U & Me = Love").equals("U ME LOVE");

        configurationParameterRepository.merge(new ConfigurationParameterEntity(PREFIX + "Work.removePunctuation", ConfigurationParameter.Type.BOOLEAN, "false"));

        assert SortAsHelper.getSortAsForValue("Work", "...Baby One More Time").equals("...BABY ONE MORE TIME");
    }
}
