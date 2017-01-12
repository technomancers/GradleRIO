package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class DeployNetConsoleHostTask extends DefaultTask{
	private File netConsoleHostFile
	def sshService

	@tasks.SkipWhenEmpty
	@tasks.InputFile
	@tasks.PathSensitive(tasks.PathSensitivity.NONE)
	public getNetConsoleHostFile(){
		return this.netConsoleHostFile
	}

	public void file(MakeNetConsoleHostTask task){
		this.netConsoleHostFile = task.file;
	}

	public void ssh(RioTask task){
		sshService = task.ssh
	}

	@tasks.TaskAction
	void deploy(){
		sshService.run{
			session(sshService.remotes.rioElevated){
				put from: netConsoleHostFile, into: "${project.gradlerio.netConsoleHostLocation}"
			}
		}
		sshService.run {
			session(sshService.remotes.rioElevated){
				execute "chmod +x ${project.gradlerio.netConsoleHostLocation}${project.gradlerio.netConsoleHostFileName}", timeoutSec: project.gradlerio.timeout
			}
		}
	}
}
