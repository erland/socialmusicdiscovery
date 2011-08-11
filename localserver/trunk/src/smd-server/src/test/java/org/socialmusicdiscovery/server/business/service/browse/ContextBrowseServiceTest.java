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

package org.socialmusicdiscovery.server.business.service.browse;

import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class ContextBrowseServiceTest extends BaseTestCase {
    boolean logging = false;

    @BeforeClass
    public void setUpClass() {
        loadTestData("org.socialmusicdiscovery.server.business.model", "Arista RCA Releases.xml");
        updateSearchRelations();
    }

    @AfterMethod
    public void tearDownMethod(Method m) {
        try {
            // Stupid delay to make IntelliJ IDEA flush System.out to the console
            if(logging) Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Do nothing
        }
    }

    @Test
    public void testBrowseArtistWithoutChilds() throws Exception {
        ContextBrowseService browseService= new ContextBrowseService();
        Result result = browseService.findChildren(null, "Artist:231424b6-b3a9-45b8-bce2-77e694e67319",null,null,false);

        assert result.getCount()>0;
        assert result.getItems().size()>0;
        Collection<ResultItem> items = result.getItems();
        for (ResultItem item : items) {
            assert item.getChildItems()==null;
            if(logging) System.out.println(item.getItem().toString());
            queryAndPrintMenuLevel("","  ",item, false,null);
        }
    }

    @Test
    public void testBrowseReleaseWithoutChilds() throws Exception {
        ContextBrowseService browseService= new ContextBrowseService();
        Result result = browseService.findChildren(null, "Release:d972b0fa-42f5-45f9-ba56-2cede7666446",null,null,false);

        assert result.getCount()>0;
        assert result.getItems().size()>0;
        Collection<ResultItem> items = result.getItems();
        for (ResultItem item : items) {
            assert item.getChildItems()==null;
            if(logging) System.out.println(item.getItem().toString());
            queryAndPrintMenuLevel("","  ",item, false,null);
        }
    }

    @Test
    public void testBrowseTrackWithoutChilds() throws Exception {
        ContextBrowseService browseService= new ContextBrowseService();
        Result result = browseService.findChildren(null, "Track:1cbb8105-7732-4dfc-a423-def548b0a927",null,null,false);

        assert result.getCount()>0;
        assert result.getItems().size()>0;
        Collection<ResultItem> items = result.getItems();
        for (ResultItem item : items) {
            assert item.getChildItems()==null;
            if(logging) System.out.println(item.getItem().toString());
            queryAndPrintMenuLevel("","  ",item, false,null);
        }
    }

    @Test
    public void testBrowseClassificationWithoutChilds() throws Exception {
        ContextBrowseService browseService= new ContextBrowseService();
        Result result = browseService.findChildren(null, "Classification:819a598c-8d46-4391-ba90-2815f2690b72",null,null,false);

        assert result.getCount()>0;
        assert result.getItems().size()>0;
        Collection<ResultItem> items = result.getItems();
        for (ResultItem item : items) {
            assert item.getChildItems()==null;
            if(logging) System.out.println(item.getItem().toString());
            queryAndPrintMenuLevel("","  ",item, false,null);
        }
    }

    @Test
    public void testBrowseWithChilds() throws Exception {
        ContextBrowseService browseService= new ContextBrowseService();
        Result result = browseService.findChildren(null, "Artist:231424b6-b3a9-45b8-bce2-77e694e67319",1,1,true);

        assert result.getCount()>0;
        assert result.getItems().size()==1;
        Collection<ResultItem> items = result.getItems();
        for (ResultItem item : items) {
            assert item.getChildItems()!=null;
            assert item.getChildItems().size()>0;
            if(logging) System.out.println(item.getItem().toString()+getChildString(item.getChildItems()));
            queryAndPrintMenuLevel("Artist:231424b6-b3a9-45b8-bce2-77e694e67319","  ",item, true, (Long)item.getChildItems().values().iterator().next());
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
        ContextBrowseService browseService= new ContextBrowseService();
        String id = parentId;
        if(parentId.length()>0) {
            id = parentId+"/";
        }
        id=id+item.getId();
        Result result = browseService.findChildren(null, id,null,null,childs);
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
