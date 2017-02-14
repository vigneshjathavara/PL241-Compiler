package Structures;

class Variable
{

  private String name;
  private int version;

  public Variable(String n)
	{
		this.name = n;
		this.version = 0;
	}

  public Variable(String n, int ver)
	{
		this.name=n;
		this.version = ver;
	}

	
  public String GetName()
	{
		return this.name;
	}
	
 public int GetVersion()
	{
		return version;
	}



}
