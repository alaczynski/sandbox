package com.sandbox

import spock.lang.Specification

class AsciiTableTest extends Specification {

    // http://stackoverflow.com/questions/5608588/any-java-libraries-for-drawing-ascii-tables
    def "ascii table based on modified https://github.com/JakeWharton/flip-tables"() {
        given:
        def fullTable = """\
            +------------+-----------+
            | first name | last name |
            +------------+-----------+
            | John       | Smith     |
            +------------+-----------+
            | Peter      | Johnson   |
            +------------+-----------+
            """.stripIndent()

        def emptyTable = """\
            +------------+-----------+
            | first name | last name |
            +------------+-----------+
            | (empty)                |
            +------------------------+
            """.stripIndent()

        expect:
        AsciiTable.of(['first name', 'last name'] as String[], [['John', 'Smith'], ['Peter', 'Johnson']] as String[][]) == fullTable
        AsciiTable.of(['first name', 'last name'] as String[], [] as String[][]) == emptyTable
    }
}
