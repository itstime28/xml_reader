package wegilant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Producer implements Runnable {

	protected BlockingQueue<String> queue = null;

	private List<String> nodes = new ArrayList<String>();
	private File xmlFile = null;

	public Producer(BlockingQueue<String> queue, File file) {
		this.queue = queue;
		xmlFile = file;
		createList();
	}

	public void run() {
		System.out.println("In Producer");
		for (String val : nodes) {
			try {
				System.out.println("In Producer " + val);
				queue.put(val);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void createList() {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc = null;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(this.xmlFile);
		} catch (Exception e) {
			System.err.println("");
			return;
		}
		NodeList nList = doc.getElementsByTagName("dependency");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				try {
					nodes.add(eElement.getElementsByTagName("groupId").item(0)
							.getTextContent());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}