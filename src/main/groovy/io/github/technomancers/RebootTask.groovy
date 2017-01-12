package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class RebootTask extends DefaultTask{
	def sshService

	public void ssh(RioTask task){
		sshService = task.ssh
	}

	@tasks.TaskAction
	void reboot(){
		sshService.run{
			session(sshService.remotes.rioElevated){
				execute 'reboot'
			}
		}
	}
}
