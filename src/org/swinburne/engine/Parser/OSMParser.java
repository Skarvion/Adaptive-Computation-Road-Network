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

    public static Graph parseFromOSM(File file) {
        return parse(file, null, null, null, null);
    }

    public static Graph parseFromOSM(File file, double top, double left, double bottom, double right) {
        return parse(file, top, left, bottom, right);
    }

    // Reference: https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
    private static Graph parse(File file, Double top, Double left, Double bottom, Double right) {
        Graph graph = new Graph();

        boolean bounded = false;
        if (top != null && left != null && bottom != null && right != null) bounded = true;
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
                org.w3c.dom.Node selectednode = nodeList.item(i);

                if (selectednode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) selectednode;

                    double lat = Double.parseDouble(element.getAttribute("lat"));
                    double lon = Double.parseDouble(element.getAttribute("lon"));

                    if (bounded)
                        if (lat > top || lat < bottom || lon < left || lon > right) continue;

                    NodeList tagList = element.getElementsByTagName("tag");
                    boolean toBreak = false;
                    for (int j = 0; j < tagList.getLength(); j++) {
                        Element selectedTag = (Element) tagList.item(j);
                        if (selectedTag.getAttribute("network") == null) {
                            System.out.println("Null");
                            continue;
                        }
                        if (selectedTag.getAttribute("network").contains("PTV")) {
                            System.out.println("Train PTV");
                            toBreak = true;
                            break;
                        }
                    }
                    if (toBreak) break;

                    Node newNode = new Node();
                    newNode.setId(element.getAttribute("id"));
                    newNode.setLatitude(lat);
                    newNode.setLongtitude(lon);

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
