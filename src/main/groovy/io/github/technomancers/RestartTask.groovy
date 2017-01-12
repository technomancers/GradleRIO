package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class RestartTask extends DefaultTask{
	def sshService

	public void ssh(RioTask task){
		sshService = task.ssh
	}

	@tasks.TaskAction
	void restart(){
		sshService.run {
			session(sshService.remotes.rioElevated){
				execute project.gradlerio.killNetConsoleCommand, ignoreError: true, timeoutSec: project.gradlerio.timeout
			}
		}
		sshService.run {
			session(sshService.remotes.rio){
				execute project.gradlerio.robotKillCommand, ignoreError: true, timeoutSec: project.gradlerio.timeout
			}
		}
		sshService.run {
			session(sshService.remotes.rio){
				execute project.gradlerio.finalCommand, timeoutSec: project.gradlerio.timeout
			}
		}
	}
}
