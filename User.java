/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sng403script;

import java.util.ArrayList;

/**
 *
 * @author Gorilla
 */
public class User {
    private int id;
    private ArrayList<Integer> booleanList;
    private String gender;
    private int firstCommitQuater=0;
    private int lastCommitQuater=0;
    
    public User(int id, String gender){
        this.id = id;
        this.gender = gender;
        booleanList=new ArrayList<>();
    }
    
    public void addEntry(int entry){
      //  System.out.println("Adding entry: " + entry);
        booleanList.add(entry);
    }
    public int getId(){
        return id;
    }
    public ArrayList<Integer> getBooleanList(){
        return booleanList;
    }
    
    public void printUser(){
        booleanList.trimToSize();
        System.out.print("User: "+id+" gender: "+gender+" has commited: " +hasCommited());
        for(int i = 0; i < booleanList.size(); i++){
            System.out.print (" Q" + i + " : " + booleanList.get(i) + ",");
        }
        System.out.println("");
    }
    public boolean hasCommited(){
        for(Integer quater: booleanList){
            if(quater==1){
                return true;
            }
        }
        return false;
    }
    public int getFirstCommitQ(){
        return firstCommitQuater;
    }
    public int getLastCommitQ(){
        return lastCommitQuater;
    }
    public void setFirstCommitQ(int firstCommitQuater){
        
        this.firstCommitQuater=firstCommitQuater;
    }
    public void setLastCommitQ(int lastCommitQuater){
        this.lastCommitQuater=lastCommitQuater;
    }
    public String getGender(){
        return gender;
    }
    
    
    
}
