ASU ID : 1210448145  / rnihalan
NAME : RAVI NIHALANI
SOFTWARE DESIGN ASSIGNMENT 1 : POX over HTTP Readme


Follow the below instructions to install the project in Eclipse IDE :

1. Copy the POX-FoodMenu-rnihalan-Eclipse.zip at any location on your local system

2. Unzip the file

3. Go to Eclipse and click on File -> Import --> General --> Existing Project into Workspace and click on Next

4. Check the "Select root directory" and click on "Browse" and select the unzipped folder POX-FoodMenu-rnihalan-Eclipse in your local system and click on Open

5. Once done, the Projects section should show POX-FoodMenu-rnihalan-Eclipse . If yes, click on Finish

6. The project has been successfully imported.

7. Now setup the Tomcat server in Eclipse by right clicking the "Servers" section at the bottom of Eclipse window.
   Select "Tomcat v7.0 Server" as the Server type,
   Type "localhost" as the Server's host name,
   Type "Tomcat v7.0 Server at localhost" as Server name, and
   Select "Apache Tomcat v7.0" as Server runtime environment
   
   Finally click on "Finish"
   
8. Now right click on the Project "POX-FoodMenu-rnihalan-Eclipse" in the Project Explorer and click on 
	
	Run as --> 1 Run on Server 
   
9. The application will start running on the browser  in Eclipse . Better, we can test on Chrome by putting the URL 
	http://localhost:8080/POX-FoodMenu-rnihalan-Eclipse/restservices/FoodItem
	
10. Put the xml to be tested in the textbox and click on Submit. Once tested, go back on browser and resubmit new xml to be tested.



Test cases :

1. Adding new FoodItem by adding the following format :

	<NewFoodItems xmlns="http://cse564.asu.edu/PoxAssignment">
    <FoodItem country="GB">
        <name>Cornish Pasty</name>
        <description>Tender cubes of steak, potatoes and swede wrapped in flakey short crust pastry.  Seasoned with lots of pepper.  Served with mashed potatoes, peas and a side of gravy</description>
        <category>Dinner</category>
        <price>15.95</price>
    </FoodItem>
	</NewFoodItems>
	
2. Adding existing FoodItem should display :
/Users/ravinihalani/POX-FoodMenu-rnihalan-Eclipse/readme.txt
<FoodItemExists xmlns="http://cse564.asu.edu/PoxAssignment">
   <FoodItemId>156</FoodItemId>
</FoodItemExists>


3. Adding invalid xml should throw invalid xml message :

<InvalidMessage xmlns="http://cse564.asu.edu/PoxAssignment"/>


4. Getting food item by putting : 

	<SelectedFoodItems xmlns="http://cse564.asu.edu/PoxAssignment">
   	<FoodItemId>100</FoodItemId>
   	<FoodItemId>156</FoodItemId>
	</SelectedFoodItems>
