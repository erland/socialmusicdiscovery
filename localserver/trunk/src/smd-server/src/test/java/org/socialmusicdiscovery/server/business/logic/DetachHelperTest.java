package org.socialmusicdiscovery.server.business.logic;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.socialmusicdiscovery.test.BaseTestCase;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;

public class DetachHelperTest  extends BaseTestCase {
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
        Various various2 = DetachHelper.createDetachedCopy(various1);
        assert various1.equals(various2);
    }

    @Test
    public void testCloneExposed() {
        Various various1 = new Various();
        various1.init(2);
        Various various2 = DetachHelper.createDetachedCopy(various1,Expose.class);
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
        Various updatedObject = DetachHelper.mergeInto(oldlObject, newObject);
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

        Various updatedObject = DetachHelper.mergeInto(oldObject,newObject,Expose.class);
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
        Various various3 = DetachHelper.mergeInto(oldObject, newObject);
        assert oldObject==various3;
        assert newObject.equals(oldObject);

        newObject = new Various();
        newObject.init(2);
        oldObject = new Various();
        oldObject.init(1);
        assert !newObject.equals(oldObject);
        various3 = DetachHelper.mergeInto(oldObject, newObject);
        assert oldObject==various3;
        assert newObject.equals(oldObject);

        newObject = new Various();
        newObject.init(2, 1);
        oldObject = new Various();
        oldObject.init(2,2);
        assert !newObject.equals(oldObject);
        various3 = DetachHelper.mergeInto(oldObject, newObject);
        assert oldObject==various3;
        assert newObject.equals(oldObject);
    }
}
