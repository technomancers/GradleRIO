package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class DeployNetConsoleHostTask extends RioTask{
	private File netConsoleHostFile

	@tasks.SkipWhenEmpty
	@tasks.InputFile
	@tasks.PathSensitive(tasks.PathSensitivity.NONE)
	public getNetConsoleHostFile(){
		return this.netConsoleHostFile
	}

	public void file(Task task){
		this.netConsoleHostFile = task.file;
	}

	@tasks.TaskAction
	void deploy(){
		ssh.run{
			session(ssh.remotes.rioElevated){
				put from: netConsoleHostFile, into: "${project.gradlerio.netConsoleHostLocation}"
			}
		}
		ssh.run {
			session(ssh.remotes.rioElevated){
				execute "chmod +x ${project.gradlerio.netConsoleHostLocation}${project.gradlerio.netConsoleHostFileName}", timeoutSec: project.gradlerio.timeout
			}
		}
	}
}
