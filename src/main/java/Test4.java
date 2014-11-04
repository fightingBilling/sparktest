import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.util.Shell.ShellCommandExecutor;

public class Test4 {

	public static void main(String[] args) {
		ShellCommandExecutor shExec = null;
		try {
			String[] command = { "bash", "/tmp/tmp.sh" };
			shExec = new ShellCommandExecutor(command, new File("/tmp"), new HashMap<String, String>()); // sanitized
			shExec.execute();
		} catch (IOException e) {
			int exitCode = shExec.getExitCode();
			System.out.println("Exit code from container " + " is : " + exitCode);
			System.out.println(e.getMessage());
		} finally {
			; //
		}
	}
}
