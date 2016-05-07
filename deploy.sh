#!/bin/bash
mkdir -pv ~/.m2
cat > ~/.m2/settings.xml << EOF
<?xml version="1.0" encoding="UTF-8"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings-1.1.0.xsd" xmlns="http://maven.apache.org/SETTINGS/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <servers>
    <server>
      <username>BINTRAY_USER</username>
      <password>BINTRAY_PASS</password>
      <id>bintray-cuzfrog-maven</id>
    </server>
  </servers>
  <profiles>
		<profile>
			<repositories>
				<repository>
					<snapshots>
						<enabled>false</enabled>
					</snapshots>
					<id>bintray-cuzfrog-maven</id>
					<name>bintray</name>
					<url>http://dl.bintray.com/cuzfrog/maven</url>
				</repository>
			</repositories>
			<pluginRepositories>
				<pluginRepository>
					<snapshots>
						<enabled>false</enabled>
					</snapshots>
					<id>bintray-cuzfrog-maven</id>
					<name>bintray-plugins</name>
					<url>http://dl.bintray.com/cuzfrog/maven</url>
				</pluginRepository>
			</pluginRepositories>
			<id>bintray</id>
		</profile>
	</profiles>
	<activeProfiles>
		<activeProfile>bintray</activeProfile>
	</activeProfiles>
</settings>
EOF