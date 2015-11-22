package org.asl.spock

import spock.lang.Specification

class SpockTest extends Specification {

    /******************************************************************************************************************/
    def 'shortest - one block - setup'() {
        setup:
        new ArrayList<>()
    }

    def 'shortest - one block - given, alias for setup'() {
        given:
        new ArrayList<>()
    }

    def 'shortest - one block - expect'() {
        expect:
        new ArrayList<>().isEmpty()
    }

    /******************************************************************************************************************/
    def 'none block - this is not spock feature method'() {
        new ArrayList().isEmpty()
    }

    /******************************************************************************************************************/
    def 'when-then vs expect - use expect for purely functional method, no side effects'() {
        expect:
        Math.max(1, 2) == 2
    }

    def 'when-then vs expect - use given-then for function with side effects'() {
        when:
        def list = new ArrayList<>()
        list.add("a")

        then:
        list.size() == 1
    }

    /******************************************************************************************************************/
    def 'expect - can contain only conditions and variable definitions'() {
        expect:
        def expectedMax = 2
        Math.max(1, 2) == expectedMax
    }
}
