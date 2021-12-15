package aerial.sync.network;

public class SensorData {

    public String datetime; // UTC 

	public String val;

    public SensorData() {
        this.datetime = "";
        this.val = "";
    }
    public SensorData(String datetime, String val) {
        this.datetime = datetime;
        this.val = val;
    }

    public String getDatetime() {
		return datetime;
	}

	public String getVal() {
		return val;
	}

    public String setDatetime(String d) {
		return datetime = d;
	}

	public String setVal(String v) {
		return val = v;
	}
}