<?php

	require_once("../../page_util.inc.php");
	
	buildXitoTop();
   
    buildLeftNav("tablelayout");
	
?>

<table width="100%" border="0" cellspacing="0" cellpadding="20">
<tr>
<td width="100%" height="403" valign="TOP" align="LEFT">
<h1>TableLayout</h1>

<a href="/projects/dialog">Dialog Project</a><br>
<a href="https://sourceforge.net/project/showfiles.php?group_id=47029&package_id=293409">Download Latest</a><br>
<a href="https://sourceforge.net/forum/forum.php?forum_id=891370">Discussion Forum</a><br>

<p>
For engineers that work on Java client user interfaces using Swing, one of the first complaints
is the complexity of <b>Layout Managers</b>. It is often the case that engineers coming
from a web HTML back ground have difficulty laying out components in a Swing UI.
</p>
<p>
After all shouldn't it be just as easy to layout components using Swing as it is to layout
components on a web page. This is why TableLayout was created. <b>TableLayout</b> provides
the same easy to use layout concept that HTML pages use, namely HTML Table Tags.
</p>
<p>
The TableLayout allows UI designers to create HTML tables and position components in HTML table
definitions and then use that HTML definitions at runtime, in their Java applications. This
allows designers to use a common tool, a Web Browser, to visualize how there UI will be laid out.
</p>
<p>
To use the TableLayout the steps are:
<ol>
    <li>Create an html file that contains a table definition to layout components</li>
    <li>In Java create an instance of the TableLayout that uses the html file for input</li>
    <li>Place Java Components in the panel that is using the table layout</li>
</ol>
</p>


<h1>Quick Example</h1>

The following example walks you through this process of: creating the html, checking the display of the
html in your browser, and finally writting the java code that will use your html table layout.

A few things to note:
<p>
You can name component cells any name you wish. The name is placed between the
&lt;td&gt; tags.  If you prefer you can also use the id attribute of the &lt;td&gt; to name the cell.
</p>
<p>
Also, if you would like a component to take up the enter space of a cell you need to use the align="full" attribute
of the &lt;td&gt; tag.
</p>


<h2>HTML Code</h2>
    <div style="background-color: rgb(240,240,240)">
    <pre class="brush:html">

    &lt;table width="100%" height="100%" &gt;
        &lt;tr height="20"&gt;&lt;/tr&gt;
        &lt;tr&gt;
            &lt;td width="20"&gt;&lt;/td&gt;
            &lt;td align="right"&gt;firstname_lbl&lt;/td&gt;
            &lt;td align="full" width="100%"&gt;firstname_fld&lt;/td&gt;
            &lt;td width="20"&gt;&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr height="8"&gt;&lt;/tr&gt;
        &lt;tr&gt;
            &lt;td&gt;&lt;/td&gt;
            &lt;td align="right"&gt;lastname_lbl&lt;/td&gt;
            &lt;td align="full" width="100%"&gt;lastname_fld&lt;/td&gt;
        &lt;/tr&gt;
        &lt;tr height="20"&gt;&lt;/tr&gt;
    &lt;/table&gt;

    </pre>
    </div>
<p>

</p>

<h2>HTML Layout</h2>

<div style="width: 400px">
    <i>The following table shows how your components will be laid out:</i>
    <br>
    <br>
    <table border="1" width="100%" height="100%">
        <tr height="20"></tr>
        <tr>
            <td width="20"></td>
            <td align="right">firstname_lbl</td>
            <td align="full" width="100%">firstname_fld</td>
            <td width="20"></td>
        </tr>
        <tr height="8"></tr>
        <tr>
            <td></td>
            <td align="right">lastname_lbl</td>
            <td align="full" width="100%">lastname_fld</td>
        </tr>
        <tr height="20"></tr>
    </table>
    <br>
    <br>
</div>

<h2>Java Code</h2>

    <div style="background-color: rgb(240,240,240)">
    <pre class="brush:java">

        //Create the Panel using a TableLayout
        //Note: The URL can be a resource in your application such as
        //MyClass.class.getResource("layout.html")
        JPanel panel = new JPanel(new TableLayout([URL to html]));

        panel.add("firstname_lbl", new JLabel("First Name:"));

        JTextField firstNameTF = new JTextField();
        panel.add("firstname_fld", firstNameTF);

        panel.add("lastname_lbl", new JLabel("Last Name:"));

        JTextField lastNameTF = new JTextField();
        panel.add("lastname_fld", lastNameTF);
        

    </pre>
    </div>


<h2>Rendered UI</h2>

<br>
<div>
    <img src="sample.png" border="0">
</div>
<br>

<h2>Samples</h2>
<p>
Too see additional html table layout samples visit the table <a href="samples/">layout gallery</a>.
</p>

<p>
License: Xito Dialog is licensed under the Apache 2.0 License. <a href="http://www.apache.org/licenses/LICENSE-2.0.txt">view</a>
</p>


</td>
</tr>
</table>

<?php
   
   buildXitoBottom();


?>
