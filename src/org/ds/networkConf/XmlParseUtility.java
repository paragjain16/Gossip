package org.ds.networkConf;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlParseUtility {
    private static List<String> nwServerIpAddrList=new ArrayList<String>();
    private static final String FILENAME="network_configuration.xml";
    
	public static List<String> getNetworkServerIPAddrs(){
		SAXParserFactory parserFactory= SAXParserFactory.newInstance();
		try {
			SAXParser parser= parserFactory.newSAXParser();
			File currentJavaJarFile = new File(XmlParseUtility.class.getProtectionDomain().getCodeSource().getLocation().getPath());   
			String currentJavaJarFilePath = currentJavaJarFile.getAbsolutePath();		
			String currentRootDirectoryPath = currentJavaJarFilePath.replace(currentJavaJarFile.getName(), "");			
			try {
				nwServerIpAddrList.clear();
				parser.parse(currentRootDirectoryPath+FILENAME, new XmlParseUtility().new TagHandler() );
				
			} catch (IOException e) {				
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e) {
			
			e.printStackTrace();
		} catch (SAXException e) {
			
			e.printStackTrace();
		}
		return nwServerIpAddrList;
	}
	
	 private class TagHandler extends DefaultHandler {
		 boolean serverAddr=false;

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {		
			if(serverAddr){
				nwServerIpAddrList.add(new String(ch, start, length));
				serverAddr=false;
			}
		}

		

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {			
			if(qName.equalsIgnoreCase("serverAddr")){
				serverAddr=true;
			}
		}
		 
	 }
}