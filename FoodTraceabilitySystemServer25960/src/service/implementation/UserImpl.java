/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.implementation;

import dao.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.User;
import service.UserInterface;


public class UserImpl extends UnicastRemoteObject implements UserInterface{

    
    public  UserImpl() throws RemoteException{
        super();
    }
    UserDao dao= new UserDao();  

    @Override
    public String registerUser(User users) throws RemoteException {
        return dao.registerUser(users);
    }

    @Override
    public String updateUser(User users) throws RemoteException {
        return dao.updateUser(users);
    }

    @Override
    public String deleteUser(User users) throws RemoteException {
        return dao.deleteUser(users);
    }

    @Override
    public List<User> retreiveAll() throws RemoteException {
        return dao.retreiveAll();
    }

    @Override
    public User retrieveById(User user) throws RemoteException {
        return dao.retrieveById(user);
    }
    
    
    
    
    
    
}
