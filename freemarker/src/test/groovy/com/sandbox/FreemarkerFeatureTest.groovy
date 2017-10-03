package com.sandbox

import freemarker.template.Configuration
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FreemarkerFeatureTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()
    private File templatesDir

    void setup() {
        templatesDir = temporaryFolder.newFolder('templates')
    }

    def 'import'() {
        given:
        file(templatesDir, templatePath) << """begin <#include "$includePath"> end"""
        file(templatesDir, templateToIncludePath) << 'b'
        Configuration configuration = new Configuration()
        configuration.setDirectoryForTemplateLoading(templatesDir)

        when:
        StringWriter writer = new StringWriter()
        configuration.getTemplate(templatePath).process([:], writer)

        then:
        writer.toString() == 'begin b end'

        where:
        templatePath   | templateToIncludePath | includePath
        'a/aa/aaa.ftl' | 'b/bb/bbb.ftl'        | '/b/bb/bbb.ftl'
        'a/aa/aaa.ftl' | 'b/bb/bbb.ftl'        | '../../b/bb/bbb.ftl'
    }

    private static File file(File parentDir, String child) {
        File file = new File(parentDir, child)
        if (!file.parentFile.exists()) {
            assert file.parentFile.mkdirs()
        }
        file
    }
}
