package org.socialmusicdiscovery.server.business.logic;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.model.SMDIdentity;
import org.socialmusicdiscovery.server.business.model.core.Track;
import org.socialmusicdiscovery.server.business.model.core.TrackEntity;
import org.socialmusicdiscovery.server.business.service.browse.LibraryBrowseService;
import org.socialmusicdiscovery.server.business.service.browse.Result;
import org.socialmusicdiscovery.server.business.service.browse.ResultItem;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class LibraryBrowseServiceTest extends BaseTestCase {
    boolean logging = false;

    @BeforeTest
    public void setUp() {
        super.setUp();
    }

    @AfterTest
    public void tearDown() {
        super.tearDown();
    }

    @BeforeMethod
    public void setUpMethod(Method m) {
        System.out.println("Executing " + getClass().getSimpleName() + "." + m.getName() + "...");
        em.clear();
    }

    @BeforeClass
    public void setUpClass() {
        try {
            loadTestData("org.socialmusicdiscovery.server.business.model", "Arista RCA Releases.xml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        new SearchRelationPostProcessor().execute(new ProcessingStatusCallback() {
            public void progress(String module, String currentDescription, Long currentNo, Long totalNo) {
            }

            public void failed(String module, String error) {
            }

            public void finished(String module) {
            }

            public void aborted(String module) {
            }
        });
    }

    @AfterMethod
    public void tearDownMethod(Method m) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    @Test
    public void testBrowseWithoutChilds() throws Exception {
        LibraryBrowseService browseService= new LibraryBrowseService();
        Result result = browseService.findChildren(null,null,null,false);

        Collection<ResultItem> items = result.getItems();
        for (ResultItem item : items) {
            if(logging) System.out.println(item.getItem().toString());
            queryAndPrintMenuLevel("  ",item, false);
        }
    }

    @Test
    public void testBrowseWithChilds() throws Exception {
        LibraryBrowseService browseService= new LibraryBrowseService();
        Result result = browseService.findChildren(null,1,1,true);

        Collection<ResultItem> items = result.getItems();
        for (ResultItem item : items) {
            if(logging) System.out.println(item.getItem().toString()+getChildString(item.getChildItems()));
            queryAndPrintMenuLevel("  ",item, true);
        }
    }

    private String getChildString(Map<String, Long> childs) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Long> entry : childs.entrySet()) {
            if(sb.length()>0) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        if(sb.length()==0) {
            return "";
        }else {
            return " ("+sb.toString()+")";
        }
    }
    private void queryAndPrintMenuLevel(String prefix, ResultItem item, boolean childs) {
        LibraryBrowseService browseService= new LibraryBrowseService();
        Result result = browseService.findChildren(item.getId(),null,null,childs);
        Collection<ResultItem> childItems = result.getItems();
        for (ResultItem childItem : childItems) {
            if(childItem.getItem() instanceof TrackEntity) {
                Track track = (Track) childItem.getItem();
                if(track.getMedium() != null) {
                    if(track.getMedium().getName()!=null) {
                        if(logging) System.out.println(prefix+track.getMedium().getName()+"-"+track.getNumber()+" "+track.getRecording().getWork().getName()+getChildString(childItem.getChildItems()));
                    }else {
                        if(logging) System.out.println(prefix+track.getMedium().getNumber()+"-"+track.getNumber()+" "+track.getRecording().getWork().getName()+getChildString(childItem.getChildItems()));
                    }
                }else {
                    if(logging) System.out.println(prefix+track.getNumber()+" "+track.getRecording().getWork().getName()+getChildString(childItem.getChildItems()));
                }
            }else if(childItem.getItem() instanceof SMDIdentity) {
                try {
                    Method m = childItem.getItem().getClass().getMethod("getName");
                    if(logging) System.out.println(prefix + m.invoke(childItem.getItem())+getChildString(childItem.getChildItems()));
                } catch (Exception e) {
                    if(logging) System.out.println(prefix+childItem.getItem().toString()+getChildString(childItem.getChildItems()));
                }
            }else {
                if(logging) System.out.println(prefix+childItem.getItem().toString()+getChildString(childItem.getChildItems()));
            }
            queryAndPrintMenuLevel(prefix+"  ",childItem, childs);
        }
    }
}
