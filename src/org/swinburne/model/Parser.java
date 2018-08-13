package org.swinburne.model;

import jdk.nashorn.internal.parser.JSONParser;
import org.apache.commons.io.FileUtils;
import org.json.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Parser {
    public Graph generateGraph(String filename) {
        try {
            File inputFile = new File(filename);
            String content = FileUtils.readFileToString(inputFile, "UTF-8");

            JSONObject graphJSON = new JSONObject(content);
            Graph result = new Graph();

            JSONArray nodeArray = graphJSON.getJSONArray("node");
            for (Object obj: nodeArray) {
                JSONObject jsonObj = (JSONObject) obj;
                Node node = new Node();
                node.setId(jsonObj.);

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
