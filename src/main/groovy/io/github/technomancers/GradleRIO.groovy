package io.github.technomancers 

import org.gradle.api.*
import groovy.util.*
import org.apache.commons.lang.StringUtils

class GradleRIO implements Plugin<Project> {
    private Project _project

    void apply(Project project){
        _project = project
        _project.extensions.create('gradlerio', GradleRIOExtension)
        _project.repositories.add(_project.repositories.mavenCentral())
        _project.getConfigurations().maybeCreate('compile')
        _project.afterEvaluate {
            assertConfiguration()
            addWPIDependencies()
            configureJar()
        }
    }

    private void assertConfiguration(){
        ArrayList<String> missingProperties = new ArrayList<String>()
        if (_project.gradlerio.teamNumber == null || _project.gradlerio.teamNumber == '' || _project.gradlerio.teamNumber == '0000'){
            println _project.gradlerio.teamNumber
            missingProperties.add('teamNumber')
        }
        if (_project.gradlerio.mainClass == null || _project.gradlerio.mainClass == ''){
            missingProperties.add('mainClass')
        }
        if (_project.gradlerio.jarFileName == null || _project.gradlerio.jarFileName == ''){
            missingProperties.add('jarFileName')
        }
        if(_project.gradlerio.ntVersion == null || _project.gradlerio.ntVersion == ''){
            missingProperties.add('ntVersion')
        }
        if(_project.gradlerio.wpiVersion == null || _project.gradlerio.wpiVersion == ''){
            missingProperties.add('wpiVersion')
        }
        if(_project.gradlerio.wpiBranch == null || _project.gradlerio.wpiBranch == ''){
            missingProperties.add('wpiBranch')
        }
        missingProperties.eachWithIndex{ prop, idx -> missingProperties[idx] = 'gradlerio.' + prop }
        if (missingProperties.size() > 0){
            throw new plugins.PluginInstantiationException('Must supply these properties: ' + StringUtils.join(missingProperties, ','))
        }
    }

    private void addWPIDependencies(){
        _project.repositories.maven {
            it.name = 'WPI'
            it.url = "http://first.wpi.edu/FRC/roborio/maven/${_project.gradlerio.wpiBranch}"
        }
        _project.dependencies.add('compile', "edu.wpi.first.wpilib.networktables.java:NetworkTables:${_project.gradlerio.ntVersion}:desktop")
        _project.dependencies.add('compile', "edu.wpi.first.wpilib.networktables.java:NetworkTables:${_project.gradlerio.ntVersion}:arm")
        _project.dependencies.add('compile', "edu.wpi.first.wpilibj:wpilibJavaFinal:${_project.gradlerio.wpiVersion}")
    }

    private void configureJar(){
        _project.plugins.apply('java')
        _project.jar.baseName = _project.gradlerio.jarFileName
        _project.jar.manifest.attributes('Main-Class': _project.gradlerio.mainClass,
                                        'Robot-Class': _project.gradlerio.robotClass,
                                        'Classpath': '.')
        _project.jar.from { _project.configurations.compile.collect { it.isDirectory() ? it : _project.zipTree(it) } }
    }

    private String whichHost(){
        if (canConnect(_project.gradlerio.rioHostName)){
            return _project.gradlerio.rioHostName
        } else if (canConnect(_project.gradlerio.rioStaticIP)){
            return _project.gradlerio.rioStaticIP
        }else if (canConnect(_project.gradlerio.rioIP)){
            return _project.gradlerio.rioIP
        }else{
            throw new artifacts.PublishException('Could not find the correct host to connect to.')
        }
    }

    private Boolean canConnect(String host) {
        _project.logger.lifecycle("Trying to connect to $host")
        try {
            Socket s = new Socket()
            s.connect(new InetSocketAddress(host, 22), 5000)
            return true
        } catch (Exception ex) {
            return false
        }
    }
}