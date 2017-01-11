package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class RebootTask extends RioTask{
	@tasks.TaskAction
	void reboot(){
		ssh.run{
			session(ssh.remotes.rioElevated){
				execute 'reboot'
			}
		}
	}
}
