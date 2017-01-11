package io.github.technomancers

import org.gradle.api.*
import groovy.util.*

class MakeNetConsoleHostTask extends DefaultTask{
	@tasks.OutputFile
	File file

	@tasks.TaskAction
	void make(){
		file = exportToTemp('launch/netconsole-host', 'netconsole-host')
	}
	
	private File exportToTemp(String resource, String filename){
		def instream = GradleRIO.class.getClassLoader().getResourceAsStream(resource)
    File dest = new File("${temporaryDir.path}/")
    File file = new File(dest, filename)
    def fos = new FileOutputStream(file)
    def out = new BufferedOutputStream(fos)
    out << instream
    out.close()
		return new File("${temporaryDir.path}/${filename}")
	}
}
