#
# Copyright (c) 2025, Manchester (http://www.manchester.ac.uk/)
# All rights reserved.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

ARG buildtag

# From Eclipse Temurin
# https://adoptium.net/
# https://hub.docker.com/_/eclipse-temurin
FROM docker.io/library/eclipse-temurin:21

ARG buildtag
ARG buildtime
ARG jarfile

LABEL maintainer "Dave Morris <docker-admin@metagrid.co.uk>"
LABEL buildtag   "${buildtag}"
LABEL buildtime  "${buildtime}"
LABEL vendor     "UK SKA Regional Centre (UKSRC)"
LABEL version    "${buildtag}"
LABEL license    "GPL-3.0-or-later"
LABEL org.opencontainers.image.created  "${buildtime}"
LABEL org.opencontainers.image.vendor   "UK SKA Regional Centre (UKSRC)"
LABEL org.opencontainers.image.version  "${buildtag}"
LABEL org.opencontainers.image.licenses "GPL-3.0-or-later"
LABEL org.opencontainers.image.source   "https://github.com/ivoa/Calycopis-broker"
LABEL org.opencontainers.image.authors  "https://github.com/Zarquan"

#
# Expose the Tomcat webapp port.
EXPOSE 8082/tcp

#
# Install our webapp jar file.
# https://spring.io/guides/gs/spring-boot-docker
RUN mkdir /opt/calycopis

COPY ${jarfile} /opt/calycopis/${jarfile}

#
# Create our run script, needed to use ${jarfile} in our ENTRYPOINT.
COPY <<EOF /opt/calycopis/run.sh
#!/bin/sh
java -jar /opt/calycopis/${jarfile}
EOF

RUN chmod a+x /opt/calycopis/run.sh

ENTRYPOINT ["/opt/calycopis/run.sh"]



