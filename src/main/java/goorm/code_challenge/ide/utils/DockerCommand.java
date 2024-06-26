package goorm.code_challenge.ide.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DockerCommand {

	//로컬 버전
	public static List<String> javaCommand(File directory) {
		String containerDirectory = "/app";
		return Arrays.asList(
			"docker", "run", "-i", "--rm",
			"-v", "/tmp" + ":/app",
			"openjdk:17",
			"java", "-cp", "/app/" + directory.toString().replace("/tmp", ""), "Main"
		);
	}

	public static  List<String> pythonCommand(File directory){
		return Arrays.asList(
			"docker", "run", "-i", "--rm",
			"-v", "/tmp"+":/app",
			"python:3.9",
			"python", "/app/" + directory.toString().replace("/tmp", "")+ "/" +  "script.py"
		);
	}


	public static List<String> javaScriptCommand(File directory) {
		return Arrays.asList(
			"docker", "run", "-i", "--rm",
			"-v", "/tmp" + ":/app",
			"node:alpine",
			"node", "/app/" + directory.toString().replace("/tmp", "") + "/" + "script.js"
		);
	}


	// 해당 디랙토리 컴파일
	// 로컬 버전
	public static List<String> compileCommand(File sourceFile, String tempDirPath) {
		return Arrays.asList(
			"docker", "run", "--rm",
			"-v", "/tmp" + "/" +
				tempDirPath.toString().replace("/tmp", "") +
				":/app",
			"-w", "/app",
			"openjdk:17",
			"javac", sourceFile.getName()
		);
	}


}
