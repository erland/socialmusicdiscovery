package org.socialmusicdiscovery.server.support.copy;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;

public class CopyHelperTest extends BaseTestCase {
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

    @AfterMethod
    public void tearDownMethod(Method m) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    public static class CopyHelperCache implements CopyHelper.Cache {
        Map<Object,Object> map = new HashMap<Object,Object>();
        @Override
        public Object load(Object cacheKey) {
            return map.get(cacheKey);
        }

        @Override
        public void store(Object cacheKey, Object object) {
            map.put(cacheKey,object);
        }
    }
    public static class Primitives {
        @Expose
        private int small;
        private long large;
        public void init() {
            small = 1;
            large = 2L;
        }
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }
    }
    public static class Immutable {
        @Expose
        private String string;
        private Integer small;
        private Long large;
        private Date date;
        public void init() {
            string = "string";
            small = 10;
            large = 20L;
            try {
                date = DATE_FORMAT.parse("2011-01-01");
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object o) {
            return EqualsBuilder.reflectionEquals(this, o);
        }
    }

    public static class Various {
        @Expose
        private int small;
        @Expose
        private String string;
        private Map<String, Various> map;
        @Expose
        private List<Various> list;
        private Set<Various> set;
        @Expose
        private SortedMap<String, Various> sortedMap;
        @Expose
        private SortedSet<Various> sortedSet;
        private Various various;
        @Expose
        private Primitives primitives;
        @Expose
        private Immutable immutable;

        public Various() {};
        public Various(int small, String string) {
            this.small = small;
            this.string = string;
        }
        public void init(int dept) {
            init(dept,1);
        }
        public void init(int dept, int collectionMultiplier) {
            small = 100;
            string = "variousstring";
            primitives = new Primitives();
            primitives.init();
            immutable = new Immutable();
            immutable.init();
            if(dept>0) {
                various = new Various(dept,"various"+dept);
                various.init(--dept);
            }
            map = new HashMap<String, Various>();
            map.put("abc",new Various(1000*collectionMultiplier,"abc"));
            map.put("123",new Various(2000*collectionMultiplier,"123"));
            map.put("xyz",new Various(3000*collectionMultiplier,"xyz"));

            list = new ArrayList<Various>();
            list.add(new Various(9000*collectionMultiplier,"9000"));
            list.add(new Various(8000*collectionMultiplier,"8000"));
            list.add(new Various(7000*collectionMultiplier,"7000"));

            set = new HashSet<Various>();
            set.add(new Various(4000*collectionMultiplier,"4000"));
            set.add(new Various(5000*collectionMultiplier,"5000"));
            set.add(new Various(6000*collectionMultiplier,"6000"));

            sortedMap = new TreeMap<String,Various>(new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareTo(s2);
                }
            });
            sortedMap.put("abc",new Various(10000*collectionMultiplier,"abc"));
            sortedMap.put("123",new Various(20000*collectionMultiplier,"123"));
            sortedMap.put("xyz",new Various(30000*collectionMultiplier,"xyz"));

            sortedSet = new TreeSet<Various>(new Comparator<Various>() {
                @Override
                public int compare(Various v1, Various v2) {
                    return v2.string.compareTo(v1.string);
                }
            });
            sortedSet.add(new Various(10000*collectionMultiplier,"abc"));
            sortedSet.add(new Various(20000*collectionMultiplier,"123"));
            sortedSet.add(new Various(30000*collectionMultiplier,"xyz"));
        }
        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object o) {
            boolean equals =  EqualsBuilder.reflectionEquals(this, o);
            if(list != null && equals) {
                if(((Various)o).list==null || list.size()!=((Various)o).list.size()) {
                    return false;
                }
                Iterator<Various> it1 = list.iterator();
                Iterator<Various> it2 = ((Various)o).list.iterator();
                while(it1.hasNext()) {
                    equals = EqualsBuilder.reflectionEquals(it1.next(),it2.next());
                    if(!equals) {
                        break;
                    }
                }
            }
            if(set != null && equals) {
                if(((Various)o).set==null || set.size()!=((Various)o).set.size()) {
                    return false;
                }
                Iterator<Various> it1 = set.iterator();
                while(it1.hasNext()) {
                    equals = ((Various)o).set.contains(it1.next());
                    if(!equals) {
                        break;
                    }
                }
            }
            if(sortedSet != null && equals) {
                if(((Various)o).sortedSet==null || sortedSet.size()!=((Various)o).sortedSet.size()) {
                    return false;
                }
                Iterator<Various> it1 = sortedSet.iterator();
                Iterator<Various> it2 = ((Various)o).sortedSet.iterator();
                while(it1.hasNext()) {
                    equals = it1.next().equals(it2.next());
                    if(!equals) {
                        break;
                    }
                }
            }
            if(map != null && equals) {
                if(((Various)o).map==null || map.size()!=((Various)o).map.size()) {
                    return false;
                }
                Iterator<String> it1 = map.keySet().iterator();
                while(it1.hasNext()) {
                    String s1 = it1.next();
                    if(((Various)o).map.containsKey(s1)) {
                        equals = EqualsBuilder.reflectionEquals(map.get(s1),((Various)o).map.get(s1));
                    }else {
                        equals = false;
                    }
                    if(!equals) {
                        break;
                    }
                }
            }
            if(sortedMap != null && equals) {
                if(((Various)o).sortedMap==null || sortedMap.size()!=((Various)o).sortedMap.size()) {
                    return false;
                }
                Iterator<String> it1 = sortedMap.keySet().iterator();
                Iterator<String> it2 = ((Various)o).sortedMap.keySet().iterator();
                while(it1.hasNext()) {
                    String s1 = it1.next();
                    String s2 = it2.next();
                    equals = s1.equals(s2) && EqualsBuilder.reflectionEquals(sortedMap.get(s1),((Various)o).sortedMap.get(s2));
                    if(!equals) {
                        break;
                    }
                }
            }
            return equals;
        }
    }

    public static class IdObjectList {
        private int id;
        private String name;
        private Set<IdObject> set;
        private List<IdObject> list;
        private Map<String, IdObject> map;

        IdObjectList(int id, String name, int seed) {
            this.id=id;
            this.name=name;
            if(seed>0) {
                set = new TreeSet<IdObject>(new Comparator<IdObject>() {
                    @Override
                    public int compare(IdObject o1, IdObject o2) {
                        return o1.id-o2.id;
                    }
                });
                set.add(new IdObject(100+1*seed,"setobject"+(1*seed)));
                set.add(new IdObject(100+2*seed,"setobject"+(2*seed)));
                set.add(new IdObject(100+3*seed,"setobject"+(3*seed)));

                list = new ArrayList<IdObject>();
                list.add(new IdObject(1000+1*seed,"listobject"+(1*seed)));
                list.add(new IdObject(1000+2*seed,"listobject"+(2*seed)));
                list.add(new IdObject(1000+3*seed,"listobject"+(3*seed)));

                map = new TreeMap<String,IdObject>(new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareTo(s2);
                    }
                });
                map.put("mapKey"+(1*seed),new IdObject(10000+1*seed,"mapobject"+(1*seed)));
                map.put("mapKey" + (2 * seed), new IdObject(10000+2 * seed, "mapobject" + (2 * seed)));
                map.put("mapKey" + (3 * seed), new IdObject(10000+3 * seed, "mapobject" + (3 * seed)));
            }
        }
        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(id).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof IdObjectList) {
                return new EqualsBuilder().append(id,id).isEquals();
            }
            return false;
        }
    }
    public static class IdObject {
        private int id;
        private String name;

        public IdObject() {}
        public IdObject(int id, String name) {
            this.id=id;
            this.name=name;
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder().append(id).hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof IdObject) {
                return new EqualsBuilder().append(id,id).isEquals();
            }
            return false;
        }
    }

    @Test
    public void testEquals() {
        Various various1 = new Various();
        various1.init(2);
        Various various2 = new Various();
        various2.init(2);
        assert various1.equals(various2);
    }

    @Test
    public void testNotEquals() {
        Various various1 = new Various();
        various1.init(2);
        Various various2 = new Various();
        various2.init(3);
        assert !various1.equals(various2);

        various1 = new Various();
        various1.init(2,1);
        various2 = new Various();
        various2.init(2,2);
        assert !various1.equals(various2);
    }

    @Test
    public void testClone() {
        Various various1 = new Various();
        various1.init(2);
        Various various2 = new CopyHelper().detachedCopy(various1);
        assert various1.equals(various2);
    }

    @Test
    public void testCloneExposed() {
        Various various1 = new Various();
        various1.init(2);
        Various various2 = new CopyHelper().detachedCopy(various1, Expose.class);
        assert !various1.equals(various2);

        // Non exposed attribute should be zero
        assert various2.set == null;
        assert various2.map == null;
        assert various2.various == null;

        // Exposed attributes should contain values
        assert various2.sortedSet != null;
        assert various2.sortedMap != null;
        assert various2.list != null;

        // Compare list elements and make sure they are the same as before
        assert various1.list.size()== various2.list.size();
        Iterator<Various> it1 = various1.list.iterator();
        Iterator<Various> it2 = various2.list.iterator();
        while(it1.hasNext()) {
            Various v1 = it1.next();
            Various v2 = it2.next();
            assert v1.string.equals(v2.string);
            assert v1.small == v2.small;
        }
    }

    @Test
    public void testMerge() {
        Various newObject = new Various();
        newObject.init(2);
        Various oldlObject = new Various();
        Various updatedObject = new CopyHelper().mergeInto(oldlObject, newObject);
        assert oldlObject==updatedObject;
        assert newObject.equals(updatedObject);
    }

    @Test
    public void testMergeExposed() {
        Various newObject = new Various();
        newObject.init(1,2);
        // Let's put null a non exposed map
        newObject.map = null;
        // Let's put null in an exposed map
        newObject.sortedMap = null;
        // Let's clear an exposed set
        newObject.sortedSet.clear();

        Various oldObject = new Various();
        oldObject.init(0);

        // Objects should differ
        assert !newObject.equals(oldObject);

        // Remember map instance and cloned copy
        Map<String,Various> oldNonExposedMap = oldObject.map;
        Map<String,Various> oldClonedNonExposedMap = new HashMap<String,Various>(oldObject.map);
        // Remember list instance and cloned copy
        List<Various> oldExposedList = oldObject.list;
        List<Various> oldClonedExposedList = new ArrayList<Various>(oldObject.list);

        Various updatedObject = new CopyHelper().mergeInto(oldObject, newObject, Expose.class);
        // Still same instance
        assert oldObject==updatedObject;
        // Still differs compared to new object since not all attributes has been exposed
        assert !newObject.equals(oldObject);

        // The map should still be the same instance and size as before
        assert updatedObject.map==oldNonExposedMap;
        assert updatedObject.map.size()==oldClonedNonExposedMap.size();

        // The list should still be the same instance and size as before but it now has different contents
        assert updatedObject.list==oldExposedList;
        assert updatedObject.list.size()==oldExposedList.size();

        // Compare list and make sure all its elements are different but the same instances
        Iterator<Various> itNew = updatedObject.list.iterator();
        Iterator<Various> itOld = oldClonedExposedList.iterator();
        while(itNew.hasNext()) {
            Various v1 = itNew.next();
            Various v2 = itOld.next();
            // Attribute should be changed
            assert v1.small!=v2.small;
        }

        // Exposed map should be set to null after the merge
        assert updatedObject.sortedMap==null;
        // Exposed set should be cleared after the merge
        assert updatedObject.sortedSet!=null;
        assert updatedObject.sortedSet.size()==0;
    }

    @Test
    public void testMergeWithPreviousData() {
        Various newObject = new Various();
        newObject.init(2, 1);
        Various oldObject = new Various();
        oldObject.init(3);
        assert !newObject.equals(oldObject);
        Various various3 = new CopyHelper().mergeInto(oldObject, newObject);
        assert oldObject==various3;
        assert newObject.equals(oldObject);

        newObject = new Various();
        newObject.init(2);
        oldObject = new Various();
        oldObject.init(1);
        assert !newObject.equals(oldObject);
        various3 = new CopyHelper().mergeInto(oldObject, newObject);
        assert oldObject==various3;
        assert newObject.equals(oldObject);

        newObject = new Various();
        newObject.init(2, 1);
        oldObject = new Various();
        oldObject.init(2,2);
        assert !newObject.equals(oldObject);
        various3 = new CopyHelper().mergeInto(oldObject, newObject);
        assert oldObject==various3;
        assert newObject.equals(oldObject);
    }

    @Test
    public void testMergeWithCache() {
        List<IdObject> oldList = new ArrayList<IdObject>();
        IdObject obj1 = new IdObject(1,"object1");
        IdObject obj2 = new IdObject(2,"object2");
        IdObject obj3 = new IdObject(3, "object3");
        CopyHelperCache cache = new CopyHelperCache();
        cache.store(obj1,obj1);
        cache.store(obj2,obj2);
        cache.store(obj3,obj3);
        oldList.add(obj1);
        oldList.add(obj2);
        oldList.add(obj3);

        List<IdObject> newList = new ArrayList<IdObject>();
        newList.add(new IdObject(1,"object1"));
        newList.add(new IdObject(2,"newobject2"));
        newList.add(new IdObject(4,"newobject4"));

        List<IdObject> updatedList = new CopyHelper(cache).mergeInto(oldList,newList);
        assert updatedList==oldList;
        assert updatedList.size()==3;
        assert updatedList.get(0) == obj1;
        assert updatedList.get(1) == obj2;
        assert updatedList.get(1).name.equals("newobject2");
        assert updatedList.get(2) != obj3;
        assert updatedList.get(2).name.equals("newobject4");
    }

    @Test
    public void testMergeCollectionsWithCache() {
        IdObjectList oldList= new IdObjectList(1,"oldlist",1);
        IdObject oldListObj3 = oldList.list.get(2);
        Iterator<IdObject> setIterator = oldList.set.iterator();
        setIterator.next();
        setIterator.next();
        IdObject oldSetObj3 = setIterator.next();
        IdObject oldMapObj3 = oldList.map.get("mapKey3");
        // By using seed=3 we will make sure that one object has the same key as the oldList
        IdObjectList newList= new IdObjectList(1,"newlist",3);

        // Make sure some of the old objects exists in the newList
        IdObject listObj1 = oldList.list.get(0);
        newList.list.add(listObj1);

        // New list contents:
        // 1003, listobject3 (not same instance as in oldList)
        // 1006, listobject6
        // 1009, listobject9
        // 1001, listobject1

        // Make the first object in the set be part of the new set
        IdObject setObj1 = oldList.set.iterator().next();
        newList.set.add(setObj1);

        // New set contents
        // 103, setobject3 (not same instance as in oldList)
        // 106, setobject6
        // 109, setobject9
        // 101, setobject1

        // Make the first key in the map to be changed but reuse same object
        Iterator<String> iterator = oldList.map.keySet().iterator();
        String remappedKey = iterator.next();
        IdObject mapRemappedObj = oldList.map.get(remappedKey);
        newList.map.put("myobject", mapRemappedObj);

        // Make the second key in the map to keep key but change object
        String mapKey2 = iterator.next();
        IdObject newMapObj2 = new IdObject(100000,"newMapObj");
        newList.map.put(mapKey2,newMapObj2);

        // New map contents
        // mapKey2 -> 100000, newMapObj
        // mapKey3 -> 10003, mapobject3 (not same instance as old map)
        // mapKey6 -> 10006, mapobject6
        // mapKey9 -> 10009, mapobject9
        // myobject -> 10001, mapobject1 (same instance as in old map)

        CopyHelperCache cache = new CopyHelperCache();
        cache.store(oldList,oldList);
        for (IdObject obj : oldList.list) {
            cache.store(obj,obj);
        }
        for (IdObject obj : oldList.map.values()) {
            cache.store(obj,obj);
        }
        for (IdObject obj : oldList.set) {
            cache.store(obj,obj);
        }

        // Stupid test to ensure that our new object isn't in the cache
        assert cache.load(newMapObj2)==null;

        IdObjectList updatedList = new CopyHelper(cache).mergeInto(oldList,newList);
        assert updatedList==oldList;
        assert updatedList.list.size()==4;
        assert updatedList.set.size()==4;
        assert updatedList.map.size()==5;

        // Verify that the first and third object still exists and is the same instance
        boolean firstFound = false;
        boolean thirdFound = false;
        for (IdObject object : updatedList.list) {
            if(object==oldListObj3) {
                thirdFound = true;
            }else if(object==listObj1) {
                firstFound = true;
            }
        }
        assert firstFound;
        assert thirdFound;

        // Verify that the first and third object still exists and is the same instance
        firstFound = false;
        thirdFound = false;
        for (IdObject object : updatedList.set) {
            if(object==oldSetObj3) {
                thirdFound = true;
            }else if(object==setObj1) {
                firstFound = true;
            }
        }
        assert firstFound;
        assert thirdFound;

        // Verify that the third object still exists and is the same instance
        assert oldMapObj3==updatedList.map.get("mapKey3");
        // Verify that the second object isn't the same instance since this should be create as it's not in the cach
        assert newMapObj2!=updatedList.map.get(mapKey2);
        // but it should still have the same identity
        assert newMapObj2.equals(updatedList.map.get(mapKey2));
        assert cache.load(newMapObj2)!=null;
        // The "myobject" key should still refer to the same object
        assert mapRemappedObj==updatedList.map.get("myobject");
    }
}
