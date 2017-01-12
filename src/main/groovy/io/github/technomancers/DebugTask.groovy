package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class DebugTask extends DeployJarTask{
	@tasks.SkipWhenEmpty
	@tasks.InputFile
	@tasks.PathSensitive(tasks.PathSensitivity.NONE)
	File debugCommand

	@tasks.SkipWhenEmpty
	@tasks.InputFile
	@tasks.PathSensitive(tasks.PathSensitivity.NONE)
	File debugFile

	public void files(MakeDebugFilesTask task){
		this.debugCommand = task.debugCommand
		this.debugFile = task.debugFile
	}

	@tasks.TaskAction
	void deploy(){
		sshService.run {
			session(sshService.remotes.rio){
				put from: debugCommand, into: project.gradlerio.commandDeployDir + project.gradlerio.robotDebugCommandFile
			}
			session(sshService.remotes.rio){
				put from: debugFile, into: project.gradlerio.frcDebugDir + project.gradlerio.frcDebugFile
			}
		}
		sshService.run {
			session(sshService.remotes.rioElevated){
				execute "chmod +x ${project.gradlerio.commandDeployDir}${project.gradlerio.robotDebugCommandFile}", timeoutSec: project.gradlerio.timeout
			}
			session(sshService.remotes.rioElevated){
				execute "chown ${project.gradlerio.robotUser}:${project.gradlerio.robotNIGroup} ${project.gradlerio.frcDebugDir}${project.gradlerio.frcDebugFile}", timeoutSec: project.gradlerio.timeout
			}
		}
	}
}
