package aerial.sync.network;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class User {
   
    public String id;
	public String username;
    public String password;
	public String token;
	public List<String> sensors; 
	  
	public User(String username,  String password, String token) {
        
		// id is automatically assigned during initialization 
		this.username = username;
		this.password = password;
		this.token = token;
		this.sensors = new ArrayList<String>();
	}

	public String getId() {
		return id;
	}

	public String getUserName() {
		return username;
	}	

	public String getPassword() {
		return password;
	}

	public String getToken() {
		return token;
	}
	
	public List<String> getSensors(){
		return sensors;
	}

	public void setId(String i) {
		id = i;
	}
	public void setUserName(String u) {
		username = u;
	}

    public void setPassword(String p) {
		password = p;
	}

    public void setToken(String t) {
		token = t;
	}
	public boolean addSensor(String serial) {
	   if (!sensors.contains(serial)) {
       	   return sensors.add(serial);
	   } else {
		   return false;
	   }
	}

	public boolean removeSensor(String serial) {
	   	if (sensors.contains(serial)) {
       	   return sensors.remove(serial);
	   } else {
		   return false;
	   }	
	}

	public String toJson() {

		String json = "{\"username\": "  + "\"" + username  + "\""  + ", \"password\": " 
		                        + "\"" + password + "\"" + ", \"units\": " + "\"" + token  + "\""
								+ " ,\"sensors\": "  + "[";

		for(int i = 0; i < sensors.size(); i++){
			if ( i < sensors.size() - 1) {
			    json +=  "{\"serial\": " + "\"" + sensors.get(i)  + "\"" +  "},";
			} else {
				json +=  "{\"serial\": " + "\"" + sensors.get(i) + "\"" +   "}";
			}
		}
		json = json + "]}";
		return json;
	}

}