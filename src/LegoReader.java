import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LegoReader {

    public static void main(String[] args) {
        try {
            // Varmista että tiedostonimi on oikein
            File inputFile = new File("lego_station.aml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            // 1. Etsi kaikki InternalElementit
            NodeList internalElements = doc.getElementsByTagName("InternalElement");

            // Käydään läpi kaikki elementit etsien ne, jotka ovat "Cell"-luokkaa
            for (int i = 0; i < internalElements.getLength(); i++) {
                Node cellNode = internalElements.item(i);

                if (cellNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element cellElement = (Element) cellNode;

                    // Tarkistetaan onko kyseessä Cell
                    // (RefBaseSystemUnitPath="SystemUnitClassLib/Cell")
                    if ("SystemUnitClassLib/Cell".equals(cellElement.getAttribute("RefBaseSystemUnitPath"))) { //

                        // Tulostetaan solun nimi (esim. Cell1 tai Cell2)
                        System.out.println(cellElement.getAttribute("Name") + ":");

                        // 2. Haetaan solun lapsielementit (LegoBuffer ja AssemblyStation)
                        NodeList children = cellNode.getChildNodes(); //

                        for (int j = 0; j < children.getLength(); j++) {
                            Node childNode = children.item(j);

                            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element deviceElement = (Element) childNode;
                                String deviceName = deviceElement.getAttribute("Name");

                                // Tarkistetaan onko lapsi LegoBuffer tai AssemblyStation
                                if (deviceName.equals("LegoBuffer") || deviceName.equals("AssemblyStation")) {

                                    // 3. Haetaan attribuutit X ja Z
                                    String xVal = getAttributeValue(deviceElement, "X"); //
                                    String zVal = getAttributeValue(deviceElement, "Z"); //

                                    // Tulostetaan muodossa: "LegoBuffer: X: 5; Z: -29;"
                                    System.out.println(deviceName + ": X: " + xVal + "; Z: " + zVal + ";");
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Apumetodi attribuutin arvon hakemiseen (Kts. PDF Vinkki-taulukon viimeinen
    // kohta)
    private static String getAttributeValue(Element element, String attributeName) {
        NodeList attributes = element.getElementsByTagName("Attribute"); //

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attrNode = attributes.item(i);
            if (attrNode.getNodeType() == Node.ELEMENT_NODE) {
                Element attrElement = (Element) attrNode;
                // Jos attribuutin nimi täsmää (esim "X" tai "Z")
                if (attrElement.getAttribute("Name").equals(attributeName)) {
                    // Haetaan <Value> elementti
                    NodeList values = attrElement.getElementsByTagName("Value"); //
                    if (values.getLength() > 0) {
                        return values.item(0).getTextContent(); //
                    }
                }
            }
        }
        return "N/A";
    }
}