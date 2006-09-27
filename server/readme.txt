
to make the build.xml work, create the following link here:

linktotomcat -> /home/xyz/apache-tomcat-5.5.17/

replace /home/xyz with the appropriate directories on your system

go to current/conf to teamfound.properties and
edit the configuration to fit your needs

ant

compiles the teamfoundserver

after compilation create a link to the build directory in you tomcat/webapps directory

ln -s /xyz/build /xzy/tomcat/webapps/MYNAME

replace MYNAME with your desired http adress-directory (http://yoursite/MYNAME/tf)

