package org.asl.hocon

import spock.lang.Specification

import static com.typesafe.config.ConfigFactory.parseResources
import static com.typesafe.config.ConfigFactory.parseString

class HoconFeatureTest extends Specification {

    /******************************************************************************************************************/
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

    /******************************************************************************************************************/
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

    /******************************************************************************************************************/
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

    /******************************************************************************************************************/
    def 'concatenation - simple values - concatenated to string'() {
        when:
        def config = parseString('''
        value = a b c
        ''')

        then:
        config.getString('value') == 'a b c'
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

    /******************************************************************************************************************/
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

    /******************************************************************************************************************/
    def 'fallback - handles like duplicates'() {
        when:
        def firstConfig = parseString('''
        foo = { a = 1, c = 2 }
        ''')
        def secondConfig = parseString('''
        foo = { b = 3, c = 4 }
        ''')
        def config = firstConfig.withFallback(secondConfig)

        then:
        config.getInt('foo.a') == 1
        config.getInt('foo.b') == 3
        config.getInt('foo.c') == 2
    }

    /******************************************************************************************************************/
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

    /******************************************************************************************************************/
    def 'file - merge many files'() {
        when:
        def generic = parseResources('merge-generic.conf')
        def specific = parseResources('merge-specific.conf')
        def merged = specific.withFallback(generic).resolve()

        then:
        merged.getInt('object.a') == 11
        merged.getInt('object.b') == 2
    }
}
