package wegilant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Main {

	public static void main(String[] args) throws Exception {
		Main main = new Main();
		System.out.println("Reading remote file: ");
		File remoteFile = main.readRemoteFile("https://appvigil.co//test1.xml");
		System.out.println("Reading local file: ");
		File localFile = main.readLocalFile("resources\\test2.xml");
		System.out.println("Merging two files: ");
		File mergedFile = new XMLMerger().mergeXml(remoteFile, localFile);
		
		BlockingQueue<String> queue = new ArrayBlockingQueue<String>(3);

        Producer producer = new Producer(queue,mergedFile);
        Consumer consumer = new Consumer(queue);

        System.out.println("Starting Producer thread");
        new Thread(producer).start();
        System.out.println("Starting Consumer thread");
        new Thread(consumer).start();

        Thread.sleep(4000);
	}
	
	private File readLocalFile(String path) {
		return new File(path);
	}

	private File readRemoteFile(String path) {
		try {
			ignoreCertificates();
			URL url = new URL(path);
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			File file = new File("resources\\test1.xml");
			OutputStream outputStream = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			outputStream.close();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void ignoreCertificates() {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs,
					String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs,
					String authType) {
			}
		} };

		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc
					.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
