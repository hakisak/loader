<?php

   require_once("../../page_util.inc.php");

   buildXitoTop("Games");
   
   buildLeftNav();
   
   ?>

<h1>Games</h1>
<table class="sidebartable" width="80%">
	<tr><th align="left">Asteroids</th></tr>
	<tr>
		<td>
			Application version of famous Asteroids Applet by Mike Hall. <br>
			Modified added full screen support by Deane Richan
		</td>
	</tr>
	<tr>
		<td>
			<a href="asteroids/asteroids.jnlp">
				<img src="../../images/jnlp_button.jpg" border="0">
			</a>
			&nbsp;&nbsp;
			<a href="asteroids/asteroids_np.jnlp">
				<img src="../../images/jnlp_restricted_button.jpg" border="0">
			</a>
		</td>
	</tr>
</table>		 

<?php
   
   buildXitoBottom();

?>