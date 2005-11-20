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
*/

var TeamFound = 
{
	// Initialisierung
	onLoad: function() 
	{
		this.initialized = true;
	}, // onLoad

	// Informations-Dialog soll angezeigt werden
	onInfo: function() 
	{
		content.location = "http://teamfound.berlios.de";
		if(this.initialized)
			alert("TeamFound - Ein Projekt der TU-Berlin\n" +
			"Copyright (c) 2005 Jan Kechel\n" +
			"Lizenz: GNU General Public License");
	}, // onInfo

	// Eine Suche soll durchgefuehrt werden
	onSearch: function()
	{
		// Text-feld auslesen
		var key = document.getElementById("tf-ml1");

		// Vorerst einfach die vom server erzeugte url anzeigen ohne weitere bearbeitung
		content.location = "http://thor/tf/search.pl?keyword=" + key.value;
	}, // onSearch

	// Eine neue Seite soll dem Index hinzugefuegt werden
	onAddPage: function() {
		// hinzuzufuegende url (globale variable)
		addpageurl = content.document.URL;

		// server-adresse zum hinzufuegen neuer seiten
		var url = "http://thor/tf/addpage.pl?url=" + addpageurl;

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
	} // onAddPageFinished
}; // TeamFound

// Window-Listener der onLoad aufruft sobald die TeamFound Extension geladen wurde
window.addEventListener(
	"load", 
	function(e) 
	{ 
		TeamFound.onLoad(e); 
	}, 
	false); 

