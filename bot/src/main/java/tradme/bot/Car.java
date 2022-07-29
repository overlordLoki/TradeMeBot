package tradme.bot;
import java.io.Serializable;

public class Car implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long ID;
	public int year;
	public String make;
	public String modle;
	public long odometer;
	public int price;
	public String title;
	public String link;
	public String engineDetails;
	public String postInfo;
	public String submodle;

	public Car(long id,int Year, String Make, String Modle,
			long Odom, int Price, String Title, String EngineDetails) {
		
		ID = id;
		year = Year;
		price = Price;
		make = Make;
		modle = Modle;
		odometer = Odom;
		title = Title;
		engineDetails = EngineDetails;
		
		postInfo = Title+"  Odom: "+Long.toString(Odom)
		+" "+engineDetails+"  price: $"+Integer.toString(Price);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (ID ^ (ID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Car other = (Car) obj;
		if (ID != other.ID)
			return false;
		return true;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModle() {
		return modle;
	}

	public void setModle(String modle) {
		this.modle = modle;
	}

	public long getOdometer() {
		return odometer;
	}

	public void setOdometer(long odometer) {
		this.odometer = odometer;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
