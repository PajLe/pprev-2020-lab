import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Stack;

public class GrupaDvaLL1SintaksniAnalizator {

	/* Kodiranje neterminalnih simbola
	 * IfStatement - 10
	 * ElsePart - 11
	 * RelExpression - 12
	 * RelExpression' - 13
	 * Expression - 14
	 * Expression' - 15
	 * Term - 16
	 * */
	private static enum Neterminal {
		IfStatement(10),
		ElsePart(11),
		RelExpression(12),
		RelExpressionPrim(13),
		Expression(14),
		ExpressionPrim(15),
		Term(16);

		private int val;

	    private Neterminal(int val) {
	        this.val = val;
	    }
	    
	    public int getVal() {return val;}
	};
	
	/* Kodiranje akcija
	 * Accept - 0
	 * Smene 1-10 - [1-10]
	 * Pop - 11
	 * Error - 12
	 * */
	
	/* Sintaksna tabela         if          (              )              :          else           >             *              ID       CONST        #
	 *                        sym.IF | sym.LEFTPAR | sym.RIGHTPAR |  sym.COLON    | sym.ELSE | sym.GREATER | sym.MULTIPLY |   sym.ID   | sym.CONST | sym.EOF
	 * sym.IF =          [0]| 11(pop)
	 * sym.LEFTPAR =     [1]|            11(pop)        
	 * sym.RIGHTPAR =    [2]|                            11(pop)      
	 * sym.COLON =       [3]|                                           11(pop)
	 * sym.ELSE =        [4]|                                                        11(pop)     
	 * sym.GREATER =     [5]|                                                                    11(pop)
	 * sym.MULTIPLY =    [6]|                                                                                  11(pop)
	 * sym.ID =          [7]|                                                                                                 11(pop)
	 * sym.CONST =       [8]|                                                                                                             11(pop)
	 * sym.EOF =         [9]|                                                                                                                        0(acc)
	 * IfStatement -    [10]| smena 1
	 * ElsePart -       [11]|                                                       smena 2
	 * RelExpression -  [12]|                                                                                                  smena 3     smena 3
	 * RelExpression' - [13]|                                                                    smena 4                       smena 5     smena 5
	 * Expression -     [14]|                                                                                                  smena 6     smena 6
	 * Expression' -    [15]|                                                       smena 8                     smena 7                              smena 8
	 * Term -           [16]|                                                                                                  smena 9     smena 10
	 * */
	
	private static final int[][] SintaksnaTabela = {
			{11,12,12,12,12,12,12,12,12,12},
			{12,11,12,12,12,12,12,12,12,12},
			{12,12,11,12,12,12,12,12,12,12},
			{12,12,12,11,12,12,12,12,12,12},
			{12,12,12,12,11,12,12,12,12,12},
			{12,12,12,12,12,11,12,12,12,12},
			{12,12,12,12,12,12,11,12,12,12},
			{12,12,12,12,12,12,12,11,12,12},
			{12,12,12,12,12,12,12,12,11,12},
			{12,12,12,12,12,12,12,12,12, 0},
			{ 1,12,12,12,12,12,12,12,12,12},
			{12,12,12,12, 2,12,12,12,12,12},
			{12,12,12,12,12,12,12, 3, 3,12},
			{12,12,12,12,12, 4,12, 5, 5,12},
			{12,12,12,12,12,12,12, 6, 6,12},
			{12,12,12,12, 8,12, 7,12,12, 8},
			{12,12,12,12,12,12,12, 9,10,12}
	};
	
	public static void main(String[] args) {
		try {
			GrupaDvaLexer leksickiAnalizator;
			java.io.FileInputStream stream;
			stream = new java.io.FileInputStream(args[0]);
			java.io.Reader reader = new java.io.InputStreamReader(stream);
			leksickiAnalizator = new GrupaDvaLexer(reader);
			
			PokreniSintaksnuAnalizu(leksickiAnalizator);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void PokreniSintaksnuAnalizu(GrupaDvaLexer leksickiAnalizator) throws IOException {
		Stack<Integer> radniMagacin = new Stack<Integer>();
		radniMagacin.push(sym.EOF); // #
		radniMagacin.push(10); // IfStatement - 10
		
		do {
			Yytoken token = leksickiAnalizator.next_token();
			if (token.m_index == sym.LEXERR) {
				System.out.println("Error u leksickom analizatoru: " + token);
				return;
			}
			
			int akcija = -1;
			do {
				akcija = SintaksnaTabela[radniMagacin.peek()][token.m_index];
				
				/* Iskorisceno kodiranje akcija (na vrhu ovog fajla) */
				switch (akcija) {
				case 0:
					Accept(radniMagacin);
					return;
				case 1:
					Smena1(radniMagacin);
					break;
				case 2:
					Smena2(radniMagacin);
					break;
				case 3:
					Smena3(radniMagacin);
					break;
				case 4:
					Smena4(radniMagacin);
					break;
				case 5:
					Smena5(radniMagacin);
					break;
				case 6:
					Smena6(radniMagacin);
					break;
				case 7:
					Smena7(radniMagacin);
					break;
				case 8:
					Smena8(radniMagacin);
					break;
				case 9:
					Smena9(radniMagacin);
					break;
				case 10:
					Smena10(radniMagacin);
					break;
				case 11:
					Pop(radniMagacin);
					break;
				case 12:
					Error(radniMagacin, token);
					return; // ne nastavljamo sa sintaksnom analizom ako nadjemo gresku
				}

			} while (akcija != 11); // ne uzimamo sledeci token dok se ne Pop-uje trenutni token
		} while (!leksickiAnalizator.yyatEOF());
	}

	private static void Error(Stack<Integer> radniMagacin, Yytoken token) {
		System.out.println("Error u sintaksnom analizatoru. " + token);
	}

	private static void Pop(Stack<Integer> radniMagacin) {
		radniMagacin.pop();
	}

	private static void Smena10(Stack<Integer> radniMagacin) { // Term -> CONST
		radniMagacin.pop();
		radniMagacin.push(sym.CONST);
	}

	private static void Smena9(Stack<Integer> radniMagacin) { // Term -> ID
		radniMagacin.pop();
		radniMagacin.push(sym.ID);
	}

	private static void Smena8(Stack<Integer> radniMagacin) { // Expression' -> e
		radniMagacin.pop();	
	}

	private static void Smena7(Stack<Integer> radniMagacin) { // Expression' -> * Term Expression'
		radniMagacin.pop();
		radniMagacin.push(Neterminal.ExpressionPrim.getVal());
		radniMagacin.push(Neterminal.Term.getVal());
		radniMagacin.push(sym.MULTIPLY);
	}

	private static void Smena6(Stack<Integer> radniMagacin) { // Expression -> Term Expression'
		radniMagacin.pop();
		radniMagacin.push(Neterminal.ExpressionPrim.getVal());
		radniMagacin.push(Neterminal.Term.getVal());
	}

	private static void Smena5(Stack<Integer> radniMagacin) { // RelExpression' -> Term
		radniMagacin.pop();
		radniMagacin.push(Neterminal.Term.getVal());
	}

	private static void Smena4(Stack<Integer> radniMagacin) { // RelExpression' -> > Term
		radniMagacin.pop();
		radniMagacin.push(Neterminal.Term.getVal());
		radniMagacin.push(sym.GREATER);
	}

	private static void Smena3(Stack<Integer> radniMagacin) { // RelExpression -> Term RelExpression'
		radniMagacin.pop();
		radniMagacin.push(Neterminal.RelExpressionPrim.getVal());
		radniMagacin.push(Neterminal.Term.getVal());
	}

	private static void Smena2(Stack<Integer> radniMagacin) { // ElsePart -> else : Expression
		radniMagacin.pop();
		radniMagacin.push(Neterminal.Expression.getVal());
		radniMagacin.push(sym.COLON);
		radniMagacin.push(sym.ELSE);
	}

	private static void Smena1(Stack<Integer> radniMagacin) { // IfStatement -> if ( RelExpression ) : Expression ElsePart
		radniMagacin.pop();
		radniMagacin.push(Neterminal.ElsePart.getVal());
		radniMagacin.push(Neterminal.Expression.getVal());
		radniMagacin.push(sym.COLON);
		radniMagacin.push(sym.RIGHTPAR);
		radniMagacin.push(Neterminal.RelExpression.getVal());
		radniMagacin.push(sym.LEFTPAR);
		radniMagacin.push(sym.IF);
	}

	private static void Accept(Stack<Integer> radniMagacin) {
		radniMagacin.pop();
		System.out.println("Accept!");
	}

}
