package com.example.moviereviews;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class RSSParser {
    public RSSParser() {
    }
    public List<RSSItem> getRSSFeedItem(String rss_url) {
        List<RSSItem> itemList = new ArrayList<>();
        String rss_feed_xml = this.getXmlFromUrl(rss_url);

        if (rss_feed_xml != null) {

            Document doc = this.getDomElement(rss_feed_xml);
            NodeList nodeList = doc.getElementsByTagName("channel");
            Element e = (Element) nodeList.item(0);

            NodeList items = e.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element e1 = (Element) items.item(i);

                String title = this.getValue(e1, "title");
                String link = this.getValue(e1, "link");
                String description = this.getValue(e1, "description");
                String pubdate = this.getValue(e1, "pubDate");
                String guid = this.getValue(e1, "guid");

                RSSItem rssItem = new RSSItem(title,  description,link, pubdate, guid);

                itemList.add(rssItem);
            }
            }
        return itemList;
        }

        private String getXmlFromUrl (String url){
                String xml=null;
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url);

                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    xml = EntityUtils.toString(httpEntity);
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return xml;
        }

        private Document getDomElement (String xml){
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {

                DocumentBuilder db = dbf.newDocumentBuilder();

                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is);

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return doc;
        }

        private String getValue(Element item, String str) {
            NodeList n = item.getElementsByTagName(str);
            return this.getElementValue(n.item(0));
        }

         private String getElementValue(Node item) {
             Node child;
             if (item != null) {
                 if (item.hasChildNodes()) {
                     for (child = item.getFirstChild(); child != null; child = child
                             .getNextSibling()) {
                         if (child.getNodeType() == Node.TEXT_NODE || (child.getNodeType() == Node.CDATA_SECTION_NODE)) {
                             return child.getNodeValue();
                         }
                     }
                 }
             }
             return "";
         }
}