/*
 * Copyright © 2010
 *
 * This file is part of "WS Portal" web application.
 *
 * WS Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WS Portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WS Portal.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unical.inf.wsportal.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.GeneratedFileInfo;

/**
 *
 * @author Simone Spaccarotella {spa.simone@gmail.com}, Carmine Dodaro {carminedodaro@gmail.com}
 */
public class Compiler {

    private ProcessBuilder processBuilder;
    private String workingDirectory;
    private String sourceDirectory;
    private String libDirectory;
    private String outputDirectory;
    private static final String JAVAC_OPTIONS_FILE = "javacOptions";
    private static final String JAVAC_SOURCES_FILE = "javacSources";

    /**
     * Instantiate a class generator that compile the ".java" source files.
     */
    public Compiler() {
        String currentDirectory = new File("").getAbsolutePath();
        workingDirectory = currentDirectory;
        outputDirectory = currentDirectory;
        sourceDirectory = currentDirectory;
        libDirectory = currentDirectory;
    }

    /**
     * Compile the source files.
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public void compile() throws IOException, FileNotFoundException, InterruptedException {
        // create the command in order to compile with "javac"
        ArrayList<String> command = new ArrayList<String>();
        command.add("javac");
        // if javac options file is not empty, add its name as a parameter
        if (fillJavacOptions()) {
            command.add("@" + JAVAC_OPTIONS_FILE);
        }
        if (fillJavacSourceFiles()) {
            command.add("@" + JAVAC_SOURCES_FILE);
        }
        // initialize a process builder, in order to compile the command
        processBuilder = new ProcessBuilder(command);
        /*
         * Any error output generated by subprocesses,
         * subsequently started by this object's start() method, 
         * will be merged with the standard output, so that both can be read
         * using the Process.getInputStream() method.
         */
        processBuilder.redirectErrorStream(true);
        // set the working directory of this new process (is the directory which contains the parameter files
        processBuilder.directory(new File(workingDirectory));
        // start the sub process
        Process process = processBuilder.start();
        process.waitFor();
        process.destroy();
    }

    /**
     * Generate the file that contains the javac options (lib path and the output directory).
     * @return true if the file is not empty
     * @throws IOException
     */
    private boolean fillJavacOptions() throws IOException {
        // says if the file is not empty
        boolean isNotEmpty = false;
        // create an handler for the file
        File javacOptionsHandler = new File(workingDirectory + File.separator + JAVAC_OPTIONS_FILE);
        // create the new file if it doesn't exist
        javacOptionsHandler.createNewFile();
        // get the list of the file names
        ArrayList<String> jarFiles = getFiles(libDirectory);
        // init the output stream toward the file
        PrintWriter out = new PrintWriter(javacOptionsHandler);
        // if the directory contains at least one file
        if (jarFiles.size() > 0) {
            // create a buffer
            StringBuilder javacOptions = new StringBuilder();
            javacOptions.append("-classpath ");
            // concatenates the names of the jar files
            for (String s : jarFiles) {
                javacOptions.append(s + ":");
            }
            // return the same string without the last ":" character
            javacOptions.deleteCharAt(javacOptions.length() - 1);
            // print the javac parameter for the classpath, into the file
            out.println(javacOptions.toString());
            // says if the file is not empty
            isNotEmpty = true;
        }
        // print the javac parameter for the output directory
        out.println("-d " + outputDirectory);
        // close the output stream
        out.close();
        return isNotEmpty;
    }

    /**
     * Generate the file that contains the ".java" source files for javac.
     * @return true if the file is not empty
     * @throws IOException
     */
    private boolean fillJavacSourceFiles() throws IOException {
        // says if the file is not empty
        boolean isNotEmpty = false;
        // create an handler for the file
        File javacSourceFilesHandler = new File(workingDirectory + File.separator + JAVAC_SOURCES_FILE);
        // create the new file if it doesn't exist
        javacSourceFilesHandler.createNewFile();
        // substitute the full stop of the package name, with a slash
        // because the file separator is a slash
        List<String> javaFiles = getFiles(sourceDirectory);
        // if the directory contains file(s)
        if (javaFiles.size() > 0) {
            // open an output stream on the file
            PrintWriter out = new PrintWriter(javacSourceFilesHandler);
            // the file is not empty
            isNotEmpty = true;
            // write the file
            for (String s : javaFiles) {
                out.println(s);
            }
            // close the stream
            out.close();
        }
        // says if the file is not empty
        return isNotEmpty;
    }

    /**
     * Get the children files of the directory with the <code>folderName</code>.
     * @param folderName the name of the directory
     * @return a list of absolute path
     */
    private ArrayList<String> getFiles(String folderName) {
        // initialize an handler for the "folderName" directory
        File folder = new File(folderName);
        // list the folder's files
        File[] files = folder.listFiles();
        ArrayList<String> list = new ArrayList<String>();
        // create a string containing all the child of this directory
        for (File f : files) {
            if (f.getName().endsWith(".java") || f.getName().endsWith(".jar")) {
                // add only the java and jar files
                list.add(f.getAbsolutePath());
            }
        }
        return list;
    }

    /**
     * Set the directory name where the ".class" must be generated.
     * @param outputDir the name of the folder
     */
    public void setOutputDir(String outputDir) {
        if (outputDir != null) {
            File f = new File(outputDir);
            f.mkdirs();
            this.outputDirectory = f.getAbsolutePath();
        }
    }

    /**
     * Get the absolute path of the directory where the ".class" files are stored.
     * @return the absolute folder path
     */
    public String getOutputDir() {
        return outputDirectory;
    }

    /**
     * Set the directory name where the ".jar" files are stored.
     * @param libDir folderName the name of the library folder
     */
    public void setLibDir(String libDir) {
        if (libDir != null) {
            File f = new File(libDir);
            libDirectory = f.getAbsolutePath();
        }
    }

    /**
     * Get the absolute path of the directory where the ".jar" files are stored.
     * @return the absolute folder path
     */
    public String getLibDir() {
        return libDirectory;
    }

    /**
     * Set the directory name where the ".java" files must be generated.
     * @param emitter the java source files generator
     */
    public void setSourceDir(Emitter emitter) {
        GeneratedFileInfo info = emitter.getGeneratedFileInfo();
        String outputDir = emitter.getOutputDir();
        if (outputDir != null) {
            sourceDirectory = outputDir;
        }
        sourceDirectory += File.separator + emitter.getPackageName().replaceAll("[.]", File.separator);
    }

    /**
     * Set the directory name where the ".java" files must be generated.
     * @param sourceDir the folder name
     */
    public void setSourceDir(String sourceDir) {
        if (sourceDir != null) {
            File f = new File(sourceDir);
            sourceDirectory = f.getAbsolutePath();
        }
    }

    /**
     * Get the absolute path of the directory where the ".java" files are stored.
     * @return the absolute folder path
     */
    public String getSourceDir() {
        return sourceDirectory;
    }

    /**
     * Set the name of the working directory where the "javac" options files are stored.
     * @param workDir the name of the "javac" foler
     */
    public void setWorkingDir(String workDir) {
        if (workDir != null) {
            File f = new File(workDir);
            f.mkdirs();
            workingDirectory = f.getAbsolutePath();
        }
    }

    /**
     * Get the absolute path of the working directory where the "javac" files are stored.
     * @return the absolute folder path
     */
    public String getWorkingDir() {
        return workingDirectory;
    }

    /**
     * Clean the source directory.
     * @param subProcessDir
     * @throws IOException
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public void cleanSources(String subProcessDir) throws IOException, FileNotFoundException, InterruptedException {
        ArrayList<String> command = new ArrayList<String>();
        command.add("rm");
        command.add("-R");
        command.add(FoldersNFiles.GENERATED_SERVICES);
        processBuilder.command(command);
        processBuilder.directory(new File(subProcessDir));
        Process process = processBuilder.start();
        process.waitFor();
        process.destroy();
    }
}
