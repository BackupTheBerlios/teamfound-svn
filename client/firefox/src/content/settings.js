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

// Settings OK-Button
function onSettingsOK()
{
	// Get the root branch
	var prefs = Components.classes["@mozilla.org/preferences-service;1"].
	getService(Components.interfaces.nsIPrefBranch);

	// Get the "extensions.teamfound." branch
	prefs = Components.classes["@mozilla.org/preferences-service;1"].
	getService(Components.interfaces.nsIPrefService);
	prefs = prefs.getBranch("extensions.teamfound.");

	var tfs = document.getElementById("tf-settings-server");
	prefs.setCharPref("settings.serverurl", tfs.value);
	return true;
} //onSettingsOK

// Settings Cancel-Button
function onSettingsCancel()
{
	return true;
} // onSettingsCancel

// Settings Load
function onSettingsLoad()
{
	// Get the root branch
	var prefs = Components.classes["@mozilla.org/preferences-service;1"].
	getService(Components.interfaces.nsIPrefBranch);

	// Get the "extensions.teamfound." branch
	prefs = Components.classes["@mozilla.org/preferences-service;1"].
	getService(Components.interfaces.nsIPrefService);
	//prefs = prefs.getBranch("extensions.teamfound.");
	prefs = prefs.getBranch("extensions.teamfound.");

	// Get the settings and insert em into the text-fields
	var test1 = prefs.getCharPref("settings.serverurl");
	var servertextbox = document.getElementById("tf-settings-server");
	servertextbox.value = test1;
} // onSettingsLoad


