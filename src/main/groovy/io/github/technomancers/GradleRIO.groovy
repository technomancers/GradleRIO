package io.github.technomancers 

import org.gradle.api.*
import groovy.util.*
import org.apache.commons.lang3.StringUtils

class GradleRIO implements Plugin<Project> {
	private Project _project
	private TaskRIO _taskRIO

	void apply(Project project){
		_project = project
		_taskRIO = new TaskRIO(_project)
		_project.extensions.create('gradlerio', GradleRIOExtension)
		_project.repositories.add(_project.repositories.mavenCentral())
		_project.getConfigurations().maybeCreate('compile')
		_project.getConfigurations().maybeCreate('riolibs')
		_project.afterEvaluate {
			assertConfiguration()
			addWPIDependencies()
			configureJar()
			_taskRIO.configureTasks()
		}
	}

	private void assertConfiguration(){
		def missingProperties = new ArrayList<String>()
		def mustHave = [
			teamNumber: _project.gradlerio.teamNumber,
			mainClass: _project.gradlerio.mainClass,
			jarFileName: _project.gradlerio.jarFileName,
			ntVersion: _project.gradlerio.ntVersion,
			wpiVersion: _project.gradlerio.wpiVersion,
			opencvVersion: _project.gradlerio.opencvVersion,
			cscoreVersion: _project.gradlerio.cscoreVersion,
			wpiBranch: _project.gradlerio.wpiBranch,
			robotUser: _project.gradlerio.robotUser,
			robotElevatedUser: _project.gradlerio.robotElevatedUser,
			netConsoleHostLocation: _project.gradlerio.netConsoleHostLocation,
			javaLocation: _project.gradlerio.javaLocation,
			ldLibraryPath: _project.gradlerio.ldLibraryPath,
			frcDebugFile: _project.gradlerio.frcDebugFile,
			frcDebugDir: _project.gradlerio.frcDebugDir,
			killNetConsoleCommand: _project.gradlerio.killNetConsoleCommand,
			robotKillCommand: _project.gradlerio.robotKillCommand,
			robotNIGroup: _project.gradlerio.robotNIGroup
		]

		mustHave.each{ key, value -> 
			if (isNullOrEmpty(value)){
				missingProperties.add(key)
			}
			if (key == 'teamNumber' && value == '0000'){
				missingProperties.add(key)
			}
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
		_project.dependencies.add('compile', "edu.wpi.first.wpilib.networktables.java:NetworkTables:${_project.gradlerio.ntVersion}:arm")
		_project.dependencies.add('compile', "org.opencv:opencv-java:${_project.gradlerio.opencvVersion}")
		_project.dependencies.add('compile', "edu.wpi.cscore.java:cscore:${_project.gradlerio.cscoreVersion}:arm")
		_project.dependencies.add('compile', "edu.wpi.first.wpilibj:athena:${_project.gradlerio.wpiVersion}")
		_project.dependencies.add('riolibs', "edu.wpi.first.wpilibj:athena-jni:${_project.gradlerio.wpiVersion}")
		_project.dependencies.add('riolibs', "edu.wpi.first.wpilib:athena-runtime:${_project.gradlerio.wpiVersion}@zip")
		_project.dependencies.add('riolibs', "org.opencv:opencv-jni:${_project.gradlerio.opencvVersion}:linux-arm")
		_project.dependencies.add('riolibs', "edu.wpi.cscore.java:cscore:${_project.gradlerio.cscoreVersion}:athena-uberzip@zip")
	}

	private void configureJar(){
		_project.plugins.apply('java')
		_project.jar.baseName = _project.gradlerio.jarFileName
		_project.jar.manifest.attributes('Main-Class': _project.gradlerio.mainClass,
																		'Robot-Class': _project.gradlerio.robotClass,
																		'Classpath': '.')
		_project.jar.from { _project.configurations.compile.collect { it.isDirectory() ? it : _project.zipTree(it) } }
	}

	private static Boolean isNullOrEmpty(String v){
		return (v == null || v == '')
	}
}
