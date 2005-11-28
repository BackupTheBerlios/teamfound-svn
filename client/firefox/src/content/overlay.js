/*
*	TeamFound, sharing your search results
*	Copyright (C) 2005 Jan Kechel
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
*/


var TeamFound = 
{
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
		if( this.initialized != true)
		{
			this.initialized = true;
			HistoryList = new Array();
		}

		// display current adress
		document.getElementById("tf-adress-label").value = content.document.URL;

		// remember adress
		TeamFound.myAddToHistory(content.document.URL);

	}, // onLoad

	onSettings: function()
	{
		var setdiag = window.openDialog("chrome://teamfound/content/settings.xul", "TeamFound preferences", "chrome,centerscreen,modal");
		setdiag.focus();

	}, // onSettings

	// Eine Suche soll durchgefuehrt werden
	onSearch: function()
	{
		// Hole Preferences Server
		// Get the root branch
		var prefs = Components.classes["@mozilla.org/preferences-service;1"].
			getService(Components.interfaces.nsIPrefBranch).
			getBranch("extensions.teamfound.");


		// Text-feld auslesen
		var search = TeamFound.myTrim(document.getElementById("tf-ml1").value);

		if( search.length == 0)
		{
			return;
		}

		// Test ob url oder suchwoerter
		// erstmal wenn kein . (punkt) im feld vorkommt dann sind es suchwoerter, sonst url
		if( /\./.test(search) && !/[ \"]/.test(search))
		{
			// wenn protokoll mit angegeben wurde, dann direkt uebernehmen, sonst http vorhaengen
			var mygoto;
			if( /:\/\//.test(search))
			{
				mygoto = search;
			}
			else
			{
				mygoto = "http://" + search;
			}

			// ok, now load page
			content.location = mygoto;
			return;
		}

		// OK, also sind suchwoerter angegeben worden.
		var allwords = search.split(" ");

		if( allwords.length == 0)
		{
			// empty string .. we won't search for this ;-)
			return;
		}

		// remember line
		TeamFound.myAddToHistory(search);

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

		// Ok, template wird gleich geladen, content folgt nach asyncroner antwort
		if( prefs.getIntPref("settings.layout") == 0)
		{	// layout: divide horizontal
			content.location = "chrome://teamfound/skin/search_h.html";
		}
		else
		{	// layout: append external results
			content.location = "chrome://teamfound/skin/search_v.html";
		}


		var pref_serverurl = prefs.getCharPref("settings.serverurl");


		// TeamFound Suche
		var teamfoundurl = pref_serverurl + "/search.pl?keyword=" + searchwithand;
		// Request erstellen (globale variable)
		xmlhttptf= new XMLHttpRequest();
		// Callback Registrieren wenn der Server fertig ist
		xmlhttptf.onreadystatechange = TeamFound.onTeamFoundSearchFinished;
		// Request methode, url und asyncron (true/false) definieren
		xmlhttptf.open("GET", teamfoundurl, true); 
		// Request senden
		xmlhttptf.send(null);


		// Externe suche
		var externurl = "http://www.google.de/search?q=" + search;
		// Request erstellen (globale variable)
		xmlhttpext = new XMLHttpRequest();
		// Callback Registrieren wenn der Server fertig ist
		xmlhttpext.onreadystatechange = TeamFound.onExternSearchFinished;
		// Request methode, url und asyncron (true/false) definieren
		xmlhttpext.open("GET", externurl, true); 
		// Request senden
		xmlhttpext.send(null);

		
	}, // onSearch

	// adds a new line to the history, well, won't soon add double-entries
	myAddToHistory: function(s)
	{
		for( var i = 0; i < HistoryList.length; i++)
		{
			if( s == HistoryList[i])
			{
				return;
			}
		}
		HistoryList.unshift(s);
	}, //myAddToHistory

	// each time the history popup is shown
	onHistoyPopup: function()
	{
		// Get the menu element that we will be working with
		var menu = document.getElementById("tf-historylist");

		// Clean up whatever is currently in the menu
		for(var i=menu.childNodes.length - 1; i>=0; i--) 
		{
			menu.removeChild(menu.childNodes.item(i));
		}

		// Load the search terms into our menu
		for(var i=0; i<HistoryList.length; i++)
		{
			// For each search term, create a menu item element
			var tempItem = null;
			tempItem = document.createElement("menuitem");
			// Set the menuitem element's various attributes:
			// The label will be the search term itself
			// The tooltip will be the text "Dynamic Item #", where # is the number of the item
			tempItem.setAttribute("label", HistoryList[i]);
			tempItem.setAttribute("tooltiptext", "Dynamic Item " + (i+1));

			// Add the item to our menu
			menu.appendChild(tempItem);
		}
	}, // onHistoyPopup

	// Eine neue Seite soll dem Index hinzugefuegt werden
	onAddPage: function() {
		// hinzuzufuegende url (globale variable)
		addpageurl = content.document.URL;

		// Hole Preferences Server

		var prefs = Components.classes["@mozilla.org/preferences-service;1"].
			getService(Components.interfaces.nsIPrefBranch).
			getBranch("extensions.teamfound.");

		var pref_serverurl = prefs.getCharPref("settings.serverurl");

		// server-adresse zum hinzufuegen neuer seiten
		var url = pref_serverurl + "/addpage.pl?url=" + addpageurl;

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
					"Anfrage: '" + url + "'\n" + 
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
		if( xmlhttpext.readyState >= 2)  
		{
			var doc = window._content.document;
			var xtd = doc.getElementById("teamfound-result-two");

			// HTTP-Request Code auswerten
			if( xmlhttpext.status == 200)
			{	// OK
				if( xtd != null)
				{
					// ok, now make google viewable from local,
					// by replacing local references with http://google

					var mygoogle = xmlhttpext.responseText;
					mygoogle = mygoogle.replace(/href=\//g, "href=http://google.de/");
					mygoogle = mygoogle.replace(/href="\//g, "href=\"http://google.de/");
					mygoogle = mygoogle.replace(/action=\//g,"action=http://google.de/");
					mygoogle = mygoogle.replace(/action="\//g,"action=\"http://google.de/");
					mygoogle = mygoogle.replace(/src=\//g,"src=http://google.de/");
					mygoogle = mygoogle.replace(/src="\//g,"src=\"http://google.de/");


					// now load google-result into page-template
					xtd.innerHTML = mygoogle;
				}
			}
			else
			{	// Fehler
				xtd.innerHTML = "Error: " + xmlhttpext.status + " - " + xmlhttpext.statusText;
			}
		}

	}, // onExternSearchFinished

	// TeamFound suche beendet
	onTeamFoundSearchFinished: function()
	{
		// nur etwas machen falls der request schon fertig ist
		if( xmlhttptf.readyState >= 2)  
		{
			var doc = window._content.document;
			var tfrone = doc.getElementById("teamfound-result-one");

			// HTTP-Request Code auswerten
			if( xmlhttptf.status == 200)
			{	// OK
				if( tfrone != null)
				{
					tfrone.innerHTML = xmlhttptf.responseText;
				}
			}
			else
			{
				tfrone.innerHTML = "Error: " + xmlhttptf.status + " - " + xmlhttptf.statusText;
			}
		}
	} // onTeamFoundSearchFinished

}; // TeamFound

// Window-Listener der onLoad aufruft sobald die TeamFound Extension geladen wurde
window.addEventListener(
	"load", 
	function(e) 
	{ 
		TeamFound.onLoad(e); 
	}, 
	true); 

// EOF
