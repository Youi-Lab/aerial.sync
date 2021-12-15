package aerial.sync.network;

import java.util.List;
import java.util.ArrayList;
import java.lang.Integer;
import java.lang.Double;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Sensor {
   
    public String id;
	public String serial;
	public String type; 
	public String units; 
	public String token;
	public String latlong;
	public String metadata;
	public List<SensorData> data; 
	  
	public Sensor(String serial,  String type, String units, String token, String latlong, String metadata) {
        
		// id is automatically assigned during initialization 
		this.serial = serial;
		this.type = type;
		this.units = units;
		this.token = token;
		this.latlong = latlong;
		this.metadata = metadata;
		this.data = new ArrayList<SensorData>();
	}

	public String getId() {
		return id;
	}

	public String getSerial() {
		return serial;
	}	

	public String getType() {
		return type;
	}

	public String getUnits() {
		return units;
	}

	public String getToken() {
		return token;
	}

	public String getLatlong() {
		return latlong;
	}	

	public String getMetadata() {
		return metadata;
	}
	
	public List<SensorData> getData(){
		return data;
	}

	public void setId(String i) {
		id = i;
	}
	public void setSerial(String s) {
		serial = s;
	}

    public void setType(String t) {
		type = t;
	}

    public void setUnits(String u) {
		units = u;
	}

    public void setLatlong(String l) {
		units = l;
	}

	public void setMetadata(String m) {
		metadata = m;
	}

    public  boolean isInteger(String string) {
        int intValue;
		boolean ret;
		
        if(string == null || string.equals("")) {
            ret = false;
        }  
        try {
            intValue = Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
           ret = false;
        }
		return ret;	
    }

    public boolean isDouble(String string) {
        double doubleValue;
		boolean ret;
		
        if(string == null || string.equals("")) {
            ret =false;
        }   
        try {
            doubleValue = Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
          ret = false;
        }
		return ret;
    }

	public boolean updateData(String datetime, String val) {

        boolean ret = false;
		SensorData sd = new SensorData(datetime, val);

       if ( isInteger(datetime)  &&  isDouble(val) ) { 
			data.add(sd);
			ret = true;
		} else {
			ret = false;
		}
		return ret;
	}

	public String toJson() {

		String json = "{\"serial\": "  + "\"" + serial  + "\""  + ", \"type\": " 
		                        + "\"" + type + "\"" + ", \"units\": " + "\"" + units  + "\""
								+   ", \"token\": " + "\"" + token   + "\"" +  ", \"latlong\": " + "\"" + latlong  + "\"" 
								+ ", \"metadata\": " + "\"" + metadata
								+ " ,\"data\": "  + "[";

		for(int i = 0; i < data.size(); i++){
			if ( i < data.size() - 1) {
			    json +=  "{\"datetime\": " + "\"" + data.get(i).getDatetime()  + "\"" +  ", \"val\": " +  "\"" + data.get(i).getVal() +  "\""  + "},";
			} else {
				json +=  "{\"datetime\": " + "\"" + data.get(i).getDatetime() + "\"" + ", \"val\": " + "\"" + data.get(i).getVal() +  "\""  + "}";
			}
		}
		json = json + "]}";
		return json;
	}

    @Override
    public String toString() {
      return "Sensor [ id=" + id + ", serial=" + serial  + ", type=" + type + ", units=" + units + "token=" + token + "latlong="  + latlong +  ", metadata=" + metadata + "]";
	}

}