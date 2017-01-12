package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class MakeDeployFileTask extends DefaultTask{
	@tasks.OutputFile
	File file = new File("${temporaryDir.path}/${project.gradlerio.robotCommandFile}")

	@tasks.TaskAction
	void make(){
		file.text = "${project.gradlerio.robotCommand}\n"
	}
}
