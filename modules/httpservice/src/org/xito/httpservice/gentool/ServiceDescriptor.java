package org.xito.httpservice.gentool;

public class ServiceDescriptor {
   
   private String interfaceName;
   private String implName;
   
   private String getPackageName(String fullName) {
      if(fullName == null) return null;
      
      return fullName.substring(0, fullName.lastIndexOf("."));
   }
   
   private String getClassName(String fullName) {
      if(fullName == null) return null;
      
      return fullName.substring(fullName.lastIndexOf(".")+1);
   }
   
   public String getInterfacePackage() {
      if(interfaceName == null) return null;
      
      return getPackageName(interfaceName);
   }
   
   public String getInterfaceClass() {
      if(interfaceName == null) return null;
      
      return getClassName(getInterfaceName());
   }
   
   public String getImplName() {
     
      if(implName == null && interfaceName != null) {
         return getInterfacePackage() + ".impl." +  getInterfaceClass() +"Impl";
      }
      else {
         return implName;
      }
   }
   
   public void setImplName(String implName) {
      this.implName = implName;
   }
   public String getInterfaceName() {
      return interfaceName;
   }
   public void setInterfaceName(String interfaceName) {
      this.interfaceName = interfaceName;
   }
   
   
   
}
