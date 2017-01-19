# Pull base image
From tomcat:8-jre8

# Maintainer
MAINTAINER "Arnoud-Deploy-War <a.aarnoudse@emcatron.com">

# Copy to images tomcat path
ADD sample.war /usr/local/tomcat/webapps/
ADD tomcat-users.xml /usr/local/tomcat/conf/