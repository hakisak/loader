<?php

/**
 * Build Xito Top
 */
function buildXitoTop($title = "Xito") {

    ?>
<html>
<head>
    <meta name="generator" content="HTML Tidy, see www.w3.org">
    <meta http-equiv="CONTENT-TYPE" content="text/html; charset=windows-1252">
    <title><?= $title ?>
    </title>
    <link rel="STYLESHEET" href="/xito.css">
    <script type="text/javascript" type="text/javascript" src="/ruzeeborders/cssquery2-p.js"></script>
    <script type="text/javascript" type="text/javascript" src="/ruzeeborders/ruzeeborders.js"></script>

    <!-- syntax highlighting of code -->
    <link type="text/css" rel="stylesheet" href="/syntaxhighlighter_2/styles/shCore.css" />
    <link type="text/css" rel="stylesheet" href="/syntaxhighlighter_2/styles/shThemeDefault.css" />
    <script type="text/javascript" src="/syntaxhighlighter_2/scripts/shCore.js"></script>
    <script type="text/javascript" src="/syntaxhighlighter_2/scripts/shBrushJava.js"></script>
    <script type="text/javascript" src="/syntaxhighlighter_2/scripts/shBrushXml.js"></script>
    <script type="text/javascript">
        SyntaxHighlighter.all();
    </script>

</head>

<!-- Script to create round rect borders -->
<script>
    RUZEE.Borders.add({
      ".roundRectBorder" : {
        borderType:"shadow",
        cornerRadius:15,
        shadowWidth:10
      }
    });

    window.onload=function(){
      RUZEE.Borders.render();
    };
</script>

<body lang="en-US">





    <MAP NAME="ImageMap">
        <AREA SHAPE=RECTANGLE HREF="/index.php" TARGET="_self" COORDS="0,0,200,200">
        <AREA SHAPE=POLYGON HREF="http://sourceforge.net/project/showfiles.php?group_id=47029" TARGET="_self" COORDS="466,143,488,175,612,175,622,143">
        <AREA SHAPE=POLYGON HREF="http://jaxito.com/platform/dev/quick_launch.jnlp" TARGET="_self" COORDS="624,143,646,175,770,175,780,143">
    </MAP>

    <table width="800" cellpadding="0" cellspacing="5">
        <tr>
            <td colspan="2"><img src="/images/xito_banner.jpg" usemap="#ImageMap" border="0"></td>
        </tr>
    </table>

<?php
}

/**
* Build Xito Bottom
*/
function buildXitoBottom() {

?>

    </td>
    </tr>
    </table>
</body>
</html>

    <?php

}

/**
 * Build Left Nav
 */
function buildLeftNav($section = "home") {

    ?>
<table width="800" cellpadding="5" cellspacing="5">
<tr>
<td valign="top">

    <table class="sidebartable" width="100%" cellpadding="1" cellspacing="1">
        <tr>
            <th><a href="/projects/index.php">Projects</a></th>
        </tr>
        <tr>
            <td>
                <a href="/projects/appmanager/index.php">App Manager</a><br>
                <a href="/projects/bootstrap/index.php">BootStrap</a><br>
                <a href="/projects/dialog/index.php">Dialog</a><br>
                <a href="/projects/tablelayout/index.php">TableLayout</a><br>
            </td>
        </tr>
    </table>
    <br>
    <table class="sidebartable" width="100%">
        <tr>
            <td>
                <a href="/apps/index.php">Applications</a><br>
            </td>
        </tr>
    </table>
    <br>
    <table class="sidebartable" width="100%">
        <tr>
            <td>
                <a href="http://sourceforge.net/project/showfiles.php?group_id=47029">Downloads</a><br>
                <a href="/documentation/index.php">Documentation</a><br>
                <a href="/documentation/articles.php">Articles</a><br><br>
                <a href="/volunteer.php">Volunteer</a><br>
                <a href="/contact.php">Contact Us</a><br>
                <a href="http://sourceforge.net/forum/?group_id=47029">Forums</a><br>

            </td>
        </tr>
    </table>

    <!-- Source Forge Logo -->
    <p>
       <a href="http://sourceforge.net/projects/xito">
        <img src="http://sflogo.sourceforge.net/sflogo.php?group_id=47029&type=11" width="120" height="30" border="0" alt="Get Xito at SourceForge.net. Fast, secure and Free Open Source software downloads" />
       </a>
    </p>

    <!-- Get Java Button -->
    <p>
        <a href="http://java.com/java/download/index.jsp?cid=jdp136223" target="_blank">
            <img width="88" height="31" border="0" alt="GetJava Download Button" title="GetJava" src="http://java.com/en/img/everywhere/getjava_sm.gif?cid=jdp136223">
        </a>
    </p>

    <!-- Open Source Logo -->
    <p>
        <a href="http://www.opensource.org/docs/definition.php">
            <img src="http://opensource.org/trademarks/opensource/web/opensource-75x65.gif" border="0">
        </a>
    </p>

</td>
<!-- Start the Body Section -->
<td width="100%" height="30" valign="top">

    <?php

}

?>

