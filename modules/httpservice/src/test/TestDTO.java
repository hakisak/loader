package test;

import java.io.Serializable;

public class TestDTO implements Serializable {
   
   private String firstName;
   private String lastName;
   
   public TestDTO(String fname, String lname) {
      firstName = fname;
      lastName = lname;
   }
   
   public String getFirstName() {
      return firstName;
   }
   public void setFirstName(String firstName) {
      this.firstName = firstName;
   }
   public String getLastName() {
      return lastName;
   }
   public void setLastName(String lastName) {
      this.lastName = lastName;
   }
   public String toString() {
      return firstName + ":" + lastName;
   }
   

}
