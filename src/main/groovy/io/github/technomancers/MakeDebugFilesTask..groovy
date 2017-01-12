package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class MakeDebugFilesTask extends DefaultTask{
	@tasks.OutputFile
	File debugCommand = new File("${temporaryDir.path}/${project.gradlerio.robotDebugCommandFile}")
	@tasks.OutputFile
	File debugFile = new File("${temporaryDir.path}/${project.gradlerio.frcDebugFile}")

	@tasks.TaskAction
	void make(){
		debugCommand.text = "${project.gradlerio.robotDebugCommand}\n"
		debugFile.text = '# This file is used as a flag to determine if debugging should be used.\n# It is uploaded to the robot when launched in debug mode and should be removed automatically once used.'
	}
}
