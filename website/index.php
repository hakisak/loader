<?php

   require_once("page_util.inc.php");

   buildXitoTop("Xito");
   
   buildLeftNav();
   
   ?>
   <img src="images/xito_release_1-0.jpg">
   <hr>
   <h1>Mission</h1>
   <p>
    The Xito platform provides tools to create better Rich Internet Applications in the Java.<a href="/documentation/Xito_Platform_White_Paper.pdf">(WhitePaper)</a>
    &nbsp;This includes user interface components and and a bootstrap module to assist in downloading application content.
   </p>

   <img src="images/xito_arch_small.jpg" align="right">
   <h2>Overview</h2>
   <p>
    The Xito platform has been created to address client deployment issues and develop a rich Java client 
    experience for end users. The platform is comprised of modular services that work together to 
    produce a network centric architecture with an easy to use user interface integrated with 
    the native host operating system.
   </p>
   <p>
    The Xito Platform will address the core issues of existing Java client technology, namely: 
    large memory and performance requirements, a poor user interface to manage Java applications, 
    and complex security model that is not understood by end users. 
   </p>
   <p>
    The Platform will also address the initial problems with Java client software which 
    hindered Java adoption for client applications in the beginning. 
    The main approach to addressing these issues are: Create very user friendly user 
    interfaces for the environment, create applications that take advantage of the network 
    and Java's unique capabilities, and create a platform that third-party developers are 
    excited to write for, creating a broad market for network deployed Java applications.
   </p>
   
   <p>
   The following chart provides an example of the performance benefits when using Xito
   to launch several applications.
   </p>	

   <img src="images/perf_chart.jpg">	
   <br><br>
      	
   <p style="font-size:80%">
   	
   <?php
   $count_my_page = ("hitcounter.txt");
   $contents = file($count_my_page);
   $hits = $contents[0];
   $hits = $hits + 1;
   $fp = fopen($count_my_page , "w");
   $sucess = fwrite($fp , $hits);
   fclose($fp);
   echo "hits: " . $hits . "<br>";
   ?>
   </p>
   <?php
   
   buildXitoBottom();


?>
