package org.swinburne.engine.Parser;

import org.swinburne.model.Way;
import org.swinburne.model.Graph;
import org.swinburne.model.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OSMParser {
    // Reference: https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
    public static Graph parseFromOSM(File file) {
        Graph graph = new Graph();

        try {
            System.out.println("Parsing from OSM");
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date startDate = new Date();
            System.out.println("Start: " + sdf.format(startDate));

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(file);

            NodeList nodeList = doc.getElementsByTagName("node");
            for (int i = 0; i < nodeList.getLength(); i++) {
//                if (i == 1500) System.out.println("i is " + i);
                org.w3c.dom.Node selectednode = nodeList.item(i);

                if (selectednode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) selectednode;

                    Node newNode = new Node();
                    newNode.setId(element.getAttribute("id"));
                    newNode.setLatitude(Double.parseDouble(element.getAttribute("lat")));
                    newNode.setLongtitude(Double.parseDouble(element.getAttribute("lon")));

                    graph.addNode(newNode);
                }
            }

            NodeList wayList = doc.getElementsByTagName("way");
            for (int i = 0; i < wayList.getLength(); i++) {
                org.w3c.dom.Node selectednode = wayList.item(i);

                if (selectednode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) selectednode;

                    Way way = new Way();
                    way.setId(element.getAttribute("id"));

                    NodeList wayNodeList = element.getElementsByTagName("nd");
//                    System.out.println("Way : " + way.getId());
                    for (int j = 0; j < wayNodeList.getLength(); j++) {
                        org.w3c.dom.Node selectedNodeInWay = wayNodeList.item(j);

                        if (selectedNodeInWay.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element nodeElement = (Element) selectedNodeInWay;
                            Node foundNode = graph.findNodeByID(nodeElement.getAttribute("ref"));
                            if (foundNode != null) {
                                way.addNode(foundNode);
//                                System.out.println("Attaching " + foundNode.getId());
                            }
                        }
                    }

                    graph.addWay(way);
//                    System.out.println("----------------");

                }
            }

            Date end = new Date();
            System.out.println("End: " + sdf.format(end));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return graph;
    }

}
