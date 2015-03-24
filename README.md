# i542

Demonstrates [IMMUTANT-542](https://issues.jboss.org/browse/IMMUTANT-542)

## Usage

The following steps include cloning this project, downloading
WildFly 8.2, creating a test user, downloading Leiningen, and finally
running the test.

    $ git clone https://github.com/jcrossley3/i542.git
    $ cd i542
    $ wget http://download.jboss.org/wildfly/8.2.0.Final/wildfly-8.2.0.Final.zip
    $ unzip wildfly-8.2.0.Final.zip
    $ export WILDFLY_HOME=wildfly-8.2.0.Final
    $ ${WILDFLY_HOME}/bin/add-user.sh --silent -a -u 'testuser' -p 'testuser' -g 'guest'
    $ wget --no-check-certificate https://raw.github.com/technomancy/leiningen/2.5.1/bin/lein
    $ chmod +x lein
    $ ./lein test

The test sends 100 messages (0-99), and then asserts that it receives
them by opening a new Session to receive each one. Usually, the test
will appear to hang after the 72nd message, but in fact, each of the
last ~25 messages will take ~30 seconds to receive. Occasionally, the
test will run very quickly as each message only takes a few ms to
receive.

The test, called `publish-and-receive`, is in `test/i542/core_test.clj`
