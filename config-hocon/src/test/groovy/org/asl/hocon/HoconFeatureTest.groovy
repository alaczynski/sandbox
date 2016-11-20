package org.asl.hocon

import com.typesafe.config.Config
import com.typesafe.config.ConfigValue
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.typesafe.config.ConfigFactory.parseString

class HoconFeatureTest extends Specification {

    @Rule TemporaryFolder temporaryFolder = new TemporaryFolder()

    def 'json vs hocon'() {
        when:
        def configFromJson = parseString('''
        {
            "foo" : {
                "bar" : 10,
                "baz" : 12
            }
        }
        ''')
        // Drop root braces
        // Drop quotes
        // Use = and omit it before {
        // Remove commas
        def configFromHocon = parseString('''
        foo {
            bar = 10
            baz = 12
        }
        ''')

        then:
        configFromJson == configFromHocon
    }

    /*
    Substitutions are a way of referring to other parts of the configuration tree.
     */
    def 'substitution'() {
        when:
        def config = parseString('''
        standard-timeout = 10ms
        foo.timeout = ${standard-timeout}
        bar.timeout = ${standard-timeout}
        ''').resolve()

        then:
        config.getString('foo.timeout') == '10ms'
        config.getString('bar.timeout') == '10ms'
    }

    def 'duplicate - object values - merge'() {
        when:
        def config = parseString('''
        foo = { a: 42 }
        foo = { b: 43 }
        ''')

        then:
        config.getInt('foo.a') == 42
        config.getInt('foo.b') == 43
    }

    def 'duplicate - simple values - last override'() {
        when:
        def config = parseString('''
        foo = { a : 1, c : 2 }
        foo = { b : 3, c : 4 }
        ''')

        then:
        config.getInt('foo.a') == 1
        config.getInt('foo.b') == 3
        config.getInt('foo.c') == 4
    }

    def 'duplicate - array values - last override'() {
        when:
        def config = parseString('''
        array = [1, 2, 3]
        array = [3, 4, 5]
        ''')

        then:
        config.getIntList('array') == [3, 4, 5]
    }

    def 'concatenation - simple values - concatenated to string'() {
        when:
        def config = parseString('''
        value = a b c
        ''')

        then:
        config.getString('value') == 'a b c'
    }

    def 'concatenation - simple values - with substitution'() {
        when:
        def config = parseString('''
        some-string = a
        concatenated-string = A${some-string}A
        concatenated-string-with-forbidden-characters = ":::"${some-string}":::"
        ''').resolve()

        then:
        config.getString('concatenated-string') == 'AaA'
        config.getString('concatenated-string-with-forbidden-characters') == ':::a:::'
    }

    def 'concatenation - array values - concatenated to new array'() {
        when:
        def config = parseString('''
        array = [1, 2]
        array = ${array} [2, 3]
        ''').resolve()

        then:
        config.getIntList('array') == [1, 2, 2, 3]
    }

    def 'concatenation - object values - objects are merged'() {
        when:
        def config = parseString('''
        generic = { a : 1, b : 2 }
        specific = ${generic} { b : 3, c : 4 }
        ''').resolve()

        then:
        config.getInt('specific.a') == 1
        config.getInt('specific.b') == 3
        config.getInt('specific.c') == 4
    }

    def 'substitution + merging = inheritance'() {
        when:
        def config = parseString('''
        generic = { a = 1 }
        specific = ${generic}
        specific = { b = 2 }
        ''').resolve()

        def configShortWithConcatenation = parseString('''
        generic = { a = 1 }
        specific = ${generic} { b = 2 }
        ''').resolve()

        then:
        config.getInt('specific.a') == 1
        config.getInt('specific.b') == 2
        config == configShortWithConcatenation
    }

    def 'fallback - handles like duplicates'() {
        when:
        def firstConfig = parseString('''
        foo = { a = 1, b = 2 }
        ''')
        def secondConfig = parseString('''
        foo = { b = 22, c = 3 }
        ''')
        def config = firstConfig.withFallback(secondConfig)

        then:
        config.getInt('foo.a') == 1
        config.getInt('foo.b') == 2
        config.getInt('foo.c') == 3
    }

    def 'fallback - circular dependency'() {
        when:
        def firstConfig = parseString('''
        object1 {
          a = ${object2.a}
          b = 2
        }
        ''')
        def secondConfig = parseString('''
        object2 {
          a = 1
          b = ${object1.b}
        }
        ''')
        def config = firstConfig.withFallback(secondConfig).resolve()
        def configWithDifferentOrder = secondConfig.withFallback(firstConfig).resolve()

        then:
        config.getInt('object1.a') == 1
        config.getInt('object1.b') == 2
        config.getObject('object1') == config.getObject('object2')
        config == configWithDifferentOrder
    }


    def 'properties - config to properties'() {
        when:
        def config = parseString('''
        a {
           b1 {
               c = C
           }
           b2 = B2
        }
        ''')
        def configAsMap = configToMap(config)

        then:
        configAsMap == [
                'a.b1.c': 'C',
                'a.b2': 'B2']
    }

    private static Map<String, Object> configToMap(Config config) {
        config.entrySet().collectEntries { Map.Entry<String, ConfigValue> entry ->
            [entry.key, entry.value.unwrapped()]
        } as Map
    }

    def 'nested config'() {
        when:
        def config = parseString('''
        a {
           b {
               c1 = C1
               c2 = C2
           }
        }
        ''')
        def nestedConfig = config.getConfig('a.b')

        then:
        nestedConfig.getString('c1') == 'C1'
        nestedConfig.getString('c2') == 'C2'
    }

    def 'keys as holder for values'() {
        when:
        def config = parseString('''
        classes {
           "com.sandbox.Object1" {
               property1 = 1
           }
           "com.sandbox.Object2" {
           }
           Object3 {
           }
        }
        ''')

        then:
        Map<String, Object> map = config.getValue('classes').unwrapped() as Map
        map.keySet() == ['com.sandbox.Object1', 'com.sandbox.Object2', 'Object3'] as Set
    }

    /*
    include feature merges root object in another file into current object,
    so foo { include "bar.json" } merges keys in bar.json into the object foo
     */
    def 'include file'() {
        given:
        def configOneFile = temporaryFolder.newFile('one.conf')
        configOneFile << '''
        one {
            a = 1
            b = ${two.a}
        }
        '''
        def configOnePath = configOneFile.path.replace('\\', '/')

        when:
        def configTwo = parseString("""
        include file("$configOnePath")

        two.a = 2
        """).resolve()

        then:
        configTwo.getInt('one.a') == 1
        configTwo.getInt('one.b') == 2
        configTwo.getInt('two.a') == 2
    }
}
