<?php

   require_once("../page_util.inc.php");

   buildXitoTop("Applications");
   
   buildLeftNav();
   
   ?>

<h1>Applications</h1>
<hr>

<h2><a href="games/index.php">Games</a></h2><br>
<!-- <h2><a href="science/index.php">Science</a></h2><br> -->
<h2><a href="tools/index.php">Tools</a></h2>

<hr>
<p>
	This list of applications and applets can be launched using Xito App Manager. Each of 
	these applications will be launched over the internet and those applications that require All Permissions
	will prompt the user to grant those permissions. 
</p>
<p>	
	Xito makes no warranty or gaurantee concerning the validaty or trust of these applications. However only applications that
	are known to not attack client machines are listed by this site. If you experience any trust issues related to these applications please 
	contact xito to have them removed from the site. 
</p>
<p>
	When <b>Restricted Applications</b> are launched they are placed in a
	restricted environment that will not enable the applications to access critical resources like your network or hard drive.
</p>
<h2>Launching Applications</h2>
<p>
	To launch these applications simple click the <img src="../images/jnlp_button.jpg"> or 
	<img src="../images/xito_button.jpg"> image next to each application. You can also <b>Drag and Drop </b>
	the Image Link to the Xito Application Manager to create an Alias to the application.
</p>
<table class="sidebartable">
	<tr>
		<td valign="top"><img src="../images/jnlp_button.jpg" align="right"></td>
		<td>
			These applications are launched by Java WebStart or your Default JNLP launcher. This application may ask for additional permissions.
			<br>
		</td>	
	</tr>
	<tr>
		<td valign="top" align="right"><img src="../images/jnlp_restricted_button.jpg" align="right"></td>
		<td>
			These applications are launched by Java WebStart or your Default JNLP launcher in a <b>restricted</b> environment. This application <b>should not</b> ask for additional permissions.
			<br>
		</td>	
	</tr>
	<tr>
		<td valign="top"><img src="../images/xito_button.jpg" align="right"></td>
		<td>
			These applications are launched by Xito Application Manager. This application may ask for additional permissions.
			<br> 
		</td>	
	</tr>
	<tr>
		<td valign="top" align="right"><img src="../images/xito_restricted_button.jpg" align="right"></td>
		<td>
			These applications are launched by Xito Application Manager in a <b>restricted</b> environment. This application <b>should not</b> ask for additional permissions.
			<br>
		</td>	
	</tr>
</table>

<?php
   
   buildXitoBottom();

?>
 

