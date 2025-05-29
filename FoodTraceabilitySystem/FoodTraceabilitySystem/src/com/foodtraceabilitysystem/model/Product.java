package com.foodtraceabilitysystem.model; // Updated package

import java.io.Serializable; // Added import
import java.util.Set;
import javax.persistence.*;

@Entity
public class Product implements Serializable { // Implemented Serializable
    
    private static final long serialVersionUID = 1L; // Added serialVersionUID

    @Id
    private int productId;
    private String name;
    private String origin;
    private String qrCode;
    private String registrationDate; // Assuming this is set on the server or by a default constructor
    
    // In the client-side model, we are more interested in the User object itself 
    // rather than just the UserId. The server will handle the foreign key relationship.
    // The 'UserId' field (int UserId) from the original server model is redundant if we have a User object.
    // We'll keep the User object for sending to the server.
    @ManyToOne 
    @JoinColumn(name = "userId") // This annotation might be mostly for server-side context
    private User users; // Changed from Set<User> to User, assuming a product is registered by one user.
                       // If a product can be associated with multiple users (e.g. shared ownership), Set<User> would be appropriate.
                       // Based on typical scenarios, a product is usually linked to a single owner/registrar User.
                       // Also, the original server model had 'private Set<User> users;' but also 'private int UserId;'.
                       // The @JoinColumn was on 'users' but its name was "userID". This implies a single user relationship.

    @OneToMany(mappedBy = "products")
    private Set<ProductStatusLog> productStatus;

    // Default constructor
    public Product() {
    }
    
    // Constructor with fields (adjust as needed, especially for User)
    public Product(int productId, String name, String origin, String qrCode, String registrationDate, User user) {
        this.productId = productId;
        this.name = name;
        this.origin = origin;
        this.qrCode = qrCode;
        this.registrationDate = registrationDate;
        this.users = user;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public User getUser() { // Changed getter name for clarity
        return users;
    }

    public void setUser(User user) { // Changed setter name for clarity
        this.users = user;
    }

    public Set<ProductStatusLog> getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Set<ProductStatusLog> productStatus) {
        this.productStatus = productStatus;
    }
}
