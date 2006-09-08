<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>

<xsl:template match="/response">

<html>
<head>
	<title>TeamFound</title>
	<link rel="stylesheet" type="text/css" href="stylesheet.css"/> 
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1"/>
	<meta http-equiv="expires" content="0"/>
</head>
<body>
<h1><xsl:value-of select="server/name"/></h1>
Version: <xsl:value-of select="server/version"/><br/>
Interface-Version: <xsl:value-of select="server/interface-version"/><br/>
<h2>Request Informationen:</h2>
Return-Value: <xsl:value-of select="teamfound/return-value"/><br/>
Return-Description: <xsl:value-of select="teamfound/return-description"/><br/>
</body>
</html>

</xsl:template>

</xsl:stylesheet>
