/*
 *  Copyright 2011 Hippo.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hippoecm.hst.demo.components;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Simple plain wikipedia to jcr Node. 
 * 
 * Note that this is <strong>not</strong> production code! Real code should use workflow instead of the low-level jcr calls in this class.
 * This just serves to import many wiki documents as efficient as possible
 * 
 *
 */
public class SimpleNonWorkflowWikiImporterComponent extends BaseHstComponent {

    public static final Logger log = LoggerFactory.getLogger(SimpleNonWorkflowWikiImporterComponent.class);
    
    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
       request.setAttribute("message", request.getParameter("message"));
    }

    @Override
    public void doAction(HstRequest request, HstResponse response) throws HstComponentException {
        
        SAXParserFactoryImpl impl = new SAXParserFactoryImpl();
        SAXParser parser;
        String numberStr = request.getParameter("number");
        int numberOfWikiDocs = 0;
        try {
            numberOfWikiDocs = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            response.setRenderParameter("message", "number must be a number but was '"+numberStr+"'");
            return;
        }
        
        if(numberOfWikiDocs <= 0) {
            response.setRenderParameter("message", "number must be a number larger than 0 but was '"+numberStr+"'");
            return;
        }
        
        String wikiContentFileSystem = request.getParameter("filesystemLocation");
        
        if(numberOfWikiDocs > 100 && StringUtils.isEmpty(wikiContentFileSystem)) {
            response.setRenderParameter("message", "When number is larger than 100, you need to specify the filesystem location (for exmaple /home/use/Downloads/enwiki-20100622-pages-articles.xml) where wikipedia content can be found that " +
            		"has more than 100 docs. If you choose less than 100, a built in wikipedia xml is used that contains 100 items");
            return;
        }
        
        try {
            parser = impl.newSAXParser();
            InputStream wikiStream = null;
            File f = null;
            if(StringUtils.isEmpty(wikiContentFileSystem)) {
                wikiStream = SimpleNonWorkflowWikiImporterComponent.class.getClassLoader().getResourceAsStream("enwiki-20081008-pages-articles.xml.100.top.xml");
            } else {
                f = new File(wikiContentFileSystem);
            }
          
            WikiPediaToJCRHandler handler = null;
            long start = System.currentTimeMillis();
            try {
                 
                Session writableSession = this.getPersistableSession(request);
                Node canonicalSiteRootNode = NodeUtils.getDeref(getSiteContentBaseBean(request).getNode());
                Node baseNode = writableSession.getNode(canonicalSiteRootNode.getPath());
                
                Node wikiFolder;
                
                if(!baseNode.hasNode("wikipedia")) {
                    wikiFolder = baseNode.addNode("wikipedia", "hippostd:folder");
                    wikiFolder.addMixin("hippo:harddocument");
                } else {
                    wikiFolder = baseNode.getNode("wikipedia");
                }
                handler = new WikiPediaToJCRHandler(wikiFolder, numberOfWikiDocs);
                
                if(wikiStream == null) {
                    parser.parse(f, handler);
                } else {
                    parser.parse(wikiStream, handler);
                }
            }catch (ForcedStopException e) {
                // succesfull handler quits after numberOfWikiDocs has been achieved
            }catch (Exception e) {
                log.warn("Exception during importing wikipedia docs", e);
                response.setRenderParameter("message", "An exception happened. Did not import wiki docs. " + e.toString());
            }
            response.setRenderParameter("message", "Successfully imported '"+handler.count+"' wikipedia documents in '"+(System.currentTimeMillis() - start)+"' ms.");
        } catch (ParserConfigurationException e) {
            response.setRenderParameter("message", "Did not import wiki: " + e.toString());
        } 
             
    }

    class WikiPediaToJCRHandler extends DefaultHandler {

        private Node wikiFolder;
        private Node doc;
        private Node finishedDoc;
        private Node currentFolder;
        private Node currentSubFolder;
        private int numberOfSubFolders = 1;
        private int total;
        private StringBuilder fieldText = new StringBuilder();
        private boolean recording = false;
        int count = 0;
        int maxDocsPerFolder = 50;
        int maxSubFolders = 25;
        long startTime = 0;

        public WikiPediaToJCRHandler(Node wikiFolder, int total) throws Exception {
            this.wikiFolder = wikiFolder;
            this.total = total;
            currentFolder = wikiFolder.addNode("wiki-" + System.currentTimeMillis(), "hippostd:folder");
            currentFolder.addMixin("hippo:harddocument");
            currentSubFolder = currentFolder.addNode("wiki-" + System.currentTimeMillis(), "hippostd:folder");
            currentSubFolder.addMixin("hippo:harddocument");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            if (qName.equals("page")) {
                try { 
                    count++; 
                    if (count >= total) {
                        System.out.println(total);
                        wikiFolder.getSession().save(); 

                        System.out.println("Total added wiki docs = " + count + ". It took " + (System.currentTimeMillis() - startTime) + " ms.");
                        throw new ForcedStopException();
                    }   
                    if ((count % maxDocsPerFolder) == 0) {
                        wikiFolder.getSession().save();
                        if(numberOfSubFolders >= maxSubFolders) {
                            currentFolder = wikiFolder.addNode("wiki-" + System.currentTimeMillis(), "hippostd:folder");
                            currentFolder.addMixin("hippo:harddocument");
                            numberOfSubFolders = 0;
                        }
                        currentSubFolder = currentFolder.addNode("wiki-" + System.currentTimeMillis(), "hippostd:folder");
                        currentSubFolder.addMixin("hippo:harddocument");
                        numberOfSubFolders++;
                        System.out.println("Counter = " + count);
                    }
                    Calendar cal = Calendar.getInstance();
                    String docName = "doc-" + cal.getTimeInMillis();
                    Node handle;

                    handle = currentSubFolder.addNode(docName, "hippo:handle");
                    handle.addMixin("hippo:hardhandle");
                    handle.addMixin("hippo:translated");

                    Node translation = handle.addNode("hippo:translation", "hippo:translation");
                    translation.setProperty("hippo:message", docName);
                    translation.setProperty("hippo:language", "");

                    doc = handle.addNode(docName, "demosite:textdocument");
                    doc.addMixin("hippo:harddocument");

                    String[] availability = {"live", "preview"};
                    doc.setProperty("hippo:availability", availability );
                    doc.setProperty("hippostd:stateSummary", "live");
                    doc.setProperty("hippostd:state", "published");
                    doc.setProperty("hippostdpubwf:lastModifiedBy", wikiFolder.getSession().getUserID());
                    doc.setProperty("hippostdpubwf:createdBy", wikiFolder.getSession().getUserID());
                    doc.setProperty("hippostdpubwf:lastModificationDate",  Calendar.getInstance());
                    doc.setProperty("hippostdpubwf:creationDate", Calendar.getInstance() );
                    doc.setProperty("hippostdpubwf:publicationDate", Calendar.getInstance() );
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
            }

            if (qName.equals("title")) {
                recording = true;
            }

            if (qName.equals("text")) {
                recording = true;
            }

            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equals("page")) {
                checkCorrectDoc();
                finishedDoc = doc;
            } else if (qName.equals("title") && recording) {
                checkCorrectDoc();
                try {
                    doc.setProperty("demosite:title", fieldText.toString().trim());
                    fieldText = new StringBuilder();
                } catch (RepositoryException e) {
                    throw new SAXException(e);
                }
            } else if (qName.equals("text") && recording) {
                checkCorrectDoc();
                try {
                    String text = fieldText.toString().trim();
                    if(text.length() > 100) {
                        doc.setProperty("demosite:summary", text.substring(0,100));
                    } else {
                        doc.setProperty("demosite:summary", text);
                    }
                    Node body = doc.addNode("demosite:body","hippostd:html");
                    body.setProperty("hippostd:content", text);
                    fieldText = new StringBuilder();
                } catch (RepositoryException e) {
                    throw new SAXException(e);
                }
            }
            super.endElement(uri, localName, qName);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (recording) {
                fieldText.append(ch, start, length);
            }
        }

        private void checkCorrectDoc() throws SAXException {
            if (doc == finishedDoc) {
                throw new SAXException("Doc is same instance as finished doc. This should never happen");
            }
        }
    }

    class ForcedStopException extends RuntimeException {
        private static final long serialVersionUID = 1L;

    }

}
