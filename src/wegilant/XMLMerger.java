package wegilant;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLMerger {

	private static final String[] NODES = { "project/dependencies" };

	public File mergeXml(File file1, File file2) throws Exception {
		return mergeXml(file1, file2, NODES);
	}

	public File mergeXml(File file1, File file2, String[] nodes) {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		Document base = null;
		try {
			docBuilderFactory.setIgnoringElementContentWhitespace(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			base = docBuilder.parse(file1);
			for (String expression : nodes) {
				XPathExpression compiledExpression = xPathFactory.newXPath()
						.compile(expression);
				Node outputNodes = (Node) compiledExpression.evaluate(base,
						XPathConstants.NODE);
				if (outputNodes == null) {
					throw new IOException("Invalid node: " + expression);
				}
				NodeList lst = outputNodes.getChildNodes();
				Map<String, Node> map = new HashMap<String, Node>();
				for (int i = 0; i < lst.getLength(); i++) {
					Node nNode = lst.item(i);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode; 
//						System.out.println(eElement.getFirstChild()!=null?eElement.getFirstChild().getNextSibling().getTextContent():"null");
						map.put(eElement.getFirstChild().getNextSibling().getTextContent(), nNode);
					}
				}
//				System.out.println(map.toString());
				Document secondFile = docBuilder.parse(file2);
				Node nextNodes = (Node) compiledExpression.evaluate(secondFile,
						XPathConstants.NODE);
				while (nextNodes.hasChildNodes()) {
					Node child = nextNodes.getFirstChild();
					nextNodes.removeChild(child);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) child; 
						if(map.get(eElement.getFirstChild().getNextSibling().getTextContent()) == null){
//							System.out.println(eElement.getFirstChild()!=null?eElement.getFirstChild().getNextSibling().getTextContent():"null");
							child = base.importNode(child, true);
							outputNodes.appendChild(child);
						}
					}
					else{
						child = base.importNode(child, true);
						outputNodes.appendChild(child);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error while merging xml files: ");
			e.printStackTrace();
		}
		return writeOutputXml(base);
	}

	private File writeOutputXml(Document base) {
		File output = null;
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		try {
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(base);
			output = new File("resources\\output.xml");
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
		} catch (Exception e) {
			System.err.println("Error while writing output xml file: "
					+ e.getStackTrace());
			e.printStackTrace();
		}
		return output;
	}

	public static void main(String[] args) throws Exception {
		File file1 = new File("resources\\test1.xml");
		File file2 = new File("resources\\test2.xml");
		new XMLMerger().mergeXml(file1, file2);

	}
}
