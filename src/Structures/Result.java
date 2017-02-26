package Structures;

import java.util.ArrayList;

public class Result
{
	public enum Kind{ CONSTANT, VARIABLE, INSTRUCTION, FUNCTION, ARRAY, FRAME_POINTER, BASE_ADDRESS, BOOLEAN}

	int value;//if constant 
	String varName; // if variable
	int ssa; //if variable
	int instructionId; //if instruction 	
	int blockId;
	Kind kind;
	boolean flag;
	ArrayList<Result> dims;
	int register;


	public int getRegister() {
		return register;
	}


	public void setRegister(int register) {
		this.register = register;
	}


	public Result()
	{
		dims = new ArrayList<Result>();
		this.register = -1;
	}


	public Result (Result R)
	{
		this.kind = R.kind;
		this.value=R.value;
		this.varName=R.varName;
		this.ssa=R.ssa;
		this.instructionId=R.instructionId;	
		dims = new ArrayList<Result>();
		if(R.dims!=null)
		{
			for(Result key : R.dims)
			{	  
				this.dims.add(key);
			}
		}
		this.register = -1;
	}

	public Result(Kind k, int v)
	{
		switch(k)
		{

		case CONSTANT:  	this.kind=k;
		this.value=v;
		break;

		case INSTRUCTION:	this.kind=k;
		this.instructionId=v;
		break;

		default: break;
		}
		this.register = -1;
	}

	public Result(String name, int ss)
	{
		this.kind = Kind.VARIABLE;
		this.varName =name;
		this.ssa = ss;
		this.register = -1;
	}

	public Result(boolean f)
	{
		this.kind = Kind.BOOLEAN;
		this.flag =f;
		this.register = -1;
	}

	public Result(String name, ArrayList<Result> d)
	{
		this.kind = Kind.ARRAY;
		this.varName = name;
		this.dims = new ArrayList<Result>();
		for(Result key : d)
		{	  
			this.dims.add(key);
		}
		this.register = -1;
	}


	public Result(Kind k)
	{
		this.kind = Kind.FRAME_POINTER;
		this.register = -1;
	}

	public Result(String name)
	{
		this.varName = name;
		this.kind = Kind.BASE_ADDRESS;
		this.register = -1;
	}

	public ArrayList<Result> GetDims()
	{
		return this.dims;
	}

	public boolean GetFlag()
	{
		return this.flag;
	}

	public int GetInstructionId()
	{
		return this.instructionId;
	}

	public int getSSA()
	{
		return this.ssa;
	}

	public int GetValue()
	{
		return this.value;
	}

	public Kind GetKind()
	{
		return this.kind;
	}

	public void SetSSA(int ss)
	{
		this.ssa=ss;
	}

	public String GetName()
	{
		return this.varName;
	}

	public String toString()
	{
		if(this.kind == Result.Kind.VARIABLE)
		{
			String res = this.varName + this.ssa;
			return res;
		}

		if(this.kind == Result.Kind.INSTRUCTION)
		{
			String res = "(" +this.instructionId +")";
			return res;
		}

		if(this.kind == Result.Kind.CONSTANT)
		{
			String res = ""+ this.value;
			return res;
		}

		if(this.kind == Result.Kind.FRAME_POINTER)
		{
			return "FP";
		}

		if(this.kind == Result.Kind.BASE_ADDRESS)
		{
			String res = this.varName + "_baseaddress";
			return res;
		}
		
		
		if(this.kind == Result.Kind.ARRAY)
		{
			String res = this.varName + dims.toString();
			return res;
		}
		return "N/A";
	}

	public boolean equals(Result r)
	{
		if(r==null)
			return false;
		
		
		if(this.kind==r.kind)
		{
			if(this.kind == Result.Kind.VARIABLE && this.varName.compareTo(r.varName)==0 && this.ssa==r.ssa)
			{
				return true;
			}

			if(this.kind == Result.Kind.INSTRUCTION && this.instructionId ==r.instructionId)
			{
				return true;
			}

			if(this.kind == Result.Kind.CONSTANT && this.value==r.value)
			{
				return true;
			}

			if(this.kind == Result.Kind.FRAME_POINTER)
			{
				return true;
			}

			if(this.kind == Result.Kind.BASE_ADDRESS)
			{
				//String res = this.varName + "_baseaddress";
				return true;
			}
		}
		
		return false;
	}
	
	public String toStringWithRegister()
	{
		if(this.register!=-1)
			return "R" +this.register;
		else
			return this.toString();
	}

	
}


