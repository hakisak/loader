<?php
   $count_my_page = ("/tmp/persistent/xito/startups.txt");
   $contents = file($count_my_page);
   $hits = $contents[0];
   $hits = $hits + 1;
   $fp = fopen($count_my_page , "w");
   $sucess = fwrite($fp , $hits);
   fclose($fp);
   echo "hits: " . $hits;
?>
