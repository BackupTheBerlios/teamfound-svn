<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>

<xsl:template match="/response">


<html>
<head>
	<title>TeamFound - share your search results</title>
	<link rel="stylesheet" type="text/css" href="stylesheet.css"/> 
	<meta http-equiv="content-type" content="text/html; charset=ISO-8859-1"/>
	<meta http-equiv="expires" content="0"/>
	<link rel="stylesheet" type="text/css" href="stylesheet.css"/>
</head>
<body>
<h1><xsl:value-of select="server/name"/></h1>
<xsl:call-template name="menu"/>
<xsl:call-template name="pageselector"/>
<!--<h2>Request Informationen:</h2>
<p>
Return-Value: <xsl:value-of select="teamfound/return-value"/><br/>
Return-Description: <xsl:value-of select="teamfound/return-description"/><br/>
</p>
-->
<p id="bottom">
<br/><br/>
<a href="http://teamfound.berlios.de/">TeamFound</a> - share your search results<br/>
Copyright (c) 2006 - Jan Kechel, Martin Klink, Jonas Heese, Andreas Bachmann <br/>
Using Server-Version: <xsl:value-of select="server/version"/> / Interface-Version: <xsl:value-of select="server/interface-version"/><br/>
</p>

</body>
</html>

</xsl:template>

<xsl:template name="menu">
	<xsl:variable name="stdserverparams">
		&amp;want=xml&amp;version=<xsl:value-of select="/response/server/interface-version"/>
	</xsl:variable>

<p>
	<xsl:if test="/response/xsltpassthrough = 'search'">
		<b>Search</b>
	</xsl:if>
	<xsl:if test="/response/xsltpassthrough != 'search'">
		<a> 
			<xsl:attribute name="href">
				?pt=search&amp;command=getcategories<xsl:value-of select="$stdserverparams"/>
			</xsl:attribute>
			Search
		</a>
	</xsl:if>
	|
	<xsl:if test="/response/xsltpassthrough = 'addpage'">
		<b>Add page</b>
	</xsl:if>
	<xsl:if test="/response/xsltpassthrough != 'addpage'">
		<a> 
			<xsl:attribute name="href">
				?pt=addpage&amp;command=getcategories<xsl:value-of select="$stdserverparams"/>
			</xsl:attribute>
			Add page
		</a>
	</xsl:if>
	|
	<xsl:if test="/response/xsltpassthrough = 'browsecats'">
		<b>Browse categories</b>
	</xsl:if>
	<xsl:if test="/response/xsltpassthrough != 'browsecats'">
		<a> 
			<xsl:attribute name="href">
				?pt=browsecats&amp;command=getcategories<xsl:value-of select="$stdserverparams"/>
			</xsl:attribute>
			Browse categories
		</a>
	</xsl:if>
	|
	<xsl:if test="/response/xsltpassthrough = 'login'">
		<b>Login</b>
	</xsl:if>
	<xsl:if test="/response/xsltpassthrough != 'login'">
		<a> 
			<xsl:attribute name="href">
				?pt=login&amp;command=getcategories<xsl:value-of select="$stdserverparams"/>
			</xsl:attribute>
			Login
		</a>
	</xsl:if>
</p>
</xsl:template>

<xsl:template name="pageselector">
	<xsl:if test="/response/xsltpassthrough = 'search'">
		<xsl:call-template name="search"/>
	</xsl:if>
	<xsl:if test="/response/xsltpassthrough = 'addpage'">
		<xsl:call-template name="addpage"/>
	</xsl:if>
	<xsl:if test="/response/xsltpassthrough = 'browsecats'">
		<xsl:call-template name="browsecats"/>
	</xsl:if>
	<xsl:if test="/response/xsltpassthrough = 'login'">
		<xsl:call-template name="login"/>
	</xsl:if>
</xsl:template>

<xsl:template name="searchfield">
	<p>
		<form id="search1" action="tf" method="post">
			<input size="50" type="text" name="keyword">
				<xsl:attribute name="value">
					<xsl:if test="count(/response/teamfound/search/keywords/word) > 0">
						<xsl:for-each select="/response/teamfound/search/keywords/word">
							<xsl:value-of select="."/>
						</xsl:for-each>
					</xsl:if>
				</xsl:attribute>
			</input>
			<input size="50" type="hidden" name="category" value="0"/>
			<input size="50" type="hidden" name="want" value="xml"/>
			<input size="50" type="hidden" name="version">
				<xsl:attribute name="value">
					<xsl:value-of select="/response/server/interface-version"/>
				</xsl:attribute>
			</input> 
			<input size="50" type="hidden" name="command" value="search"/>
			<input size="50" type="submit" value="Search"/>
		</form>
	</p>
</xsl:template>

<xsl:template name="search">
	<xsl:call-template name="searchfield"/>
	<xsl:if test="count(/response/teamfound/search/result) > 0">
		<p>
			<xsl:value-of select="count(/response/teamfound/search/result/found)"/>
			of 
			<xsl:value-of select="/response/teamfound/search/result/count"/>
			results shown:<br/>
			<ul>
				<xsl:for-each select="/response/teamfound/search/result/found">
					<li>
						<a>
							<xsl:attribute name="href">
								<xsl:value-of select="url"/>
							</xsl:attribute>
							<xsl:value-of select="title"/>
						</a>
						<br/>
						<xsl:value-of select="url"/> (category <xsl:value-of select="incategory"/>)
					</li>
				</xsl:for-each>
			</ul>
		</p>
		<xsl:call-template name="searchfield"/>
	</xsl:if>
</xsl:template>

<xsl:template name="addpage">
<p>
	<xsl:if test="count(/response/teamfound/addPage) > 0">
		URL <a>
				<xsl:attribute name="href">
					<xsl:value-of select="/response/teamfound/addPage/url"/>
				</xsl:attribute>
				<xsl:value-of select="/response/teamfound/addPage/url"/>
			</a>
		successfully added.<br/><br/>
	</xsl:if>
	<form id="addpage1" action="tf" method="post">
	<input size="50" type="text" name="url" value="http://"/>
		<input size="50" type="hidden" name="category" value="0"/>
		<input size="50" type="hidden" name="want" value="xml"/>
		<input size="50" type="hidden" name="version">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/server/interface-version"/>
			</xsl:attribute>
		</input> 
		<input size="50" type="hidden" name="command" value="addpage"/>
		<input size="50" type="submit" value="Add URL"/>
	</form>
</p>
</xsl:template>

<xsl:template name="recursecats">
	<xsl:if test="count(subcategories/category) > 0">
		<ul>
			<xsl:for-each select="subcategories/category">
				<li>
					<xsl:value-of select="name"/> - <xsl:value-of select="description"/>
					<xsl:call-template name="recursecats"/>
				</li>
			</xsl:for-each>
		</ul>
	</xsl:if>
</xsl:template>

<xsl:template name="recursecatsasoption">
	<xsl:if test="count(subcategories/category) > 0">
			<xsl:for-each select="subcategories/category">
				<option>
					<xsl:attribute name="value">
						<xsl:value-of select="id"/>
					</xsl:attribute>
					<xsl:value-of select="name"/>
				</option>
				<xsl:call-template name="recursecatsasoption"/>
			</xsl:for-each>
	</xsl:if>
</xsl:template>


<xsl:template name="browsecats">
<p>
Categories:
	<ul>
		<xsl:for-each select="/response/teamfound/getcategories/category">
			<li>
				<xsl:value-of select="name"/> - <xsl:value-of select="description"/>
				<xsl:call-template name="recursecats"/>
			</li>
		</xsl:for-each>
	</ul>
	<form id="addcategory1" action="tf" method="post">
		Add category as subcategory of
		<select size="0" name="subcategoryof">
			<xsl:for-each select="/response/teamfound/getcategories/category">
				<option>
					<xsl:attribute name="value">
						<xsl:value-of select="id"/>
					</xsl:attribute>
					<xsl:value-of select="name"/>
				</option>
				<xsl:call-template name="recursecatsasoption"/>
			</xsl:for-each>
		</select>
		<table>
			<tr><td>Category-Name:</td><td><input name="name" type="text"/></td></tr>
			<tr><td>Category-Description:</td><td><input name="description" type="text"/></td></tr>
			<tr><td colspan="2"><input type="submit" value="create new category"/></td></tr>
		</table>
		<input type="hidden" name="want" value="xml"/>
		<input size="50" type="hidden" name="version">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/server/interface-version"/>
			</xsl:attribute>
		</input> 
		<input type="hidden" name="command" value="addcategory"/>
	</form>
</p>
</xsl:template>

<xsl:template name="login">
<p>
	<form id="login1" action="tf" method="post">
	<table>
	<tr><td>Username:</td><td><input size="20" type="text" name="username" value=""/></td></tr>
	<tr><td>Password:</td><td><input size="20" type="password" name="password" value=""/></td></tr>
	<tr><td><input size="50" type="submit" value="Login"/></td></tr>
	</table>
		<input type="hidden" name="want" value="xml"/>
		<input size="50" type="hidden" name="version">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/server/interface-version"/>
			</xsl:attribute>
		</input> 
		<input type="hidden" name="command" value="login"/>
	</form>
</p>
</xsl:template>

</xsl:stylesheet>
