import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Coolway on 8/13/2016.
 */
public class CommandListGenerator{

	private static final String commandListPath = "src/main/java/coolway99/discordpokebot/EventHandler.java";
	private static final String outputFile = "commandList.txt";

	public static void main(String... args){
		File commandListFile = new File(commandListPath);
		StringBuffer commandList = new StringBuffer();
		try(Scanner in = new Scanner(commandListFile, "UTF-8")){
			in.useDelimiter("[\r\n]+");
			while(in.hasNext()){
				String next = in.next();
				if(next.matches("^\t{4}case \"\\w*?\":.*?$")){
					commandList.append(next.replaceFirst("^\t{4}case \"", "").replaceFirst("\":.*?$", "\n"));
				} else if(next.matches("^\t}\\s*?((//|/\\*).*?)?$")){
					break;
				}
			}
		} catch(FileNotFoundException e){
			System.err.println("File not found");
			System.exit(0);
		}
		try(PrintWriter out = new PrintWriter(new FileOutputStream(new File(outputFile)))){
			out.print(commandList);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}
