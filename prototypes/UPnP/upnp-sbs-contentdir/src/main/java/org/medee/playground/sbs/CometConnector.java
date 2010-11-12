package org.medee.playground.sbs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.codehaus.jackson.*;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.*;

public class CometConnector {
	
    private static final String SUBSCRIPTION = "subscription";
	private static final String META_SUBSCRIBE = "/meta/subscribe";
	private static final String CONNECTION_TYPE = "connectionType";
	private static final String META_CONNECT = "/meta/connect";
	private static final String SUCCESSFUL = "successful";
	private static final String LONG_POLLING = "long-polling";
	private static final String SUPPORTED_CONNECTION_TYPES = "supportedConnectionTypes";
	private static final String VERSION_1_0 = "1.0";
	private static final String VERSION = "version";
	private static final String META_HANDSHAKE = "/meta/handshake";
	private static final String CHANNEL = "channel";
	private static final String SERVICE_URL = "http://192.168.1.3:9000/cometd";
	private static final String CLIENT_ID = "clientId";
	
	URL url; 
    URLConnection conn;
	JsonFactory jf;
	StringWriter mySw;
	ObjectMapper m;

	String clientId;
    
    public CometConnector() {
    	try {
			url = new URL(SERVICE_URL);
			jf = new JsonFactory();
			mySw =  new StringWriter();
			m = new ObjectMapper();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    }

    // sends handshake and return clientId
    public String doHandshake() {
    	JsonNode rootNode = this.SendRequest(this.CometHandShakeRequest());
	

		if(rootNode.get(0).get(SUCCESSFUL) != null) {
			this.clientId = rootNode.get(0).get(CLIENT_ID).getTextValue();			
		} else {
			System.err.println("handshake error");
		}
		return(this.clientId);
    }

    // sends handshake and return clientId
    public String doConnect() {
    	JsonNode rootNode = this.SendRequest(this.CometConnectRequest());
	

		if(rootNode.get(0).get(SUCCESSFUL) != null) {
			this.clientId = rootNode.get(0).get(CLIENT_ID).toString();			
		} else {
			System.err.println("connect error");
		}
		if(rootNode.get(1).get(SUCCESSFUL) != null) {
			this.clientId = rootNode.get(0).get(CLIENT_ID).toString();			
		} else {
			System.err.println("subscribe error");
		}
		return(this.clientId);
    }
    
    // sends a comet request and return the answer as a String
    private JsonNode SendRequest(String request) {
    	StringBuilder sb = new StringBuilder();
    	String line;

    	System.out.print("Request: ");
    	System.out.println(request);
		try {
			conn = url.openConnection();
			conn.setDoOutput(true);
			
			OutputStreamWriter out = new OutputStreamWriter(
					conn.getOutputStream());
			
			out.write(request);
			out.close();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							conn.getInputStream()));
			
			while ((line = in.readLine()) != null) {
				sb.append(line).append("\n");
			}
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.print("Answer: ");
		System.out.println(sb.toString());
		
		JsonNode rootNode=null;
		try {
			rootNode = m.readTree(sb.toString());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return rootNode;
    }
    
    private String CometConnectRequest() {
    	mySw.getBuffer().setLength(0);
    	JsonGenerator g;
		try {
			g = jf.createJsonGenerator(mySw);
			g.writeStartArray();
				g.writeStartObject();
					g.writeStringField(CHANNEL, META_CONNECT);
					g.writeStringField(CLIENT_ID, this.clientId);
					g.writeStringField(CONNECTION_TYPE, LONG_POLLING);
				g.writeEndObject();
				g.writeStartObject();
					g.writeStringField(CHANNEL, META_SUBSCRIBE);
					g.writeStringField(CLIENT_ID, this.clientId);
					g.writeStringField(SUBSCRIPTION, "/"+this.clientId+"/**");
				g.writeEndObject();
			g.writeEndArray();
			g.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return(mySw.toString());
    }
    
    private String CometHandShakeRequest () {
    	mySw.getBuffer().setLength(0);
    	JsonGenerator g;
		try {
			g = jf.createJsonGenerator(mySw);
			g.writeStartArray();
				g.writeStartObject();
				g.writeStringField(CHANNEL, META_HANDSHAKE);
				g.writeStringField(VERSION, VERSION_1_0);
					g.writeArrayFieldStart(SUPPORTED_CONNECTION_TYPES);
						g.writeString(LONG_POLLING);
					g.writeEndArray();
				g.writeEndObject();
			g.writeEndArray();
			g.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return(mySw.toString());
    }

    public String SampleRequest () {
    	mySw.getBuffer().setLength(0);
    	
    	try {
			JsonGenerator g;
			g = jf.createJsonGenerator(mySw);
			g.writeStartArray();
				g.writeStartObject();
				g.writeStringField(CHANNEL, META_HANDSHAKE);
				g.writeStringField(VERSION, VERSION_1_0);
					g.writeArrayFieldStart(SUPPORTED_CONNECTION_TYPES);
						g.writeString(LONG_POLLING);
					g.writeEndArray();
				g.writeEndObject();
			g.writeEndArray();
			g.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return(mySw.toString());
    	
// TreeModel version
//    	ArrayNode cometPayload = m.createArrayNode();
//    	ObjectNode handshake = m.createObjectNode();
//    	cometPayload.add(handshake);
//    	handshake.put(CHANNEL, META_HANDSHAKE);
//    	handshake.put(VERSION, VERSION_1_0);
//    	handshake.put(SUPPORTED_CONNECTION_TYPES, m.createArrayNode());
//    	((ArrayNode)handshake.get(SUPPORTED_CONNECTION_TYPES)).add(LONG_POLLING);
//    	try {
//			return(m.writeValueAsString(cometPayload));
//		} catch (JsonGenerationException e) {
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

    }
}
