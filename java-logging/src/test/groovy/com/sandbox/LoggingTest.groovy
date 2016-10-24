package com.sandbox

import spock.lang.Specification

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger

class LoggingTest extends Specification {

    def "logger hierarchy"() {
        expect:
        Logger.getLogger("").getParent() == null
        Logger.getLogger("").getName() == ""

        Logger.getLogger("a").getParent() == Logger.getLogger("")
        Logger.getLogger("a.b").getParent() == Logger.getLogger("a")
    }

    def 'handler'() {
        expect:
        Logger.getLogger("").getHandlers().size() == 1
        ConsoleHandler rootConsoleHandler = Logger.getLogger("").getHandlers()[0] as ConsoleHandler
        Logger.getLogger("").getLevel() == Level.INFO
        rootConsoleHandler.getLevel() == Level.INFO
    }
}
