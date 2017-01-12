package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class MakeNetConsoleHostTask extends DefaultTask{
	@tasks.OutputFile
	File file = new File("${temporaryDir.path}/${project.gradlerio.netConsoleHostFileName}")

	@tasks.TaskAction
	void make(){
		exportToTemp("launch/netconsole-host", "${project.gradlerio.netConsoleHostFileName}")
	}
	
	private void exportToTemp(String resource, String filename){
		def instream = GradleRIO.class.getClassLoader().getResourceAsStream(resource)
    def fos = new FileOutputStream(file)
    def out = new BufferedOutputStream(fos)
    out << instream
    out.close()
	}
}
