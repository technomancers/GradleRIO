package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class DeployLibrariesTask extends DefaultTask{
	private file.FileCollection libFiles = project.files()
	def sshService

	@tasks.SkipWhenEmpty
	@tasks.InputFiles
	@tasks.PathSensitive(tasks.PathSensitivity.NONE)
	public getLibFiles(){
		return this.libFiles
	}

	public void libDir(GetLibrariesTask libDir){
		this.libFiles = project.files(libDir);
	}

	public void ssh(RioTask task){
		sshService = task.ssh
	}

	@tasks.TaskAction
	void deploy(){
		sshService.run{
			session(sshService.remotes.rioElevated){
				put from: libFiles.files, into: "${project.gradlerio.ldLibraryPath}"
			}
		}
		sshService.run{
			session(sshService.remotes.rioElevated){
				execute "chmod -R +x ${project.gradlerio.ldLibraryPath}", timeoutSec: project.gradlerio.timeout
			}
		}
		sshService.run {
			session(sshService.remotes.rioElevated){
				execute "ldconfig", timeoutSec: project.gradlerio.timeout
			}
		}
	}
}
