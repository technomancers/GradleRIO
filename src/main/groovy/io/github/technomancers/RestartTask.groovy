package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class RestartTask extends RioTask{
	@tasks.TaskAction
	void restart(){
		project.logger.info('Restarting RoboRIO\'s code.')
		ssh.run {
			session(ssh.remotes.rioElevated){
				execute project.gradlerio.killNetConsoleCommand, ignoreError: true, timeoutSec: project.gradlerio.timeout
			}
		}
		ssh.run {
			session(ssh.remotes.rio){
				execute project.gradlerio.robotKillCommand, ignoreError: true, timeoutSec: project.gradlerio.timeout
			}
		}
		ssh.run {
			session(ssh.remotes.rio){
				execute project.gradlerio.finalCommand, timeoutSec: project.gradlerio.timeout
			}
		}
	}
}
