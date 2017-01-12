package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class DeployTask extends DeployJarTask{
	@tasks.SkipWhenEmpty
	@tasks.InputFile
	@tasks.PathSensitive(tasks.PathSensitivity.NONE)
	File deployCommand

	public void file(Task task){
		this.deployCommand = task.file
	}
	
	@tasks.TaskAction
	void deploy() {
		sshService.run {
			session(sshService.remotes.rio){
				put from: deployCommand, into: project.gradlerio.commandDeployDir + project.gradlerio.robotCommandFile
			}
		}
		sshService.run {
			session(sshService.remotes.rioElevated){
				execute "chmod +x ${project.gradlerio.commandDeployDir}${project.gradlerio.robotCommandFile}", timeoutSec: project.gradlerio.timeout
			}
		}
	}
}
