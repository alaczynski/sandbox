package com.sandbox

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.HierarchicalStreamWriter
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver
import com.thoughtworks.xstream.io.json.JsonWriter
import org.skyscreamer.jsonassert.JSONAssert
import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat

class XstreamTest extends Specification {

    def "marshalling"() {
        given:
        def xstream = new XStream()
        def person = new Person('John', 'Smith')
        xstream.alias('person', Person)

        when:
        def xml = xstream.toXML(person)

        then:
        assertThat(xml).isXmlEqualTo('''
        <person>
            <firstname>John</firstname>
            <lastname>Smith</lastname>
        </person>
        ''')
    }

    // default constructor is not needed, final fields supported
    def "unmarshalling"() {
        given:
        def xml = '''\
        <person>
            <firstname>John</firstname>
            <lastname>Smith</lastname>
        </person>
        '''
        def xstream = new XStream()
        xstream.alias('person', Person)

        when:
        def person = xstream.fromXML(xml) as Person

        then:
        person.firstname == 'John'
        person.lastname == 'Smith'
    }

    def 'marshalling to json'() {
        given:
        def xstream = new XStream(new JsonHierarchicalStreamDriver() {
            HierarchicalStreamWriter createWriter(Writer writer) {
                return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
            }
        })
        def person = new Person('John', 'Smith')
        xstream.alias('person', Person)

        when:
        def json = xstream.toXML(person)

        then:
        JSONAssert.assertEquals(json, '''
        {
          "firstname": "John",
          "lastname": "Smith"
        }
        ''', false)
    }
}
