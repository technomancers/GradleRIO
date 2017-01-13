package io.github.technomancers;

import org.gradle.api.*
import groovy.util.*

class KillNetConsoleHostTask extends DefaultTask{
	def sshService

	public void ssh(RioTask task){
		sshService = task.ssh
	}

	@tasks.TaskAction
	void kill(){
		sshService.run {
			session(sshService.remotes.rioElevated){
				execute project.gradlerio.killNetConsoleCommand, ignoreError: true, timeoutSec: project.gradlerio.timeout
			}
		}
	}
}
