package aerial.sync.network;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.MongoClientSettings;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util. List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

import org.springframework.data.mongodb.core.query.Criteria;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.update;
import org.springframework.data.mongodb.core.query.Query;
import static org.springframework.data.mongodb.core.query.Query.query;


@RestController
@RequestMapping("/aerialsync")
public class NetworkController {

	static String aerialSyncSensorsDb = "aerialSyncSensors";
	static String aerialSyncUsersDb = "aerialSyncUsers";

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

	 
	@PostMapping("/sensors/greetsensor")
	public String greetsensorpar(@RequestParam String serial,
							     @RequestParam String type,
							     @RequestParam String units,
								 @RequestParam String token,
								 @RequestParam String latlong,
								 @RequestParam String metadata) {

       // Create mongo client
		MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoClientDbFactory(MongoClients.create(), aerialSyncSensorsDb) );

        // Check if sensor is already in database
		String json;
		Sensor sensor; 
		Sensor s;
        Query query = new Query(Criteria.where("serial").is(serial));
		json = "{" + "\"serial: \"" + serial + "\", type: \"" + type +  "\", units: \"" + units + "\", token: \"" + token + "\", latlong: \"" + latlong  + "\", metadata: \"" + metadata + "}";
        sensor = mongoTemplate.findOne(query, Sensor.class);
		if (sensor == null) {
			s = mongoTemplate.insert(new Sensor(serial,type, units, token, latlong, metadata));
			return json;
		} else {
			return  "Sensor " + json +  " is already in the data base.";
		}
	} // curl -X POST "http://localhost:8080/aerialsync/sensors/greetsensor" -F "serial=IPI-SENSOR-00000100"  -F "type=PM2.5" -F "units=um/m3" -F "token=1234" -F "latlong= 111,222" -F "metadata= my new sensor"
      // curl -X POST "http://192.168.6.61:8083/aerialsync/sensors/greetsensor" -F "serial=IPI-SENSOR-00000100"  -F "type=PM2.5" -F "units=um/m3" -F "token=1234" -F "latlong= 111,222" -F "metadata= my new sensor"

   
	@PostMapping("/sensors/greetsensorjson")
	public String greetsensorjson( @RequestBody Sensor sensor) {

       // Create mongo client
		MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoClientDbFactory(MongoClients.create(), aerialSyncSensorsDb) );

        // Check if sensor is already in database
		String json;
		Sensor s; 
        Query query = new Query(Criteria.where("serial").is(sensor.getSerial()));
		json = "{" + "\"serial: \"" + sensor.getSerial() +  "\", type: \"" + sensor.getType() +  "\", units: \"" + sensor.getUnits() + "\", token: \"" + sensor.getToken()  + "\", latlong : \"" + sensor.getLatlong() + "\", metadata: \"" + sensor.getMetadata() + "}";

        s = mongoTemplate.findOne(query, Sensor.class);
		if (s == null) {
			mongoTemplate.insert(new Sensor(sensor.getSerial(), sensor.getType(), sensor.getUnits(), sensor.getToken(), sensor.getLatlong(), sensor.getMetadata()));
			return json;
		} else {
			return  "Sensor " + json +  " is already in the data base.";
		}
	} // curl -X POST http://localhost:8080/aerialsync/sensors/greetsensorjson -H 'Content-type:application/json' -d '{"serial":"IPI-SENSOR-00000103", "type":"PM2.5", "units":"um/m3", "token":"1234", "latlong":"111,222", "metadata":"my new sensor"}'

	@PostMapping("/sensors/uploadmeasurement")
	public String datasensor( @RequestParam String serial, 
	                          @RequestParam String datetime, 
							  @RequestParam String val) {

       
	   
	   // Create mongo client
		MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoClientDbFactory(MongoClients.create(), aerialSyncSensorsDb) );

        // Check if sensor is already in database
		String json;
		Sensor sensor; 
        Query query = new Query(Criteria.where("serial").is(serial));
		json = "{" + "\"serial: \"" + serial + "\", datetime: \"" + datetime + "\", val: \"" + val + "}";

        // datetime should be in a 10 digit UTC number

	    if (isNumeric(datetime) == false || datetime.length() != 10 ) {
	    	 return  "Error: datetime " + json +  " datetime should be a UTC 10 digit number.";  
	    }

        sensor = mongoTemplate.findOne(query, Sensor.class);
		if (sensor != null) {
			sensor.updateData(datetime, val);
			mongoTemplate.updateFirst(query(where("id").is(sensor.getId())), update("data", sensor.getData()), Sensor.class);
			return json;
		} else {
			return  "Error: Sensor " + json +  " is not in the data base.";
		}
	} // curl -X POST http://localhost:8080/aerialsync/sensors/uploadmeasurement -F "serial=IPI-SENSOR-00000100" -F "datetime=123456" -F "val=1000.1" 
	  // curl -X POST http://192.168.6.61:8083/aerialsync/sensors/uploadmeasurement -F "serial=IPI-SENSOR-00000100" -F "datetime=123456" -F "val=1000.1" 
	
	@PostMapping("/sensors/downloadjson")
	public String datasensor( @RequestParam String serial ) {

       // Create mongo client
		MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoClientDbFactory(MongoClients.create(), aerialSyncSensorsDb) );

        // Check if sensor is already in database
		String json;
		Sensor sensor; 
        Query query = new Query(Criteria.where("serial").is(serial));
		json = "{" + "\"serial: \"" + serial + "}";
        sensor = mongoTemplate.findOne(query, Sensor.class);
		if (sensor != null) {
			return sensor.toJson();
		} else {
			return  "Error: Sensor " + json +  " is not in data base.";
		}
	} // curl -X POST http://localhost:8080/aerialsync/sensors/downloadjson -F "serial=IPI-SENSOR-00000100"  
      // curl -X POST http://192.168.6.61:8083/aerialsync/sensors/downloadjson -F "serial=IPI-SENSOR-00000100" 
	@PostMapping("/users/greetuser")
	public String greetuser(@RequestParam String username,
							     @RequestParam String password,
								 @RequestParam String token) {

       // Create mongo client
		MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoClientDbFactory(MongoClients.create(), aerialSyncUsersDb) );

        // Check if sensor is already in database
		String json;
		User user; 
		User u;
        Query query = new Query(Criteria.where("username").is(username));
		user = mongoTemplate.findOne(query, User.class);
		json = "{" + "\"user: \"" + username + "\", password: \"" + password +  "\", token: \"" + token  + "}";

		if (user == null) {
			u = mongoTemplate.insert(new User(username, password, token));
			return json;
		} else {
			return  "User " + json +  " is already in the data base.";
		}
	} // curl -X POST "http://localhost:8080/aerialsync/users/greetuser" -F "username=yo"  -F "password=mypass" -F "token=1234" 
      // curl -X POST "http://192.168.6.61:8083/aerialsync/users/greetuser" -F "username=yo"  -F "password=mypass" -F "token=1234" 

	@PostMapping("/users/registersensor")
	public String registersensor(@RequestParam String username,
							     @RequestParam String password,
								 @RequestParam String token,
								 @RequestParam String serial) {

	   //Log log = LogFactory.getLog(NetworkController.class);							 

       // Create mongo client
		MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoClientDbFactory(MongoClients.create(), aerialSyncUsersDb));
		MongoTemplate mongoTemplateSensors = new MongoTemplate(new SimpleMongoClientDbFactory(MongoClients.create(), aerialSyncSensorsDb));

        // Check if sensor is already in database
		String json;
		User user; 
        Query query = new Query(Criteria.where("username").is(username));
        // Check  whether user is registered in database
        user = mongoTemplate.findOne(query, User.class);
	
		if ((user != null) && (user.getUserName().equals(username)) && (user.getPassword().equals(password)) && (user.getToken().equals(token))) {  

			//log.info("Entro");

			Sensor sensor;
			query = new Query(Criteria.where("serial").is(serial));
			// Check whether sensor is registered in database
            sensor = mongoTemplateSensors.findOne(query, Sensor.class);
			if ( sensor != null) {
				if ( user.addSensor(serial)) {
				    mongoTemplate.updateFirst(query(where("id").is(user.getId())), update("sensors", user.getSensors()), User.class);
				    json = "{" + "\"user: \"" + user + "\", password: \"" + password +  "\", token: \"" + token  + "} added " + "sensor "  + serial + ".";
					return json;
				} else {
                    return "The " + sensor + " is already assigned to the user.";
				}
			
			} else {
				return  "Sensor: " + serial +  " is not registered in the data base.";
			}
		} else {
			//log.info("Entro por otro");
			json = "{" + "\"user: \"" + username + "\", password: \"" + password +  "\", token: \"" + token  + "}";
			return  "User " + json +  " is not registered in the data base or user´s credentials do not mach.";
		}
	} // curl -X POST "http://localhost:8080/aerialsync/users/registersensor" -F "username=yo"  -F "password=mypass" -F "token=1234" -F "serial=IPI-SENSOR-00000101" 
      // curl -X POST "http://192.168.6.61:8083/aerialsync/users/registersensor" -F "username=yo"  -F "password=mypass" -F "token=1234" -F "serial=IPI-SENSOR-00000100" 

	@PostMapping("/users/removesensor")
	public String removesensor(@RequestParam String username,
							     @RequestParam String password,
								 @RequestParam String token,
								 @RequestParam String serial) {

       // Create mongo client
		MongoTemplate mongoTemplate = new MongoTemplate(new SimpleMongoClientDbFactory(MongoClients.create(), aerialSyncUsersDb));
		MongoTemplate mongoTemplateSensors = new MongoTemplate(new SimpleMongoClientDbFactory(MongoClients.create(), aerialSyncSensorsDb));


        // Check if sensor is already in database
		String json;
		User user; 
        Query query = new Query(Criteria.where("username").is(username));
        // Check  whether user is registered in database
        user = mongoTemplate.findOne(query, User.class);
		if ((user != null) && (user.getUserName().equals(username)) && (user.getPassword().equals(password)) && (user.getToken().equals(token))) {   
			Sensor sensor;
			query = new Query(Criteria.where("serial").is(serial));
			// Check whether sensor is registered in database
            sensor = mongoTemplateSensors.findOne(query, Sensor.class);
			if ( sensor != null) {
				if ( user.removeSensor(serial) ) {
				    mongoTemplate.updateFirst(query(where("id").is(user.getId())), update("sensors", user.getSensors()), User.class);
				 	json = "{" + "\" user: \"" + user + "\", password: \"" + password +  "\", token: \"" + token  + "}  deleted " + "sensor: "  + serial + ".";
					return json; 
				} else {
					return "Sensor:" + sensor + " was not assigned to user.";
				}
			} else {
				return  "Sensor: " + serial +  " is not registered in the data base.";
			}
		} else {
			json = "{" + "\"user: \"" + username + "\", password: \"" + password +  "\", token: \"" + token  + "}.";
			return  "User " + json +  " is not registered in the data base or user´s credentials do not mach.";
		}
	} // curl -X POST "http://localhost:8080/aerialsync/users/removesensor" -F "username=yo"  -F "password=mypass" -F "token=1234" -F "serial=IPI-SENSOR-00000101" 


}

