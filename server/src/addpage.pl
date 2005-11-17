#!/usr/bin/perl -w
#	
#	TeamFound, sharing your search results
#	Copyright (C) 2005 Jan Kechel, Martin Klink
#
#	This program is free software; you can redistribute it and/or
#	modify it under the terms of the GNU General Public License
#	as published by the Free Software Foundation; either version 2
#	of the License, or (at your option) any later version.
#
#	This program is distributed in the hope that it will be useful,
#	but WITHOUT ANY WARRANTY; without even the implied warranty of
#	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#	GNU General Public License for more details.
#
#	You should have received a copy of the GNU General Public License
#	along with this program; if not, write to the Free Software
#	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#

use strict;
use CGI;

my $cgi = new CGI;

my $url = $cgi->param('url');

print "Content-type: text/html\n\n";
print "Getting: $url<br>";
`wget -P download $url`;

print "Indiziere: $url<br>";
`java -Xmx5m -cp "/home/jan/teamfound/lucene/lucene-1.4.3.jar:/home/jan/teamfound/lucene/lucene-demos-1.4.3.jar" org.apache.lucene.demo.IndexFiles $url download`;

print "Loesche: $url<br>";
`rm download/*`;

