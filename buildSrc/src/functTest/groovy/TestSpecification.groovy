import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * Created by aleksandr on 16.04.16.
 */
@Slf4j
class TestSpecification extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    List<File> pluginClasspath

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')

        def pluginClasspathResource = new File('/Users/aleksandr/github/jpoint2016-gradle/buildSrc/build/createClasspathManifest/plugin-classpath.txt')//getClass().classLoader.findResource("plugin-classpath.txt")
        if (pluginClasspathResource == null) {
            throw new IllegalStateException("Did not find plugin classpath resource, run `testClasses` build task.")
        }

        pluginClasspath = pluginClasspathResource.readLines()
                .collect { new File(it) }
    }

    def "execution of documentation distribution task is up to date"() {
        given:
        def classpathString = pluginClasspath
                .collect { it.absolutePath.replace('\\', '\\\\') } // escape backslashes in Windows paths
                .collect { "'$it'" }
                .join(", ")

        buildFile << """
            buildscript {
              repositories { jcenter() }
              dependencies {
                classpath files($classpathString)
                classpath 'org.asciidoctor:asciidoctor-gradle-plugin:1.5.3'
              }
            }

            apply plugin: 'org.asciidoctor.convert'
            apply plugin: 'ru.jpoint.documentation'

            docs {
              debug = true
            }

            dependencies {
              asciidoctor 'org.asciidoctor:asciidoctorj:1.5.4'
              docs 'org.slf4j:slf4j-api:1.7.2'
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments('documentationDistZip')
                .build()

        then:
        //result.output.contains('Hello world!')
        result.task(":documentationDistZip").outcome == TaskOutcome.UP_TO_DATE
    }
}
