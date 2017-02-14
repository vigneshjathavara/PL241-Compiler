package Parser;
import java.io.*;


public class FileReader
{
	public char sym;
	private BufferedReader buffer;
	private int index;
	private String line;
	int currentLineNumber;

	public void Next()
	{
		if(index == -1)
			NextLine();

		if(line == null)
		{
			this.sym = (char) -1;
			return;
		}


		this.sym = line.charAt(index); 
		index++;
		

		if(index == line.length())
			index = -1;
	}

	private void NextLine()
	{
		index = 0;
		try{
			line = this.buffer.readLine();
			if(line!=null)
				line = line.trim();
			currentLineNumber++;
			while(line!=null && (line.isEmpty() || line.matches("^(#|//).*")))
				{
				line = this.buffer.readLine().trim();
				currentLineNumber++;
				}
		} catch (IOException e) {
			System.out.println(e);
		}

		if(line == null)
			return;

		line = line + "\n";
	}

	public void Error(String errorMsg)
	{
		System.out.println(errorMsg + " -> at LineNo:" + currentLineNumber + "  Position:" + index + "  At Character:" + this.sym);
		this.sym = (char)0;
		throw new RuntimeException(errorMsg);
	}

	public FileReader(String fileName)
	{
		this.sym = ' ';
		this.index=-1;
		currentLineNumber=0;
		try {
			InputStream in = new FileInputStream(fileName);
			InputStreamReader reader = new InputStreamReader(in);
			this.buffer = new BufferedReader(reader);
		} catch (FileNotFoundException e) {
			System.out.println("Exception Encountered :" + e);
		}
	}
}
