package org.socialmusicdiscovery.server.business.model.core;

import jo4neo.ObjectGraph;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class Neo4JLoader {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy");

    public static void loadFile(ObjectGraph g, String file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();

			//parse using builder to get DOM representation of the XML file
			Document dom = db.parse(file);
            NodeList nodeList = dom.getElementsByTagName("dataset");
            handleNodeList(g,nodeList.item(0).getChildNodes(),null);

		} catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    private static void handleNodeList(ObjectGraph g, NodeList list, Object parent) throws Exception {
        for(int i=0;i<list.getLength();i++) {
            Node node = list.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                String elementName = node.getNodeName();
                if(elementName.equals("relation")) {
                    NamedNodeMap attributes = node.getAttributes();
                    Node classNode = attributes.getNamedItem("class");
                    String className = classNode.getNodeValue();
                    Object obj = Neo4JLoader.class.getClassLoader().loadClass(className).newInstance();
                    Node indexNode= attributes.getNamedItem("index");
                    String index = indexNode.getNodeValue();
                    Node valueNode= attributes.getNamedItem("value");
                    String value = valueNode.getNodeValue();
                    obj = g.find(obj).where(PropertyUtils.getProperty(obj,index)).is(value).result();
                    Node idNode = attributes.getNamedItem("id");
                    String id = idNode.getNodeValue();
                    Node reverseIdNode = attributes.getNamedItem("reverseid");
                    String reverseId = idNode.getNodeValue();
                    setRelation(g,parent,id,reverseId,obj);
                }else if(elementName.equals("reverserelation")) {
                    NamedNodeMap attributes = node.getAttributes();
                    Node classNode = attributes.getNamedItem("class");
                    String className = classNode.getNodeValue();
                    Object obj = Neo4JLoader.class.getClassLoader().loadClass(className).newInstance();
                    Node indexNode= attributes.getNamedItem("index");
                    String index = indexNode.getNodeValue();
                    Node valueNode= attributes.getNamedItem("value");
                    String value = valueNode.getNodeValue();
                    obj = g.find(obj).where(PropertyUtils.getProperty(obj,index)).is(value).result();
                    Node idNode = attributes.getNamedItem("id");
                    String id = idNode.getNodeValue();
                    Node reverseIdNode = attributes.getNamedItem("reverseid");
                    String reverseId = idNode.getNodeValue();
                    setRelation(g,obj,id,reverseId,parent);
                }else {
                    Object obj = Neo4JLoader.class.getClassLoader().loadClass(elementName).newInstance();
                    NamedNodeMap attributes = node.getAttributes();
                    String reverseId = null;
                    String id = null;
                    for(int j=0;j<attributes.getLength();j++) {
                        Node attribute = attributes.item(j);
                        if(attribute.getNodeName().equals("id")) {
                            id = attribute.getNodeValue();
                        }else if(attribute.getNodeName().equals("reverseid")) {
                            reverseId = attribute.getNodeValue();
                        }else {
                            Class type = PropertyUtils.getPropertyType(obj,attribute.getNodeName());
                            if(Date.class.isAssignableFrom(type)) {
                                PropertyUtils.setProperty(obj,attribute.getNodeName(),DATE_FORMAT.parse(attribute.getNodeValue()));
                            }else if(Number.class.isAssignableFrom(type)) {
                                BeanUtils.setProperty(obj,attribute.getNodeName(),attribute.getNodeValue());
                            }else {
                                PropertyUtils.setProperty(obj,attribute.getNodeName(),attribute.getNodeValue());
                            }
                        }
                    }
                    g.persist(obj);
                    if(id!=null || reverseId!=null) {
                        setRelation(g,parent,id,reverseId,obj);
                    }
                    NodeList childNodes = node.getChildNodes();
                    if(childNodes != null && childNodes.getLength()>0) {
                        handleNodeList(g,childNodes,obj);
                    }
                }
            }
        }
    }
    private static void setRelation(ObjectGraph g, Object parent, String id, String reverseId, Object obj) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if(id != null && parent != null) {
            Class type = PropertyUtils.getPropertyType(parent,id);
            if(Collection.class.isAssignableFrom(type)) {
                Collection col = (Collection) PropertyUtils.getProperty(parent,id);
                col.add(obj);
                g.persist(parent);
            }else {
                PropertyUtils.setProperty(parent,id,obj);
                g.persist(parent);
            }
        }else if(reverseId != null && parent != null) {
            Class type = PropertyUtils.getPropertyType(obj,reverseId);
            if(Collection.class.isAssignableFrom(type)) {
                Collection col = (Collection) PropertyUtils.getProperty(obj,reverseId);
                col.add(parent);
                g.persist(obj);
            }else {
                PropertyUtils.setProperty(obj,reverseId,parent);
                g.persist(obj);
            }
        }

    }
}
