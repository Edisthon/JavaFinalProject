/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foodtraceabilitysystem.service; // Updated package

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import com.foodtraceabilitysystem.model.ProductStatusLog; // Updated import


public interface ProductStatusLogInterface extends Remote{
    
    public String registerProductStatusLog(ProductStatusLog products) throws RemoteException;
    public String updateProductStatusLog(ProductStatusLog products) throws RemoteException;
    public String deleteProductStatusLog(ProductStatusLog products) throws RemoteException;
    public List<ProductStatusLog> retreiveAll() throws RemoteException;
    public ProductStatusLog retrieveById(ProductStatusLog product) throws RemoteException; // Consider changing parameter to int logId
    
}
