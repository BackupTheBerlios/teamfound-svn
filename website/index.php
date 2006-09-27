<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
   <title>TeamFound - share your search results</title>
<link rel="stylesheet" href="stylesheet.css" type="text/css">
</head>

<body>
<div align="center">
<table width="480"><tr><td>
<table width="100%">
<tr><td>
<img src="images/logo1_300.png">
</td><td align="right">
	<a href="http://developer.berlios.de">
	<img src="http://developer.berlios.de/bslogo.php?group_id=5199&type=1" width="124" height="32" border="0" alt="BerliOS Logo">
	</A>
</td></tr>
</table>

<font size="+2" color="#000080"> - share your search results</font><br><br>
<a href="#overview">Overview</a> -
<a href="#status">Status</a> -
<a href="#downloadinstall">Download & Installation</a> -
<a href="#webinterface">Web-Interface</a> -
<a href="#screenshots">Screenshots</a> -
<a href="#license">License</a> -
<a href="#architecture">Architecture</a> -
<a href="#documentation">Documentation</a> -
<a href="#interface">Interfaces & Clones</a> -
<a href="#development">Development</a> -
<a href="#firefoxchangelog">Changelog</a>

<h2><a name="overview"></a>Overview</h2>
TeamFound gives a team the capability to share search results without any usage-overhead. The toolbar (firefox and ie) can be used to mark interesting pages and full-text-search those while also showing normal search-engine results for the same keywords.

<h2><a name="status"></a>Status</h2>
Try our <b>new Testserver Milestone 3</b> at <a href="http://teamfound.dyndns.org:8080/tf/tf">http://teamfound.dyndns.org:8080/tf/tf</a>. 
<ul>
<li>register your own project</li>
<li>create categories and subcategories</li>
<li>add other users to your project</li>
<li>no email required for registration</li>
<li>it's completely free</li>
<li>install <a href="http://prdownload.berlios.de/teamfound/teamfound-0.9-fx+fl.xpi">teamfound-0.9-fx+fl.xpi</a> Firefox Extension (use 'Settings' for your own Project-ID)</li>
<li>have fun</li>
</ul>

<h2>New Server released 24-SEP-2006</h2>
Binary: <a href="http://prdownload.berlios.de/teamfound/teamfoundserver-0.3.bin.tar.bz2">teamfoundserver-0.3.bin.tar.bz2</a><br>
Source: <a href="http://prdownload.berlios.de/teamfound/teamfoundserver-0.3.src.tar.bz2">teamfoundserver-0.3.src.tar.bz2</a><br>
<br>
New Features:
<ul>
<li>Runs as Servlet (tested with tomcat 5.0 and 5.5)</li>
<li>User-/Rightsmanagement</li>
<li>Project-Administration</li>
<li>Categorys</li>
<li>Automatic updates for indexed URLs</li>
</ul>

<h2><a name="downloadinstall"></a>Download & Installation</h2>
<div id="release">
<h3>Extension for Internet Explorer</h3>
<table>
<tr><td width="150px">Current Version:</td><td>0.1</td></tr>
<tr><td>Released:</td><td>28. Nov 2005</td></tr>
<tr><td>Download:</td><td><a href="http://download.berlios.de/teamfound/TeamFound.IE.0.1.zip">http://download.berlios.de/teamfound/TeamFound.IE.0.1.zip</a></td></tr>
<tr><td>Installation:</td><td>unpack zip-file into an **empty** directory, run install.cmd</td></tr>
<tr><td>Requires:</td><td><a href="http://www.microsoft.com/downloads/details.aspx?familyid=262d25e3-f589-4842-8157-034d1e7cf3a3">.NET Framework 1.1</a></td></tr>
<tr><td>Tested: </td><td>only on Windows XP</td></tr>
<tr><td>Notes: </td><td>first public release, very basic, does not combine search results with external search-engines yet, to try this with our TeamFound test server enter the url <code>http://teamfound.dyndns.org:8080/tf/tf</code> into Properties</td></tr>
</table>
</div>
<br>

<div id="release">
<h3>Extension for Firefox and Flock</h3>
<table>

<tr><td>Download</td><td><a href="https://addons.mozilla.org/addon.php?id=1526">latest stable release</a> (currently v0.8 - milestone 2)<br>
<font color="#FF5555">not compatible with current test-server milestone 3</font><br>
 - use <a href="http://prdownload.berlios.de/teamfound/teamfound-0.9-fx+fl.xpi">teamfound-0.9-fx+fl.xpi</a> instead</td></tr>

<!--<tr><td></td><td><a href="https://addons.mozilla.org/addon.php?id=1526">from addons.mozilla.org</a> (currently v0.7 - milestone 1)</td></tr>-->

<tr><td>Requires:</td><td><a href="http://www.mozilla.org/projects/firefox/">Firefox 1.5</a></td></tr>
<tr><td>Tested: </td><td>with Firefox 1.5 on Windows XP and Linux, with Flock 1.4.1 on Linux</td></tr>
<tr><td>Notes: </td><td>The toolbar is preconfigured to use our test server, though you'll probably find sites related to this project. To use TeamFound for your own project you have to setup your own project on our testserver or your can also setup your own server.<br>
For testing, using our public test server project, try the names of our developers ('martin', 'jonas', 'andreas', 'jan') or sth. related to TeamFound like 'lucene', 'xul', or 'textbox' as search words.</td></tr>
<tr><td>Features:</td><td>take a look at the <a href="#firefoxchangelog">changelog</a></td></tr>
</table>
</div>
<br>
<div id="release">
<h3>TeamFound Server</h3>
<table>
<tr><td width="150px">Current Version:</td><td>0.3</td></tr>
<tr><td>Released:</td><td>24. Sep 2006</td></tr>
<tr><td>Download: </td><td>Binary: <a href="http://prdownload.berlios.de/teamfound/teamfoundserver-0.3.bin.tar.bz2">teamfoundserver-0.3.bin.tar.bz2</a><br>
Source: <a href="http://prdownload.berlios.de/teamfound/teamfoundserver-0.3.src.tar.bz2">teamfoundserver-0.3.src.tar.bz2</a>
</td></tr>
<tr><td>Installation: </td><td>please read the readme.txt included with this package</td></tr>
<tr><td>Requires: </td><td>Java 1.5 and Tomcat (or another servlet container)</td></tr>
<tr><td>Tested: </td><td>only on Linux with Tomcat 5.0 and 5.5 using Java 1.5</td></tr>
<tr><td>Notes: </td><td>Milestone 3 Release, pretty complete functionality</td></tr>
<tr><td>Test-Server URL (v0.3): </td><td><a href="http://teamfound.dyndns.org:8080/tf/tf">http://teamfound.dyndns.org:8080/tf/tf</a></td></tr>
</table>
</div>


<h2><a name="webinterface"></a>Web-Interface</h2>
TeamFound can also be used using our Web-Interface: <a href="http://teamfound.dyndns.org:8080/tf/tf">http://teamfound.dyndns.org:8080/tf/tf</a>

<h2><a name="screenshots"></a>Screenshots</h2>
<h3>Firefox extension v0.8</h3>
<img src="images/ffextension-kategorien.png">
<h3>Example search result with server v0.1</h3>
<img src="images/screenshot-search-server-0.1-firefox-toolbar-0.4.jpg">


<h2><a name="license"></a>License</h2>
<a href="http://www.gnu.org/copyleft/gpl.html">GNU General Public License</a><br><br>
TeamFound - share your search results<br>
Copyright (c) 2005-2006 Jan Kechel, Martin Klink, Jonas Heese, Andreas Bachmann<br>
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

<h2><a name="architecture"></a>Architecture</h2>
<img src="images/architecture1.png">

<h2><a name="documentation"></a>Documentation - yet only in german</h2>
<h3>New Features in Milestone 3</h3>
<a href="teamfound_german_doc_m3/index.html">teamfound_german_doc_m3/index.html</a> - view online<br>
teamfound_german_doc_m3 <a href="download/teamfound_german_doc_m3.ps">.ps</a> (600k) / <a href="download/teamfound_german_doc_m3.pdf">.pdf</a> (170k)

<h3>Detailed technical documentation - Milestone 2</h3>
<a href="teamfound_german_doc/index.html">teamfound_german_doc/index.html</a> - view online<br>
teamfound_german_doc <a href="download/teamfound_german_doc.ps">.ps</a> (2 MB) / <a href="download/teamfound_german_doc.pdf">.pdf</a> (0.5 MB)

<h2><a name="interface"></a>Interfaces & Clones</h2>
We are going to provide an open interface standard!<br><br>
Though if you want to write a TeamFound-clone (no matter whether server or just your own extension) we want to encourage you to stick with our interfaces. Please read our specification at <a href="http://wiki.jonasheese.de/index.php/Interface-Spezifikation">http://wiki.jonasheese.de/index.php/Interface-Spezifikation</a> and help keeping things simple. Things will be simple if there is only one standard all TeamFound like clients and servers have to deal with!<br>
<br>
If you miss things or have suggestions on how we could do sth. better or maybe just different, please feel free to write to our mailinglist <a href="mailto:teamfound-development@lists.berlios.de">teamfound-development@lists.berlios.de</a> or post to our forum <a href="https://developer.berlios.de/forum/?group_id=5199">https://developer.berlios.de/forum/?group_id=5199</a>.<br>
<br>
Currently our implementation uses interface milestone 1, but interface milestone 2 is nearly finished, and maybe interface milestone 3 or 4 will become a specification with all we need for TeamFound-like searches ;-)<br>
<br>
Interfaces are yet only available in german, though we are going to translate it to other languages as soon as it is comprehensive and usable.

<h2><a name="development"></a>Development</h2>
TeamFound is a project at the <a href="http://www.tu-berlin.de/eng/index.html">Technische Universit&auml;t Berlin</a>, Germany. The lecture is called 'Infrastrukturen zur Open Source Softwareentwicklung'.<br>
<br>
Development in the german language takes place at <a href="http://wiki.jonasheese.de/index.php/TeamFound">http://wiki.jonasheese.de/index.php/TeamFound</a>.
<br>
Older releases and Source-Code can be obtained from <a href="https://developer.berlios.de/projects/teamfound">https://developer.berlios.de/projects/teamfound</a>


<h2><a name="firefoxchangelog"></a>Changelog</h2>
<h3><a name="firefoxchangelog0.9"></a>Firefox & Flock toolbar 0.9 (23-SEP-2006)</h3>
<ul>
<li>implements interface milestone 3</li>
</ul>
Download: <a href="http://prdownload.berlios.de/teamfound/teamfound-0.9-fx+fl.xpi">http://prdownload.berlios.de/teamfound/teamfound-0.9-fx+fl.xpi</a>


<h3><a name="firefoxchangelog0.8"></a>Firefox & Flock toolbar 0.8 (17-APR-2006)</h3>
<ul>
<li>implements interface milestone 2</li>
<li>supports categories</li>
<li>does not yet allow to add categories via toolbar</li>
</ul>
Download: <a href="http://prdownload.berlios.de/teamfound/teamfound-fx-fl-0.8.xpi">http://prdownload.berlios.de/teamfound/teamfound-fx-fl-0.8.xpi</a>

<h3><a name="firefoxchangelog0.7"></a>Firefox & Flock toolbar 0.7 (07-DEC-2005)</h3>
<ul>
<li>Bug-Fix: Add-Button actually works now</li>
<li>Label with current URL removed (though we are not going to replace the location-bar)</li>
<li>Compatibility with Flock browser tested and added to install.rdf</li>
</ul>
Download: <a href="http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.7-interface-m1.xpi">http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.7-interface-m1.xpi</a>

<h3><a name="firefoxchangelog0.6"></a>Firefox toolbar 0.6 (30-NOV-2005)</h3>
<ul>
<li>Input-field now behaves the same like the normal Firefox locationbar (including history popup) when urls are typed. Soon this can replace the Firefox locationbar and the Firefox searchbar with only one input-field ;-)</li>
<li>Settings-Dialog allows to give custom external search engines</li>
<li>Settings-Dialog allows to only search external, only search TeamFound or search both at once</li>
</ul>
Download: <a href="http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.6-interface-m1.xpi">http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.6-interface-m1.xpi</a>

<h3>Firefox toolbar 0.5 (28-NOV-2005)</h3>
<ul>
<li>Google search results are actually working now, including further results</li>
<li>Compatible with Firefox 1.5 release candidates</li>
</ul>
Download: <a href="http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.5-interface-m1.xpi">http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.5-interface-m1.xpi</a>

<h3>Firefox toolbar 0.4 (27-NOV-2005)</h3>
<ul>
<li>Clicking on TF-Icon opens preferences dialog, here you can setup your TeamFound server url and choose between two search result layouts</li>
<li>Add page - Adds the current page to the TeamFound-Index.</li>
<li>text field - Here you can enter search words or urls. The toolbar tries to automatically figure out what you meant and though will either search or just visit the given url.</li>
<li>current url - The current url is always displayed at the end of the toolbar, though not overwriting your current search words.</li>
</ul>
Download: <a href="http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.4-interface-m1.xpi">http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.4-interface-m1.xpi</a>

<h3>Firefox toolbar 0.3 (24-NOV-2005)</h3>
<ul>
<li>adds search at extern search-engine</li>
<li>shows google and teamfound search-results in same window</li>
</ul>
Download: <a href="http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.3-interface-m1.xpi">http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.3-interface-m1.xpi</a>

<h3>Firefox toolbar 0.2 (22-NOV-2005)</h3>
<ul>
<li>first release at berlios.de</li>
<li>available as .xpi</li>
</ul>
Download: <a href="http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.2-interface-m1.xpi">http://prdownload.berlios.de/teamfound/teamfound-firefox-1.5-extension-0.2-interface-m1.xpi</a>

<h3>Firefox toolbar 0.1 (19-NOV-2005)</h3>
<ul>
<li>initial release</li>
</ul>
</td></tr></table>


<br><br>
	Copyright (c) 2005/2006 Jan Kechel
</div>
</body>
</html>
