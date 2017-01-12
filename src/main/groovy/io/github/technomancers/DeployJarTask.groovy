package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class DeployJarTask extends DefaultTask{
	def sshService

	public void ssh(RioTask task){
		sshService = task.ssh
	}

	@tasks.TaskAction
	void deployJar() {
		sshService.run{
			session(sshService.remotes.rio){
				execute "${project.gradlerio.javaLocation}java -version", timeoutSec: project.gradlerio.timeout
			}
		}
		sshService.run {
			session(sshService.remotes.rio){
				put from: project.jar.archivePath.path, into: "${project.gradlerio.deployDir}${project.gradlerio.jarFileName}.jar"
			}
		}
	}
}
