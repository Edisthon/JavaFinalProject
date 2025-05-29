
package model;

import java.util.Set;
import javax.persistence.*;

@Entity

public class Product {
    @Id
    private int productId;
    private String name;
    private String origin;
    private String qrCode;
    private String registrationDate;
    private int UserId;
    
    @ManyToOne
    @JoinColumn(name = "userID")
    private Set<User> users;
    
    
    @OneToMany(mappedBy = "products")
    private Set<ProductStatusLog> productStatus;
    
    public int getUserId() {
        return UserId;
    }

    public void setUserId(int UserId) {
        this.UserId = UserId;
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
    
    
}
