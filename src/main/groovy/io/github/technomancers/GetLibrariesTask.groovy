package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class GetLibrariesTask extends DefaultTask{
	private file.FileCollection zipFiles = project.files()
	
	@tasks.OutputDirectory
	File libDir = project.file("${temporaryDir.path}")

	@tasks.SkipWhenEmpty
	@tasks.InputFiles
	@tasks.PathSensitive(tasks.PathSensitivity.NONE)
	public file.FileCollection getZipFiles() {
		return this.zipFiles
	}

	public void zips(file.FileCollection zipFiles){
		this.zipFiles = this.zipFiles.plus(zipFiles)
	}

	@tasks.TaskAction
	void get(){
		project.copy{
			into libDir
			zipFiles.each { 
				from (project.zipTree(it).matching { 
					exclude 'include/'
					exclude '**/*.MF'
					exclude '**/*.a'
				}.files)
			}
		}
	}
}
