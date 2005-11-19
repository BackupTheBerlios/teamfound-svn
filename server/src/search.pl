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

my $key = $cgi->param('keyword');

print "Content-type: text/html\n\n";

print "Suche: $key<br>";
print `echo "$key" | java -Xmx5m -cp "lucene/lucene-1.4.3.jar:lucene/lucene-demos-1.4.3.jar" org.apache.lucene.demo.SearchFiles`;

