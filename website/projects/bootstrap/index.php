<?php

	require_once("../../page_util.inc.php");
	
	buildXitoTop();
   
        buildLeftNav("bootstrap");
	
?>


<table width="100%" border="0" cellspacing="0" cellpadding="20">
<tr>
<td width="100%" height="403" valign="TOP" align="LEFT">
<h1>BootStrap</h1>

<p>
BootStrap will boot a Java environment discribed as a set of service applications that
are located on the local machine or on the Internet. BootStrap can be used to boot your application  
off the Internet. It provides a Security Manager and Cache Manager to load your applications. For detailed information view the <a href="/documentation/BootStrap_Dev_Guide.pdf">BootStrap Developers Guide</a>.
</p>
<p>
To see a good example of the BootStrap in action download the <a href="../appmanager/index.php">Xito Application Manager</a>. It uses Bootstrap to boot its services and provide the
basic security, caching, and execution framework.
</p>
<h2>Screen Shots</h2>
<p>
<a href="bootstrap_dialogs.jpg">Sample BootStrap Dialogs (win32)</a><br>
<a href="bootstrap_dialogs_xp.jpg">Sample BootStrap Dialogs (win xp)</a><br>
<a href="bootstrap_swingsets.jpg">BootStrap Launching lots of SwingSets in the same VM (win32)</a><br>
<a href="bootstrap_macosx1.jpg">BootStrap Sample running on MacOS X</a><br>


</td>
</tr>
</table>

<?php
   
   buildXitoBottom();


?>
