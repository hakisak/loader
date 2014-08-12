<?php

   require_once("../page_util.inc.php");

   buildXitoTop("Xito Projects");
   
   buildLeftNav();
   
   ?>
   
   <TABLE WIDTH="100%" BORDER="0" CELLSPACING="0" CELLPADDING="20">
	<TR>
	<TD WIDTH="100%" VALIGN="TOP" ALIGN="LEFT">
		<h1><a href="appmanager/index.php">AppManager</a></h1>
		<p>
			The Xito Application Manager provides a simple to use Client application to manage and launch Java and Web Applications off the internet. 
			It is built using the Xito Platform to provide an easy to use system for launching rich interactive safe content. More information about
			Xito Application Manager will be coming soon. For more information about the Xito Platform you can view the <a href="/documentation/Xito_Platform_White_Paper.pdf">Xito Whitepaper.</a>
		</p>
		<h1><a href="bootstrap/index.php">BootStrap</a></h1>
		<p>
			BootStrap will boot a Java environment discribed as a set of service applications that
			are located on the local machine or on the Internet. BootStrap can be used to boot your application  
			off the Internet. It provides a Security Manager and Cache Manager to load your applications. For Detailed 
			information about the BootStrap you can view the <a href="/documentation/BootStrap_Dev_Guide.pdf">Developers Guide.</a>
		</p>
		<h1><a href="dialog/index.php">Dialog Framework</a></h1>
		<p>
			Xito Dialog Framework provides an easy to use facility for displaying alerts or other dialogs. Xito Dialog is included in the Xito BootStrap.
		</p>
		<h1>Reflect Kit</h1>
		<p>
			Reflect Kit provides an lightwieght reflection api that is a facade over the current Java reflection API. 
			Reflect Kit makes it much easier to use Reflection in your applications.
		</p>
		
		
	</TD>
	</TR>
	</TABLE>

	
<?php
   
   buildXitoBottom();

?>
 
