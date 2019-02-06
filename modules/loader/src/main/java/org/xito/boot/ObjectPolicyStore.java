// Copyright 2007 Xito.org
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.xito.boot;

import java.util.*;
import java.util.logging.*;
import java.net.*;
import java.security.*;
import java.security.cert.*;
import java.io.*;
import java.beans.*;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

/**
 * Store Policy information in a Object File system
 *
 * @author  Deane Richan
 */
public class ObjectPolicyStore implements PolicyStore {

   private static final Logger logger = Logger.getLogger(ObjectPolicyStore.class.getName());

   private final String ALL_PERMISSION_KEY = "allPermissionkey";
   private final String DEFAULT_KEYSTORE_PASS = "xitokeypass";
   private File storeDir;
   private HashSet policyCache = new HashSet();
   private SecretKey secretKey;
   
   
   /** Creates a new instance of ObjectPolicyStore */
   protected ObjectPolicyStore() {
      storeDir = new File(System.getProperty(Boot.APP_BASEDIR), "/security");
      if(storeDir.exists()) {
         if(storeDir.isFile()) {
            //There is a file in the base dir called security already it needs to be moved
            throw new RuntimeException("Can't create security policy storage dir: "+storeDir.toString());
         }
      }
      else {
         if(storeDir.mkdir()==false) {
            //Could not Make the Store Dir
            throw new RuntimeException("The security policy storage dir: "+storeDir.toString()+" could not be created.");
         }
      }
   }
   
   /**
    * Get the key we use to encrypt text
    */
   private SecretKey getSecretKey() {
      
      if(secretKey == null) {
         try {
            SecretKeyFactory fac = SecretKeyFactory.getInstance("DES");
            secretKey = fac.generateSecret(new DESKeySpec(DEFAULT_KEYSTORE_PASS.getBytes()));
         }
         catch(Exception invalid) {
            BootSecurityManager.securityLogger.log(Level.SEVERE, invalid.getMessage(), invalid);
            return null;
         }
      }
            
      return secretKey;
   }
   
   /**
    * Return a Base64 encoded string of encrypted text for the specified text
    * @param text
    */
   private String getEncrypted(String text) {
      
      return text;
      
      /*
      if(text == null) return null;
      
      try {
         Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
         cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

         byte clearText[] = text.getBytes();
         byte encryptedText[] = cipher.doFinal(clearText);

         return new BASE64Encoder().encode(encryptedText);
      }
      catch(Exception invalid) {
         BootSecurityManager.securityLogger.log(Level.SEVERE, invalid.getMessage(), invalid);
         return text;
      }
       */
   }
   
   /**
    * Return decoded text for a Base64 encoded string of encrypted text 
    * @param text
    */
   private String getDecrypted(String text) {
      
      return text;
      /*
      if(text == null) return null;

      try {
         byte encryptedText[] = new BASE64Decoder().decodeBuffer(text);
         Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
         cipher.init(Cipher.DECRYPT_MODE, getSecretKey());

         return new String(cipher.doFinal(encryptedText));
      }
      catch(Exception invalid) {
         BootSecurityManager.securityLogger.log(Level.SEVERE, invalid.getMessage(), invalid);
         return text;
      }
      */
   }
   
   /**
    * Get a set of Permissions from the Policy Store
    */
   public PermissionCollection getPermissions(ExecutableDesc execDesc) {
      
      if(execDesc == null) return null;
      
      //If the app only wants restricted permissions don't bother getting stored perms
      if(execDesc.useRestrictedPermissions()) return null;
      
      String perm = null;
      String fileName = execDesc.getSerialNumber();
      if(fileName == null) return null;
      
      fileName = generateFilename(fileName.getBytes());
      File f = getFile(fileName);
      logger.info("Looking for app security file:"+fileName);
      if(f == null || f.exists() == false) {
         return null;
      }
      
      try {
         FileInputStream in = new FileInputStream(f);
         ObjectInputStream objIn = new ObjectInputStream(in);
         Object obj = objIn.readObject();
         String str = (String)obj;
         
         perm = this.getDecrypted(str);
         objIn.close();
         in.close();
      }
      catch(ClassCastException castExp) {
         BootSecurityManager.securityLogger.log(Level.SEVERE, "Invalid stored permission for "+execDesc.getDisplayName(), castExp);
      }
      catch(Exception exp) {
         BootSecurityManager.securityLogger.log(Level.SEVERE, exp.getMessage(), exp);
      }
      
      if(perm != null && perm.equals(ALL_PERMISSION_KEY)) {
         Permissions result = new Permissions();
         result.add( new AllPermission() );
         return result;
      }
      else {
         return null;
      }
   }
   
   /**
    * Get a set of Permissions for the Certification Path
    */
   public PermissionCollection getPermissions(X509Certificate cert) {

      if(cert == null) return null;
      
      String perm = null;
      
      String fileName = generateFilename(cert.getSerialNumber().toByteArray());
      if(fileName == null) return null;
      
      logger.info("Looking for cert security file:"+fileName);
      File f = getFile(fileName);
      
      if(f == null || f.exists() == false) {
         return null;
      }
      
      try {
         FileInputStream in = new FileInputStream(f);
         ObjectInputStream objIn = new ObjectInputStream(in);
         Object obj = objIn.readObject();
         String str = (String)obj;
         
         perm = this.getDecrypted(str);
         objIn.close();
         in.close();
      }
      catch(ClassCastException castExp) {
         BootSecurityManager.securityLogger.log(Level.SEVERE, "Invalid stored permission for "+cert.getSubjectDN().toString(), castExp);
      }
      catch(Exception exp) {
         BootSecurityManager.securityLogger.log(Level.SEVERE, exp.getMessage(), exp);
      }
      
      if(perm != null && perm.equals(ALL_PERMISSION_KEY)) {
         Permissions result = new Permissions();
         result.add( new AllPermission() );
         return result;
      }
      else {
         return null;
      }
   }
   
   /**
    * Get the Permissions for a given
   private PermissionCollection getPermissionsForFile(File f) {
      
   }
   
   /**
    * Store Permissions for an Application
    */
   public void storePermissions(ExecutableDesc execDesc, PermissionCollection perms) throws PolicyStoreException {
      
      //Can only store AllPermission for Now
      if(perms.implies(new AllPermission())==false) {
         throw new PolicyStoreException("Can only store (All Permissions)", null);
      }
      
      String fileName = generateFilename(execDesc.getSerialNumber().getBytes());
      if(fileName == null) throw new PolicyStoreException("Can't generate filename for permission.", null);
      
      logger.info("Storing file:"+fileName);
      File f = new File(storeDir, fileName);
      
      try {
         FileOutputStream out = new FileOutputStream(f);
         ObjectOutputStream objOut = new ObjectOutputStream(out);
         objOut.writeObject(this.getEncrypted(ALL_PERMISSION_KEY));
         objOut.close();
         out.close();
      }
      catch(IOException ioExp) {
         throw new PolicyStoreException(ioExp.getMessage(), ioExp);
      }
   }
   
   /**
    * Attempt to encode the FileName and locate the file if it failes to find a
    * file it will call itself recursively encode again until it finds one or reaches the retry count
    */
   private File getFile(String name) {
      
      File f = new File(storeDir, name);
      return f;
   }
   
   /**
    * Generate a filename from a byte[] by using an MD5 Digest
    */
   private String generateFilename(byte[] data) {
      
      String encoded = null;
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         byte[] digest = md.digest(data);

         String digestSTR = Base64.getEncoder().encodeToString(digest);
         encoded = URLEncoder.encode(digestSTR, "UTF-8");
      }
      catch(NoSuchAlgorithmException noAlg) {
         //Shouldn't happen MD5 is built into JDK
         noAlg.printStackTrace();
      }
      catch(UnsupportedEncodingException badEnc) {
         //Shouldn't happen since UTF-8 is built into JDK
         badEnc.printStackTrace();
      }
      
      return encoded;
   }
   
   /**
    * Store Permissions for the Certification Path
    */
   public void storePermissions(X509Certificate cert, PermissionCollection perms) throws PolicyStoreException {
      
      //Can only store AllPermission for Now
      if(perms.implies(new AllPermission())==false) {
         throw new PolicyStoreException("Can only store (All Permissions)", null);
      }
      
      //Certificate can't be null
      if(cert == null) {
         throw new PolicyStoreException("Can not store null Certificate", null);
      }
      
      String fileName = generateFilename(cert.getSerialNumber().toByteArray());
      logger.info("Storing file:"+fileName);
      File f = new File(storeDir, fileName);
      
      try {
         FileOutputStream out = new FileOutputStream(f);
         ObjectOutputStream objOut = new ObjectOutputStream(out);
         objOut.writeObject(this.getEncrypted(ALL_PERMISSION_KEY));
         objOut.close();
         out.close();
      }
      catch(IOException ioExp) {
         throw new PolicyStoreException(ioExp.getMessage(), ioExp);
      }
   }
   
   /**
    * Get the KeyStore associated with this PolicyStore
    */
   public synchronized KeyStore getKeyStore() throws KeyStoreException {
      if(storeDir.exists()== false) throw new RuntimeException("Can't access security storage dir: "+storeDir.toString());

      //If .keystore file exists use it with default password of xitokeypass else build a new keystore based on cacerts
      File keyStoreFile = new File(storeDir, ".keystore");
      if(keyStoreFile.exists() == false) {
         buildDefaultKeyStore(keyStoreFile);
      }
      
      try {
         KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
         keyStore.load(new FileInputStream(keyStoreFile), DEFAULT_KEYSTORE_PASS.toCharArray());
         
         return keyStore;
      }
      catch(Exception exp) {
         KeyStoreException keyStoreExp = new KeyStoreException("Could not load KeyStore");
         keyStoreExp.initCause(exp);
         throw keyStoreExp;
      }
   }
   
   /**
    * Build a new Default KeyStore
    */
   private void buildDefaultKeyStore(File keyStoreFile) throws KeyStoreException {
      
      try {
         //Load the cacerts KeyStore from the java home
         String sep = File.separator;
         String cacertsPath = System.getProperty("java.home")+sep+"lib"+sep+"security"+sep+"cacerts";
         File cacertsFile = new File(cacertsPath);
         
         KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
         keyStore.load(new FileInputStream(cacertsFile), "changeit".toCharArray());
         keyStore.store(new FileOutputStream(keyStoreFile), DEFAULT_KEYSTORE_PASS.toCharArray());
      }
      catch(Exception exp) {
         KeyStoreException keyStoreExp = new KeyStoreException("Could not load KeyStore");
         keyStoreExp.initCause(exp);
         throw keyStoreExp;
      }
   }
      
}
