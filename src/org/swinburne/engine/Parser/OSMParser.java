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
import java.util.HashMap;
import java.util.Map;

public class OSMParser {
    public static Graph parseFromOSM(File file) {
        return parse(file, null, null, null, null);
    }

    public static Graph parseFromOSM(File file, Double top, Double left, Double bottom, Double right) {
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

                    Map<String, String> tagMap = getTagList(element);
                    if (tagMap.get("building") != null || tagMap.get("playground") != null || tagMap.get("leisure") != null || tagMap.get("amenity") != null || tagMap.get("power") != null || tagMap.get("shop") != null)
                        continue;

                    String network = tagMap.get("network");
                    if (network != null) {
                        if (network.contains("PTV")) continue;
                    } else if (tagMap.get("railway") != null) continue;

                    Node newNode = new Node();
                    newNode.setId(element.getAttribute("id"));
//                    newNode.setLabel(newNode.getId());
                    if (tagMap.get("name") != null) newNode.setLabel(tagMap.get("name"));
                    newNode.setLatitude(lat);
                    newNode.setLongitude(lon);

                    graph.addNode(newNode);
                }
            }

            NodeList wayList = doc.getElementsByTagName("way");
            for (int i = 0; i < wayList.getLength(); i++) {
                org.w3c.dom.Node selectednode = wayList.item(i);

                if (selectednode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element element = (Element) selectednode;

                    Map<String, String> tagMap = getTagList(element);

                    if (tagMap.get("cables") != null || tagMap.get("power") != null || tagMap.get("wires") != null)
                        continue;
                    String network = tagMap.get("network");
                    String railway = tagMap.get("railway");
                    if (network != null) {
                        if (network.contains("PTV")) continue;
                    } else if (railway != null) continue;

                    if (tagMap.get("leisure") != null || tagMap.get("building") != null || tagMap.get("office") != null || tagMap.get("waterway") != null) continue;
                    if (tagMap.get("amenity") != null || tagMap.get("foot") != null || tagMap.get("bicycle") != null || tagMap.get("landuse") != null) continue;

                    if (tagMap.get("highway") != null) {
                        if (tagMap.get("highway").equalsIgnoreCase("path") || tagMap.get("highway").equalsIgnoreCase("footway")) continue;
                    }

                    Way way = new Way();
                    way.setId(element.getAttribute("id"));
                    if (tagMap.get("name") != null) way.setLabel(tagMap.get("name"));
                    if (tagMap.get("maxspeed") != null) way.setSpeedLimitKmh(Float.parseFloat(tagMap.get("maxspeed")));
                    else way.setSpeedLimitKmh(50); // Default speed reference: https://en.wikipedia.org/wiki/Speed_limits_in_Australia

                    NodeList wayNodeList = element.getElementsByTagName("nd");
                    for (int j = 0; j < wayNodeList.getLength(); j++) {
                        org.w3c.dom.Node selectedNodeInWay = wayNodeList.item(j);

                        if (selectedNodeInWay.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                            Element nodeElement = (Element) selectedNodeInWay;
                            Node foundNode = graph.getNode(nodeElement.getAttribute("ref"));
                            if (foundNode != null) {
                                way.addNode(foundNode);
                            }
                        }
                    }

                    if (way.getNodeOrderedList().size() > 0) graph.addWay(way);

                }
            }

            Date end = new Date();
            System.out.println("End: " + sdf.format(end));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return graph;
    }

    private static Map<String, String> getTagList(Element element) {
        Map<String, String> tagMap = new HashMap<>();
        NodeList tagList = element.getElementsByTagName("tag");

        for (int i = 0; i < tagList.getLength(); i++) {
            org.w3c.dom.Node selectedTag = tagList.item(i);

            if (selectedTag.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element tagElement = (Element) selectedTag;
                tagMap.put(tagElement.getAttribute("k"), tagElement.getAttribute("v"));
            }
        }

        return tagMap;
    }

}