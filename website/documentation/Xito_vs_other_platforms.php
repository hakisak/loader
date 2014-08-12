<?php

	require_once("../page_util.inc.php");
	
	buildXitoTop("Xito - Articles");
   
    buildLeftNav("documentation");
	
?>

<h1>Xito Rich Client Platform compared to other rich client platforms</h1>
<b>--by Deane Richan (June 28, 2005)</b>
<p>
Rich Client Platforms have been getting lots of interest lately. Several have asked my why
one should use the Xito platform rather then other client platforms and what specific benefits Xito provides. 
This article will attempt to specify some of the reasons Xito is better suited for internet based client applictions. 
then other frameworks. 
</p>
<h2>Network deployed Applications</h2>
<p>
Frameworks such as Eclipse Rich Client Framework or the Netbeans platform provide rich client functionality however
applicatons deployed in those environments are not network deployed. Both these platforms support automatic updates
of modules however users much manage and work through the update process.<br><br>

Xito on the other hand, has rich built in support for deploying your applications on the network or internet. Basically once
your application is created and placed in Jars those jars are placed on a web server and the next time
your application is launched the new resources are downloaded and executed the User isn't involved in the process at all.
This enables your applications to be updated as seemlessly as websites are updated today. You never get asked if you
want to download a new version of a website this is because it is assumed. Xito has the same straight forward functionality.<br><br>

The developer also has the choice. They can deploy several components on the local machine or launch all components over the network so you
get the best of both worlds.
</p>

<h2>Ease of Development</h2>
<p>
Xito does not require that your application have any explicit knowledge of Xito or its API's. Your applications can be completely
written without Xito in mind using any IDE, editor, or build process you choose. When you are ready to deploy your application it can 
be deployed as an Xito Service or a JNLP Application. Both of which only require you write an XML file that lists the resources
your application uses. It is very simple to deploy an application using Xito.
</p>

<h2>Security is Built In</h2>
<p>
Since other frameworks are designed to launch resources locally rather then over the internet, Security is often an afterthought.
With Xito, Security is integrated into the core Xito BootStrap which enables your users to access your applications or other applications
in a secure environment.
</p>

<h2>Very Small Size</h2>
<p>
The Xito BootStrap is less then 300k and can be used to launch your entire application over the internet. 
This small size enables your users to easily download and install your application and then launch the application over the network.
Other services can be addeded to the BootStrap to provide additional functionality however the BootStrap is all that is required to
get your application up and running.
</p>

<h2>OpenSource Framework</h2>
<p>
Xito provides an Opensource license (CDDL) which provides for a very flexible license to use and redistribute the Xito Code. Some
other frameworks are proprietary and require you to purchase special licenses to use the frameworks.
</p>

<h2>Lower Memory and Faster loading of Multiple Applications</h2>
<p>
If you need to deploy several applications together, Xito enables your applications to all be executed in the Same VM. 
This enables applications to have a much smaller memory footprint and applications startup much faster. You can
even use the Xito Application Manager to deploy your applications and rebrand it with your own splash screen etc.
</p>
<p>
In summary, Applications can be lighter, faster, and easier to write when deployed using Xito technology. For more 
information see:
</p>
<p>
<a href="Xito_Platform_White_Paper.pdf">Xito Platform WhitePaper</a><br>
<a href="BootStrap_Dev_Guide.pdf">Xito BootStrap Developers Guide</a>
</p>

<?php
   
   buildXitoBottom();


?>

