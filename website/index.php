<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
   <title>TeamFound - share your search results</title>
<link rel="stylesheet" href="stylesheet.css" type="text/css">
</head>

<body>
<table width="100%">
<tr><td>
<img src="images/logo1_300.png">
</td><td align="right">
	<a href="http://developer.berlios.de">
	<img src="http://developer.berlios.de/bslogo.php?group_id=5199&type=1" width="124" height="32" border="0" alt="BerliOS Logo">
	</A>
</td></tr>
</table>

<font size="+2" color="#000080"> - share your search results</font>

<h2>Overview</h2>
TeamFound gives a team the capability to share search results without any usage-overhead. The toolbar (firefox and ie) can be used to mark interesting pages and full-text-search those while also showing normal search-engine results for the same keywords.

<h2>Status</h2>
TeamFound is still under development. Beta versions are available.

<h2>Download & Installation</h2>
<div id="release">
<h3>Extension for Internet Explorer</h3>
<table>
<tr><td width="150px">Current Version:</td><td>0.1</td></tr>
<tr><td>Released:</td><td>28. Nov 2005</td></tr>
<tr><td>Download:</td><td><a href="http://download.berlios.de/teamfound/TeamFound.IE.0.1.zip">http://download.berlios.de/teamfound/TeamFound.IE.0.1.zip</a></td></tr>
<tr><td>Installation:</td><td>unpack zip-file into an **empty** directory, run install.cmd</td></tr>
<tr><td>Requires:</td><td><a href="http://www.microsoft.com/downloads/details.aspx?familyid=262d25e3-f589-4842-8157-034d1e7cf3a3">.NET Framework 1.1</a></td></tr>
<tr><td>Tested: </td><td>only on Windows XP</td></tr>
<tr><td>Notes: </td><td>first public release, very basic, does not combine search results with external search-engines yet, to try this with our TeamFound test server enter the url <code>http://hqpm.dyndns.org/tf</code> into Properties</td></tr>
</table>
</div>
<br>
<div id="release">
<h3>Extension for Firefox</h3>
<table>
<tr><td width="150px">Current Version:</td><td>0.5</td></tr>
<tr><td>Released:</td><td>28. Nov 2005</td></tr>
<tr><td>Download: </td><td><a href="http://download.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.5-interface-m1.xpi">http://download.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.5-interface-m1.xpi</a></td></tr>
<tr><td>Requires: </td><td><a href="http://www.mozilla.org/projects/firefox/">Firefox 1.5</a></td></tr>
<tr><td>Tested: </td><td>on Windows XP and Linux</td></tr>
<tr><td>Notes: </td><td>The toolbar is preconfigured to use our test server, though you'll probably find sites related to this project. To use TeamFound for your own project you have to setup your own server. For testing, using our test server, try the names of our developers ('martin', 'jonas', 'andreas', 'jan') or sth. related to TeamFound like 'lucene', 'xul', or 'textbox' as search words.
</td></tr>
</table>
</div>
<br>
<div id="release">
<h3>TeamFound Server</h3>
<table>
<tr><td width="150px">Current Version:</td><td>0.1</td></tr>
<tr><td>Released:</td><td>23. Nov 2005</td></tr>
<tr><td>Download: </td><td><a href="http://download.berlios.de/teamfound/teamfound-server-0.1.tar.bz2">http://download.berlios.de/teamfound/teamfound-server-0.1.tar.bz2</a></td></tr>
<tr><td>Installation: </td><td>please read the install.txt included with this package</td></tr>
<tr><td>Requires: </td><td>Apache httpd, Perl and Java 1.5</td></tr>
<tr><td>Tested: </td><td>only on Linux</td></tr>
<tr><td>Notes: </td><td>first public release, very basic, uses Apache Lucene as search engine</td></tr>
</table>
</div>
<br>
Older releases can be obtained from <a href="https://developer.berlios.de/projects/teamfound">https://developer.berlios.de/projects/teamfound</a>


<h2>Web-Interface</h2>
TeamFound can also be used (also it's not that handy) using a simple web-form.<br>
Try the form at our test-server: <a href="http://hqpm.dyndns.org/tf">hqpm.dyndns.org/tf</a>.

<h2>Screenshots</h2>
Firefox extension 0.2:<br><br>
<img src="images/screenshot_firefox_toolbar_0.2.png">
<br><br>
Example search result using TeamFound server 0.1 with Firefox extension 0.4:<br><br>
<img src="images/screenshot-search-server-0.1-firefox-toolbar-0.4.jpg">


<h2>License</h2>
<a href="http://www.gnu.org/copyleft/gpl.html">GNU General Public License</a><br><br>
<code>
TeamFound - share your search results<br>
Copyright (c) 2005 Jan Kechel, Martin Klink, Jonas Heese, Andreas Bachmann<br>
<br>
This program is free software; you can redistribute it and/or<br>
modify it under the terms of the GNU General Public License<br>
as published by the Free Software Foundation; either version 2<br>
of the License, or (at your option) any later version.<br>
This program is distributed in the hope that it will be useful,<br>
but WITHOUT ANY WARRANTY; without even the implied warranty of<br>
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br>
GNU General Public License for more details.<br>
You should have received a copy of the GNU General Public License<br>
along with this program; if not, write to the Free Software<br>
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
</code>

<h2>Architecture</h2>
<img src="images/architecture1.png">

<h2>Interfaces & Clones</h2>
We are going to provide an open interface standard!<br><br>
Though if you want to write a TeamFound-clone (no matter whether server or just your own extension) we want to encourage you to stick with our interfaces. Please read our specification at <a href="http://wiki.jonasheese.de/index.php/Interface-Spezifikation">http://wiki.jonasheese.de/index.php/Interface-Spezifikation</a> and help keeping things simple. Things will be simple if there is only one standard all TeamFound like clients and servers have to deal with!<br>
<br>
If you miss things or have suggestions on how we could do sth. better or maybe just different, please feel free to write to our mailinglist <a href="mailto:teamfound-development@lists.berlios.de">teamfound-development@lists.berlios.de</a> or post to our forum <a href="https://developer.berlios.de/forum/?group_id=5199">https://developer.berlios.de/forum/?group_id=5199</a>.<br>
<br>
Currently our implementation uses interface milestone 1, but interface milestone 2 is nearly finished, and maybe interface milestone 3 or 4 will become a specification with all we need for TeamFound-like searches ;-)<br>
<br>
Interfaces are yet only available in german, though we are going to translate it to other languages as soon as it is comprehensive and usable.

<h2>Development</h2>
TeamFound is a project at the <a href="http://www.tu-berlin.de/eng/index.html">Technische Universit&auml;t Berlin</a>, Germany. The lecture is called 'Infrastrukturen zur Open Source Softwareentwicklung'.<br>
<br>
Development in the german language takes place at <a href="http://wiki.jonasheese.de/index.php/TeamFound">http://wiki.jonasheese.de/index.php/TeamFound</a>.


<h2>Changelog</h2>
<h3>Firefox toolbar 0.5 changes</h3>
<ul>
<li>Google search results are actually working now, including further results</li>
<li>Compatible with Firefox 1.5 release candidates</li>
</ul>
<h3>Firefox toolbar 0.4 features</h3>
<ul>
<li>Clicking on TF-Icon opens preferences dialog, here you can setup your TeamFound server url and choose between two search result layouts</li>
<li>Add page - Adds the current page to the TeamFound-Index.</li>
<li>text field - Here you can enter search words or urls. The toolbar tries to automatically figure out what you meant and though will either search or just visit the given url.</li>
<li>current url - The current url is always displayed at the end of the toolbar, though not overwriting your current search words.</li>
</ul>

<br><br>
<div align="center">Copyright (c) 2005 Jan Kechel</div>
</body>
</html>
