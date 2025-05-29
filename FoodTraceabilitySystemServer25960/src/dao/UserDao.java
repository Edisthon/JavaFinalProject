
package dao;

import java.util.List;
import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;


public class UserDao {
    
     public String registerUser(User user){
        try{
            //1. Create a Session
            Session ss= HibernateUtil.getSessionFactory().openSession();
            //2.Create a transaction
            Transaction tr= ss.beginTransaction();
            ss.save(user);
            tr.commit();
            ss.close();
            return "Data saved succesfully";
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
     public String updateUser(User user){
        try{
            //1. Create a Session
            Session ss= HibernateUtil.getSessionFactory().openSession();
            //2.Create a transaction
            Transaction tr= ss.beginTransaction();
            ss.save(user);
            tr.commit();
            ss.close();
            return "Data updated succesfully";
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
     public String deleteUser(User user){
        try{
            //1. Create a Session
            Session ss= HibernateUtil.getSessionFactory().openSession();
            //2.Create a transaction
            Transaction tr= ss.beginTransaction();
            ss.save(user);
            tr.commit();
            ss.close();
            return "Data deleted succesfully";
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
     
      public List<User> retreiveAll(){
        Session ss= HibernateUtil.getSessionFactory().openSession();
        List<User> userList=ss.createQuery("select us from"
                + "User us").list();
        ss.close();
        return userList;
    }
    public User retrieveById(User user){
        Session ss= HibernateUtil.getSessionFactory().openSession();
        User users=(User)ss.get(User.class,user.getUserId());
        ss.close();
        return users;
    }
}
