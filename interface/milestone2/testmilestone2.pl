#!/usr/bin/perl -w
#	
#	TeamFound, sharing your search results
#	Copyright (C) 2005 Jan Kechel
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

my $command = $cgi->param('command');

print "Content-type: text/xml\n\n";

if( $command =~ /addpage/)
{

#<?xml version="1.0"?>
   print << "EOF";

<response>

   <interface-version>2</interface-version>

   <return-value>0</return-value>
   <return-description>OK</return-description>

   <server>
      <name>TeamFound Testscript Milestone 2</name>
      <version>0.2</version>
   </server>

   <addpage>
      <url>http://blabla.html</url>
   </addpage>
              
</response>
EOF
}

if( $command =~ /search/)
{
#<?xml version="1.0"?>
   print << "EOF";

<response>

   <interface-version>2</interface-version>
   <return-value>0</return-value>
   <return-description>OK</return-description>

   <server>
      <name>TeamFound Testscript Milestone 2</name>
      <version>0.2</version>
   </server>

   <search>

      <keywords>
         <word>xxx</word>
         <word>yyy</word>
      </keywords>

      <result>
         <count>30</count>
         <offset>0</offset>

         <found>
            <url>http://xxx1.html</url>
            <title>Der Titel der Seite 1</title>
            <incategory>15</incategory>
         </found>

         <found>
            <url>http://xxx2.html</url>
            <title>Der Titel der Seite 2</title>
            <incategory>50</incategory>
         </found>

         <found>
            <url>http://xxx3.html</url>
            <title>Der Titel der Seite 3</title>
            <incategory>5</incategory>
            <incategory>3</incategory>
            <incategory>7</incategory>
         </found>
      </result>
   </search>
                                                                                                    
</response>
EOF
}

if( $command =~ /getcategories/ )
{
#<?xml version="1.0"?>
   print << "EOF";
<response>

   <interface-version>2</interface-version>

   <return-value>0</return-value>
   <return-description>OK</return-description>

   <server>
      <name>TeamFound Testscript Milestone 2</name>
      <version>0.2</version>
   </server>

   <getcategories>
      <category>
         <name>name der 1. kategorie</name>
         <description>laengere beschreibung</description>
         <id>0</id>

         <subcategories>
            <category>
               <name>unterkat 1.1</name>
               <id>1</id>
            </category>
            <category>
               <name>unterkat 1.2</name>
               <id>2</id>
            </category>
         </subcategories>
      </category>
      <category>
         <name>name der 2. kategorie</name>
         <description>laengere beschreibung 2</description>
         <id>3</id>
      </category>
   </getcategories>
</response>
EOF
}

if( $command =~ /addcategory/)
{
#<?xml version="1.0"?>
   print << "EOF";
<response>

   <interface-version>2</interface-version>

   <return-value>0</return-value>
   <return-description>OK</return-description>

   <server>
      <name>TeamFound Testscript Milestone 2</name>
      <version>0.2</version>
   </server>

   <addcategory>
      <name>kategoriename</name>
      <gotid>53</gotid>
   </addcategory>

</response>
EOF
}

