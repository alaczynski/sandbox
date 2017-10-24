package org.asl.groovy

import spock.lang.Specification

import java.util.regex.Matcher

class GroovyFeatureTest extends Specification {

    def 'regex - find operator'() {
        when:
        def matcher = '123' =~ /\d+/

        then:
        matcher instanceof Matcher
        matcher.find() == true
        matcher.matches() == true
       if (!matcher) assert false
    }
}
