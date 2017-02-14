package Parser;
import java.util.*;

public class Scanner
{

	
	private FileReader fReader;
	public int sym;
	public int val;
	public int id;
	public static HashMap<Integer, String> stringTable;
	private int stringTablePtr;

	public static final int errorToken = 0;
	public static final int timesToken = 1;
	public static final int divToken = 2;

	public static final int plusToken = 11;
	public static final int minusToken = 12;

	public static final int eqlToken = 20;
	public static final int neqToken = 21;
	public static final int lssToken = 22;
	public static final int geqToken = 23;
	public static final int leqToken = 24;
	public static final int gtrToken = 25;

	public static final int periodToken = 30;
	public static final int commaToken = 31;
	public static final int openbracketToken = 32;
	public static final int closebracketToken = 34;
	public static final int closeparenToken = 35;

	public static final int becomesToken = 40;
	public static final int thenToken = 41;
	public static final int doToken = 42;

	public static final int openparenToken = 50;

	public static final int number = 60;
	public static final int ident = 61;

	public static final int semiToken = 70;

	public static final int endToken = 80;
	public static final int odToken = 81;
	public static final int fiToken = 82;

	public static final int elseToken = 90;

	public static final int letToken = 100;
	public static final int callToken = 101;
	public static final int ifToken = 102;
	public static final int whileToken = 103;
	public static final int returnToken = 104;

	public static final int varToken = 110;
	public static final int arrToken = 111;
	public static final int funcToken = 112;
	public static final int procToken = 113;

	public static final int beginToken = 150;
	public static final int mainToken = 200;
	public static final int eofToken = 255;


	public Scanner(String fileName)
	{
		fReader = new FileReader(fileName);
		this.sym = -1;
		this.val = -1;
		this.id = -1;
		this.stringTablePtr = 256;
		Scanner.stringTable = new HashMap<Integer, String>();
		Scanner.stringTable.put(41, "then");
		Scanner.stringTable.put(42,"do");
		Scanner.stringTable.put(81,"od");
		Scanner.stringTable.put(82,"fi");
		Scanner.stringTable.put(90,"else");
		Scanner.stringTable.put(100,"let");
		Scanner.stringTable.put(101,"call");
		Scanner.stringTable.put(102,"if");
		Scanner.stringTable.put(103,"while");
		Scanner.stringTable.put(104,"return");
		Scanner.stringTable.put(110,"var");
		Scanner.stringTable.put(111,"array");
		Scanner.stringTable.put(112,"function");
		Scanner.stringTable.put(113,"procedure");
		Scanner.stringTable.put(200,"main");

	}

	public void Next()
	{
		//fReader.Next();
		while(fReader.sym == ' ' || fReader.sym == '\n')
			fReader.Next();

		switch(fReader.sym)
		{
			case (char) 0 	: 	this.sym = errorToken;
						fReader.Next();
						return;
			case (char) -1 	: 	this.sym = eofToken;
						fReader.Next();
						return;
			case '*'    	: 	this.sym = timesToken;
						fReader.Next();
						return;
			case '/' 	: 	this.sym = divToken;
						fReader.Next();
						return;
			case '+'        : 	this.sym = plusToken;
						fReader.Next();
						return;
			case '-'        : 	this.sym = minusToken;
						fReader.Next();
						return;
			case '.'        : 	this.sym = periodToken;
						fReader.Next();
						return;
			case ','        : 	this.sym = commaToken;
						fReader.Next();
						return;
			case '['        : 	this.sym = openbracketToken;
						fReader.Next();
						return;
			case ']'        : 	this.sym = closebracketToken;
						fReader.Next();
						return;
			case '('        : 	this.sym = openparenToken;
						fReader.Next();
						return;
			case ')'        : 	this.sym = closeparenToken;
						fReader.Next();
						return;
			case ';'        : 	this.sym = semiToken;
						fReader.Next();
						return;
			case '{'        :	this.sym = beginToken;
						fReader.Next();
						return;
			case '}'        : 	this.sym = endToken;
						fReader.Next();
						return;
			default		: 	break;

		}

		if(Character.isDigit(fReader.sym))
		{
			StringBuffer buff = new StringBuffer();
			while(Character.isDigit(fReader.sym))
			{
				buff.append(fReader.sym);
				fReader.Next();
			}				
			this.val = Integer.parseInt(buff.toString());
			this.sym = number;

		}

		else if(Character.isLetter(fReader.sym))
		{
			//System.out.println("Letter enter: "+ fReader.sym);
			this.sym = ident;
			StringBuffer buff = new StringBuffer();
			while(Character.isDigit(fReader.sym) || Character.isAlphabetic(fReader.sym))
			{   
				buff.append(fReader.sym);
				fReader.Next();
			} 
			boolean flag =false;
			String temp = buff.toString();
			for(int key : Scanner.stringTable.keySet()) {
				if(Scanner.stringTable.get(key).equals(temp)) {
					flag =true;
					this.id = key;
					if(key<256)
					    this.sym=key;
				}
			}
			if(flag==false)
			{
				this.id=stringTablePtr;
				stringTablePtr++;
				Scanner.stringTable.put(id,buff.toString());

			}


			//this.sym = ident;
			//System.out.println("Letter Exit: " + fReader.sym);


		}

		else if(fReader.sym == '>'|| fReader.sym == '<' || fReader.sym=='='|| fReader.sym=='!')
		{
			switch(fReader.sym) {
				case '=':
					fReader.Next();
					if(fReader.sym == '=') { // ==
						fReader.Next();
						this.sym =  eqlToken;
					} else { // = ... syntax error
						fReader.Error("syntax error: undefined token: '='");
					}
					return;
				case '!': 
					fReader.Next();
					if(fReader.sym == '=') { // != 
						fReader.Next();
						this.sym = neqToken;
					} else { // ! ... syntax error
						fReader.Error("syntax error: undefined token: '!'");
					}
					return;
				case '>': 
					fReader.Next();
					if(fReader.sym == '=') { // >=
						fReader.Next();
						this.sym = geqToken;
					} else { // >
						this.sym = gtrToken;
					}
					return;
				case '<':
					fReader.Next();
					if(fReader.sym == '=') { // <=
						fReader.Next();
						this.sym = leqToken;
					} else if (fReader.sym == '-') { // <-
						fReader.Next();
						this.sym = becomesToken;
					} else { // <
						this.sym = lssToken;
					}
					return;
				default: fReader.Error("Disaster Alert");
			}

		}
		else
		{
			fReader.Error("Unrecognized Character");
		}




	}

	public void Error(String errorMsg)
	{
		if(this.sym==61)
			System.out.println("Error " +stringTable.get(this.id));
		else
			System.out.println("Error " + sym); 
		fReader.Error(errorMsg);
		
	}

	public String Id2String(int id)
	{
		String name="";
		for(int key : Scanner.stringTable.keySet())
		{
			if(key==id)
			{
				name = Scanner.stringTable.get(key);
				break;
			}
		}
		
		return name;
	}

	public int String2Id(String name)
	{
		int id=-1;
		for(int key: Scanner.stringTable.keySet())
		{
			if(Scanner.stringTable.get(key).equals(name))
			{
				id=key;
				break;
			}
		}
		
		return id;

	}

/*	private InitializeSymbolTable()
	{

	}
*/


}
