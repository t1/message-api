# too bad that this is not as simple as it could be

# set the prepareRelease property to disable the non-prepare profile
mvn -DprepareRelease clean release:prepare

# put the password into system properties, so GPG doesn't block
read -p "please enter the GPG passphrase: " -s PWD
echo .
mvn -Dgpg.passphrase="$PWD" -Darguments="-Dgpg.passphrase=$PWD" release:perform
