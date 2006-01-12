#
#	this file just packages the extension into an xpi-file
#
#	(c) 2005 Jan Kechel - GPL
#	22-NOV-2005
#
all:
	rm -rf build
	mkdir build
	cp src/chrome.manifest.xpi build/chrome.manifest
	cp src/install.rdf build
	mkdir build/defaults
	mkdir build/defaults/preferences
	cp src/defaults/preferences/*.js build/defaults/preferences
	mkdir build/chrome
	cd src; jar -cvf ../build/chrome/teamfound.jar content/*.js content/*.xul skin/*.png skin/*.css skin/*.html
	cd build; zip -r ../teamfound-fx-fl-0.8.xpi *

