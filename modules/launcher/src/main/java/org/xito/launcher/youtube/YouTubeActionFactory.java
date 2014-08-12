package org.xito.launcher.youtube;

import java.awt.Frame;
import java.net.URL;
import java.util.HashMap;

import javax.swing.ImageIcon;

import org.w3c.dom.Element;
import org.xito.launcher.LauncherAction;
import org.xito.launcher.LauncherActionFactory;
import org.xito.launcher.LauncherService;
import org.xito.launcher.Resources;
import org.xito.launcher.jnlp.JavaAppAction;

public class YouTubeActionFactory extends LauncherActionFactory {

   public static final String ELEMENT_NAME = "youtube-action";
    
   public YouTubeActionFactory() {
      setName(Resources.youtubeBundle.getString("action.display.name"));
      setSmallIcon(YouTubeActionBeanInfo.icon16);
      setLargeIcon(YouTubeActionBeanInfo.icon32);
   }
   
   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createActionFromDataElement(org.w3c.dom.Element)
    */
   @Override
   public LauncherAction createActionFromDataElement(Element element) {
      
      YouTubeDesc desc = new YouTubeDesc();
      initNameAndId(desc, element);
      
      desc.setVideioId(element.getAttribute("videoid"));
      
      return new YouTubeAction(this, desc);
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#generateDataElement(org.xito.launcher.LauncherAction)
    */
   @Override
   public Element generateDataElement(LauncherAction action) {
      
      YouTubeDesc desc = (YouTubeDesc)action.getLaunchDesc();
      
      org.w3c.dom.Element e = createInitialElement(desc);
      e.setAttribute("videoid", desc.getVideoId());
            
      return e;
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#getElementName()
    */
   @Override
   public String getElementName() {
      return ELEMENT_NAME;
   }

   /* (non-Javadoc)
    * @see org.xito.launcher.LauncherActionFactory#createAction()
    */
   @Override
   public LauncherAction createAction() {
      
      return new YouTubeAction(this);
   }
   
   @Override
   public LauncherAction createAction(Frame owner, Object obj) {
      //Can't be null
      if(obj == null)
         return null;
      
      //It needs to be a URL object
      if(!(obj instanceof URL)) 
         return null;
      
      //url needs to have youtube as the host
      URL url = (URL)obj;
      int i = url.getHost().indexOf("youtube.com");
      if(i<0) {
         return null;
      }
      
      HashMap params = parseQuery(url.getQuery());
      String videoID = (String)params.get("v");
      if(videoID == null || videoID.length()==0) return null;
      
      YouTubeAction action = new YouTubeAction(this);
      YouTubeDesc youTubeDesc = (YouTubeDesc)action.getLaunchDesc();
      youTubeDesc.setVideioId((String)videoID);
      
      //Get the Title
      try {
         youTubeDesc.initializeWebInfo();
         return action;
      }
      catch(Exception exp) {
         //exp.printStackTrace();
         if(action.edit(owner)) {
            return action;
         }
         else {
            return null;
         }
      }
   }
   
   /**
    * Parse the query string and get the youtube video id
    * @param query
    * @return
    */
   protected HashMap parseQuery(String query) {
      
      HashMap map = new HashMap();
      if(query == null) return map;
      String[] tupels = query.split("&");
      for(int i=0;i<tupels.length;i++) {
         String s = (tupels[i]);
         String[] nv = s.split("=");
         if(nv != null && nv.length>0) {
            map.put(nv[0], nv[1]);
         }
      }
      
      return map;
   }

}
