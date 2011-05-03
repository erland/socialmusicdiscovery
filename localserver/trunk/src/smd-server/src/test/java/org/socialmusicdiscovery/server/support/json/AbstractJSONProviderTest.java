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

package org.socialmusicdiscovery.server.support.json;

import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.*;

public class AbstractJSONProviderTest extends BaseTestCase {
    public static interface ItemInterface {
        String getId();

        void setId(String id);
    }

    public static interface ObjectInterface {
        List<ItemInterface> getList();

        void setList(List<ItemInterface> list);

        Set<ItemInterface> getSet();

        void setSet(Set<ItemInterface> set);
    }

    public static class ServiceItemInstance implements ItemInterface {
        @Expose
        String id;

        @Expose
        String reversedId;

        public ServiceItemInstance() {
        }

        public ServiceItemInstance(String id) {
            this.id = id;
            this.reversedId = "Service" + StringUtils.reverse(id);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
            this.reversedId = "Service" + StringUtils.reverse(id);
        }

        public String getReversedId() {
            return reversedId;
        }
    }

    public static class ClientItemInstance implements ItemInterface, Comparable {
        @Expose
        String id;

        String reversedId;

        public ClientItemInstance() {
        }

        public ClientItemInstance(String id) {
            this.id = id;
            this.reversedId = "Client" + StringUtils.reverse(id);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
            this.reversedId = "Client" + StringUtils.reverse(id);
        }

        public String getReversedId() {
            return reversedId;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ClientItemInstance)) return false;
            return EqualsBuilder.reflectionEquals(this, o);
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public int compareTo(Object o) {
            return CompareToBuilder.reflectionCompare(this, o);
        }
    }

    public static class ClientObjectInstance implements ObjectInterface {
        @Expose
        ClientList<ItemInterface> list = new ClientLinkedList();
        @Expose
        ClientSet<ItemInterface> set = new ClientTreeSet<ItemInterface>();

        public ClientObjectInstance() {
        }

        public List<ItemInterface> getList() {
            return list;
        }

        public void setList(List<ItemInterface> list) {
            this.list.clear();
            this.list.addAll(list);
        }

        public Set<ItemInterface> getSet() {
            return set;
        }

        public void setSet(Set<ItemInterface> set) {
            this.set.clear();
            this.set.addAll(set);
        }
    }

    public static class ConcreteObjectInstance implements ObjectInterface {
        @Expose
        ConcreteLinkedList<ItemInterface> list = new ConcreteLinkedList<ItemInterface>();
        @Expose
        ConcreteTreeSet<ItemInterface> set = new ConcreteTreeSet<ItemInterface>();

        public ConcreteObjectInstance() {
        }

        public List<ItemInterface> getList() {
            return list;
        }

        public void setList(List<ItemInterface> list) {
            this.list.clear();
            this.list.addAll(list);
        }

        public Set<ItemInterface> getSet() {
            return set;
        }

        public void setSet(Set<ItemInterface> set) {
            this.set.clear();
            this.set.addAll(set);
        }
    }

    public static class ServiceObjectInstance implements ObjectInterface {
        @Expose
        List<ItemInterface> list = new ArrayList<ItemInterface>();
        @Expose
        Set<ItemInterface> set = new HashSet<ItemInterface>();

        public ServiceObjectInstance() {
        }

        public List<ItemInterface> getList() {
            return list;
        }

        public void setList(List<ItemInterface> list) {
            this.list.clear();
            this.list.addAll(list);
        }

        public Set<ItemInterface> getSet() {
            return set;
        }

        public void setSet(Set<ItemInterface> set) {
            this.set.clear();
            this.set.addAll(set);
        }
    }

    public static interface ClientList<T> extends List {
    }

    public static interface ConcreteList extends List {
    }

    public static interface ClientSet<T> extends Set<T> {
    }

    public static interface ConcreteSet extends Set {
    }

    public static class ClientLinkedList extends LinkedList implements ClientList {
    }

    public static class ConcreteLinkedList<T> extends LinkedList implements ConcreteList {
    }

    public static class ClientTreeSet<T> extends TreeSet<T> implements ClientSet<T> {
    }

    public static class ConcreteTreeSet<T> extends TreeSet implements ConcreteSet {
    }

    public static class ClientJSONProvider extends AbstractJSONProvider {
        public ClientJSONProvider() {
            super(true);
        }

        @Override
        protected Map<Class, Class> getConversionMap() {
            Map<Class, Class> converters = new HashMap<Class, Class>();

            converters.put(ItemInterface.class, ClientItemInstance.class);
            converters.put(ObjectInterface.class, ClientObjectInstance.class);
            converters.put(ClientSet.class, ClientTreeSet.class);
            converters.put(ClientList.class, ClientLinkedList.class);
            converters.put(Collection.class, ArrayList.class);
            return converters;
        }
    }

    public static class ConcreteJSONProvider extends AbstractJSONProvider {
        public ConcreteJSONProvider() {
            super(true);
        }

        @Override
        protected Map<Class, Class> getConversionMap() {
            Map<Class, Class> converters = new HashMap<Class, Class>();

            converters.put(ItemInterface.class, ClientItemInstance.class);
            converters.put(ObjectInterface.class, ConcreteObjectInstance.class);
            converters.put(ConcreteLinkedList.class, ConcreteLinkedList.class);
            converters.put(ConcreteTreeSet.class, ConcreteTreeSet.class);
            return converters;
        }
    }

    public static class ServiceJSONProvider extends AbstractJSONProvider {
        public ServiceJSONProvider() {
            super(true);
        }

        @Override
        protected Map<Class, Class> getConversionMap() {
            Map<Class, Class> converters = new HashMap<Class, Class>();

            converters.put(ItemInterface.class, ServiceItemInstance.class);
            converters.put(ObjectInterface.class, ServiceObjectInstance.class);
            return converters;
        }
    }

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

    @Test
    public void testClientFromJson() {
        String json = "[\n" +
                "\t{\n" +
                "\t\tlist: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"first\",\n" +
                "\t\t\t\treversedId: \"tsrif\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"second\",\n" +
                "\t\t\t\treversedId: \"dnoces\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\tset: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"third\",\n" +
                "\t\t\t\treversedId: \"driht\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"forth\",\n" +
                "\t\t\t\treversedId: \"htrof\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\tlist: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"fifth\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"sixth\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\tset: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"seventh\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"eight\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "]";
        Collection<ClientObjectInstance> result = (Collection<ClientObjectInstance>) new ClientJSONProvider().fromJson(json, new TypeToken<Collection<ClientObjectInstance>>() {
        }.getType());
        assert result != null;
        assert result instanceof ArrayList;
        assert result.size() == 2;
        Iterator iterator = result.iterator();
        Object first = iterator.next();
        Object second = iterator.next();

        assert first instanceof ClientObjectInstance;
        assert second instanceof ClientObjectInstance;

        assert ((ClientObjectInstance) first).getList() instanceof ClientLinkedList;
        assert ((ClientObjectInstance) first).getList().size() == 2;
        assert ((ClientObjectInstance) first).getList().iterator().next() instanceof ClientItemInstance;
        assert ((ClientItemInstance) ((ClientObjectInstance) first).getList().iterator().next()).getReversedId() == null;
        assert ((ClientObjectInstance) second).getList() instanceof ClientLinkedList;
        assert ((ClientObjectInstance) second).getList().size() == 2;
        assert ((ClientObjectInstance) second).getList().iterator().next() instanceof ClientItemInstance;
        assert ((ClientItemInstance) ((ClientObjectInstance) second).getList().iterator().next()).getReversedId() == null;

        assert ((ClientObjectInstance) first).getSet() instanceof ClientTreeSet;
        assert ((ClientObjectInstance) first).getSet().size() == 2;
        assert ((ClientObjectInstance) first).getSet().iterator().next() instanceof ClientItemInstance;
        assert ((ClientItemInstance) ((ClientObjectInstance) first).getSet().iterator().next()).getReversedId() == null;
        assert ((ClientObjectInstance) second).getSet() instanceof ClientTreeSet;
        assert ((ClientObjectInstance) second).getSet().size() == 2;
        assert ((ClientObjectInstance) second).getSet().iterator().next() instanceof ClientItemInstance;
        assert ((ClientItemInstance) ((ClientObjectInstance) second).getSet().iterator().next()).getReversedId() == null;
    }

    @Test
    public void testClientToJson() {
        ClientObjectInstance firstInstance = new ClientObjectInstance();
        firstInstance.getList().add(new ClientItemInstance("first"));
        firstInstance.getList().add(new ClientItemInstance("second"));
        firstInstance.getSet().add(new ClientItemInstance("third"));
        firstInstance.getSet().add(new ClientItemInstance("forth"));

        ClientObjectInstance secondInstance = new ClientObjectInstance();
        secondInstance.getList().add(new ClientItemInstance("fifth"));
        secondInstance.getList().add(new ClientItemInstance("sixth"));
        secondInstance.getSet().add(new ClientItemInstance("seventh"));
        secondInstance.getSet().add(new ClientItemInstance("eight"));

        Collection<ClientObjectInstance> instance = new ArrayList<ClientObjectInstance>();
        instance.add(firstInstance);
        instance.add(secondInstance);

        String json = new ClientJSONProvider().toJson(instance);
        assert json != null;
        assert json.length() > 0;
        assert !json.contains("Client");
        assert !json.contains("Service");
        assert !json.contains("SetItem");
        assert !json.contains("ListItem");
        assert json.contains("first");
        assert !json.contains("tsrif");
        assert json.contains("second");
        assert !json.contains("dnoces");
        assert json.contains("third");
        assert !json.contains("driht");
        assert json.contains("forth");
        assert !json.contains("htrof");
        assert json.contains("fifth");
        assert !json.contains("htfif");
        assert json.contains("sixth");
        assert !json.contains("htxis");
        assert json.contains("seventh");
        assert !json.contains("htneves");
        assert json.contains("eight");
        assert !json.contains("thgie");
    }

    @Test
    public void testServiceFromJson() {
        String json = "[\n" +
                "\t{\n" +
                "\t\tlist: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"first\",\n" +
                "\t\t\t\treversedId: \"tsrif\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"second\",\n" +
                "\t\t\t\treversedId: \"dnoces\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\tset: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"third\",\n" +
                "\t\t\t\treversedId: \"driht\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"forth\",\n" +
                "\t\t\t\treversedId: \"htrof\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\tlist: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"fifth\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"sixth\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\tset: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"seventh\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"eight\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "]";
        Collection<ServiceObjectInstance> result = (Collection<ServiceObjectInstance>) new ServiceJSONProvider().fromJson(json, new TypeToken<Collection<ServiceObjectInstance>>() {
        }.getType());
        assert result != null;
        assert result.size() == 2;
        Iterator iterator = result.iterator();
        Object first = iterator.next();
        Object second = iterator.next();

        assert first instanceof ServiceObjectInstance;
        assert second instanceof ServiceObjectInstance;

        assert !(((ServiceObjectInstance) first).getList() instanceof ClientList);
        assert ((ServiceObjectInstance) first).getList().size() == 2;
        assert ((ServiceObjectInstance) first).getList().iterator().next() instanceof ServiceItemInstance;
        assert ((ServiceItemInstance) ((ServiceObjectInstance) first).getList().iterator().next()).getReversedId() != null;
        assert !(((ServiceObjectInstance) second).getList() instanceof ClientList);
        assert ((ServiceObjectInstance) second).getList().size() == 2;
        assert ((ServiceObjectInstance) second).getList().iterator().next() instanceof ServiceItemInstance;
        assert ((ServiceItemInstance) ((ServiceObjectInstance) second).getList().iterator().next()).getReversedId() == null;

        assert !(((ServiceObjectInstance) first).getSet() instanceof ClientSet);
        assert ((ServiceObjectInstance) first).getSet().size() == 2;
        assert ((ServiceObjectInstance) first).getSet().iterator().next() instanceof ServiceItemInstance;
        assert ((ServiceItemInstance) ((ServiceObjectInstance) first).getSet().iterator().next()).getReversedId() != null;
        assert !(((ServiceObjectInstance) second).getSet() instanceof ClientSet);
        assert ((ServiceObjectInstance) second).getSet().size() == 2;
        assert ((ServiceObjectInstance) second).getSet().iterator().next() instanceof ServiceItemInstance;
        assert ((ServiceItemInstance) ((ServiceObjectInstance) second).getSet().iterator().next()).getReversedId() == null;
    }

    @Test
    public void testServiceToJson() {
        ServiceObjectInstance firstInstance = new ServiceObjectInstance();
        firstInstance.getList().add(new ServiceItemInstance("first"));
        firstInstance.getList().add(new ServiceItemInstance("second"));
        firstInstance.getSet().add(new ServiceItemInstance("third"));
        firstInstance.getSet().add(new ServiceItemInstance("forth"));

        ServiceObjectInstance secondInstance = new ServiceObjectInstance();
        secondInstance.getList().add(new ServiceItemInstance("fifth"));
        secondInstance.getList().add(new ServiceItemInstance("sixth"));
        secondInstance.getSet().add(new ServiceItemInstance("seventh"));
        secondInstance.getSet().add(new ServiceItemInstance("eight"));

        Collection<ServiceObjectInstance> instance = new ArrayList<ServiceObjectInstance>();
        instance.add(firstInstance);
        instance.add(secondInstance);

        String json = new ServiceJSONProvider().toJson(instance);
        assert json != null;
        assert json.length() > 0;
        assert !json.contains("Client");
        assert json.contains("Service");
        assert !json.contains("SetItem");
        assert !json.contains("ListItem");
        assert json.contains("first");
        assert json.contains("tsrif");
        assert json.contains("second");
        assert json.contains("dnoces");
        assert json.contains("third");
        assert json.contains("driht");
        assert json.contains("forth");
        assert json.contains("htrof");
        assert json.contains("fifth");
        assert json.contains("htfif");
        assert json.contains("sixth");
        assert json.contains("htxis");
        assert json.contains("seventh");
        assert json.contains("htneves");
        assert json.contains("eight");
        assert json.contains("thgie");
    }

    @Test
    public void testConcreteFromJson() {
        String json = "[\n" +
                "\t{\n" +
                "\t\tlist: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"first\",\n" +
                "\t\t\t\treversedId: \"tsrif\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"second\",\n" +
                "\t\t\t\treversedId: \"dnoces\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\tset: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"third\",\n" +
                "\t\t\t\treversedId: \"driht\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"forth\",\n" +
                "\t\t\t\treversedId: \"htrof\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\tlist: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"fifth\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"sixth\"\n" +
                "\t\t\t}\n" +
                "\t\t],\n" +
                "\t\tset: [\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"seventh\"\n" +
                "\t\t\t},\n" +
                "\t\t\t{\n" +
                "\t\t\t\tid: \"eight\"\n" +
                "\t\t\t}\n" +
                "\t\t]\n" +
                "\t}\n" +
                "]";
        Collection<ObjectInterface> result = (Collection<ObjectInterface>) new ConcreteJSONProvider().fromJson(json, new TypeToken<Collection<ObjectInterface>>() {
        }.getType());
        assert result != null;
        assert result.size() == 2;
        Iterator iterator = result.iterator();
        Object first = iterator.next();
        Object second = iterator.next();

        assert first instanceof ConcreteObjectInstance;
        assert second instanceof ConcreteObjectInstance;

        assert ((ConcreteObjectInstance) first).getList() instanceof ConcreteLinkedList;
        assert ((ConcreteObjectInstance) first).getList().size() == 2;
        assert ((ConcreteObjectInstance) first).getList().iterator().next() instanceof ClientItemInstance;
        assert ((ClientItemInstance) ((ConcreteObjectInstance) first).getList().iterator().next()).getReversedId() == null;
        assert ((ConcreteObjectInstance) second).getList() instanceof ConcreteLinkedList;
        assert ((ConcreteObjectInstance) second).getList().size() == 2;
        assert ((ConcreteObjectInstance) second).getList().iterator().next() instanceof ClientItemInstance;
        assert ((ClientItemInstance) ((ConcreteObjectInstance) second).getList().iterator().next()).getReversedId() == null;

        assert ((ConcreteObjectInstance) first).getSet() instanceof ConcreteTreeSet;
        assert ((ConcreteObjectInstance) first).getSet().size() == 2;
        assert ((ConcreteObjectInstance) first).getSet().iterator().next() instanceof ClientItemInstance;
        assert ((ClientItemInstance) ((ConcreteObjectInstance) first).getSet().iterator().next()).getReversedId() == null;
        assert ((ConcreteObjectInstance) second).getSet() instanceof ConcreteTreeSet;
        assert ((ConcreteObjectInstance) second).getSet().size() == 2;
        assert ((ConcreteObjectInstance) second).getSet().iterator().next() instanceof ClientItemInstance;
        assert ((ClientItemInstance) ((ConcreteObjectInstance) second).getSet().iterator().next()).getReversedId() == null;
    }

    @Test
    public void testConcreteToJson() {
        ConcreteObjectInstance firstInstance = new ConcreteObjectInstance();
        firstInstance.getList().add(new ClientItemInstance("first"));
        firstInstance.getList().add(new ClientItemInstance("second"));
        firstInstance.getSet().add(new ClientItemInstance("third"));
        firstInstance.getSet().add(new ClientItemInstance("forth"));

        ConcreteObjectInstance secondInstance = new ConcreteObjectInstance();
        secondInstance.getList().add(new ClientItemInstance("fifth"));
        secondInstance.getList().add(new ClientItemInstance("sixth"));
        secondInstance.getSet().add(new ClientItemInstance("seventh"));
        secondInstance.getSet().add(new ClientItemInstance("eight"));

        Collection<ConcreteObjectInstance> instance = new ArrayList<ConcreteObjectInstance>();
        instance.add(firstInstance);
        instance.add(secondInstance);

        String json = new ConcreteJSONProvider().toJson(instance);
        assert json != null;
        assert json.length() > 0;
        assert !json.contains("Client");
        assert !json.contains("Service");
        assert !json.contains("SetItem");
        assert !json.contains("ListItem");
        assert json.contains("first");
        assert !json.contains("tsrif");
        assert json.contains("second");
        assert !json.contains("dnoces");
        assert json.contains("third");
        assert !json.contains("driht");
        assert json.contains("forth");
        assert !json.contains("htrof");
        assert json.contains("fifth");
        assert !json.contains("htfif");
        assert json.contains("sixth");
        assert !json.contains("htxis");
        assert json.contains("seventh");
        assert !json.contains("htneves");
        assert json.contains("eight");
        assert !json.contains("thgie");
    }

}
