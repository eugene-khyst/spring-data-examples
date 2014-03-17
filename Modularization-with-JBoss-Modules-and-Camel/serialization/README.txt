mvn clean install

rm -r $JBOSS_HOME/modules/org/
rm -r $JBOSS_HOME/modules/com/
rm $JBOSS_HOME/standalone/deployments/*

unzip jboss-modules/target/jboss-modules.jar -d $JBOSS_HOME/modules/
cp client-war/target/client-war.war $JBOSS_HOME/standalone/deployments
cp service-war/target/service-war.war $JBOSS_HOME/standalone/deployments

$JBOSS_HOME/bin/standalone.sh

curl http://localhost:8080/client-war/hello?name=test
curl http://localhost:8080/client-war/goodbye?name=test
