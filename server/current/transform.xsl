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
			<script src="ui.js" type="text/javascript"></script>
		</head>
		<body>
			<h1><xsl:value-of select="server/name"/></h1>
			<xsl:call-template name="menu"/>
			<xsl:call-template name="showcommandresults"/>
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
				Using Server-Version: <xsl:value-of select="server/version"/> / Interface-Version: <xsl:value-of select="server/interface-version"/>
				<br/>
			</p>
		</body>
	</html>
</xsl:template>

<xsl:template name="showcommandresults">
	<xsl:if test="/response/teamfound/return-value != '0'">
		<p id="error">
			Error #<xsl:value-of select="/response/teamfound/return-value"/><br/>
			<xsl:value-of select="/response/teamfound/return-description"/>
		</p>
	</xsl:if>

	<xsl:if test="/response/teamfound/return-value = '0'">
		<xsl:if test="count(/response/teamfound/search/result) > 0">
			<xsl:if test="/response/xsltpassthrough != 'viewallsites' or count(/response/xsltpassthrough) = 0">
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
			</xsl:if>
			<xsl:if test="/response/xsltpassthrough = 'viewallsites'">
				<p>
					This category contains <xsl:value-of select="count(/response/teamfound/search/result/found)"/> URLs:
					<ul>
						<xsl:for-each select="/response/teamfound/search/result/found">
							<li>
								<a>
									<xsl:attribute name="href">
										<xsl:value-of select="url"/>
									</xsl:attribute>
									<xsl:value-of select="url"/>
								</a>
								(<a>
									<xsl:attribute name="href">
									?pt=nyi
									</xsl:attribute>
									remove from category
								</a> / 
								<a>
									<xsl:attribute name="href">
									?pt=nyi
									</xsl:attribute>
									delete from project
								</a> )
							</li>
						</xsl:for-each>
					</ul>
				</p>

			</xsl:if>
		</xsl:if>

		<xsl:if test="count(/response/teamfound/addPage) > 0">
			<p>
				Add URL <a>
						<xsl:attribute name="href">
							<xsl:value-of select="/response/teamfound/addPage/url"/>
						</xsl:attribute>
						<xsl:value-of select="/response/teamfound/addPage/url"/>
					</a>: <xsl:value-of select="/response/teamfound/return-description"/>
			</p>
		</xsl:if>

		<xsl:if test="count(/response/teamfound/registeruser) > 0">
			<p>
				Register username <xsl:value-of select="/response/teamfound/registeruser/user"/>: <xsl:value-of select="/response/teamfound/return-description"/>
			</p>
		</xsl:if>

		<xsl:if test="count(/response/teamfound/login) > 0">
			<p>
				Login user <xsl:value-of select="/response/teamfound/login/user"/>: <xsl:value-of select="/response/teamfound/return-description"/>
			</p>
		</xsl:if>

		<xsl:if test="count(/response/teamfound/addcategory) > 0">
			<p>
				Create category <xsl:value-of select="/response/teamfound/addcategory/name"/>: <xsl:value-of select="/response/teamfound/return-description"/>
			</p>
		</xsl:if>

		<xsl:if test="count(/response/teamfound/editcategory) > 0">
			<p>
				Edit category <xsl:value-of select="/response/teamfound/editcategory/name"/> - <xsl:value-of select="/response/teamfound/editcategory/description"/>: <xsl:value-of select="/response/teamfound/return-description"/>
			</p>
		</xsl:if>
	</xsl:if>
</xsl:template>

<xsl:template name="menu">
	<xsl:variable name="stdserverparams">
		<xsl:if test="count(/response/xsltpassthrough2) > 0">
			&amp;want=xml&amp;version=<xsl:value-of select="/response/server/interface-version"/>&amp;pt2=<xsl:value-of select="/response/xsltpassthrough2"/>&amp;projectid=<xsl:value-of select="/response/xsltpassthrough2"/>
		</xsl:if>
		<xsl:if test="count(/response/xsltpassthrough2) = 0">
			&amp;want=xml&amp;version=<xsl:value-of select="/response/server/interface-version"/>
		</xsl:if>
	</xsl:variable>
	<xsl:variable name="stdserverparamsnopt2">
			&amp;want=xml&amp;version=<xsl:value-of select="/response/server/interface-version"/>
	</xsl:variable>

	<p>

	<xsl:if test="/response/xsltpassthrough = 'allprojects'">
		<b>Browse projects</b>
	</xsl:if>
	<xsl:if test="/response/xsltpassthrough != 'allprojects' or count(/response/xsltpassthrough) = 0">
		<a> 
			<xsl:attribute name="href">
				?pt=allprojects&amp;command=getprojects<xsl:value-of select="$stdserverparamsnopt2"/>
			</xsl:attribute>
			Browse projects
		</a>
	</xsl:if>
	|
	<xsl:if test="/response/session/name = 'guest'">
		<xsl:if test="/response/xsltpassthrough = 'login'">
			<b>Login</b>
		</xsl:if>
		<xsl:if test="/response/xsltpassthrough != 'login' or count(/response/xsltpassthrough) = 0">
			<a> 
				<xsl:attribute name="href">
					?pt=login<xsl:value-of select="$stdserverparamsnopt2"/>
				</xsl:attribute>
				Login
			</a>
		</xsl:if>
	</xsl:if>

	<xsl:if test="/response/session/name != 'guest'">
		<xsl:if test="/response/xsltpassthrough = 'login'">
			<b><xsl:value-of select="/response/session/name"/>'s projects</b>
		</xsl:if>
		<xsl:if test="/response/xsltpassthrough != 'login' or count(/response/xsltpassthrough) = 0">
			<a> 
				<xsl:attribute name="href">
					?pt=login&amp;command=getprojects<xsl:value-of select="$stdserverparamsnopt2"/>
				</xsl:attribute>
				<xsl:value-of select="/response/session/name"/>'s projects
			</a>
		</xsl:if>
	</xsl:if>

	<xsl:if test="count(/response/xsltpassthrough2) > 0">
		<br/>
		Project 
		<xsl:for-each select="/response/teamfound/getcategories/category">
			<xsl:if test="id = /response/xsltpassthrough2">
				<xsl:value-of select="name"/>
			</xsl:if>
		</xsl:for-each>
		 (#<xsl:value-of select="/response/xsltpassthrough2"/>): 
		<xsl:if test="/response/xsltpassthrough = 'search'">
			<b>Search</b>
		</xsl:if>
		<xsl:if test="/response/xsltpassthrough != 'search' or count(/response/xsltpassthrough) = 0">
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
		<xsl:if test="/response/xsltpassthrough != 'addpage' or count(/response/xsltpassthrough) = 0">
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
		<xsl:if test="/response/xsltpassthrough != 'browsecats' or count(/response/xsltpassthrough) = 0">
			<a> 
				<xsl:attribute name="href">
					?pt=browsecats&amp;command=getcategories<xsl:value-of select="$stdserverparams"/>
				</xsl:attribute>
				Browse categories
			</a>
		</xsl:if>
	</xsl:if>
	</p>
</xsl:template>

<xsl:template name="pageselector">
	<xsl:if test="/response/teamfound/return-value = '0'">
		<xsl:if test="/response/xsltpassthrough = 'search'">
			<xsl:call-template name="search"/>
		</xsl:if>

		<xsl:if test="/response/xsltpassthrough = 'allprojects'">
			<xsl:call-template name="allprojects"/>
		</xsl:if>

		<xsl:if test="/response/xsltpassthrough = 'addpage'">
			<xsl:call-template name="addpage"/>
		</xsl:if>
		<xsl:if test="/response/xsltpassthrough = 'browsecats'">
			<xsl:call-template name="browsecats"/>
		</xsl:if>
		<xsl:if test="/response/xsltpassthrough = 'login'">
			<xsl:if test="/response/session/name = 'guest'">
				<h2>Login</h2>
				<xsl:call-template name="login"/>
				<h2>Register</h2>
				<xsl:call-template name="register"/>
			</xsl:if>
			<xsl:if test="/response/session/name != 'guest'">
				<xsl:call-template name="myprojects"/>
			</xsl:if>
		</xsl:if>
	</xsl:if>
</xsl:template>

<xsl:template name="searchfield">
	<p>
		<form id="search1" action="tf" method="post">
			<input type="text" size="50" name="keyword">
				<xsl:attribute name="value">
					<xsl:if test="count(/response/teamfound/search/keywords/word) > 0">
						<xsl:for-each select="/response/teamfound/search/keywords/word">
							<xsl:value-of select="."/>
						</xsl:for-each>
					</xsl:if>
				</xsl:attribute>
			</input>
			<br/>
			Category: <select size="0" name="category" value="0">

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

			<input type="hidden" name="want" value="xml"/>
			<input type="hidden" name="version">
				<xsl:attribute name="value">
					<xsl:value-of select="/response/server/interface-version"/>
				</xsl:attribute>
			</input> 
			<input type="hidden" name="command" value="search"/>
			<input type="submit" value="Search"/>
			<input type="hidden" name="pt2">
				<xsl:attribute name="value">
					<xsl:value-of select="/response/xsltpassthrough2"/>
				</xsl:attribute>
			</input>
		</form>
	</p>
</xsl:template>

<xsl:template name="search">
	<xsl:call-template name="searchfield"/>
</xsl:template>

<xsl:template name="addpage">
	<p>
	<form id="addpage1" action="tf" method="post">
		<input type="text" size="50" name="url" value="http://"/>
		<br/>
		Category: <select size="0" name="category" value="0">
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
		<input type="hidden" name="want" value="xml"/>

		<input type="hidden" name="version">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/server/interface-version"/>
			</xsl:attribute>
		</input> 
		<input type="hidden" name="pt2">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/xsltpassthrough2"/>
			</xsl:attribute>
		</input>
		<input type="hidden" name="command" value="addpage"/>
		<input type="submit" value="Add URL"/>
	</form>
	</p>
</xsl:template>

<xsl:template name="recursecats">
	<xsl:if test="count(subcategories/category) > 0">
		<ul>
			<xsl:for-each select="subcategories/category">
				<li>
					<xsl:value-of select="name"/> - <xsl:value-of select="description"/>
					(<a>
						<xsl:attribute name="href">
							?pt=viewallsites&amp;command=search&amp;getall=yes&amp;category=<xsl:value-of select="id"/>&amp;pt2=<xsl:value-of select="/response/xsltpassthrough2"/>&amp;version=<xsl:value-of select="/response/server/interface-version"/>
						</xsl:attribute>
						view URLs 
					 </a> /
					 <a>
						<xsl:attribute name="href">
							javascript:showhide('editcat<xsl:value-of select="id"/>');
						</xsl:attribute>
						edit category
					 </a>)
					<xsl:call-template name="editcats"/>
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

<xsl:template name="editcats">
	<div style="display: none;">
		<xsl:attribute name="id">editcat<xsl:value-of select="id"/></xsl:attribute>
		<form action="tf" name="editcat" method="post">
			<table>
				<tr>
					<td>Name:</td>
					<td>
						<input type="text" name="name">
							<xsl:attribute name="value">
								<xsl:value-of select="name"/>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td>Description:</td>
					<td>
						<input type="text" name="description">
							<xsl:attribute name="value">
								<xsl:value-of select="description"/>
							</xsl:attribute>
						</input>
					</td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" value="save"/></td>
				</tr>
			</table>

			<input type="hidden" name="command" value="editcategory"/>
			<input type="hidden" name="want" value="xml"/>
			<input type="hidden" name="version">
				<xsl:attribute name="value">
					<xsl:value-of select="/response/server/interface-version"/>
				</xsl:attribute>
			</input> 
			<input type="hidden" name="pt2">
				<xsl:attribute name="value">
					<xsl:value-of select="/response/xsltpassthrough2"/>
				</xsl:attribute>
			</input> 
			<input type="hidden" name="category">
				<xsl:attribute name="value">
					<xsl:value-of select="id"/>
				</xsl:attribute>
			</input> 

		</form>
	</div>
</xsl:template>

<xsl:template name="browsecats">
	<p>
	<ul>
		<xsl:for-each select="/response/teamfound/getcategories/category">
			<li>
				<xsl:value-of select="name"/> - <xsl:value-of select="description"/>
				(<a>
					<xsl:attribute name="href">
						?pt=viewallsites&amp;command=search&amp;getall=yes&amp;category=<xsl:value-of select="id"/>&amp;pt2=<xsl:value-of select="/response/xsltpassthrough2"/>&amp;version=<xsl:value-of select="/response/server/interface-version"/>
					</xsl:attribute>
					view URLs 
				 </a> /
				 <a>
					<xsl:attribute name="href">
						javascript:showhide('editcat<xsl:value-of select="id"/>');
					</xsl:attribute>
					edit category
				 </a>)
				<xsl:call-template name="editcats"/>
				<xsl:call-template name="recursecats"/>
			</li>
		</xsl:for-each>
	</ul>
	<h2>Add category</h2>
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
		<input type="hidden" name="version">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/server/interface-version"/>
			</xsl:attribute>
		</input> 
		<input type="hidden" name="pt2">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/xsltpassthrough2"/>
			</xsl:attribute>
		</input> 

		<input type="hidden" name="command" value="addcategory"/>
	</form>
	</p>
</xsl:template>

<xsl:template name="allprojects">
	<p>
	<ul>
		<xsl:for-each select="/response/teamfound/projects/project">
			<li>
				<a>
					<xsl:attribute name="href">
						?pt=search&amp;command=getcategories&amp;projectid=<xsl:value-of select="id"/>&amp;pt2=<xsl:value-of select="id"/>&amp;version=<xsl:value-of select="/response/server/interface-version"/>
					</xsl:attribute>
					<xsl:value-of select="name"/> 
				</a>
				- <xsl:value-of select="description"/>
			</li>
		</xsl:for-each>
	</ul>
	</p>
</xsl:template>

<xsl:template name="editproject">
	(<a>
	<xsl:attribute name="href">
	javascript:showhide('projectpermissions<xsl:value-of select="id"/>');
	</xsl:attribute>
	view/edit permissions</a> / 
	<a>
	<xsl:attribute name="href">
	?pt=manageusers&amp;command=getusers&amp;projectid=<xsl:value-of select="id"/>&amp;pt2=<xsl:value-of select="id"/>&amp;version=<xsl:value-of select="/response/server/interface-version"/>
	</xsl:attribute>
	manage users
	</a> /
	<a>
	<xsl:attribute name="href">
	?pt=browsecats&amp;command=getcategories&amp;projectid=<xsl:value-of select="id"/>&amp;pt2=<xsl:value-of select="id"/>&amp;version=<xsl:value-of select="/response/server/interface-version"/>
	</xsl:attribute>
	manage categories &amp; URLs
	</a> )
	<div style="display: none;">
		<xsl:attribute name="id">projectpermissions<xsl:value-of select="id"/></xsl:attribute>
		<form action="tf" name="editproject" method="post">
			<table>
				<tr><td>Guest permissions</td><td>User permissions</td></tr>
				<tr>
					<td>
						<input type="checkbox" id="idguestread" name="guestread" value="yes">
							<xsl:if test="guestread = 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="idguestread">read</label>
					</td>
					<td>
						<input type="checkbox" id="iduseruseradd" name="useruseradd" value="yes">
							<xsl:if test="useruseradd = 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="iduseruseradd">add users</label>
					</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" id="idguesturledit" name="guesturledit" value="yes">
							<xsl:if test="guesturledit= 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="idguesturledit">url edit</label>
					</td>
					<td>
						<input type="checkbox" id="iduserurledit" name="userurledit" value="yes">
							<xsl:if test="userurledit= 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="iduserurledit">url edit</label>
					</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" id="idguestcatedit" name="guestcatedit" value="yes">
							<xsl:if test="guestcatedit= 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="idguestcatedit">cat edit</label>
					</td>
					<td>
						<input type="checkbox" id="idusercatedit" name="usercatedit" value="yes">
							<xsl:if test="usercatedit= 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="idusercatedit">cat edit</label>
					</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" id="idguestaddurl" name="guestaddurl" value="yes">
							<xsl:if test="guestaddurl= 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="idguestaddurl">url add</label>
					</td>
					<td>
						<input type="checkbox" id="iduseraddurl" name="useraddurl" value="yes">
							<xsl:if test="useraddurl= 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="iduseraddurl">url add</label>
					</td>
				</tr>
				<tr>
					<td>
						<input type="checkbox" id="idguestaddcat" name="guestaddcat" value="yes">
							<xsl:if test="guestaddcat= 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="idguestaddcat">add cat</label>
					</td>
					<td>
						<input type="checkbox" id="iduseraddcat" name="useraddcat" value="yes">
							<xsl:if test="useraddcat= 'yes'">
								<xsl:attribute name="checked">checked</xsl:attribute>
							</xsl:if>
						</input>
						<label for="iduseraddcat">add cat</label>
					</td>
				</tr>
				<tr>
					<td colspan="2"><input type="submit" value="save"/></td>
				</tr>
			</table>
			<input type="hidden" name="want" value="xml"/>
			<input type="hidden" name="version">
				<xsl:attribute name="value">
					<xsl:value-of select="/response/server/interface-version"/>
				</xsl:attribute>
			</input> 
			<input type="hidden" name="pt2">
				<xsl:attribute name="value">
					<xsl:value-of select="id"/>
				</xsl:attribute>
			</input> 
			<input type="hidden" name="projectid">
				<xsl:attribute name="value">
					<xsl:value-of select="id"/>
				</xsl:attribute>
			</input> 
			<input type="hidden" name="command" value="editpermissions"/>
		</form>
	</div>
</xsl:template>

<xsl:template name="listmyprojects">
	<p>
		<xsl:if test="/response/teamfound/projects/project/isadmin = 'yes'">
			<h2>I'm admin for:</h2>
			<ul>
				<xsl:for-each select="/response/teamfound/projects/project">
					<xsl:if test="isadmin = 'yes'">
						<li>
							<a>
								<xsl:attribute name="href">
									?pt=search&amp;command=getcategories&amp;projectid=<xsl:value-of select="id"/>&amp;pt2=<xsl:value-of select="id"/>&amp;version=<xsl:value-of select="/response/server/interface-version"/>
								</xsl:attribute>
								<xsl:value-of select="name"/> 
							</a>
							- <xsl:value-of select="description"/> <xsl:call-template name="editproject"/>
						</li>
					</xsl:if>
				</xsl:for-each>
			</ul>
		</xsl:if>

		<xsl:if test="/response/teamfound/projects/project/isadmin = 'no'">
			<h2>I'm user in:</h2>
			<ul>
				<xsl:for-each select="/response/teamfound/projects/project">
					<xsl:if test="isadmin = 'no'">
						<li>
							<a>
								<xsl:attribute name="href">
									?pt=search&amp;command=getcategories&amp;projectid=<xsl:value-of select="id"/>&amp;pt2=<xsl:value-of select="id"/>&amp;version=<xsl:value-of select="/response/server/interface-version"/>
								</xsl:attribute>
								<xsl:value-of select="name"/> 
							</a>
							- <xsl:value-of select="description"/>
						</li>
					</xsl:if>
				</xsl:for-each>
			</ul>
		</xsl:if>
	</p>
</xsl:template>

<xsl:template name="register">
	<p>
	<form id="register" action="tf" method="post">
	<table>
	<tr><td>Username:</td><td><input size="20" type="text" name="user" value=""/></td></tr>
	<tr><td>Password:</td><td><input size="20" type="password" name="pass" value=""/></td></tr>
	<tr><td><input type="submit" value="Register"/></td></tr>
	</table>
		<input type="hidden" name="want" value="xml"/>
		<input type="hidden" name="version">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/server/interface-version"/>
			</xsl:attribute>
		</input> 
		<input type="hidden" name="command" value="register"/>
		<input type="hidden" name="pt" value="login"/>
	</form>
	</p>
</xsl:template>

<xsl:template name="myprojects">
	<xsl:call-template name="listmyprojects"/>
	<p>
	<h2>Register new Project:</h2>
	<form id="newproject" action="tf" method="post">
	<table>
	<tr><td>Projectname:</td><td><input size="20" type="text" name="name" value=""/></td></tr>
	<tr><td>Description:</td><td><input size="20" type="text" name="description" value=""/></td></tr>
	<tr><td><input type="submit" value="Create Project"/></td></tr>
	</table>
		<input type="hidden" name="want" value="xml"/>
		<input type="hidden" name="version">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/server/interface-version"/>
			</xsl:attribute>
		</input> 
		<input type="hidden" name="command" value="addcategory"/>
		<input type="hidden" name="subcategoryof" value="-1"/>
	</form>
	</p>
</xsl:template>

<xsl:template name="login">
	<p>
	<form id="login1" action="tf" method="post">
	<table>
	<tr><td>Username:</td><td><input size="20" type="text" name="user" value=""/></td></tr>
	<tr><td>Password:</td><td><input size="20" type="password" name="pass" value=""/></td></tr>
	<tr><td>UniForge:</td><td><input size="20" type="checkbox" name="uniforgeuser" value="yes"/></td></tr>
	<tr><td><input type="submit" value="Login"/></td></tr>
	</table>
		<input type="hidden" name="want" value="xml"/>
		<input type="hidden" name="version">
			<xsl:attribute name="value">
				<xsl:value-of select="/response/server/interface-version"/>
			</xsl:attribute>
		</input> 
		<input type="hidden" name="command" value="login"/>
	</form>
	</p>
</xsl:template>

</xsl:stylesheet>
