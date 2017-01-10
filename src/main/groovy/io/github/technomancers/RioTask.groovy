package io.github.technomancers

import org.gradle.api.*
import groovy.util.*
import org.hidetake.groovy.ssh.Ssh

class RioTask extends DefaultTask{
	def ssh = org.hidetake.groovy.ssh.Ssh.newService()
	String rioHost

	@tasks.TaskAction
	void connect() {
		if(!project.gradlerio.dryRun){
			rioHost = whichHost()
		}else{
			rioHost = '127.0.0.1'
		}
		project.logger.info("Resolved RoboRIO location to $rioHost")
		configureRioSSH()
	}

	private void configureRioSSH(){
		ssh.settings {
			dryRun = project.gradlerio.dryRun
		}
		ssh.remotes{
			rio {
				host = rioHost
				user = project.gradlerio.robotUser
				password = project.gradlerio.robotPass
				knownHosts = allowAnyHosts
			}
			rioElevated {
				host = rioHost
				user = project.gradlerio.robotElevatedUser
				password = project.gradlerio.robotElevatedPass
				knownHosts = allowAnyHosts
			}
		}
	}

	private String whichHost(){
		if (canConnect(project.gradlerio.rioHostName)){
			return InetAddress.getByName(project.gradlerio.rioHostName).getHostAddress()
		} else if (canConnect(project.gradlerio.rioStaticIP)){
			return project.gradlerio.rioStaticIP
		}else if (canConnect(project.gradlerio.rioIP)){
			return project.gradlerio.rioIP
		}else{
			throw new artifacts.PublishException('Could not find the correct host to connect to.')
		}
	}

	private Boolean canConnect(String host) {
		project.logger.info("Trying to connect to $host")
		try {
			Socket s = new Socket()
			s.connect(new InetSocketAddress(host, 22), project.gradlerio.timeout * 1000)
			return true
		} catch (Exception ex) {
			project.logger.debug(ex.getMessage())
			return false
		}
	}
}
