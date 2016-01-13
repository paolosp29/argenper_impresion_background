package pe.com.argenper.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class MyUtils {
	private static Properties PROPERTY = null;

	private MyUtils() {
	}

	public static Properties getMainConfig() throws IOException {
		if (PROPERTY == null) {
			PROPERTY=new Properties();
			InputStream inputStream = null;
			String resource = "mainConfig.properties";
			try {
				inputStream = MyUtils.class.getClassLoader().getResourceAsStream(resource);
				PROPERTY.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				inputStream.close();
			}
		}
		return PROPERTY;
	}
	
	public static String getProperty(String key){
		String property=null;
		try {
			property=getMainConfig().getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return property;
	}
	
	public static String getHtmlFromUrl(String strUrl) throws IOException{
		URL url = new URL(strUrl);
		InputStream is = url.openStream();
		int ptr = 0;
		StringBuffer buffer = new StringBuffer();
		while ((ptr = is.read()) != -1) {
		    buffer.append((char)ptr);
		}
		return buffer.toString();
	}
}
