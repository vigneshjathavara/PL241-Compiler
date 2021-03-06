package Parser;
import Structures.*;
import Structures.Instruction.Type;

import java.util.*;

import CodeGen.IcCodeGen;
import CodeGen.PhiGen;

public class Parser
{
	Scanner scn;
	CFG main;
	BasicBlock currentBlock;
	IcCodeGen icGen;

	public Parser(String fName)
	{
		scn = new Scanner(fName);
		main = new CFG();
		currentBlock = main.GetRoot();
		icGen = new IcCodeGen();
	}


	/*************************************************************************************************
Function : Computation
	 **/

	public void computation()
	{	
		//System.out.println("Computation Enter");
		BasicBlock root = main.GetRoot();
		scn.Next();
		//System.out.println("After Scan: " + scn.sym);


		while(scn.sym != Scanner.mainToken && scn.sym != Scanner.eofToken)//Check for main
		{
			scn.Next();
			//System.out.println(" While Next: " + scn.sym + "  ID: " + scn.id + "Value is: "+ scn.stringTable.get(scn.id));
		}
		//System.out.println("After Main or EOF");	

		if(scn.sym == Scanner.eofToken)
		{	
			//System.out.println("EOF error");
			scn.Error("Main not found");
			//return;

		}
		else{
			scn.Next();
			//System.out.println("After Main token is:" + scn.sym);	
		}
		//System.out.println("After Next");

		while(scn.sym == Scanner.varToken || scn.sym == Scanner.arrToken) //var|array
		{
			varDecl(root,main);
			scn.Next();
		}

		//System.out.println("After varDecl");

		while(scn.sym == Scanner.funcToken || scn.sym == Scanner.procToken) // function|procedure
		{
			funcDecl(root,main);
		}

		if(scn.sym != Scanner.beginToken)				// { check
		{
			scn.Error("Begin Token Not Found");
			return;
		}

		scn.Next();

		statSequence(root,main);						// statSequence

		//System.out.println("After statSeq: "+scn.sym);

		if(scn.sym != Scanner.endToken)					// } check
		{
			scn.Error("End Token Not Found");
			return;
		}
		scn.Next();

		if(scn.sym != Scanner.periodToken)				// . Check
		{       
			scn.Error("Period Token Not Found");
			return;
		}
		icGen.generate(null, null, Instruction.end, currentBlock, main);
		main.setTail(currentBlock);
		System.out.println("Parsed Successfully");

	}	

	/***********************************************************************************************
Function : VarDecl
	 **/

	void varDecl(BasicBlock bb, CFG c)
	{
		//System.out.println("VarDecl Enter");
		ArrayList<Integer> dims = new ArrayList<Integer>();
		int varOrArray = typeDecl(bb,c,dims);
		//scn.Next();
		if(scn.sym != Scanner.ident)
		{
			scn.Error("Identifier Not Found");
			return;
		}
		scn.Next();
		while(scn.sym == Scanner.commaToken)
		{
			scn.Next();
			if(scn.sym != Scanner.ident)
			{
				scn.Error("Identifier Not Found");
				return;
			}
			if(varOrArray==Scanner.arrToken)
			{
				String name = scn.Id2String(scn.id);
				bb.AddNewArray(name, dims);
				c.AddNewArray(name, dims);

			}

			else
			{
				String name= scn.Id2String(scn.id);
				bb.AddNewVariable(name);
				c.AddNewVariable(name);
			}
			scn.Next();
		}
		if(scn.sym != Scanner.semiToken)
		{
			scn.Error("Semi Colon Not Found");
			return;
		}
		//System.out.println("VarDecl End");
	}

	/***********************************************************************************************
Function : typeDecl
	 **/

	int typeDecl(BasicBlock bb, CFG c, ArrayList<Integer> dims)
	{
		//System.out.println("TypeDecl Enter");
		if(scn.sym == Scanner.varToken)
		{	
			//System.out.println("qwerty");

			scn.Next();
			if(scn.sym == Scanner.ident)
			{
				String name= scn.Id2String(scn.id);
				bb.AddNewVariable(name);
				c.AddNewVariable(name);
			}

			return Scanner.varToken;

		}

		else if(scn.sym == Scanner.arrToken)
		{
			//ArrayList<Integer> dims = new ArrayList<Integer>();
			scn.Next();
			if(scn.sym!=Scanner.openbracketToken)
			{
				scn.Error("Open Bracket Not Found");
				return -1;					
			}
			scn.Next();
			if(scn.sym!=Scanner.number)
			{
				scn.Error("Number Not Found");
				return -1;
			}
			dims.add(scn.val);

			scn.Next();
			if(scn.sym!=Scanner.closebracketToken)
			{
				scn.Error("Close Bracket Not Found");
				return -1;
			}
			scn.Next();
			while(scn.sym == Scanner.openbracketToken)
			{
				scn.Next();
				if(scn.sym!=Scanner.number)
				{
					scn.Error("Number Not Found");
					return -1;
				}
				dims.add(scn.val);
				scn.Next();
				if(scn.sym!=Scanner.closebracketToken)
				{
					scn.Error("Close Bracket Not Found");
					return -1;
				}
				scn.Next();
			}

			if(scn.sym == Scanner.ident)
			{
				String name = scn.Id2String(scn.id);
				bb.AddNewArray(name, dims);
				c.AddNewArray(name, dims);
			}
			return Scanner.arrToken;
		}
		else
		{
			scn.Error("Type declaration  Not Found");
			return -1;
		}
		//System.out.println("TypeDecl End");
	}

	/***********************************************************************************************
Function : FuncDecl
	 **/

	void funcDecl(BasicBlock bb, CFG c)
	{
		//System.out.println("FuncDecl Enter");
		
		
		boolean returns =false;
		if(scn.sym == Scanner.funcToken )
			returns=true;
		
		
		scn.Next();
		if(scn.sym!=Scanner.ident)
		{
			scn.Error("identifier  Not Found");
			return;
		}
		
		CFG f = new CFG();
		int identId = scn.id;
		String name = scn.Id2String(identId);
		f.setReturns(returns);
		c.AddFunction(name, f);
		BasicBlock root = f.GetRoot();
		BasicBlock parent = this.currentBlock;
		this.currentBlock = root;
		
		
		
		scn.Next();

		if(scn.sym == Scanner.openparenToken)
		{
			formalParam(root,f);			
		}

		scn.Next();

		if(scn.sym!=Scanner.semiToken)
		{
			scn.Error("Semi Colon  Not Found");
			return;

		}

		scn.Next();
		HashMap<String,ArrayList<Integer>> vTable = c.getVariableTable();
		for(String key:vTable.keySet())
		{
			root.AddNewVariable(key);
			f.AddNewVariable(key);
			
			int ssa = f.AddNewSSA(key);
			//System.out.println("Variable:"+key+" ssa:"+ssa);
			root.AddNewSSA(key, ssa);
		}
		
		HashMap<String,ArrayList<Integer>> arrTable = c.getArrayTable();
		for(String key:arrTable.keySet())
		{
			root.AddNewArray(key, c.GetArrayDims(key));
			f.AddNewArray(key, c.GetArrayDims(key));
		}

		funcBody(root,f);

		scn.Next();
		if(scn.sym!= Scanner.semiToken)
		{
			scn.Error("semi Colonr  Not Found");
			return;
		}
		scn.Next();	
		f.setTail(this.currentBlock);
		this.currentBlock = parent;
		//System.out.println("FuncDecl End");
	}

	/***********************************************************************************************
Function : FormalParam
	 **/

	void formalParam(BasicBlock bb, CFG c)
	{
		//System.out.println("formalParam Enter");
		scn.Next();
		if(scn.sym==Scanner.ident)
		{	
			String name= scn.Id2String(scn.id);
			//name += "_p";
			//System.out.println("Test0");
			bb.AddNewVariable(name);
			c.AddNewVariable(name);
			c.AddNewParam(name);
			int ssa = c.AddNewSSA(name);
			//System.out.println("Variable:"+name+" ssa:"+ssa);
			bb.AddNewSSA(name, ssa);
			
			//System.out.println("Formal Param Test1");
			scn.Next();
			while(scn.sym == Scanner.commaToken)
			{
				scn.Next();
				if(scn.sym != Scanner.ident)
				{
					scn.Error("identifier  Not Found");
					return;
				}
				name= scn.Id2String(scn.id);
				//name+="_p";
				bb.AddNewVariable(name);
				c.AddNewVariable(name);
				c.AddNewParam(name);
				ssa = c.AddNewSSA(name);
			//	System.out.println("Variable:"+name+" ssa:"+ssa);
				bb.AddNewSSA(name, ssa);
				scn.Next();
			}
		}
		if(scn.sym!=Scanner.closeparenToken)
		{
			scn.Error("Close Paren  Not Found");
			return;
		}
		//System.out.println("formalParam End");
	}


	/***********************************************************************************************
Function : FuncBody
	 **/

	void funcBody(BasicBlock bb, CFG c)
	{
		//System.out.println("FuncBody Enter");
		while(scn.sym == Scanner.varToken || scn.sym == Scanner.arrToken) //var|array
		{
			varDecl(bb,c);
			scn.Next();
		}

		if(scn.sym!=Scanner.beginToken)
		{
			scn.Error("Begin Token  Not Found");
			return;
		}

		ArrayList<String> params = c.getParamList();
		for(String str: params)
		{
			int ssa = bb.GetLastestSSAOf(str);
			Result r1 = new Result(str,ssa);
			bb.AddNewSSA(r1.GetName(), ssa);
			Result r2 = icGen.generate(r1,Instruction.pop, bb, c);
			
			icGen.generate(r1, r2, Instruction.move, this.currentBlock, c);
			
		}

		
		scn.Next();
		//System.out.println("Test2");
		if(scn.sym == Scanner.letToken ||scn.sym == Scanner.callToken||scn.sym == Scanner.ifToken||scn.sym == Scanner.whileToken||scn.sym == Scanner.returnToken)
		{	
			statSequence(bb,c);
		}
		//System.out.println("Test3");
		if(scn.sym != Scanner.endToken)
		{
			scn.Error("End Token  Not Found");
			return;
		}
		//System.out.println("FuncBody End");

	}

	/***********************************************************************************************
Function : StatSequence
	 **/

	void statSequence(BasicBlock bb, CFG c)
	{
		//System.out.println("StatSequence Enter  :" +this.currentBlock.GetId() );
		//if(scn.sym == scn.letToken ||scn.sym == scn.callToken||scn.sym == scn.ifToken||scn.sym == scn.whileToken||scn.sym == scn.returnToken)
		//{
		statement(this.currentBlock,c);
		//scn.Next();
		//System.out.println("StatSequence Test1");
		while(scn.sym == Scanner.semiToken)
		{
			scn.Next();
			statement(this.currentBlock,c);
			//scn.Next();
		}

		//}

		//else
		//{
		//scn.Error("Statement  Not Found");
		//  return;
		//}
		//System.out.println("StatSequence End");

	}
	/***********************************************************************************************
Function : Statement
	 **/


	void statement(BasicBlock bb, CFG c)
	{
		//System.out.println("Statement Enter");

		if(scn.sym ==  Scanner.letToken)
		{
			scn.Next();
			assignment(this.currentBlock,c);
		}

		else if(scn.sym ==Scanner.callToken)
		{
			scn.Next();
			funcCall(this.currentBlock,c);
			//scn.Next();
		}

		else if(scn.sym == Scanner.ifToken)
		{
			scn.Next();
			ifStatement(this.currentBlock,c);
			scn.Next();
		}

		else if (scn.sym == Scanner.whileToken)
		{
			scn.Next();
			whileStatement(this.currentBlock,c);
			scn.Next();
		}

		else if (scn.sym == Scanner.returnToken)
		{
			scn.Next();
			returnStatement(this.currentBlock,c);
		}

		else
		{
			scn.Error("Statement  Not Found");
			return;
		}
		//System.out.println("Statement End");


	}


	/***********************************************************************************************
Function : Assignment
	 **/

	void assignment(BasicBlock bb, CFG c)
	{
		//System.out.println("Assignment Enter");

		//ArrayList<Result> dims = new ArrayList<Result>();
		Result r1 = designator(this.currentBlock, c);
		
	//	System.out.println("Assignment Test1 :"+ r1.toString());
		
		if(scn.sym != Scanner.becomesToken)
		{
			scn.Error("Becomes Token  Not Found");
			return;
		}
		scn.Next();
		
		//System.out.println("Assignment Test2");
		
		Result r2 = expression(this.currentBlock,c);
		
	//	System.out.println("Assignment Test3 :"+ r2.toString());
		
		
		if(r1.GetKind()==Result.Kind.VARIABLE)
		{
			//System.out.println("Assignment Test4");
			
			int ssa = c.AddNewSSA(r1.GetName());
			r1.SetSSA(ssa);
			bb.AddNewSSA(r1.GetName(), ssa);
			icGen.generate(r1, r2, Instruction.move, this.currentBlock, c);

		}

		else
		{
			//System.out.println("Assignment Test5");
			
			r1 = icGen.generate(r1, bb, c);
			
			//System.out.println("Assignment Test6");
			
			icGen.generate(r2, r1, Instruction.store, this.currentBlock, c);


		}


		//System.out.println("Assignment End");
	}

	/***********************************************************************************************
Function : Designator
	 **/
	Result designator(BasicBlock bb, CFG c)
	{
		//System.out.println("Designator Enter");
		boolean varOrArray=false;
		ArrayList<Result> dims = new ArrayList<Result>();

		if(scn.sym != Scanner.ident)
		{
			scn.Error("identifier  Not Found");
			return null;
		}
		int identId = scn.id;
		scn.Next();
		while(scn.sym == Scanner.openbracketToken)
		{
			varOrArray=true;
			scn.Next();
			Result r = expression(bb,c);
			dims.add(r);
			if(scn.sym != Scanner.closebracketToken)
			{
				scn.Error("identifier  Not Found");
				return null;
			}
			scn.Next();
		}

		if(varOrArray==false)
		{
			String name = scn.Id2String(identId);
			/*int ssa;
			if(flag==true)
				{
					ssa = c.AddNewSSA(name);
					bb.AddNewSSA(name, ssa);
				}
			else*/
			int ssa = bb.GetLastestSSAOf(name);

			Result r = new Result(name,ssa);
			return r;

		}

		else
		{		
			
			String name = scn.Id2String(identId);
			Result r=new Result(name ,dims);
			return r;

			//To Be Implemented-----done
		}

	}	

	/***********************************************************************************************
Function : Expression
	 **/

	Result expression(BasicBlock bb, CFG c)
	{
		//System.out.println("Expression Enter");
		Result r1 = term(bb,c);
		Result r2 =null;
		int op =-1;
		while(scn.sym == Scanner.plusToken || scn.sym == Scanner.minusToken)
		{
			op = scn.sym==Scanner.plusToken ? Instruction.add: Instruction.sub;
			scn.Next();

			r2 =term(bb,c);
			r1 = icGen.generate(r1, r2, op, this.currentBlock, c);
		}

		return r1;
		//System.out.println("Expression End");
	}


	/***********************************************************************************************
Function : FuncCall
	 **/

	Result funcCall(BasicBlock bb, CFG c)
	{
		//System.out.println("FuncCall Enter");
		/*	if(scn.sym != scn.callToken)
			{
			scn.Error("Call Token  Not Found");
			return;
			}

			scn.Next();
		 */
		if(scn.sym != Scanner.ident)
		{
			scn.Error("Ident  Not Found");
			return null;
		}
		
	//	System.out.println("Function being called:" + scn.Id2String(scn.id));
		
		if(scn.Id2String(scn.id).compareTo("OutputNum")==0)
		{
			Result num = null;
			scn.Next();
			if(scn.sym == Scanner.openparenToken)
			{
				scn.Next();
				if(scn.sym == Scanner.ident || scn.sym == Scanner.number || scn.sym == Scanner.openparenToken || scn.sym == Scanner.callToken)
				{
					num = expression(bb,c);				
				}
				else
				{
					scn.Error("Nothing to Write/Output");
					return null;
				}
			}
			else
			{
				scn.Error("Open parenthisis  Not Found");
				return null;
			}
			Result r = icGen.generate(num, Instruction.write, bb, c);
			scn.Next();
			return r;
		}
		
		if(scn.Id2String(scn.id).compareTo("InputNum")==0)
		{
			scn.Next();
			if(scn.sym != Scanner.openparenToken)
			{
				scn.Error("Open parenthisis  Not Found");
				return null;
				
			}
			scn.Next();
			if(scn.sym != Scanner.closeparenToken)
			{
				scn.Error("Close parenthisis  Not Found");
				return null;
			}
			
			
			Result r = icGen.generate(null,null, Instruction.read, bb, c);
			scn.Next();
			return r;
			
		}
		
		if(scn.Id2String(scn.id).compareTo("OutputNewLine")==0)
		{
			scn.Next();
			if(scn.sym != Scanner.openparenToken)
			{
				scn.Error("Open parenthisis  Not Found");
				return null;
				
			}
			scn.Next();
			if(scn.sym != Scanner.closeparenToken)
			{
				scn.Error("Close parenthisis  Not Found");
				return null;
			}
			
			
			Result r = icGen.generate(null,null, Instruction.writeNL, bb, c);
			scn.Next();
			return r;
			
		}
		
		CFG fCFG = c.getFunctionList().get(scn.Id2String(scn.id));
		BasicBlock fTail = fCFG.getTail();
		BasicBlock fRoot = fCFG.GetRoot();
		
		//System.out.println("Root:" +fRoot.GetId()+" Tail:" + fTail.GetId());
		
		scn.Next();
		if(scn.sym == Scanner.openparenToken)
		{
			scn.Next();
			int paramCount=0;
			if(scn.sym == Scanner.ident || scn.sym == Scanner.number || scn.sym == Scanner.openparenToken || scn.sym == Scanner.callToken)
			{
				Result r = expression(bb,c);
				paramCount++;
				Result r2 = icGen.generate(r,Instruction.push, bb, c);
				
				while(scn.sym == Scanner.commaToken)
				{
					scn.Next();
					r = expression(bb,c);
					paramCount++;
					r2 = icGen.generate(r,Instruction.push, bb, c);
				}
			}
			if(scn.sym != Scanner.closeparenToken)
			{
				scn.Error("Close parenthisis  Not Found");
				return null;
			}
			
			if (paramCount != fCFG.getParamList().size())
			{
				scn.Error("Parameter mismatch");
				return null;
			}
			scn.Next();									
		}
		
		
	//	System.out.println("FuncCall Test1");
		
		Result r = icGen.generate(fRoot.GetId(), Instruction.bra, bb, c);
		
		//System.out.println("FuncCall Test2");
		//BasicBlock fJoin = new BasicBlock(BasicBlock.BlockType.NORMAL, bb.GetLatestVariableVersion(), bb.GetArrayTable(),bb, c );
		//this.currentBlock = fJoin;
	//	System.out.println("FuncCall Test3");
		//bb.SetLeft(fJoin);
		
		/*
		
		fCFG.setReturnTo(fJoin.GetId());		
		System.out.println("FuncCall Test4");
		Instruction ins = c.GetInstruction(r.GetInstructionId());
		System.out.println("FuncCall Test5 :" +"Block Type"+ fTail.getType() +(fTail.GetInstructionList().size() - 1));
		Instruction returnIns =fCFG.GetInstruction( fTail.GetInstructionList().get(fTail.GetInstructionList().size()-1));
		System.out.println("FuncCall Test6");
		returnIns.setTargetBlock(fJoin.GetId());
		ins.setReturnValue(returnIns.getReturnValue());
		
		System.out.println("FuncCall End");
		return returnIns.getReturnValue();
		
		*/
		
		if(fCFG.isReturns())
		{
			Result r2 = icGen.generate(null,Instruction.pop, bb, c);
			//System.out.println("Return");
			return r2;
			
		}
		
		else{
			//System.out.println("No Return");
			return null;
		}
			
		
	}

	/***********************************************************************************************
Function : IfStatement
	 **/

	void ifStatement(BasicBlock bb, CFG c)
	{
		//System.out.println("ifStatement Enter");

		BasicBlock parent = this.currentBlock;
	
		Result r =relation(bb,c);


		BasicBlock elseTop=null;


		BasicBlock ifBlock = null;

		BasicBlock elseBlock = null;

		
		if(scn.sym != Scanner.thenToken)
		{
			scn.Error("then Token  Not Found");
			return;
		}
		scn.Next();
		
		
		if(r.GetKind() != Result.Kind.BOOLEAN)
		{
			ifBlock = new BasicBlock(BasicBlock.BlockType.IF,bb.GetLatestVariableVersion(), bb.GetArrayTable(),parent, c);
			parent.SetLeft(ifBlock);
			this.currentBlock = ifBlock;
			statSequence(this.currentBlock,c);
			ifBlock = this.currentBlock;
		}
		else
		{
			if(r.GetFlag())
			{
				statSequence(bb,c);
			}
			
			else
			{
				BasicBlock RejectedIfBlock = new BasicBlock(BasicBlock.BlockType.IF,bb.GetLatestVariableVersion(), bb.GetArrayTable(),null,null);
				//System.out.println("Rejected:"+RejectedIfBlock.GetId());
				this.currentBlock = RejectedIfBlock;
				statSequence(this.currentBlock,c);
				this.currentBlock = parent;
			}

		}

		if(scn.sym == Scanner.elseToken)
		{
			scn.Next();
			if(r.GetKind() != Result.Kind.BOOLEAN)
			{
				elseBlock = new BasicBlock(BasicBlock.BlockType.ELSE,bb.GetLatestVariableVersion(), bb.GetArrayTable(),parent,c);
				elseTop = elseBlock;
				this.currentBlock = elseBlock;
				parent.SetRight(elseBlock);
			}
			if((r.GetKind() != Result.Kind.BOOLEAN))
			{
				statSequence(this.currentBlock,c);
				elseBlock = this.currentBlock;
			}
			else
			{
				if(!r.GetFlag())
				{
					statSequence(bb,c);
				}
				
				else
				{
					BasicBlock RejectedElseBlock = new BasicBlock(BasicBlock.BlockType.IF,bb.GetLatestVariableVersion(), bb.GetArrayTable(), null, null);
					//System.out.println("Rejected:"+RejectedElseBlock.GetId());
					this.currentBlock = RejectedElseBlock;
					statSequence(this.currentBlock,c);
					this.currentBlock = parent;
					
				}
				
			}
		}
		if(scn.sym != Scanner.fiToken)
		{
			scn.Error("fi Token  Not Found");
			return;
		}
		
		if(r.GetKind() != Result.Kind.BOOLEAN)
		{
			BasicBlock joinBlock = new BasicBlock(BasicBlock.BlockType.JOIN, bb.GetLatestVariableVersion(),bb.GetArrayTable(), null, c);
			if(elseBlock == null)
			{
				parent.SetRight(joinBlock);
				joinBlock.AddParent(ifBlock);
				ifBlock.SetLeft(joinBlock);
				joinBlock.AddParent(parent);
				joinBlock.setBranchParent(parent);//--------
				c.FixUp(r.GetInstructionId(),joinBlock.GetId());
			
				new PhiGen().Phi_if(ifBlock, parent, joinBlock, icGen, c);

			}
			else
			{
				//System.out.println("Else Block exits");
				
				ifBlock.SetLeft(joinBlock);
				elseBlock.SetLeft(joinBlock);
				joinBlock.AddParent(ifBlock);
				joinBlock.AddParent(elseBlock);
				joinBlock.setBranchParent(parent);//---------
				c.FixUp(r.GetInstructionId(),elseTop.GetId());
				
				new PhiGen().Phi_if(ifBlock, elseBlock, joinBlock, icGen, c);
			}
			icGen.generate(joinBlock.GetId(), Instruction.bra, ifBlock, c);
			this.currentBlock = joinBlock;
			parent.setIfJoin(joinBlock);
			parent.setType(BasicBlock.BlockType.IF_MAIN);
		}
	//	System.out.println("IfStatement End");

	}

	/***********************************************************************************************
Function : WhileStatement
	 **/

	void whileStatement(BasicBlock bb, CFG c)
	{
		//System.out.println("WhileStatement Enter");



		/*
		//To Be Implemented
		if(r.GetKind()==Result.Kind.BOOLEAN)
		{
			if(r.GetFlag())
			{
				System.out.println("Infinite");
			}

			else
			{
				//To Be Implemented
			}

		}

		 */


		BasicBlock parent = this.currentBlock;

		BasicBlock whileMain = new BasicBlock(BasicBlock.BlockType.WHILE_MAIN,bb.GetLatestVariableVersion(), bb.GetArrayTable(), parent, c);
		this.currentBlock = whileMain;//---!
		Result r = relation(whileMain,c);
		whileMain.setBranchParent(parent);


		/*When while condition is constant*/
		if(r.GetKind()==Result.Kind.BOOLEAN)
		{
			if(scn.sym != Scanner.doToken)
			{
				scn.Error("do Token  Not Found");
				return;
			}
			scn.Next();


			if(r.GetFlag())
			{
				//System.out.println("Infinite");
				scn.Error("Infinite Loop Encountered");
				//statSequence(this.currentBlock,c);

			}

			else
			{
				//System.out.println("Warning :Loop Body Never Reached");
				BasicBlock rejectedWhileBody = new BasicBlock(BasicBlock.BlockType.WHILE_BODY, whileMain.GetLatestVariableVersion(), whileMain.GetArrayTable(), null, null);
				this.currentBlock = rejectedWhileBody;
				statSequence(rejectedWhileBody,c);
				this.currentBlock = parent;
			}

			if(scn.sym != Scanner.odToken)
			{
				scn.Error("od Token  Not Found");
				return;
			}
			return;
		}




		

		parent.SetLeft(whileMain);

		BasicBlock whileBody = new BasicBlock(BasicBlock.BlockType.WHILE_BODY, whileMain.GetLatestVariableVersion(), whileMain.GetArrayTable(), whileMain, c);
		

		whileMain.SetLeft(whileBody);
		

		this.currentBlock = whileBody;

		if(scn.sym != Scanner.doToken)
		{
			scn.Error("do Token  Not Found");
			return;
		}

		scn.Next();
		statSequence(this.currentBlock,c);
		BasicBlock whileBodyLast = this.currentBlock;
		icGen.generate(whileMain.GetId(), Instruction.bra, whileBodyLast, c);
		whileBodyLast.SetLeft(whileMain);
		whileMain.setWhileBodyLast(whileBodyLast);//------------

		if(scn.sym != Scanner.odToken)
		{
			scn.Error("od Token  Not Found");
			return;
		}

		PhiGen pgen = new PhiGen();
		ArrayList<String> phiVariables = pgen.Phi_if(parent, whileBodyLast, whileMain, icGen, c);
		
		
		/*for(String str:phiVariables)
				System.out.print(str+" ");*/
		ArrayList<Integer> visited = new ArrayList<Integer>();
		visited.add(whileMain.GetId());
		//System.out.println("WhileStatement Test1:" +bb.GetId());
		pgen.PropagatePhi(phiVariables, whileBody,parent,c,visited);
	//	System.out.println("WhileStatement Test2");
		BasicBlock whileJoin = new BasicBlock(BasicBlock.BlockType.WHILE_JOIN, whileMain.GetLatestVariableVersion(), whileMain.GetArrayTable(), whileMain,c);
		whileMain.SetRight(whileJoin);
		c.FixUp(r.GetInstructionId(), whileJoin.GetId());
		
		this.currentBlock = whileJoin;
		whileMain.setWhileJoin(whileJoin);
		//System.out.println("WhileStatement End");
	}

	/***********************************************************************************************
Function : returnStatement
	 **/

	void returnStatement(BasicBlock bb, CFG c)
	{
	//	System.out.println("returnStatement Enter");
		//System.out.println("Return to:"+ c.getReturnTo());
		
		
		
		
		
		//Result r = icGen.generate(c.getReturnTo(), Instruction.bra, bb, c);
		//Instruction ins = c.GetInstruction(r.GetInstructionId());
		
		if(scn.sym == Scanner.ident || scn.sym == Scanner.number || scn.sym == Scanner.openparenToken || scn.sym == Scanner.callToken)
		{
			Result res = expression(bb,c);
			icGen.generate(res,Instruction.push, bb, c);
		} 
	//	System.out.println("returnStatement End");
	}

	/***********************************************************************************************
Function : relation for IfStatement
	 **/

	Result relation(BasicBlock bb, CFG c)
	{
		//System.out.println("relation Enter");
		Result r1 = expression(bb,c);
		int opCode=0;

		if(scn.sym != Scanner.eqlToken && scn.sym != Scanner.neqToken && scn.sym != Scanner.lssToken && scn.sym != Scanner.geqToken && scn.sym != Scanner.leqToken && scn.sym != Scanner.gtrToken)	
		{
			scn.Error("Relational operator  Not Found");
			return null;

		}

		switch(scn.sym)
		{
		case Scanner.eqlToken : opCode = Instruction.bne;
		break;

		case Scanner.neqToken : opCode = Instruction.beq;
		break;

		case Scanner.lssToken : opCode = Instruction.bge;
		break;

		case Scanner.geqToken : opCode = Instruction.blt;
		break;

		case Scanner.leqToken : opCode = Instruction.bgt;
		break;

		case Scanner.gtrToken : opCode = Instruction.ble;
		break;
		}


		scn.Next();
		Result r2 = expression(bb,c);

		Result r3=null,r4=null;

		if(r1.GetKind() == Result.Kind.CONSTANT && r2.GetKind() == Result.Kind.CONSTANT)
		{
			boolean f =false;
			switch(opCode)
			{
			case  Instruction.bne	:   if(r1.GetValue() == r2.GetValue())
				f=true;
			break;

			case  Instruction.beq	:	if(r1.GetValue() != r2.GetValue())
				f=true;
			break;

			case Instruction.bge	:	if(r1.GetValue() < r2.GetValue())
				f=true;
			break;

			case Instruction.blt	:	if(r1.GetValue() >= r2.GetValue())
				f=true;
			break;

			case Instruction.bgt	:	if(r1.GetValue() <= r2.GetValue())
				f=true;
			break;

			case Instruction.ble	:	if(r1.GetValue() > r2.GetValue())
				f=true;
			break;
			}

			Result res = new Result(f);
			return res;
		}	

		else
		{
			r3 = icGen.generate(r1, r2, Instruction.cmp, bb, c);

			r4 = icGen.generate(r3, opCode, bb, c);

			return r4;

		}

		//System.out.println("relation End");
	}

	/***********************************************************************************************
Function : term
	 * @return 
	 **/

	Result term(BasicBlock bb, CFG c)
	{
		//System.out.println("term Enter");
		Result r1 =factor(bb,c);
		Result r2=null;
		int op=-1; 
		while(scn.sym == Scanner.timesToken || scn.sym == Scanner.divToken)
		{
			op = (scn.sym==Scanner.timesToken) ? Instruction.mul:Instruction.div;
			scn.Next();
			r2 =factor(bb,c);
			r1 = icGen.generate(r1, r2, op, bb, c);

		}
		return r1;
		//System.out.println("term End");
	}

	/***********************************************************************************************
Function : factor
	 **/

	Result factor(BasicBlock bb, CFG c)
	{
		//System.out.println("factor Enter");
		//ArrayList<Result> dims = new ArrayList<Result>();

		if(scn.sym == Scanner.ident)
		{
			Result r = designator(bb,c);
			if (r.GetKind() == Result.Kind.ARRAY)
			{
				r = icGen.generate(r, bb, c);
				r = icGen.generate(r, Instruction.load, bb, c);

			}

			return r;
		}

		else if(scn.sym == Scanner.number)
		{
			int val = scn.val;
			Result r = new Result(Result.Kind.CONSTANT,val);
			scn.Next();
			return r;

		}
		else if(scn.sym == Scanner.openparenToken)
		{
			scn.Next();
			Result r =  expression(bb,c);
			if(scn.sym != Scanner.closeparenToken)
			{
				scn.Error("close Parenthisis  Not Found");
				return null;
			}
			scn.Next();
			return r;
		}
		else if(scn.sym == Scanner.callToken)
		{
			scn.Next();
			return funcCall(bb,c);
			//To be Implemented
			
		}
		return null;
		//System.out.println("Factor End");

	}

	public CFG GetCFG()
	{
		return this.main;
	}

} 
