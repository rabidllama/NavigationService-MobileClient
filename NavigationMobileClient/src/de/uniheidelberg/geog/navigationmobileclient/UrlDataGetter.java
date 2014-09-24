package de.uniheidelberg.geog.navigationmobileclient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class UrlDataGetter {
	byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}
	
	public String getUrl(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}
	
	public String getPostData(String url, String jsonMessage) throws IOException, HttpException {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		httpPost.setHeader("content-type", "application/json; charset=utf8");
		httpPost.setHeader("accept", "application/json");
		
		StringEntity se = new StringEntity(jsonMessage.toString());
		httpPost.setEntity(se);
		HttpResponse response = httpClient.execute(httpPost);
		// Check that the data is OK
		if(response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
			// Error communicating
			throw new HttpException("Error communicating with server: " + response.getStatusLine().getReasonPhrase());
		}
		InputStream iStream = response.getEntity().getContent();
		
		String result = "";
		if(iStream != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
			String line = "";
			while((line = reader.readLine()) != null) {
				result += line;
			}
			iStream.close();
		}
		return result;
	}
}
