package org.asl.hocon

import com.typesafe.config.ConfigFactory
import spock.lang.Specification

class ConfigTest extends Specification {

    def 'test'() {
        setup:
        def config = ConfigFactory.parseString("""
            some {
                namespace {
                    users = [
                        {
                            name = "firstUser"
                            age = 25
                        }
                        {
                            name = "secondUser"
                            age = 35
                        }
                    ]
                }
            }
            """);

        expect:
        config.getObjectList('some.namespace.users').get(0).get('name').unwrapped() == 'firstUser'
        config.getObjectList('some.namespace.users').get(1).get('name').unwrapped() == 'secondUser'
    }
}
