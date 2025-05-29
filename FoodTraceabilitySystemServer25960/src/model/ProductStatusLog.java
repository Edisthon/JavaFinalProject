

package model;

import java.util.Set;
import javax.persistence.*;


@Entity
public class ProductStatusLog {        
    @Id
    private int logId;
    private int productId;
    private String location;
    private double temperature;
    private double humidity;
    private String timestamp;
    
    @ManyToOne
    @JoinColumn(name = "productID")
    private Set<Product> products;
    
    
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    
}
