<?php

	require_once("../../page_util.inc.php");
	
	buildXitoTop();
   
    buildLeftNav("dialog");
	
?>


<table width="100%" border="0" cellspacing="0" cellpadding="20">
<tr>
<td width="100%" height="403" valign="TOP" align="LEFT">
<h1>Application Manager</h1>

<p>
The Xito Application Manager is a simple to use Network Application Manager which is designed to bring network based Java Applications and content to the masses. 
The User Interface is designed for consumer users with functionality making it easy to launch and 
access Protected Java Content over the internet.  
</p>
<p>
You can <a href="http://sourceforge.net/project/showfiles.php?group_id=47029&package_id=152630&release_id=397306">Download the 1.0 Release</a> of Xito Application Manager From <a href="http://sourceforge.net/project/showfiles.php?group_id=47029">Xito Sourceforge Downloads</a>
<br>
<img src="ui_shot.jpg" border="0" align="right">
</p>
<p>
<b>Some of the major functionality of Xito App Manager is:</b><br>
<br>
<ul>
<li>Manages and Launches Web Based Java Applets</li>
<li>Manages and Launches Web Based JNLP Applications</li>
<li>Manages and Launches Local or Remote Java Applications</li>
<li>Manages and Launches Local Native Applications</li>
<li>Manages and Launches Web Sites in your default Browser</li>
<li>Optionally launches JNLP Applications using Java Web Start</li>
<li>Optionally launches Applications in the Same VM increasing performance and reducing memory requirements</li>
<li>Provides Full Security Manager for execution of untrusted code</li>
<li>Allows Drag and Drop Application URLs from Web Browser</li>
</ul>
<b>Xito Application manager is based on the following Services:</b><br>
<br>
<ul>
<li>Xito BootStrap</li>
<li>Xito Launcher Service (JNLP, Applet, Java Apps etc)</li>
<li>Xito Preference Service</li>
<li>Xito XML Document Service</li>
<li>Xito Control Panel</li>
<li>Xito Splash Screen Service</li>
<li>Xito JDIC Wraps the JDIC (JDesktop Integration Components from java.net into an Xito Service)</li>
</ul>

For more information about the Xito Platform read the <a href="/documentation/Xito_Platform_White_Paper.pdf">Xito Whitepaper</a>.
</p>

<h2>Screen Shots</h2>
<p>
<a href="appmanager_1.jpg">App Manager Screen Shot (Windows XP)</a><br>
<a href="appmanager_macosx1.jpg">App Manager Screen Shot (MacOS X)</a>
</p>
</td>
</tr>
</table>

<?php
   
   buildXitoBottom();


?>
