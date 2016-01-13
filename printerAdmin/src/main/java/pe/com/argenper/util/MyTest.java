package pe.com.argenper.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.swt.graphics.ImageData;

public class MyTest {

	public static void main(String[] args) throws IOException {
		//ImageData imageData = new ImageData(MyUtils.class.getClassLoader().getResourceAsStream("impresora.png"));
		//System.out.println(imageData);
		//tareaBack().start();
		URL url = new URL("http://desarrollo.argenpercorp.com/sys/commons_asp/impresiones_acc.asp?token=8BB59682-3D10-42D6-BCA6-BDACF41BA0C5&acc_imp=LOADDOCIMPRE&app=app&app_imp=app&Imp_idOperacion=11882&Imp_idImpre=1159");
		InputStream is = url.openStream();
		int ptr = 0;
		StringBuffer buffer = new StringBuffer();
		while ((ptr = is.read()) != -1) {
		    buffer.append((char)ptr);
		}
		System.out.println("buffer: "+buffer.toString());
	}
	public static Thread tareaBack() 
	{	
		return new Thread() {
	    	public void run() {
	    		System.out.println("probando...");
	    		try {
		          Thread.sleep(Long.parseLong(MyUtils.getProperty("time.recarga"))); // cinco minutos
		        } catch (InterruptedException e) {
		          e.printStackTrace();
		        }
	    		
	    	}
	    };
	}
	public static Properties getMainConfig() throws IOException {

		Properties PROPERTY=new Properties();
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
		return PROPERTY;
	}
}
