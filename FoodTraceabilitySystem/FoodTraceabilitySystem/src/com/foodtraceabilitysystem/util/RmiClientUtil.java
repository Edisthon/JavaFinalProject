package com.foodtraceabilitysystem.util;

import com.foodtraceabilitysystem.service.UserInterface;
import com.foodtraceabilitysystem.service.ProductInterface;
import com.foodtraceabilitysystem.service.ProductStatusLogInterface;
import java.rmi.Naming;

public class RmiClientUtil {
    // Server is running on localhost (127.0.0.1) and port 81
    private static final String SERVER_URL_PREFIX = "rmi://localhost:81/"; 

    public static UserInterface getUserService() throws Exception {
        // Service is bound as "user"
        return (UserInterface) Naming.lookup(SERVER_URL_PREFIX + "user"); 
    }

    public static ProductInterface getProductService() throws Exception {
        // Service is bound as "product"
        return (ProductInterface) Naming.lookup(SERVER_URL_PREFIX + "product"); 
    }

    public static ProductStatusLogInterface getProductStatusLogService() throws Exception {
        // Service is bound as "productstatus"
        return (ProductStatusLogInterface) Naming.lookup(SERVER_URL_PREFIX + "productstatus"); 
    }
}
