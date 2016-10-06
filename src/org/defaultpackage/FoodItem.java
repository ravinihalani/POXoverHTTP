package org.defaultpackage;

import javax.xml.transform.stream.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.regex.Pattern;

/**
 * Servlet implementation class FoodItem
 */
// @WebServlet("/FoodItem")
@WebServlet(description = "A simple servlet", urlPatterns = { "/FoodItem", "/restservices/FoodItem" })
public class FoodItem extends HttpServlet {

	static Vector<String> existing_ids = new Vector<String>(50, 1);
	static Vector<String> existing_names = new Vector<String>(50, 1);
	static Vector<String> existing_descriptions = new Vector<String>(50, 1);
	static Vector<String> existing_categories = new Vector<String>(50, 1);
	static Vector<String> existing_prices = new Vector<String>(50, 1);
	static Vector<String> existing_countries = new Vector<String>(50, 1);

	static Vector<String> inserted_ids = new Vector<String>(50, 1);
	static Vector<String> inserted_names = new Vector<String>(50, 1);
	static Vector<String> inserted_descriptions = new Vector<String>(50, 1);
	static Vector<String> inserted_categories = new Vector<String>(50, 1);
	static Vector<String> inserted_prices = new Vector<String>(50, 1);
	static Vector<String> inserted_countries = new Vector<String>(50, 1);

	static Vector<String> requested_ids = new Vector<String>(50, 1);
	static int requested_matching_ids[] = new int[500];

	private static final long serialVersionUID = 1L;
	private static final String inputForm = "<html><body><form method =\"POST\" action=\"FoodItem\">"
			+ "<h2>ASU ID : rnihalan</h2><h3>POX over HTTP Assignment : Enter the xml below and click Submit</h3><textarea type=\"text\" name=\"xml_value_name1\" cols=\"100\" rows=\"25\"></textarea><br>"
			+ "<br><input type=\"submit\"/></form></body></html>";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().write(inputForm);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			// TODO Auto-generated method stub
			response.setContentType("application/xml");
			/*
			 * StringBuilder buffer = new StringBuilder(); BufferedReader reader
			 * = request.getReader(); String line; while ((line =
			 * reader.readLine()) != null) { buffer.append(line + "\n"); }
			 * 
			 * String xml_post_input = buffer.toString().replaceAll("\\s+", " "
			 * );
			 */

			String xml_post_input = request.getParameter("xml_value_name1");

			DocumentBuilderFactory dbtest = DocumentBuilderFactory.newInstance();
			DocumentBuilder dtestBuilder = dbtest.newDocumentBuilder();
			InputStream istr = new ByteArrayInputStream(xml_post_input.getBytes());
			Document doctestInput = dtestBuilder.parse(istr);
			doctestInput.getDocumentElement().normalize();

			System.out.println("this is from client" + xml_post_input);
			String[] lines = xml_post_input.split(System.getProperty("line.separator"));

			PrintWriter out = response.getWriter();

			for (int i = 0; i < requested_matching_ids.length; i++)
				requested_matching_ids[i] = -1;
			String read_FoodItem_xml = FoodItem.getAllUserNames("FoodItemData.xml");
			// System.out.println(read_FoodItem_xml);
			Enumeration<String> vEnum = existing_ids.elements();
			System.out.println("\nElements in vector existing_ids:");
			while (vEnum.hasMoreElements())
				System.out.print(vEnum.nextElement() + " ");

			if (lines[0].indexOf("<SelectedFoodItems xmlns") >= 0) { // IF
																		// GETFOOD
																		// ITEM
																		// REQUEST

				Pattern p = Pattern.compile("<FoodItemId>(\\w+)</FoodItemId>", Pattern.MULTILINE);
				Matcher m = p.matcher(xml_post_input);
				String temp = "";
				int j = 0;
				while (m.find()) {
					temp = m.group(1);
					int i = 0;
					// System.out.println(temp);
					inner_loop: while (i < existing_ids.size()) {
						if (existing_ids.get(i).toString().trim().equalsIgnoreCase(temp)) {
							// System.out.println("Inside if");
							System.out.println(temp + " matched");
							requested_matching_ids[j] = i;
							j++;
							break inner_loop;
						}

						if (i == existing_ids.size() - 1) {
							System.out.println(temp + " doesnt exist");
							requested_matching_ids[j] = -1 * Integer.parseInt(temp);
							j++;
						}
						++i;
					}
				}
				if (j == 0)
					response.getWriter()
							.write("<InvalidMessage xmlns=" + '"' + "http://cse564.asu.edu/PoxAssignment" + '"' + "/>");
				else {
					String xml_to_return = xml_generate(requested_matching_ids, j);
					System.out.println(xml_to_return.substring(xml_to_return.indexOf('\n') + 1));
					// response.getWriter().write(xml_to_return.substring(xml_to_return.indexOf('\n')
					// + 1));
					response.getWriter().write(xml_to_return.substring(xml_to_return.indexOf('\n') + 1));

				}
			} else if (lines[0].indexOf("<NewFoodItems xmlns") >= 0) { // IF
																		// INSERT
																		// FOOD
																		// ITEM
																		// REQUEST
				System.out.println("Logic for Insertion");

				java.net.URL url = FoodItem.class.getResource("FoodItemData.xml");

				System.out.println(url.getPath().replaceAll("FoodItemData.xml", "") + "input.xml");
				PrintWriter writer = new PrintWriter(url.getPath().replaceAll("FoodItemData.xml", "") + "input.xml",
						"UTF-8");
				System.out.println(getMaxID());
				for (int i = 0; i < lines.length; i++) {
					if (lines[i].indexOf("<name>") >= 0) {
						lines[i] = lines[i].replace("<name>", "<id>" + (getMaxID() + 1) + "</id> <name>");
					}
					writer.println(lines[i]);
				}
				writer.close();

				String read_inserted_xml = FoodItem.getAllNewlyInserted("input.xml");
				System.out.println(read_inserted_xml);
				Enumeration<String> vEnum2 = inserted_ids.elements();
				System.out.println("\nElements in vector inserted_ids:");
				while (vEnum2.hasMoreElements())
					System.out.print(vEnum2.nextElement() + " ");

				if (countryAndNameExists(inserted_countries.get(0).toString(), inserted_names.get(0).toString()) == -1) // Doesn't
																														// exist
																														// and
																														// we
																														// need
																														// to
																														// enter
				{
					System.out.println("Logic to insert the new fooditem in the xml file");
					updateEntryInFile();
					System.out.println("check new file");
					response.getWriter()
							.write("<FoodItemAdded xmlns=" + '"' + "http://cse564.asu.edu/PoxAssignment" + '"' + ">");
					response.getWriter().write("<FoodItemId>" + inserted_ids.get(0).toString() + "</FoodItemId>");
					response.getWriter().write("</FoodItemAdded>");

				} else // food item already exists
				{
					System.out.println("Food item already exists");
					response.getWriter()
							.write("<FoodItemExists xmlns=" + '"' + "http://cse564.asu.edu/PoxAssignment" + '"' + ">");
					response.getWriter()
							.write("<FoodItemId>" + countryAndNameExists(inserted_countries.get(0).toString(),
									inserted_names.get(0).toString()) + "</FoodItemId>");
					response.getWriter().write("</FoodItemExists>");

				}

			} else {
				response.getWriter()
						.write("<InvalidMessage xmlns=" + '"' + "http://cse564.asu.edu/PoxAssignment" + '"' + "/>");
			}

			// End of Logic
		} catch (Exception e) {
			response.getWriter()
					.write("<InvalidMessage xmlns=" + '"' + "http://cse564.asu.edu/PoxAssignment" + '"' + "/>");
		}

	}

	public static void updateEntryInFile() {
		try {

			java.net.URL url = FoodItem.class.getResource("FoodItemData.xml");
			File inputFile = new File(url.getPath());
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			// creating input stream
			Document doc = builder.parse(inputFile);

			Element peopleTag = (Element) doc.getElementsByTagName("FoodItemData").item(0);

			Element newPerson = doc.createElement("FoodItem");

			newPerson.setAttribute("country", inserted_countries.get(0).toString().trim());

			Element id = doc.createElement("id");
			id.setTextContent(inserted_ids.get(0).toString().trim());

			Element name = doc.createElement("name");
			name.setTextContent(inserted_names.get(0).toString().trim());

			Element description = doc.createElement("description");
			description.setTextContent(inserted_descriptions.get(0).toString().trim());

			Element category = doc.createElement("category");
			category.setTextContent(inserted_categories.get(0).toString().trim());

			Element price = doc.createElement("price");
			price.setTextContent(inserted_prices.get(0).toString().trim());

			newPerson.appendChild(id);
			newPerson.appendChild(name);
			newPerson.appendChild(description);
			newPerson.appendChild(category);
			newPerson.appendChild(price);

			peopleTag.appendChild(newPerson);

			// writing xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			// File outputFile = new
			// File(url.getPath().replaceAll("FoodItemData.xml",
			// "UpdatedFoodItemData.xml"));
			File outputFile = new File(url.getPath());
			StreamResult result = new StreamResult(outputFile);
			// creating output stream
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getAllUserNames(String fileName) {
		String tempxml = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			java.net.URL url = FoodItem.class.getResource(fileName);
			File file = new File(url.getPath());
			if (file.exists()) {
				Document doc = db.parse(file);
				Element docEle = doc.getDocumentElement();

				NodeList studentList = docEle.getElementsByTagName("FoodItem");

				if (studentList != null && studentList.getLength() > 0) {
					for (int i = 0; i < studentList.getLength(); i++) {

						Node node = studentList.item(i);

						if (node.getNodeType() == Node.ELEMENT_NODE) {

							tempxml += "=====================";
							tempxml += "\n";
							Element e = (Element) node;
							tempxml += ("Country =" + e.getAttribute("country") + "\n");
							existing_countries.addElement(new String(e.getAttribute("country")));
							NodeList nodeList = e.getElementsByTagName("id");
							tempxml += ("ID: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							existing_ids
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
							nodeList = e.getElementsByTagName("name");
							tempxml += ("Name: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							existing_names
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
							nodeList = e.getElementsByTagName("description");
							tempxml += ("Description: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							existing_descriptions
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
							nodeList = e.getElementsByTagName("category");
							tempxml += ("Category: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							existing_categories
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
							nodeList = e.getElementsByTagName("price");
							tempxml += ("Price: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							existing_prices
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
						}
					}
					return tempxml;
				} else {
					System.exit(1);

				}
			}
			return tempxml;
		} catch (Exception e) {

			System.out.println(e);
			return "Error";
		}
	}

	public static String getAllNewlyInserted(String fileName) {
		String tempxml = "";
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			java.net.URL url = FoodItem.class.getResource(fileName);
			File file = new File(url.getPath());
			if (file.exists()) {
				Document doc = db.parse(file);
				Element docEle = doc.getDocumentElement();

				NodeList studentList = docEle.getElementsByTagName("FoodItem");

				if (studentList != null && studentList.getLength() > 0) {
					for (int i = 0; i < studentList.getLength(); i++) {

						Node node = studentList.item(i);

						if (node.getNodeType() == Node.ELEMENT_NODE) {

							tempxml += "=====================";
							tempxml += "\n";
							Element e = (Element) node;
							tempxml += ("Country =" + e.getAttribute("country") + "\n");
							inserted_countries.addElement(new String(e.getAttribute("country")));
							NodeList nodeList = e.getElementsByTagName("id");
							tempxml += ("ID: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							inserted_ids
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
							nodeList = e.getElementsByTagName("name");
							tempxml += ("Name: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							inserted_names
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
							nodeList = e.getElementsByTagName("description");
							tempxml += ("Description: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							inserted_descriptions
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
							nodeList = e.getElementsByTagName("category");
							tempxml += ("Category: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							inserted_categories
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
							nodeList = e.getElementsByTagName("price");
							tempxml += ("Price: " + nodeList.item(0).getChildNodes().item(0).getNodeValue());
							inserted_prices
									.addElement(new String(nodeList.item(0).getChildNodes().item(0).getNodeValue()));
							tempxml += "\n";
						}
					}
					return tempxml;
				} else {
					System.exit(1);

				}
			}
			return tempxml;
		} catch (Exception e) {

			System.out.println(e);
			return "Error";
		}
	}

	public static String xml_generate(int position[], int limit) {
		DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder icBuilder;
		try {
			icBuilder = icFactory.newDocumentBuilder();
			Document doc = icBuilder.newDocument();
			Element mainRootElement = doc.createElementNS("http://cse564.asu.edu/PoxAssignment", "RetrievedFoodItems");
			doc.appendChild(mainRootElement);
			String temp_invalid = "";
			for (int i = 0; i < limit; i++) {
				// System.out.println(position[i]);
				if (position[i] < 0) {
					temp_invalid = "" + Math.abs(position[i]);
					mainRootElement.appendChild(getInvalidFI(doc, temp_invalid));
				} else {
					mainRootElement.appendChild(getFI(doc, existing_countries.get(position[i]).toString().trim(),
							existing_ids.get(position[i]).toString().trim(),
							existing_names.get(position[i]).toString().trim(),
							existing_descriptions.get(position[i]).toString().trim(),
							existing_categories.get(position[i]).toString().trim(),
							existing_prices.get(position[i]).toString().trim()));
				}
			}
			// output DOM XML to console
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			// StreamResult console = new StreamResult(System.out);
			StringWriter console = new StringWriter();
			transformer.transform(source, new javax.xml.transform.stream.StreamResult(console));
			String s = console.toString();
			return s;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "<InvalidMessage xmlns=" + '"' + "http://cse564.asu.edu/PoxAssignment" + '"' + "/>";
	}

	private static Node getInvalidFI(Document doc, String id) {
		Element FI = doc.createElement("InvalidFoodItem");
		FI.appendChild(getCompanyElements(doc, FI, "FoodItemId", id));
		return FI;
	}

	private static Node getFI(Document doc, String country, String id, String name, String description, String category,
			String price) {
		Element FI = doc.createElement("FoodItem");
		FI.setAttribute("country", country);
		FI.appendChild(getCompanyElements(doc, FI, "id", id));
		FI.appendChild(getCompanyElements(doc, FI, "name", name));
		FI.appendChild(getCompanyElements(doc, FI, "description", description));
		FI.appendChild(getCompanyElements(doc, FI, "category", category));
		FI.appendChild(getCompanyElements(doc, FI, "price", price));
		return FI;
	}

	// utility method to create text node
	private static Node getCompanyElements(Document doc, Element element, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}

	private static int getMaxID() {
		int max = 0;
		for (int i1 = 0; i1 < existing_ids.size(); i1++) {
			if (Integer.parseInt(existing_ids.get(i1).toString().trim()) > max)
				max = Integer.parseInt(existing_ids.get(i1).toString().trim());
		}
		return max;
	}

	private static int countryAndNameExists(String country, String name) {
		country = country.trim();
		name = name.trim();

		for (int i1 = 0; i1 < existing_ids.size(); i1++) {
			if (existing_countries.get(i1).toString().trim().equals(country)
					&& existing_names.get(i1).toString().trim().equals(name))
				return Integer.parseInt(existing_ids.get(i1).toString().trim());
		}
		return -1;
	}

}
