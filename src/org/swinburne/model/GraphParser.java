package org.swinburne.model;

import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.io.FileUtils;
import org.json.*;
import org.swinburne.util.RandomStringGenerator;

import java.io.*;
import java.util.ArrayList;

public class GraphParser {
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
                node.setLongtitude(jsonObj.getDouble("longitude"));

                result.addNode(node);
            }

            JSONArray edgeArray = graphJSON.getJSONArray("edge");
            for (Object obj : edgeArray) {
                JSONObject jsonObj = (JSONObject) obj;
                Edge edge = new Edge();

                Node source = result.findNodeByID(jsonObj.getString("source"));
                Node destination = result.findNodeByID(jsonObj.getString("destination"));

                edge.setSource(source);
                edge.setDestination(destination);

                try {
                    edge.setId(jsonObj.getString("id"));
                } catch (JSONException jsone) {
                    edge.setId(RandomStringGenerator.generateRandomString(10));
                }
                edge.setLabel(jsonObj.getString("label"));
                edge.setDistance(jsonObj.getFloat("distance"));
                edge.setSpeedLimit(jsonObj.getFloat("speed-limit"));
                edge.setTraffic(jsonObj.getFloat("traffic"));
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

        for (Node n : graph.getNodeList()) {
            JSONObject nodeJSON = new JSONObject();
            nodeJSON.put("id", n.getId());
            nodeJSON.put("label", n.getLabel());
            nodeArray.put(nodeJSON);
        }

        jsonFile.put("node", nodeArray);

        ArrayList<Edge> foundEdge = new ArrayList<>();
        // Questioning the optimization on this
        JSONArray edgeArray = new JSONArray();
        for (Node n : graph.getNodeList()) {

            // Can we make this shorter later?
            for (Edge e : n.getInEdge()) {
                if (!foundEdge.contains(e)) {
                    JSONObject edgeJSON = new JSONObject();
                    edgeJSON.put("id", e.getId());
                    edgeJSON.put("label", e.getLabel());
                    edgeJSON.put("distance", e.getDistance());
                    edgeJSON.put("speed-limit", e.getSpeedLimit());
                    edgeJSON.put("traffic", e.getTraffic());
                    edgeJSON.put("source", e.getSource().getId());
                    edgeJSON.put("destination", e.getDestination().getId());

                    foundEdge.add(e);
                    edgeArray.put(edgeJSON);
                }
            }

            for (Edge e : n.getOutEdge()) {
                if (!foundEdge.contains(e)) {
                    JSONObject edgeJSON = new JSONObject();
                    edgeJSON.put("id", e.getId());
                    edgeJSON.put("label", e.getLabel());
                    edgeJSON.put("distance", e.getDistance());
                    edgeJSON.put("speed-limit", e.getSpeedLimit());
                    edgeJSON.put("traffic", e.getTraffic());
                    edgeJSON.put("source", e.getSource().getId());
                    edgeJSON.put("destination", e.getDestination().getId());

                    foundEdge.add(e);
                    edgeArray.put(edgeJSON);
                }
            }
        }

        jsonFile.put("edge", edgeArray);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false));
//            System.out.println(jsonFile.toString());
            writer.write(jsonFile.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
