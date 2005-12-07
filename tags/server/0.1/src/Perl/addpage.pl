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

my $filename;

print "Content-type: text/html\n\n";

print "<html><body>";
print "Getting: $url<br>";
`wget -E -P download $url`;
print $url;
if ( $url=~/http:\/\/.+\/(.+)/ ) 
   { 
      #print $1;
      $filename = $1;
      
      print "Indiziere: $url<br>";
      `java -cp "teamfound.jar" Index.TeamFoundIndexer download/$filename* $url`;
      
      print "<br>Loesche: $filename<br>";
      `rm download/$filename*`;

   }
else
{
   print "Loesche: $url<br>";
   `rm download/*`;
}; 
print '<br><br><a href="javascript:history.back();"><< go back</a>';
print "</body></html>";  
