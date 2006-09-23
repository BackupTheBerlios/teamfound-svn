/*
*	TeamFound, sharing your search results
*	Copyright (C) 2005-2006 Jan Kechel (jan@kechel.de)
*
*	This program is free software; you can redistribute it and/or
*	modify it under the terms of the GNU General Public License
*	as published by the Free Software Foundation; either version 2
*	of the License, or (at your option) any later version.
*
*	This program is distributed in the hope that it will be useful,
*	but WITHOUT ANY WARRANTY; without even the implied warranty of
*	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*	GNU General Public License for more details.
*
*	You should have received a copy of the GNU General Public License
*	along with this program; if not, write to the Free Software
*	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*
*	global variables:
*		xmlhttp - adding page http-request
*		xmlhttptf - searching teamfound
*		xmlhttpext - searching extern search engine
*		xmlhttpcat - loading categories
*		prefs - TeamFound preferences
*/

var TeamFound = 
{
	// entfernz leerzeichen
	 myTrim: function(s)
	 {
		 // Remove leading spaces and carriage returns
		 while ((s.substring(0,1) == ' ') || (s.substring(0,1) == '\n') || (s.substring(0,1) == '\r'))
		 { s = s.substring(1,s.length); }

		 // Remove trailing spaces and carriage returns
		 while ((s.substring(s.length-1,s.length) == ' ') || (s.substring(s.length-1,s.length) == '\n') || (s.substring(s.length-1,s.length) == '\r'))
		 { s = s.substring(0,s.length-1); }

		 return s;
	 },

	// Initialisierung
	onLoad: function() 
	{
		// nur beim starten des browsers einmal
		if( this.initialized != true)
		{
			// ok, also nur einmal
			this.initialized = true;


			// Hole Preferences Server
			// Get the root branch
			prefs = Components.classes["@mozilla.org/preferences-service;1"].
				getService(Components.interfaces.nsIPrefBranch).
				getBranch("extensions.teamfound.");

			tf_default_category = 0;
			tf_default_menuitem = 0;
			TeamFound.loadCategories();
		}

	}, // onLoad

	loadCategories: function()
	{
		var pref_serverurl = prefs.getCharPref("settings.serverurl");

		// Milestone 2 request: getcategories
		var command = "&version=3&command=getcategories";

		// Request erstellen (globale variable)
		xmlhttpcat = new XMLHttpRequest();
		// Callback Registrieren wenn der Server fertig ist
		xmlhttpcat.onreadystatechange = TeamFound.onLoadCategoriesFinished;
		// Request methode, url und asyncron (true/false) definieren
		xmlhttpcat.open("GET", pref_serverurl + command, true); 
		// Request senden
		xmlhttpcat.send(null);
	}, // loadCategories



	onLoadCategoriesFinished: function()
    {
		// nur etwas machen falls der request schon fertig ist
		if( xmlhttpcat.readyState == 4)  
		{
			// HTTP-Request Code auswerten
			if( xmlhttpcat.status == 200)
			{	// OK

				catmenu = document.getElementById("tf-categories");
				
				// menue loeschen
				for(var i = catmenu.childNodes.length - 1; i >= 0; i--) 
				{
					catmenu.removeChild(catmenu.childNodes.item(i));
				}

				// Auf Fehlercode 0 (OK) testen
				if( xmlhttpcat.responseXML.getElementsByTagName("return-value")[0].firstChild.nodeValue != 0)
				{
					alert("Error: " + xmlhttpcat.responseXML.getElementsByTagName("return-description")[0].firstChild.nodeValue + "\n");
					return;
				}

				// Server-Name anzeigen
				//catmenu.setAttribute("label", xmlhttpcat.responseXML.getElementsByTagName("server")[0].getElementsByTagName("name")[0].firstChild.nodeValue);

				// popup erzeugen
				var catpopup = document.createElement("menupopup");
				catmenu.appendChild(catpopup);

				//TeamFound.addCategory(xmlhttpcat.responseXML.getElementsByTagName("getcategories")[0].childNodes, catpopup, xmlhttpcat.responseXML.getElementsByTagName("server")[0].getElementsByTagName("name")[0].firstChild.nodeValue, 0);

				var xmlteamfound = xmlhttpcat.responseXML.getElementsByTagName("teamfound")[0];
				var xmlgetcagegories = xmlteamfound.getElementsByTagName("getcategories")[0];
				var firstcategory = xmlgetcagegories.childNodes;

				var m_catname = firstcategory.item(1).getElementsByTagName("name")[0].firstChild.nodeValue;
				tf_default_category = firstcategory.item(1).getElementsByTagName("id")[0].firstChild.nodeValue;
				catmenu.setAttribute("label", m_catname);

				var childnodes = firstcategory;

				if( childnodes.item(1).getElementsByTagName("subcategories")[0])
				{	// more subcategories exist
					TeamFound.addCategory( childnodes.item(1).getElementsByTagName("subcategories")[0].childNodes, catpopup, childnodes.item(1).getElementsByTagName("name")[0].firstChild.nodeValue, childnodes.item(1).getElementsByTagName("id")[0].firstChild.nodeValue);
				}
				else
				{	// no more subcategories
					TeamFound.addCategory( false, catpopup, childnodes.item(1).getElementsByTagName("name")[0].firstChild.nodeValue, childnodes.item(1).getElementsByTagName("id")[0].firstChild.nodeValue);
				}

			}
			else
			{
				alert("Error loading categories from server..");
			}
		}
	}, // onLoadCategoriesFinished


		
	onSettings: function()
	{
		var setdiag = window.openDialog("chrome://teamfound/content/settings.xul", "TeamFound preferences", "chrome,centerscreen,modal");
		setdiag.focus();

	}, // onSettings

	// Eine Suche soll durchgefuehrt werden
	onSearch: function(event, category)
	{
		// Text-feld auslesen, leerzeichen abscheiden
		var search = TeamFound.myTrim(document.getElementById("tf-input").value);

		if( search.length == 0)
		{
			return;
		}

		// Test ob url oder suchwoerter
		// erstmal wenn kein . (punkt) im feld vorkommt dann sind es suchwoerter, sonst url
		if( /\./.test(search) && !/[ \"]/.test(search))
		{
			TeamFound.myGotoUrl(search, event);
			return;
		}
		if( /^about:/.test(search))
		{
			TeamFound.myGotoUrl(search, event);
			return;
		}

		// Ok, template wird gleich geladen, content folgt nach asyncroner antwort
		if( prefs.getIntPref("settings.layout") == 0)
		{	// layout: divide horizontal
			content.location = "chrome://teamfound/skin/search_h.html";
			TeamFound.myTeamFoundSearch(search, category);
			TeamFound.myExternSearch(search);
		}
		else if( prefs.getIntPref("settings.layout") == 1)
		{	// layout: append external results
			content.location = "chrome://teamfound/skin/search_v.html";
			TeamFound.myTeamFoundSearch(search, category);
			TeamFound.myExternSearch(search);

		}
		else if( prefs.getIntPref("settings.layout") == 2)
		{	// only extern
			TeamFound.myExternSearch(search);
		}
		else if( prefs.getIntPref("settings.layout") == 3)
		{	// only teamfound
			TeamFound.myTeamFoundSearch(search, category);
		}
	}, // onSearch

	myGotoUrl: function(search, event)
	{
		// wenn protokoll mit angegeben wurde, dann direkt uebernehmen, sonst http vorhaengen
		var mygoto;
		if( /:\/\//.test(search) || /about:/.test(search))
		{
			mygoto = search;
		}
		else
		{
			mygoto = "http://" + search;
		}

		// ok, now load page
		content.location = mygoto;

		// delete input-field now
		document.getElementById("tf-input").value = "";

	}, // myGotoUrl

	myTeamFoundSearch: function(search, category)
	{
		var allwords = search.split(" ");

		if( allwords.length == 0)
		{
			return; // empty string .. we won't search for this ;-)
		}

		// insert 'AND' between different search-words
		var searchwithand = allwords[0];
		for( var i = 1; i < allwords.length; i++)
		{
			// make sure no empty searches (happens if there are 
			// several spaces between two words or appending spaces in search-field are given)
			if( allwords[i].length > 0)
			{
				searchwithand = searchwithand + " AND " + allwords[i];
			}
		}

		var pref_serverurl = prefs.getCharPref("settings.serverurl");

		if( category == -1)
		{
			category = tf_default_category;
		}
		// TeamFound Suche
		var teamfoundurl = pref_serverurl + "&command=search&version=3&keyword=" + searchwithand + "&category=" + category;

		//alert(teamfoundurl);

		// nur layouts 0 und 1 benutzen templates
		if( prefs.getIntPref("settings.layout") > 1)
		{	content.location=teamfoundurl;
			return;
		}
		//var teamfoundurl = pref_serverurl + "?keyword=" + searchwithand;
		// Request erstellen (globale variable)
		xmlhttptf= new XMLHttpRequest();
		// Callback Registrieren wenn der Server fertig ist
		xmlhttptf.onreadystatechange = TeamFound.onTeamFoundSearchFinished;
		// Request methode, url und asyncron (true/false) definieren
		xmlhttptf.open("GET", teamfoundurl, true); 
		// Request senden
		xmlhttptf.send(null);
	}, // myTeamFoundSearch

	myExternSearch: function(search)
	{
		var pref_searchurl = prefs.getCharPref("settings.searchurl");
		var externurl = pref_searchurl + search;

		// nur layouts 0 und 1 benutzen templates
		if( prefs.getIntPref("settings.layout") > 1)
		{
			content.location=externurl;
			return;
		}
		// Request erstellen (globale variable)
		xmlhttpext = new XMLHttpRequest();
		// Callback Registrieren wenn der Server fertig ist
		xmlhttpext.onreadystatechange = TeamFound.onExternSearchFinished;
		// Request methode, url und asyncron (true/false) definieren
		xmlhttpext.open("GET", externurl, true); 
		// Request senden
		xmlhttpext.send(null);
	}, // myExternSearch

	// Eine neue Seite soll dem Index hinzugefuegt werden
	onAddPage: function(category) 
	{
		// hinzuzufuegende url (globale variable) 
		addpageurl = content.document.URL;

		var pref_serverurl = prefs.getCharPref("settings.serverurl");

		if( category == -1)
		{
			category = tf_default_category;
		}

		// server-adresse zum hinzufuegen neuer seiten
		var url = pref_serverurl + "&command=addpage&version=3&url=" + addpageurl + "&category=" + category;
		//alert(url);

		// Request erstellen (globale variable)
		// XMLHttpRequest funktioniert mit Mozilla, Firefox, Safari, und Netscape (nicht mit IE)
		// (da aber diese extension im IE auch nicht geht ist das wohl egal ;-)
		xmlhttp = new XMLHttpRequest();

		// Callback Registrieren wenn der Server fertig ist
		xmlhttp.onreadystatechange = TeamFound.onAddPageFinished;

		// Request methode, url und asyncron (true/false) definieren
		xmlhttp.open("GET", url, true); 

		// Request senden
		xmlhttp.send(null);
	}, // onAddPage

	// Eine neue Seite wurde dem Index hinzugefuegt
	// (Callback-Funktion fuer den XMLHttpRequest)
	onAddPageFinished: function()
	{
		// nur etwas machen falls der request schon fertig ist
		if( xmlhttp.readyState == 4)  
		{
			// HTTP-Request Code auswerten
			if( xmlhttp.status == 200)
			{	// OK
				alert("Die Seite\n'" + addpageurl + "'\nwurde dem Index hinzugefuegt.");
			}
			else
			{	// Fehler
				alert(  "Es ist ein Fehler aufgetreten. Die Seite\n'" + 
					addpageurl + 
					"'\nkonnte dem Index nicht hinzugefuegt werden.\n" +
					"\n" +
					"Antwort: " + xmlhttp.status + " - " + xmlhttp.statusText);
			}
		}
	}, // onAddPageFinished

	// Externe suche beendet
	onExternSearchFinished: function()
	{
		// nur etwas machen falls der request schon fertig ist
		/*
		readonly PRInt32 readyState
		The state of the request.
		Possible values: 
		0 UNINITIALIZED open() has not been called yet. 
		1 LOADING send() has not been called yet. 
		2 LOADED send() has been called, headers and status are available. 
		3 INTERACTIVE Downloading, responseText holds the partial data. 
		4 COMPLETED Finished with all operations.
		*/
		if( xmlhttpext.readyState >= 3)  
		{
			var doc = window._content.document;
			var xtd = doc.getElementById("teamfound-result-two");

			// HTTP-Request Code auswerten
			if( xmlhttpext.status == 200)
			{	// OK
				if( xtd != null)
				{
					// ok, now make extern viewable from local,
					// by replacing local references with http://extern-base-url

					var pref_searchurl = prefs.getCharPref("settings.searchurl");
					
					var base = /(http.*?\/\/.*?\/)/.exec(pref_searchurl)[0];

					var externtolocal = xmlhttpext.responseText;

					externtolocal = externtolocal.replace(/href=\//g, "href="+base);
					externtolocal = externtolocal.replace(/href="\//g, "href=\""+base);
					externtolocal = externtolocal.replace(/action=\//g,"action="+base);
					externtolocal = externtolocal.replace(/action="\//g,"action=\""+base);
					externtolocal = externtolocal.replace(/src=\//g,"src="+base);
					externtolocal = externtolocal.replace(/src="\//g,"src=\""+base);

					// now load google-result into page-template
					xtd.innerHTML = externtolocal;
				}
			}
			else
			{	// Fehler
				xtd.innerHTML = "Error: " + xmlhttpext.status + " - " + xmlhttpext.statusText;
			}
		}

	}, // onExternSearchFinished

	onSelectAsDefault: function(c_category, c_menuitem)
    {
		tf_default_category = c_category;
		if( tf_default_menuitem != 0)
		{
			document.getElementById(tf_default_menuitem).setAttribute("checked", false);
		}
		tf_default_menuitem = c_menuitem;
		document.getElementById(tf_default_menuitem).setAttribute("checked", true);
		var x = document.getElementById(tf_default_menuitem).label.match(/.*'(.*)'.*/);
		catmenu.setAttribute("label", x[1]);

    }, //onSelectAsDefault

	addCategory: function(ac_childnodes, ac_menup, ac_desc, ac_id)
	{
		// create search, add and select menu-items
		var ac_sep = document.createElement("menuseparator");

		var ac_s = document.createElement("menuitem");
		ac_s.setAttribute("label", "Search '" + ac_desc + "'");
		ac_s.setAttribute("oncommand", "TeamFound.onSearch(0, " + ac_id + ");", false);

		var ac_a = document.createElement("menuitem");
		ac_a.setAttribute("label", "Add page to '" + ac_desc + "'");
		ac_a.setAttribute("oncommand", "TeamFound.onAddPage(" + ac_id + ");", false);

		var ac_std = document.createElement("menuitem");
		ac_std.setAttribute("label", "Select '" + ac_desc + "' as default");
		ac_std.setAttribute("id", "tfselectasdefault"+ac_id);
		ac_std.setAttribute("oncommand", "TeamFound.onSelectAsDefault(" + ac_id + ", \"tfselectasdefault"+ac_id+"\");", false);


		// foreach sub-category
		if( ac_childnodes != false)
		{
			for( var i = 0; i <  ac_childnodes.length; i++)
			{
				if( ac_childnodes.item(i).nodeName == "category")
				{
					// category values
					var ac_catid = ac_childnodes.item(i).getElementsByTagName("id")[0].firstChild.nodeValue;
					var ac_catname = ac_childnodes.item(i).getElementsByTagName("name")[0].firstChild.nodeValue;
					//var catdesc = childnodes.item(i).getElementsByTagName("description")[0].firstChild.nodeValue;

					var ac_item1 = document.createElement("menu");
					ac_item1.setAttribute("label", ac_catname);
					ac_menup.appendChild(ac_item1);

					var ac_item2 = document.createElement("menupopup");
					ac_item1.appendChild(ac_item2);

					
					if( ac_childnodes.item(i).getElementsByTagName("subcategories")[0])
					{	// more subcategories exist
						TeamFound.addCategory( ac_childnodes.item(i).getElementsByTagName("subcategories")[0].childNodes, ac_item2, ac_childnodes.item(i).getElementsByTagName("name")[0].firstChild.nodeValue, ac_catid);
					}
					else
					{	// no more subcategories
						TeamFound.addCategory( false, ac_item2, ac_childnodes.item(i).getElementsByTagName("name")[0].firstChild.nodeValue, ac_catid);
					}
				}
			}

			ac_menup.appendChild(ac_sep);
		}

		ac_menup.appendChild(ac_s);
		ac_menup.appendChild(ac_a);
		ac_menup.appendChild(ac_std);

	},


	// TeamFound suche beendet
	onTeamFoundSearchFinished: function()
	{
		// nur etwas machen falls der request schon fertig ist
		if( xmlhttptf.readyState == 4)  
		{
			var doc = window._content.document;
			var tfrone = doc.getElementById("teamfound-result-one");

			// HTTP-Request Code auswerten
			if( xmlhttptf.status == 200)
			{	// OK
				if( tfrone != null)
				{
					//tfrone.innerHTML = xmlhttptf.responseText;
					// let's analize the xml
					var tf_found = xmlhttptf.responseXML.getElementsByTagName("teamfound")[0].getElementsByTagName("search")[0].getElementsByTagName("result")[0].getElementsByTagName("found");
					//alert(tf_found.length);
					var tf_html = "";
					if( !tf_found)
					{
						tfrone.innerHTML = "TeamFound - no results found";
						return;
					}
					if( tf_found.length == 0)
					{
						tfrone.innerHTML = "TeamFound - no results found";
						return;

					}

					tf_html = '<b>TeamFound results:</b><br><br>';
						
					for( var i = 0; i < tf_found.length; i++)
					{
						var tf_res_url = tf_found[i].getElementsByTagName("url")[0].firstChild.nodeValue;
						var tf_res_title = tf_found[i].getElementsByTagName("title")[0].firstChild.nodeValue;
						tf_html += '<a href="' + tf_res_url + '">' + tf_res_title + '</a><br><font size="-1" color="#008000">' + tf_res_url + '</font><br><br>';
					}
					tfrone.innerHTML = tf_html;
					
				}
			}
			else
			{
				tfrone.innerHTML = "Error: " + xmlhttptf.status + " - " + xmlhttptf.statusText;
			}
		}
	} // onTeamFoundSearchFinished

}; // TeamFound

// Window-Listener (einmalig initalisierung, dann aktualisierung der URL)
window.addEventListener(
	"focus", 
	function(e) 
	{ 
		TeamFound.onLoad(e); 
	}, 
	true); 


// EOF
