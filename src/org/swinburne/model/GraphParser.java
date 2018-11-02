package org.swinburne.model;

import org.apache.commons.io.FileUtils;
import org.json.*;
import org.swinburne.util.RandomStringGenerator;

import java.io.*;
import java.util.ArrayList;

public class GraphParser {
    // Due to moving to OSM, this might be deprecateed
    public static Graph generateGraph(String filename) {
        try {
            File inputFile = new File(filename);
            String content = FileUtils.readFileToString(inputFile, "UTF-8");

            JSONObject graphJSON = new JSONObject(content);
            Graph result = new Graph();

            JSONArray nodeArray = graphJSON.getJSONArray("node");
            for (Object obj: nodeArray) {
                JSONObject jsonObj = (JSONObject) obj;
                Node node = new Node();
                try {
                    node.setId(jsonObj.getString("id"));
                } catch (JSONException jsone) {
                    node.setId(RandomStringGenerator.generateRandomString(10));
                }
                node.setLabel(jsonObj.getString("label"));
                node.setLatitude(jsonObj.getDouble("latitude"));
                node.setLongitude(jsonObj.getDouble("longitude"));

                result.addNode(node);
            }

            JSONArray edgeArray = graphJSON.getJSONArray("edge");
            for (Object obj : edgeArray) {
                JSONObject jsonObj = (JSONObject) obj;
                Way way = new Way();

                Node source = result.getNode(jsonObj.getString("source"));
                Node destination = result.getNode(jsonObj.getString("destinationNode"));

                way.addNode(source);
                way.addNode(destination);

                try {
                    way.setId(jsonObj.getString("id"));
                } catch (JSONException jsone) {
                    way.setId(RandomStringGenerator.generateRandomString(10));
                }
                way.setLabel(jsonObj.getString("label"));
                way.setDistance(jsonObj.getFloat("distance"));
                way.setSpeedLimitKmh(jsonObj.getFloat("speed-limit"));
                way.setTraffic(jsonObj.getFloat("traffic"));

                if (jsonObj.getBoolean("two-way")) {
                    // Use copy method, doesn't copy the id, source and destinationNode. Will review
                    Way secondWay = new Way(way);
                    secondWay.setId(RandomStringGenerator.generateRandomString(10));
                    secondWay.addNode(source);
                    secondWay.addNode(destination);
                }
            }

            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void saveGraph(Graph graph, String filename) {
        JSONObject jsonFile = new JSONObject();

        JSONArray nodeArray = new JSONArray();

        for (Node n : graph.getNodeMap().values()) {
            JSONObject nodeJSON = new JSONObject();
            nodeJSON.put("id", n.getId());
            nodeJSON.put("label", n.getLabel());
            nodeArray.put(nodeJSON);
        }

        jsonFile.put("node", nodeArray);

        ArrayList<Way> foundWay = new ArrayList<>();
        // Questioning the optimization on this
        JSONArray edgeArray = new JSONArray();
        for (Node n : graph.getNodeMap().values()) {

//            for (Way e : n.getOutEdge()) {
//                if (!foundWay.contains(e)) {
//                    JSONObject edgeJSON = new JSONObject();
//                    edgeJSON.put("id", e.getId());
//                    edgeJSON.put("label", e.getLabel());
//                    edgeJSON.put("distance", e.getDistance());
//                    edgeJSON.put("speed-limit", e.getSpeedLimit());
//                    edgeJSON.put("traffic", e.getTraffic());
//                    edgeJSON.put("source", e.getSource().getId());
//                    edgeJSON.put("destinationNode", e.getDestination().getId());
//
//                    foundWay.add(e);
//                    edgeArray.put(edgeJSON);
//                }
//            }
        }

        jsonFile.put("edge", edgeArray);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
            writer.write(jsonFile.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
