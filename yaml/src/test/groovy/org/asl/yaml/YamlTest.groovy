package org.asl.yaml

import org.yaml.snakeyaml.Yaml
import spock.lang.Specification

class YamlTest extends Specification {

    def 'list'() {
        when:
        def document = '''
        - one
        - two
        '''
        List<String> list = new Yaml().load(document) as List<String>

        then:
        list == ['one', 'two']
    }

    def 'map'() {
        when:
        def document = '''
        key1: value1
        key2: value2
        '''
        Map<String, String> map = new Yaml().load(document) as Map<String, String>

        then:
        map == ['key1': 'value1', 'key2': 'value2']
    }

    def 'types'() {
        when:
        def document = '''
        none: [~, null]
        bool: [true, false, on, off]
        int: 42
        float: 3.14159
        list: [a, b, c]
        map: {k1: 1, k2: 2}
        '''
        Map<String, Object> types = new Yaml().load(document) as Map<String, Object>

        then:
        types.none == [null, null]
        types.bool == [true, false, true, false]
        types.int == 42
        types.float == 3.14159
        types.list == ['a', 'b', 'c']
        types.map == [k1: 1, k2: 2]
    }

}
