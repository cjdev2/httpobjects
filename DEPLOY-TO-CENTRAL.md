# Deploy to Maven Central

**tldr; ```mvn -P release-central clean deploy```**

If you are a maintainer, you can easily deploy new
versions of this project to Maven Central.

## Handy Script

Use the awesome ```deploy-to-central.sh``` script if you
follow the environment variable conventions described
below.

## Some Detailed Instructions

Before you start, you need permissions to deploy to the
*org.httpobjects* groupId in Maven Central.
[Start with this page](http://central.sonatype.org/pages/apache-maven.html)
for help.

You'll need a GPG key. Basically, run ```gpg --gen-key``` then
publish your key with 
```gpg --send-keys <keyname> --keyserver hkp://pool.sks-keyservers.net:80```
The link above points to more detail if you need it.

You'll need a *settings.xml* file in your *~/.m2* directory.
It needs to specify your
Maven Central username and pasword, the gpg key name, and passphrase for your
keyring.  It is a good idea to inerpolate those from the
environment so you don't end up with them on disk or in
your shell history.  Ex:

```xml
<?xml version="1.0"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <!-- Maven Central repo username and interpolated password -->
  <servers>
    <server>
      <id>ossrh</id>
      <username>aztecrex</username>
      <password>${env.MAVEN_CENTRAL_DEPLOY_PASSWORD}</password>
    </server>
    ...
  </servers>
  <profiles>
    <profile>
      <id>my-awesome-profile</id>
      <!-- the keyame and interpolated passphrase -->
      <properties>
        <gpg.keyname>DDA4E618</gpg.keyname>
        <gpg.passphrase>${env.GPG_PASSPHRASE}</gpg.passphrase>
      </properties>
      ...
     </profile>
     ...
  </profiles>
  <activeProfiles>
    <activeProfile>my-awesome-profile</activeProfile>
  </activeProfiles>
  ...
</settings>

````

With the above in place you can just run
```deploy-to-central.sh``` and it will prompt you for
your password and passphrase.


