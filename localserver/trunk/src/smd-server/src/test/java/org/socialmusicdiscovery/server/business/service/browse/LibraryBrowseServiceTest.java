package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.server.api.mediaimport.ProcessingStatusCallback;
import org.socialmusicdiscovery.server.business.logic.SearchRelationPostProcessor;
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
        try {
            // Stupid delay to make IntelliJ IDEA flush System.out to the console
            if(logging) Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Do nothing
        }
    }

    @Test
    public void testBrowseWithoutChilds() throws Exception {
        LibraryBrowseService browseService= new LibraryBrowseService();
        Result result = browseService.findChildren(null,null,null,false);

        Collection<ResultItem> items = result.getItems();
        for (ResultItem item : items) {
            assert item.getChildItems()==null;
            if(logging) System.out.println(item.getItem().toString());
            queryAndPrintMenuLevel("","  ",item, false,null);
        }
    }

    @Test
    public void testBrowseWithChilds() throws Exception {
        LibraryBrowseService browseService= new LibraryBrowseService();
        Result result = browseService.findChildren(null,1,1,true);

        Collection<ResultItem> items = result.getItems();
        for (ResultItem item : items) {
            assert item.getChildItems()!=null;
            assert item.getChildItems().size()>0;
            if(logging) System.out.println(item.getItem().toString()+getChildString(item.getChildItems()));
            queryAndPrintMenuLevel("","  ",item, true, (Long)item.getChildItems().values().iterator().next());
        }
    }

    private String getChildString(Map<String, Long> childs) {
        StringBuffer sb = new StringBuffer();
        if(childs!=null) {
            for (Map.Entry<String, Long> entry : childs.entrySet()) {
                if(sb.length()>0) {
                    sb.append(", ");
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        if(sb.length()==0) {
            return "";
        }else {
            return " ("+sb.toString()+")";
        }
    }
    private void queryAndPrintMenuLevel(String parentId, String prefix, ResultItem item, boolean childs, Long noOfChilds) {
        LibraryBrowseService browseService= new LibraryBrowseService();
        String id = parentId;
        if(parentId.length()>0) {
            id = parentId+"/";
        }
        id=id+item.getId();
        Result result = browseService.findChildren(id,null,null,childs);
        Collection<ResultItem> childItems = result.getItems();
        if(childs) {
            assert noOfChilds==childItems.size();
        }
        for (ResultItem childItem : childItems) {
            if(logging) System.out.println(prefix+childItem.getName());
            if(childs) {
                assert childItem.getChildItems()!=null;
                if(childItem.getChildItems().size()>0) {
                    assert childItem.getChildItems().size()==1;
                    queryAndPrintMenuLevel(id,prefix+"  ",childItem, childs, (Long)childItem.getChildItems().values().iterator().next());
                }else {
                    queryAndPrintMenuLevel(id,prefix+"  ",childItem, childs, 0L);
                }
            }else {
                assert childItem.getChildItems()==null;
                queryAndPrintMenuLevel(id,prefix+"  ",childItem, childs, 0L);
            }
        }
    }
}
