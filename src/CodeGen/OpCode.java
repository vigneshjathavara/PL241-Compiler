package CodeGen;


public enum OpCode {
	//Arithmetic Instructions
	ADD(0, Format.F2),
	SUB(1, Format.F2),
	MUL(2, Format.F2),
	DIV(3, Format.F2),
	MOD(4, Format.F2),
	CMP(5, Format.F2),
	OR(8, Format.F2),
	AND(9, Format.F2),
	BIC(10, Format.F2),
	XOR(11, Format.F2),
	LSH(12, Format.F2),
	ASH(13, Format.F2),
	CHK(14, Format.F2),
	
	ADDI(16, Format.F1),
	SUBI(17, Format.F1),
	MULI(18, Format.F1),
	DIVI(19, Format.F1),
	MODI(20, Format.F1),
	CMPI(21, Format.F1),
	ORI(24, Format.F1),
	ANDI(25, Format.F1),
	BICI(26, Format.F1),
	XORI(27, Format.F1),
	LSHI(28, Format.F1),
	ASHI(29, Format.F1),
	CHKI(30, Format.F1),
	
	//Load/Store Instructions
	LDW(32, Format.F1),
	LDX(33, Format.F2),
	POP(34, Format.F1),
	STW(36, Format.F1),
	STX(37, Format.F2),
	PSH(38, Format.F1),
	
	//Control Instructions
	BEQ(40, Format.F1),
	BNE(41, Format.F1),
	BLT(42, Format.F1),
	BGE(43, Format.F1),
	BLE(44, Format.F1),
	BGT(45, Format.F1),
	
	BSR(46, Format.F1),
	JSR(48, Format.F3),
	RET(49, Format.F2),
	
	//Input/Output Instructions
	RDD(50, Format.F2),
	WRD(51, Format.F2),
	WRH(52, Format.F2),
	WRL(53, Format.F1);
	
	private int opCode;
	private Format format;
	private OpCode(int opCode, Format format){
		this.opCode = opCode;
		this.format = format;
	}
	
	public int getOpcode(){
		return opCode;
	}
	
	public Format getFormat() {
		return format;
	}
}
