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
print << "EOF";
<html>
<head>
<title>TeamFound Search-Result</title>
</head>
<body style="font-family:sans-serif">
EOF

print "<font size=+2 color=\"#000080\" family=\"courier-new\">TeamFound</font><br>searching for '$key' ...";
print `java -cp "teamfound.jar" Search.SearchFiles "$key"`;

print '<br><br><a href="javascript:history.back();"><< go back</a>';
print << "EOF";
</body>
</html>
EOF
