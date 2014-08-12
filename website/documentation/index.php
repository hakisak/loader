<?php

	require_once("../page_util.inc.php");
	
	buildXitoTop("Xito - Documentation");
   
    buildLeftNav("documentation");
	
?>


<TABLE WIDTH="100%" BORDER="0" CELLSPACING="0" CELLPADDING="20">
<TR>
<TD WIDTH="100%" HEIGHT="403" VALIGN="TOP" ALIGN="LEFT">

	<h1><a href="Xito_Platform_White_Paper.pdf">Xito WhitePaper</a></h1>
	<p>
	A White Paper describing the goals of the Xito Project and a high level architecture.
	</p>
	<h1><a href="BootStrap_Dev_Guide.pdf">BootStrap Developers Guide</a></h1>
	<p>
	A Developers guide describing the BootStrap and how developers can use it to deploy their rich client applications.
	</p>
	<h1><a href="articles.php">Articles</a></h1>
	<p>
	Articles about Xito technologies or other related or interesting articles related to Java Client applications
	</p>
	<h1><a href="javadoc/index.php">JavaDoc APIs</a></h1>
	<p>
	Java API Documentation for the various Xito Projects
	</p>


<BR>
<BR>
</TD>
</TR>
</TABLE>

<?php
   
   buildXitoBottom();


?>
