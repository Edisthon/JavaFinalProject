package com.foodtraceabilitysystem.model; // Updated package

import java.io.Serializable; // Added import
// import java.util.Set; // Not needed if 'products' is a single Product
import javax.persistence.*;

@Entity
public class ProductStatusLog implements Serializable { // Implemented Serializable
    
    private static final long serialVersionUID = 1L; // Added serialVersionUID

    @Id
    private int logId;
    // private int productId; // Redundant if we have a Product object
    private String location;
    private double temperature;
    private double humidity;
    private String timestamp; // Assuming this is set on the server or by a default constructor
    
    @ManyToOne
    @JoinColumn(name = "productId") // This annotation might be mostly for server-side context
    private Product products; // Changed from Set<Product> to Product, assuming a log entry is for one product.
                             // The original server model had 'private Set<Product> products;' but also 'private int productId;'.
                             // The @JoinColumn was on 'products' but its name was "productID". This implies a single product relationship.

    // Default constructor
    public ProductStatusLog() {
    }

    // Constructor with fields (adjust as needed)
    public ProductStatusLog(int logId, String location, double temperature, double humidity, String timestamp, Product product) {
        this.logId = logId;
        this.location = location;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
        this.products = product;
    }
    
    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    // Getter and Setter for Product object
    public Product getProduct() { // Changed getter name
        return products;
    }

    public void setProduct(Product product) { // Changed setter name
        this.products = product;
    }
    
    // public int getProductId() { // Kept for now if needed, but prefer getProduct().getProductId()
    //     return productId;
    // }

    // public void setProductId(int productId) { // Kept for now
    //     this.productId = productId;
    // }

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
