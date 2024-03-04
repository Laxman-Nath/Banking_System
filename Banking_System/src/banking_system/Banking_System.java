// Banking System in java

package banking_system;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

class Users{
    Connection conn;
    Scanner sc;
    public Users(Connection conn,Scanner sc){
        this.conn=conn;
        this.sc=sc;
    }
    public void register(){
        sc.nextLine();
        System.out.println("Enter full_name");
        String full_name=sc.nextLine();
        System.out.println("Enter email");
        String email=sc.nextLine();
        System.out.println("Enter password:");
        String password=sc.nextLine();
        if(user_exist(email)){
            System.out.println("This email already exists.Try with another email");
            return;
        }
       String insert_Query="Insert into user(full_name,email, password) VALUES (?,?,?)";
       try{
           PreparedStatement ps=conn.prepareStatement(insert_Query);
           ps.setString(1,full_name);
           ps.setString(2, email);
           ps.setString(3, password);
           int affectedRows=ps.executeUpdate();
           if(affectedRows>0){
               System.out.println("Registration successfull");
              
           }
           else{
               System.out.println("Error registering");
           }
       }
       catch(SQLException e){
           e.printStackTrace();
       }
    }
    
    public boolean user_exist(String email){
        String query="Select * from user where email=?";
        try{
        PreparedStatement ps=conn.prepareStatement(query);
        ps.setString(1, email);
        ResultSet rs=ps.executeQuery();
        if(rs.next()){
            return true;
        }
        else{
            return false;
        }
        }
        catch(SQLException e){
            e.printStackTrace();
            
        }
        return false;
        }
    public String Log_in(){
        sc.nextLine();
        System.out.println("Enter email:");
        String email=sc.nextLine();
       
        System.out.println("Enter password:");
        String password=sc.nextLine();
        String query="Select * from user where email=? and password=?";
        try{
        PreparedStatement ps=conn.prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, password);
         ResultSet rs=ps.executeQuery();
         if(rs.next()){
             return rs.getString("email");
         }
         else{
             return null;
         }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}

class Account{
    Connection conn;
    Scanner sc;
    public Account(Connection conn,Scanner sc){
        this.conn=conn;
        this.sc=sc;
    }
    public long create_Account(String email){
      if(!Account_exists(email)){
          sc.nextLine();
        System.out.println("Enter full name:");
        String fullname=sc.nextLine();
        System.out.println("Enter initial balance:");
        double balance=sc.nextDouble();
        System.out.println("Enter security pin:");
        String security_pin=sc.next();
        String query="Insert into accounts(account_no,full_name,email, balance,security_pin)"
                + "VALUES(?,?,?,?,?);";
        try{
            PreparedStatement ps=conn.prepareStatement(query);
            long account_number=generateAccountNo(email);
            ps.setLong(1, account_number);
            ps.setString(2, fullname);
            ps.setString(3, email);
            ps.setDouble(4, balance);
            ps.setString(5, security_pin);
            int affectedRows=ps.executeUpdate();
            if(affectedRows>0){
                return account_number;
            }
            else{
                throw new RuntimeException("Error creating account");
            }
        }
    
        catch(SQLException e){
            e.printStackTrace();
        }
    }
      throw new RuntimeException("Account already exists!!");
    }
    
    public boolean Account_exists(String email){
        String sql="Select * from accounts where email=?";
        try{
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                return true;
            }
            else{
                return false;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public long generateAccountNo(String email){
        String query="Select account_no from accounts ORDER BY account_no DESC LIMIT 1";
       try{
           PreparedStatement ps=conn.prepareStatement(query);
           ResultSet rs=ps.executeQuery();
           if(rs.next()){
               return rs.getLong("account_no")+1;
           }
           else{
               return 10000100;
           }
       }
       catch(SQLException e){
           e.printStackTrace();
       }
        return 10000100;
        
        
    }
    
    public long getAccountNumber(String email){
        String sql="Select account_no from accounts where email=?";
        try{
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
               return rs.getLong("account_no");
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException ("Account number does not exist");
 
    }
}

class AccountManager{
    Connection conn;
    Scanner sc;
    public AccountManager(Connection conn,Scanner sc){
        this.conn=conn;
        this.sc=sc;
    }
    public void deposit(long accno) {
        System.out.println("Enter amount to be deposited:");
       double amount=sc.nextDouble();
       System.out.println("Enter the security pin:");
       String pin=sc.next();
       if(accno!=0){
       String sql1="Select * from accounts where account_no=? and security_pin=?";
       try{
           conn.setAutoCommit(false);
           
           PreparedStatement ps=conn.prepareStatement(sql1);
           ps.setLong(1, accno);
           ps.setString(2, pin);
           ResultSet rs=ps.executeQuery();
           if(rs.next()){
               String sql2="Update accounts SET balance=balance+? where account_no"
                       + "=?";
               PreparedStatement ps2=conn.prepareStatement(sql2);
               ps2.setDouble(1, amount);
               ps2.setLong(2, accno);
               int affectedRows=ps2.executeUpdate();
               if(affectedRows>0){
                   System.out.println(amount + " is deposited successfully");
                   conn.commit();
                   conn.setAutoCommit(true);
               }
               else{
                   System.out.println("Error depositing amount:");
                   conn.rollback();
                   conn.setAutoCommit(true);
               }
           }
           else{
               System.out.println("Invalid pin");
           }
       }
       catch(SQLException e){
           e.printStackTrace();
       }
       }
       else{
           System.out.println("Invalid account number");
       }
    }
    
    public void withdraw(long accno){
        System.out.println("Enter amount to be withdrawl:");
        double amount=sc.nextDouble();
        System.out.println("Enter security pin:");
        String pin=sc.next();
        if(accno!=0){
       String sql1="Select * from accounts where account_no=? and security_pin=?";
       try{
           conn.setAutoCommit(false);
           
           PreparedStatement ps=conn.prepareStatement(sql1);
           ps.setLong(1, accno);
           ps.setString(2, pin);
           ResultSet rs=ps.executeQuery();
           if(rs.next()){
               String sql3="select balance from accounts where account_no= ?";
                 PreparedStatement ps3=conn.prepareStatement(sql3);
                 ps3.setLong(1, accno);
                 ResultSet rs1=ps3.executeQuery();
                 double balance=0;
                 if(rs1.next()){
                   balance=rs1.getDouble("balance");
//                 System.out.println(balance);
                 }

                 
           if(amount<=balance){
               String sql2="Update accounts SET balance=balance-? where account_no"
                       + "=?";
               PreparedStatement ps2=conn.prepareStatement(sql2);
               ps2.setDouble(1, amount);
               ps2.setLong(2, accno);
               int affectedRows=ps2.executeUpdate();
               if(affectedRows>0){
                   System.out.println(amount + " is deducted successfully");
                   conn.commit();
                   conn.setAutoCommit(true);
               }
               else{
                   System.out.println("Error depositing amount:");
                   conn.rollback();
                   conn.setAutoCommit(true);
               }
           }
               else{
                   System.out.println("Insufficient balance:");
                   conn.rollback();
                   conn.setAutoCommit(true);
               }
               }
               
           else{
               System.out.println("Invalid pin");
           }
       }
       catch(SQLException e){
           e.printStackTrace();
       }
       }
       else{
           System.out.println("Invalid account number");
       }

    }
    public void getBalance(long accno){
        System.out.println("Enter security pin:");
        String pin=sc.next();
        if(accno!=0){
        String sql="select * from accounts where account_no=? and security_pin=?";
        try{
            PreparedStatement ps=conn.prepareStatement(sql);
            ps.setLong(1, accno);
            ps.setString(2, pin);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                System.out.println("Your balance is:"+rs.getDouble("balance"));
            }
            else{
                System.out.println("Invalid pin:");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        }
        else{
            System.out.println("Invalid account number:");
        }
        
    }
    public void transferMoney(long senderaccno){
        System.out.println("Enter Receiver accno");
        long receiveraccno=sc.nextLong();
        System.out.println("Enter security pin of senders account");
        String pin=sc.next();
        System.out.println("Enter amount to be transferred from senders account:");
        double amount=sc.nextDouble();
        String sql="select * from accounts where account_no=?";
        
        try{
           conn.setAutoCommit(false);
             PreparedStatement p=conn.prepareStatement(sql);
             p.setLong(1, senderaccno);
            ResultSet r=p.executeQuery();
            if(r .next()){
            String sql1="select * from accounts where account_no=? and security_pin=?";
            String sql3="select * from accounts where account_no=?";

                PreparedStatement ps=conn.prepareStatement(sql1);
                 ps.setLong(1, senderaccno);
                ps.setString(2,pin);
                ResultSet rs=ps.executeQuery();
               
                if(rs.next()){
                    double balance=rs.getDouble("balance");
                    if(amount<=balance){
                        String sql2="UPDATE accounts SET balance=balance-? where account_no=?";
                        PreparedStatement ps2=conn.prepareStatement(sql2);
                        ps2.setDouble(1, amount);
                        ps2.setLong(2, senderaccno);
                        int affectedRows1=ps2.executeUpdate();

                        String sql4="UPDATE accounts SET balance=balance+? where account_no=?";
                        PreparedStatement ps3=conn.prepareStatement(sql4);
                        ps3.setDouble(1, amount);
                        ps3.setLong(2, receiveraccno);
                        int affectedRows2=ps3.executeUpdate();
                        if(affectedRows1>0 && affectedRows2>0){
                            System.out.println("Successfully tranferred money:");
                        conn.commit();
                         conn.setAutoCommit(true);
                        }
                        else{
                             PreparedStatement psr=conn.prepareStatement(sql3);
                             psr.setLong(1, receiveraccno);
                             ResultSet rsr=ps.executeQuery();
                             if(!rsr.next()){
                            System.out.println("Error tranferring money:");
                             }
                             else{
                                 System.out.println("Invalid receiver account number:");
                             }
                            conn.rollback();
                            conn.setAutoCommit(true);
                        }
                    }
                    else{
                        System.out.println("Insufficient balance to be tranferred:");
                    }
                }
                else{
                    System.out.println("Invalid sender's security pin");
                }
            }
             else{
            System.out.println("Invalid sender's account number");
        }
        }
            catch(SQLException e){
                e.printStackTrace();
            }
            
        }

    }

public class Banking_System {

private static final String url ="jdbc:mysql://localhost:3306/banking_db";
 private static final String username="root";
 private static final String password="siddha@123";
 
    public static void main(String[] args) throws SQLException,ClassNotFoundException
    ,RuntimeException{
       Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn=DriverManager.getConnection(url, username, password);
        Scanner sc=new Scanner(System.in);
        Users u=new Users(conn, sc);
        Account acc=new Account(conn, sc);
        AccountManager am=new AccountManager(conn, sc);
        String email;
        long accno;
        int choice;
        do{
            System.out.println("*** Welcome to banking System:***");
            System.out.println("Select a option:");
            System.out.println("1.Register");
            System.out.println("2.Log_in");
            System.out.println("3.Exit");
            System.out.println("*******************");
            choice=sc.nextInt();
            switch (choice) {
                case 1:
                    u.register();
                    break;
                case 2:
                    email=u.Log_in();
                    if(email!=null){
                        System.out.println("User logged in:");
                        if(!acc.Account_exists(email)){
                            System.out.println("/****************************");
                            System.out.println("1.Open a new bank account");
                            System.out.println("2.Exit");
                            System.out.println("/****************************");
                            if(sc.nextInt()==1){
                              accno=  acc.create_Account(email);
                              System.out.println("Account was created successfully!");
                              System.out.println("Your account number is:"+accno);
                            }
                            else{
                                break;
                            }
                        }
                        accno=acc.getAccountNumber(email);
                        int choice2;
                        do{
                            
                        System.out.println("Enter your choice:");
                         System.out.println("/****************************");
                        System.out.println("1.Deposit money:");
                        System.out.println("2.Withdraw money:");
                        System.out.println("3.Transfer money:");
                        System.out.println("4.Check Balance:");
                        System.out.println("5.Log out:");
                         System.out.println("/****************************");
                        choice2=sc.nextInt();
                            switch (choice2) {
                                case 1:
                                    am.deposit(accno);
                                    break;
                                case 2:
                                    am.withdraw(accno);
                                    break;
                                case 3:
                                    am.transferMoney(accno);
                                    break;
                                case 4:
                                    am.getBalance(accno);
                                    break;
                                case 5:
                                    break;
                                default:
                                    System.out.println("Enter valid choice!");
                            }
                        }while(choice2!=5);
                    }
                    else{  
                        System.out.println("Invalid email:");
                    }
                case 3:
                    System.out.println("Thanks for using our banking System:");
                    System.out.println("Exiting system.....");
                    break;
                default:
                    System.out.println("Enter valid choice!");
            }
        }while(choice!=3);
       
    }
    
}
