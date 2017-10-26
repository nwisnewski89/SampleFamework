package zeta.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestSettings {
	Properties properties;
	
	public TestSettings(){
		properties= new Properties();
		try {
			properties.load(new FileInputStream("settings.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getAdminUserName(){
		return properties.getProperty("admin_userName");
	}
	
	public String getAdminPassWord(){
		return properties.getProperty("admin_passWord");
	}
	
	public String getUserName(){
		return properties.getProperty("user_userName");
	}
	
	public String getPassWord(){
		return properties.getProperty("user_passWord");
	}
	
	public String getAdminFirstName(){
		return properties.getProperty("admin_firstName");
	}
	
	public String getDummyText(){
		return properties.getProperty("dummyText");
	}
	
	public String getHub(){
		return properties.getProperty("hub");
	}
	
	public String getBStackName(){
		return properties.getProperty("bStackName");
	}
	
	public String getBStackKey(){
		return properties.getProperty("bStackKey");
	}

}
