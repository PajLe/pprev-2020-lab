import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

public class GrupaDvaLR1SintaksniAnalizator {

	/* Kodiranje neterminalnih simbola
	 * IfStatement - 10
	 * ElsePart - 11
	 * RelExpression - 12
	 * Expression - 13
	 * Term - 14
	 * */
	private static enum Neterminal {
		IfStatement(10),
		ElsePart(11),
		RelExpression(12),
		Expression(13),
		Term(14);

		private int val;

	    private Neterminal(int val) {
	        this.val = val;
	    }
	    
	    public int getVal() {return val;}
	};
	
	/* Kodiranje akcija
	 * Accept - 0
	 * shift(i) - i [0-19]
	 * reduce(i) - 20 + i [21-28]
	 * Error - -1
	 * */
	
	/* Kodiranje prelaza
	 * prelaz(stanje) - stanje [0-19]
	 * */

	/* Sintaksna tabela
	 * 						AKCIJE 								 PRELAZI
	stanja|	if	(	)	:	else	>	*	ID	CONST	#	IS	EP	RE	E	T
		0	s2										        1				
		1										       acc					
		2		s3													
		3								    s6	s7				    4	    5
		4			s8					 							
		5			r4			   s9									
		6			r7		r7	   r7	r7			    r7					
		7			r8		r8	   r8	r8			    r8					
		8				s10											
		9								    s6	s7						    11
		10								    s6	s7					   12   13
		11			r3				   								
		12					s16		   s15					    14			
		13					r6		    r6			    r6					
		14										        r1					
		15								    s6	s7						    17
		16				s18											
		17					r5		    r5			    r5					
		18								    s6	s7					   19   13
		19							   s15			    r2					
	 */
	

	private static final int[][] SintaksnaTabela = {
			/*0*/{ 2,-1,-1,-1,-1,-1,-1,-1,-1,-1, 1,-1,-1,-1,-1},
			/*1*/{-1,-1,-1,-1,-1,-1,-1,-1,-1, 0,-1,-1,-1,-1,-1},
			/*2*/{-1, 3,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			/*3*/{-1,-1,-1,-1,-1,-1,-1, 6, 7,-1,-1,-1, 4,-1, 5},
			/*4*/{-1,-1, 8,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			/*5*/{-1,-1,24,-1,-1, 9,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			/*6*/{-1,-1,27,-1,27,27,27,-1,-1,27,-1,-1,-1,-1,-1},
			/*7*/{-1,-1,28,-1,28,28,28,-1,-1,28,-1,-1,-1,-1,-1},
			/*8*/{-1,-1,-1,10,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
			/*9*/{-1,-1,-1,-1,-1,-1,-1, 6, 7,-1,-1,-1,-1,-1,11},
		   /*10*/{-1,-1,-1,-1,-1,-1,-1, 6, 7,-1,-1,-1,-1,12,13},
		   /*11*/{-1,-1,23,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
		   /*12*/{-1,-1,-1,-1,16,-1,15,-1,-1,-1,-1,14,-1,-1,-1},
		   /*13*/{-1,-1,-1,-1,26,-1,26,-1,-1,26,-1,-1,-1,-1,-1},
		   /*14*/{-1,-1,-1,-1,-1,-1,-1,-1,-1,21,-1,-1,-1,-1,-1},
		   /*15*/{-1,-1,-1,-1,-1,-1,-1, 6, 7,-1,-1,-1,-1,-1,17},
		   /*16*/{-1,-1,-1,18,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
		   /*17*/{-1,-1,-1,-1,25,-1,25,-1,-1,25,-1,-1,-1,-1,-1},
		   /*18*/{-1,-1,-1,-1,-1,-1,-1, 6, 7,-1,-1,-1,-1,19,13},
		   /*19*/{-1,-1,-1,-1,-1,-1,15,-1,-1,22,-1,-1,-1,-1,-1}
	};
		
	@SuppressWarnings("serial")
	private static final HashMap<Integer, Integer> SmenaNeterminal = new HashMap<Integer, Integer>() {{ // has side effects - see: https://stackoverflow.com/a/6802502/12162243
		put(1, Neterminal.IfStatement.getVal());
		put(2, Neterminal.ElsePart.getVal());
		put(3, Neterminal.RelExpression.getVal());
		put(4, Neterminal.RelExpression.getVal());
		put(5, Neterminal.Expression.getVal());
		put(6, Neterminal.Expression.getVal());
		put(7, Neterminal.Term.getVal());
		put(8, Neterminal.Term.getVal());
	}};
	
	@SuppressWarnings("serial")
	private static final HashMap<Integer, Integer> SmenaDuzinaDesneStrane = new HashMap<Integer, Integer>() {{ // has side effects - see: https://stackoverflow.com/a/6802502/12162243
		put(1, 7); // IS -> if ( RE ) : E EP
		put(2, 3); // EP -> else : E
		put(3, 3); // RE -> T > T
		put(4, 1); // RE -> T
		put(5, 3); // E -> E * T
		put(6, 1); // E -> T
		put(7, 1); // T -> ID
		put(8, 1); // T -> CONST
	}};

	
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
		radniMagacin.push(0); 
		
		do {
			Yytoken token = leksickiAnalizator.next_token();
			if (token.m_index == sym.LEXERR) {
				System.out.println("Error u leksickom analizatoru: " + token);
				return;
			}
			
			int akcija = -1;
			do {
				akcija = SintaksnaTabela[radniMagacin.peek()][token.m_index];
				if (akcija == -1) {
					Error(radniMagacin, token);
					return; // ne nastavljamo sa sintaksnom analizom ako nadjemo gresku
				}
				
				if (akcija == 0) { // accept
					Accept(radniMagacin);
					return;
				}
				
				if (akcija < 20) { // shift akcija
					Shift(radniMagacin, akcija, token);
				} else if (akcija < 29) { // reduce akcija
					Reduce(radniMagacin, akcija);
				}

			} while (akcija > 20); // ne uzimamo sledeci token se ne izvrse sve reduce akcije za trenutni
		} while (!leksickiAnalizator.yyatEOF());
	}

	private static void Reduce(Stack<Integer> radniMagacin, int akcija) {
		int smena = akcija - 20;
		int duzinaDesneStraneSmene = SmenaDuzinaDesneStrane.get(smena);
		for (int i = 0; i < duzinaDesneStraneSmene * 2; i++) {
			radniMagacin.pop();
		}
		int neterminalniSimbolZaSmenu = SmenaNeterminal.get(smena);
		int prelaz = SintaksnaTabela[radniMagacin.peek()][neterminalniSimbolZaSmenu];
		
		radniMagacin.push(neterminalniSimbolZaSmenu);
		radniMagacin.push(prelaz);
	}

	private static void Shift(Stack<Integer> radniMagacin, int akcija, Yytoken token) {
		radniMagacin.push(token.m_index);
		radniMagacin.push(akcija);
	}

	private static void Error(Stack<Integer> radniMagacin, Yytoken token) {
		System.out.println("Error u sintaksnom analizatoru. " + token);
	}

	private static void Accept(Stack<Integer> radniMagacin) {
		System.out.println("Accept!");
	}


}
