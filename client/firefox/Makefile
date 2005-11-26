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
	mkdir build/chrome
	cd src; jar -cvf ../build/chrome/teamfound.jar content/overlay.* skin/*.png skin/overlay.css content/settings.xul
	cd build; zip -r ../teamfound-firefox-1.5-extension-0.4-interface-m1.xpi *

