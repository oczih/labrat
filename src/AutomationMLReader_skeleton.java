import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class AutomationMLReader_skeleton {

    public static void main(String[] args) {
        try {
            File inputFile = new File("testing_station.aml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // 1. Change Outer Loop: Search for "SystemUnitClass" first, not InternalElement
            NodeList systemUnitList = doc.getElementsByTagName("SystemUnitClass");

            // Get the list of InternalElements once to use in the inner loop
            NodeList internalElementList = doc.getElementsByTagName("InternalElement"); // [cite: 141]

            // Loop through every SystemUnitClass found
            for (int temp = 0; temp < systemUnitList.getLength(); temp++) {
                Node nNode = systemUnitList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element systemUnitElement = (Element) nNode;

                    // 2. Store the class name and create the search path [cite: 118, 140]
                    String className = systemUnitElement.getAttribute("Name");
                    String searchPath = "SystemUnitClassLib/" + className;

                    System.out.println("Internal elements with system unit class " + searchPath);

                    // 3. Inner Loop: Iterate through all InternalElements to find matches [cite:
                    // 141, 144]
                    for (int temp2 = 0; temp2 < internalElementList.getLength(); temp2++) {
                        Node nNode2 = internalElementList.item(temp2);

                        if (nNode2.getNodeType() == Node.ELEMENT_NODE) {
                            Element internalElement = (Element) nNode2;

                            // 4. Compare RefBaseSystemUnitPath with the current class path
                            String refPath = internalElement.getAttribute("RefBaseSystemUnitPath");

                            if (refPath.equals(searchPath)) {
                                // If match, print the name of the InternalElement [cite: 152]
                                System.out.println(internalElement.getAttribute("Name"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}