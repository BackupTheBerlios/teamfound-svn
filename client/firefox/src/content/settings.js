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
*
*	global variables: prefs, initialized in function onLoad
*/

// Settings OK-Button
var TeamFoundSettings =
{
	onSettingsOK: function()
	{
		prefs.setCharPref("settings.serverurl", document.getElementById("tf-settings-server").value);
		return true;
	}, //onSettingsOK

	// Settings Cancel-Button
	onSettingsCancel: function()
	{
		return true;
	}, // onSettingsCancel

	// Settings-Dialog gets loaded
	onSettingsLoad: function()
	{
		prefs = Components.classes["@mozilla.org/preferences-service;1"].
			getService(Components.interfaces.nsIPrefService).
			getBranch("extensions.teamfound."); // this declares the global variable

		// Get the settings and insert em into the text-fields
		var serverurl = prefs.getCharPref("settings.serverurl");
		var servertextbox = document.getElementById("tf-settings-server");
		servertextbox.value = serverurl;
	}, // onSettingsLoad

	// Settings defaults-button
	onSettingsDefault: function()
	{
		// Get the root branch
		var defprefs = Components.classes["@mozilla.org/preferences-service;1"].
			getService(Components.interfaces.nsIPrefBranch).
			getDefaultBranch("extensions.teamfound."); // this is where we decide to get defaults instead of current configuration

		// Get the settings and insert em into the text-fields
		var defurl = defprefs.getCharPref("settings.serverurl");
		var servertextbox = document.getElementById("tf-settings-server");
		servertextbox.value = defurl;
	} // onSettingsDefault
};


