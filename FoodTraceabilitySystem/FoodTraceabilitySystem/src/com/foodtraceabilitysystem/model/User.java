package com.foodtraceabilitysystem.model; // Updated package

import java.io.Serializable; // Added import
import java.util.Set;
import javax.persistence.*;

@Entity
public class User implements Serializable { // Implemented Serializable
    
    private static final long serialVersionUID = 1L; // Added serialVersionUID

    @Id
    private int userId;    
    private String username;
    private String password;
    private String role;
    private String createdAt; // Assuming this is set on the server or by a default constructor
    private String email;            
    
    // Assuming cascade types and fetch strategies are handled server-side
    // For client-side model, the direct relationship mapping is less critical than the fields themselves
    @OneToMany(mappedBy = "users")    
    private Set<Product> products;
    
    // Default constructor (important for Serializable and some frameworks)
    public User() {
    }

    // Constructor with all fields might be useful
    public User(int userId, String username, String password, String role, String email, String createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.createdAt = createdAt;
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
