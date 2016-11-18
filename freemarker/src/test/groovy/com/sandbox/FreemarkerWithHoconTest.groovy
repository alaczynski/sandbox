package com.sandbox

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import freemarker.template.Configuration
import spock.lang.Specification

class FreemarkerWithHoconTest extends Specification {

    def 'resolves variables from unwrapped hocon config'() {
        given:
        Config config = ConfigFactory.parseFile(new File('src/test/resources/config.conf'))
        Map<String, Object> unwrappedConfig = config.root().unwrapped()
        and:
        Configuration configuration = new Configuration()
        configuration.setDirectoryForTemplateLoading(new File('src/test/resources'))
        StringWriter stringWriter = new StringWriter()

        when:
        configuration.getTemplate('template.ftl').process(unwrappedConfig, stringWriter)

        then:
        stringWriter.toString() == new File('src/test/resources/expected.txt').text
    }
}
