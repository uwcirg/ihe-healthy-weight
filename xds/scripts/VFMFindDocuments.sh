#!/bin/sh
#
# Wrapper to the VFMFindDocuments servlet.  Use curl to make the request.
# This wrapper can then easily be "gridified" via Opal Toolkit.
#
# As these values don't change often, simplify the bash scripting by storing
# the urlencoded values.

# $patID = urlencode ("1950001^^^%261.3.6.1.4.1.21367.2005.3.7%26ISO")
#patID="1950001%5E%5E%5E"
patID="1950001%5E%5E%5E%25261.3.6.1.4.1.21367.2005.3.7%2526ISO"

# $targetUrl = urlencode ("http://xds-ibm.lgs.com:9081/IBMXDSRegistry/XDSa/Registry");
targetUrl="http%3A%2F%2Fxds-ibm.lgs.com%3A9081%2FIBMXDSRegistry%2FXDSa%2FRegistry"

# $atnaUrl = urlencode ("xds-ibm.lgs.com:1514");
atnaUrl="xds-ibm.lgs.com%3A1514"

# $vfmUrl = urlencode ("http://72.214.26.5:8080/VFMImmunizationCareRecord");
vfmUrl="http%3A%2F%2F72.214.26.5%3A8080%2FVFMImmunizationCareRecord"

#$vfmCardUrl = urlencode ("http://72.214.26.5:8080/IZCertExport");
vfmCardUrl="http%3A%2F%2F72.214.26.5%3A8080%2FIZCertExport"

curl -s "http://localhost:8080/xds/servlet/XDSController?reqtype=vfmFindDocsReq&patientId=${patID}&timeSlot=creationTime&startTime=20081008000000&stopTime=20081010000000&targetUrl=${targetUrl}&vfmUrl=${vfmUrl}&vfmCardUrl=${vfmCardUrl}&atnaUrl=${atnaUrl}&usePH=0&useTls=0"

