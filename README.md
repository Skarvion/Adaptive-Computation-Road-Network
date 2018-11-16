# Vehicle Path Search Framework
This framework is used to quickly develop and test search and heuristic algorithm for vehicular navigation. It has a built-in GUI controller, map visualizer and can load OSM file to create a real-world road map model. 

The aim of this framework is to allow future developers to build and test their algorithm first quickly without setting up the architecture and the map handling.

## Instruction Manual
This framework is running a user-friendly interface for ease of testing procedure. The following section describe on how to use the GUI environment. To run the program, use the built jar file in the out directory. 
<br />![Program GUI](https://raw.githubusercontent.com/Skarvion/Vehicle-Path-Search-Framework/master/readme/Program.png)

### Loading OSM File
Before any operation can be done, the software must load an OSM file first. The traffic signal CSV is optional.

1.	Select “Open map file…”. Following prompt appear: <br />
![Oepning map files](https://github.com/Skarvion/Vehicle-Path-Search-Framework/blob/master/readme/Open%20File.PNG)
2.	Select “Open...” for traffic signal CSV
3.	Select specified VicRoad CSV file
4.	Select “Open…”
5.	Select a specified OSM file
6.	If you wish to render the whole map without boundary, select “Unbounded” radio button. Otherwise, select “Bounded” radio button and fill in the boundary coordinates
7.	Select “Ok”

If you would like to reset map, select “Reload” on the main window.

### Performing Search Function
1.	Load an OSM file
2.	Select an implemented search algorithm in the drop-down menu
3.	Select “Start” radio button
4.	Select the starting node position on the map. It will be marked with a blue pin
5.	Select “Finish” radio button
6.	Select the destination node position on the map. It will be marked with a red pin
7.	Select “Search Path”

User can also find node with any selected node selector mode with the ID search at the bottom of the Search Function section.

### Generate Test Case
1.	Load an OSM file
2.	Select “Generate Test Case”. Following prompt appear:
<br />![Test case generator](https://github.com/Skarvion/Vehicle-Path-Search-Framework/blob/master/readme/Test%20Case.PNG)
3.	Select number of test cases
4.	Fill in the file prefix
5.	Choose the directory with “Select directory…” 
6.	Select “Generate”

For each search setting, the resultant test case will be saved in the output directory, with the name of the file prefix and appended with the name of the search setting. They are saved as separate CSV files.

### Examining Node Property
With any of the node selector mode picked, click on any node. Detailed information will be shown in the Properties table.

### Navigation Control
User can use the arrow key after selecting the map and use the scroll bars. User can also use the zoom in and zoom out button at the Navigation section.

## Using the Framework
This framework allows for quick test of search algorithm and heuristic generation. To quickly implement new search algorithm, implement the **SearchSetting** class in the code. The most important function is to implement the *computeDirection* to calculate the navigation path from start to finish and *deriveSolution* to return the array of nodes path as result. Other visualization tool can be used such as *drawFrontier* function. After implementation, they should be added to the static list of **SearchSetting** in the abstract class itself, so they can be used by the rest of the framework.

**Node** and **Way** class are used to represent the details of a map and all of them are stored in a single **Graph** class. They are currently following the convention from OSM and has many built-in features such as unique ID identifier, graph connection and reset state. They can hold a map/dictionary of metadata which allow for storage of non-essential data. They are free to be modified to suit different needs.

Detailed explanation of the software architecture can be seen in the research paper and the JavaDocs zip file on the repository. Examples of implementing the search settings can be seen in the **BestDistanceSearch** and **BestTimeSearch** class.
