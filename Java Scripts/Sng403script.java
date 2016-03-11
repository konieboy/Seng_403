/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sng403script;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;

public class Sng403script {
private static ArrayList<Integer> projects;
private static ArrayList<User> users;
private static ArrayList<User> confirmedUsers;
private static Date projectStart;
private static int projectNum;

private static float genderRatio;
private static float turnOverAvg;
private static float productivity;
private static float tenure;
private static int totalQuaters;


private static int numPeople;
static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
static final String DB_URL = "jdbc:mysql://localhost:8889/test2";
private static int commits;
 public static void main(String[] args) {
     
     projects = new ArrayList<>();
     projects.add(1);
     projects.add(7);
     projects.add(8);
     projects.add(9);
     projects.add(12);
     projects.add(3583);
     projects.add(9215);
     projects.add(9636);
     projects.add(13566);
     projects.add(19580);
     projects.add(22980);
     projects.add(22981);
     projects.add(23781);
     projects.add(24292);
     projects.add(25875);
     projects.add(47382);
     projects.add(51669);
     projects.add(51671);
     projects.add(62501);
     projects.add(62502);
     projects.add(63250);
     projects.add(75984);
     projects.add(78852);
     projects.add(91020);
     projects.add(91331);
     projects.add(95385);
     projects.add(104307);
     projects.add(107085);
     projects.add(107186);
     projects.add(107534);
     
     confirmedUsers = new ArrayList<>();
     users= new ArrayList<>();
     initialize();
     int totalProjNum = 0;
     for(Integer project: projects){
        projectNum=project;
        confirmedUsers.clear();
        getProjectUsers();
        checkCommits();
        removeNonCommitters();
        //writeToCsv();
        calculateTurnOver();
        printStats();
//        firstLast();
        totalProjNum++;
     }
     //System.out.println("totalProjNum: " + totalProjNum);

  }

  public static void initialize() {
        
        
	String csvFile = "Tenure Data.csv";
	BufferedReader br = null;
	String line = "";
	String cvsSplitBy = ",";

	try {

		br = new BufferedReader(new FileReader(csvFile));
                br.readLine();
		while ((line = br.readLine()) != null) {
		        // use comma as separator
			String[] entries = line.split(cvsSplitBy);
                        User temp = new User(Integer.parseInt(entries[0]),entries[8]);
                        users.add(temp);
		}
                
               // //System.out.println("Checking " + userIDs.size() + " users.");

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

  }
  
  public static void removeNonCommitters(){
      confirmedUsers.trimToSize();
      for(int i = 0; i<confirmedUsers.size(); i++){
          if(confirmedUsers.get(i).hasCommited()==false){
              //System.out.println("Removing user: " + confirmedUsers.get(i).getId() + " hasCommited:" + confirmedUsers.get(i).hasCommited());
              confirmedUsers.remove(i);
          }
      }
  }
  
public static void getProjectUsers() {
   Connection conn = null;
   Statement stmt = null;
   try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      //System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL,"root","asaasa");

      //STEP 4: Execute a query
      //System.out.println("Creating statement...");
      stmt = conn.createStatement();
      String sql;
      
      sql="Select * from projects where id="+projectNum;
      ResultSet rs1 = stmt.executeQuery(sql);
      if(rs1.next()){
        projectStart = rs1.getDate("created_at");
      }
       //System.out.println("Project started: " + projectStart);
       rs1.close();

      for(User element: users){
        sql = "SELECT * FROM project_members where user_id="+element.getId()+" AND repo_id="+projectNum;
        ResultSet rs = stmt.executeQuery(sql);
        ////System.out.println("here");
        if(rs.next()){
            confirmedUsers.add(element);
        } 
        numPeople = confirmedUsers.size();
        confirmedUsers.trimToSize();
        rs.close();
      }
      
      //System.out.println("Total number of people in project: " + confirmedUsers.size());
      
      //System.out.println("Total number of people in project: " + confirmedUsers.size());
      
      //STEP 6: Clean-up environment
      
      stmt.close();
      conn.close();
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
 //  //System.out.println("Goodbye!");
}//end main

public static User getUser(int id){
    for(User tmpUser: users){
        if(id==tmpUser.getId()){
            return tmpUser;
        }
    }
    
    return null;
    
    
}

public static void checkCommits() {
   Connection conn = null;
   Statement stmt = null;
   
   ArrayList<Integer> q1users = new ArrayList<>();
   try{
       FileWriter writer = new FileWriter("result.csv");
       Date higherLimitDate;
       Date lowerLimitDate = projectStart;
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
    //  //System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL,"root","asaasa");

      //STEP 4: Execute a query
     // //System.out.println("Creating statement...");
      stmt = conn.createStatement();
      String sql;
      Calendar cal = Calendar.getInstance();
      Date today = new Date();
       //System.out.println("Today: " + today);
      cal.setTime(lowerLimitDate);
      cal.add(Calendar.DATE, 90);
      higherLimitDate = (Date)cal.getTime();
      boolean done=false;
      
      boolean projHasStarted = false;
      int startQuater = 0;
      int quater = 1;
      boolean firstCommit = true;
      while(!done){
        
        ////System.out.println("Checking date range: " + lowerLimitDate + " to " + higherLimitDate);
        int usersAdded = 0;

        for(int i = 0; i<confirmedUsers.size(); i++){
            
        //    //System.out.println("userid: " +userID);
            sql = "Select * from commits where author_id=" + confirmedUsers.get(i).getId() + " AND project_id =" + projectNum;
            ResultSet rs = stmt.executeQuery(sql);
            boolean found = false;
            while(rs.next() && found==false){//for each result of the users commit for this proj check and see if any are in the range specified.
               // //System.out.print ("author_id = " + confirmedUsers.get(i).getId() + " or ");

                Date commitDate =  rs.getDate("created_at");
                
                if(commitDate.compareTo(higherLimitDate)<=0 && commitDate.compareTo(lowerLimitDate)>=1){
                  //  //System.out.println("Found commit for user: " + confirmedUserIDs.get(i));

                    confirmedUsers.get(i).addEntry(1);
                    q1users.add(confirmedUsers.get(i).getId());
                    usersAdded++;
                    found=true;
                    projHasStarted=true;
                    
                }
            }
           // if(projHasStarted=false){startQuater = quater;}
            
            if(!found){
                confirmedUsers.get(i).addEntry(0);
            }

        }
        
      
      for(Integer uid: q1users){
          
      }
      
      
      
        //if(usersAdded==0 && projHasStarted) done=true;
      if(higherLimitDate.compareTo(today)>=0){
          System.out.println("Last date checked: " + higherLimitDate);
          done=true;
      }
        lowerLimitDate = higherLimitDate;
        cal.setTime(higherLimitDate);
        cal.add(Calendar.DATE, 90);
        higherLimitDate = cal.getTime();
        quater++;
        q1users.clear();
     //   //System.out.println("");
      }
       //System.out.println("Project " + projectNum + " ended in quater: " + quater);
      

      


      
      stmt.close();
      conn.close();
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
  // //System.out.println("Goodbye!");
}//end main

   private static void writeToCsv()
   {
	try
	{
	    FileWriter writer = new FileWriter(projectNum+".csv");
		writer.append("UserID");
            for(int i = 0; i < users.get(0).getBooleanList().size();i++){
                writer.append(",");
                writer.append("Q"+i);
            }
            writer.append("\n");
            for(User user: confirmedUsers){
                ArrayList<Integer> booleanList = user.getBooleanList();
                booleanList.trimToSize();
                writer.append(String.valueOf(user.getId()));
                for(int i = 0; i < booleanList.size(); i++){
                    writer.append(",");
                    writer.append(String.valueOf(booleanList.get(i)));
                }
                writer.append("\n");
            }
			
	    //generate whatever data you want
			
	    writer.flush();
	    writer.close();
            //System.out.println(projectNum+".csv created!");
	}
	catch(IOException e)
	{
	     e.printStackTrace();
	} 
    }
   public static void calculateTurnOver(){
       
       int numQuaters = confirmedUsers.get(0).getBooleanList().size();
       ArrayList<User> CommittersLowQuater = new ArrayList<>();
       ArrayList<User> CommittersHigherQuater = new ArrayList<>();
       float[] turnOverList = new float[numQuaters];
       int peopleWhoCommited = 0;
       int turnOver = 0;
       int numberUsers = 0;
       float turnOverPercent = 0;
       boolean firstRun = true;
       int numMale = 0;
       int numNone = 0;
       int numFemale = 0;
       for(User user: confirmedUsers){
                if(user.getGender().equals("male")){
                    numMale++;
                } else if(user.getGender().equals("female")){
                    numFemale++;
                } else {
                    numNone++;
                }
       }
       
       //asdf123
       getTotalCommits(numFemale+numMale+numNone);
       firstLast(numFemale+numMale+numNone);
       
       for(int i = 0; i<numQuaters;i++){
           peopleWhoCommited= 0;

            for(User user: confirmedUsers){
                
                //numberUsers++;
                ArrayList<Integer> booleanList = user.getBooleanList();
                booleanList.trimToSize();
                if(i+1<user.getBooleanList().size()){
                    if(booleanList.get(i)==1){
                        numberUsers++;
                    }
                    if(booleanList.get(i)>booleanList.get(i+1))
                    {
                        turnOver++;
                    }           
                }
                
            }

            if(numberUsers!=0)turnOverPercent =  ((turnOver*100)/numberUsers);
            turnOverList[i] = turnOverPercent;
            
           // //System.out.println("For quater: " + i + " turn over is: " + turnOver + " turnover percent is: " + turnOverPercent + " total num people " +  numberUsers);
            turnOver=0;
            numberUsers=0;
            turnOverPercent=0;

            }
        int i;
        double sum = 0;
        for(int k = 0; k<turnOverList.length; k++){
            sum += turnOverList[k];
        }
        
        float genderRatioMale = (float)numMale/(float)(numFemale+numMale);
        float genderRatioFemale = (float)numFemale/(float)(numFemale+numMale);
      //  //System.out.println("genderRatioMale: " + genderRatioMale + " genderRatioFemale " + genderRatioFemale);
         genderRatio = 1-(float)((genderRatioMale*genderRatioMale)+(genderRatioFemale*genderRatioFemale));

        //System.out.println("Num male:" + numMale + " Num female:" + numFemale + " None: " + numNone + " GenderRatio: " + genderRatio);
    //    //System.out.println(turnOverList.length);
        //System.out.println("TurnOver AVG: " + sum/(turnOverList.length-1));
       turnOverAvg = (float)sum/(float)(turnOverList.length-1);
   }
   
   
   public static void firstLast(int numOfPeople){
       int sumOfTenure = 0;
       for(User user: confirmedUsers){
           ArrayList<Integer> booleanList = user.getBooleanList();
           booleanList.trimToSize();
           boolean firstCommit = true;
           for(int i = 0; i<booleanList.size();i++){
               if(booleanList.get(i)==1){
                   if(firstCommit){
                       user.setFirstCommitQ(i);
                       firstCommit = false;
                   } 
                 user.setLastCommitQ(i);
                 
               }
           }
           sumOfTenure = sumOfTenure + (user.getLastCommitQ()-user.getFirstCommitQ());
      //    System.out.println("User: " + user.getId() + " first commited in q: " + user.getFirstCommitQ() + " Last commit in q: " +  user.getLastCommitQ() + " last - first " + (user.getLastCommitQ()-user.getFirstCommitQ()));
       }
     //  System.out.println("Sum of tenure: " + sumOfTenure);

      tenure = ((float)sumOfTenure/(float)numOfPeople);
     //  System.out.println("Sum of tenure / Number of people :" + ((float)sumOfTenure/(float)numOfPeople));
       
   }

   public static void getTotalCommits(int numPeople) {
     //  System.out.println("AJSKFHAJKHFDJSKF");
   Connection conn = null;
   Statement stmt = null;
   int totalCommits = 0;
   try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      //System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL,"root","asaasa");

      //STEP 4: Execute a query
      //System.out.println("Creating statement...");
      stmt = conn.createStatement();
      String sql;
      
      sql="Select * from projects where id="+projectNum;
      ResultSet rs1 = stmt.executeQuery(sql);
      if(rs1.next()){
        projectStart = rs1.getDate("created_at");
      }
       System.out.println("Project started: " + projectStart);
       rs1.close();

      for(User element: confirmedUsers){
     //   //System.out.print(" author_id= " + element.getId() + " or ");
        sql = "SELECT * FROM commits where author_id="+element.getId()+" AND project_id="+projectNum;
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            totalCommits++;
        }
        
      }
      commits = totalCommits;
    //  System.out.println("Total number of COMMITS: " + totalCommits + ", total users:"+numPeople+ " Productivity: " +(float)totalCommits/(float)numPeople);
      productivity = (float)totalCommits/(float)numPeople;
      
      //STEP 6: Clean-up environment
      
      stmt.close();
      conn.close();
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
 //  //System.out.println("Goodbye!");
}//end main

public static void printStats(){
    System.out.println("Project #: " + projectNum + ", Total number of quaters: " + confirmedUsers.get(0).getBooleanList().size());
    System.out.println("Total Number Of Users: " + numPeople);
    System.out.println("Total Number Of Users(excluding people who did not commit): " + confirmedUsers.size());
    System.out.println("Tenure: (Quaters/person) " + tenure);
    System.out.println("Productivity: (Commits/Person) " + productivity + " Total commits: " + commits);
    System.out.println("Turn Over:(%) " + turnOverAvg);
    System.out.println("Gender Ratio: " + genderRatio);
    System.out.println("");    
    

}
}
