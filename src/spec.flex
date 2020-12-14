// import sekcija

%%

// sekcija opcija i deklaracija
%class GrupaDvaLexer
%function next_token
%line
%column
%debug
%type Yytoken

%eofval{
return new Yytoken( sym.EOF, null, yyline, yycolumn);
%eofval}

%{
//dodatni clanovi generisane klase
KWTable kwTable = new KWTable();
Yytoken getKW()
{
	return new Yytoken( kwTable.find( yytext() ),
	yytext(), yyline, yycolumn );
}
%}

//stanja
%xstate KOMENTAR
//makroi
slovo = [a-zA-Z]
cifra = [0-9]

%%

// pravila
\/\*\* { yybegin( KOMENTAR ); }
<KOMENTAR>~"*/" { yybegin( YYINITIAL ); }

// beli znaci
[\t\n\r ] { ; }

// zagrade
\( { return new Yytoken( sym.LEFTPAR, yytext(), yyline, yycolumn ); }
\) { return new Yytoken( sym.RIGHTPAR, yytext(), yyline, yycolumn ); }
\{ { return new Yytoken( sym.LEFTCURLY, yytext(), yyline, yycolumn ); }
\} { return new Yytoken( sym.RIGHTCURLY, yytext(), yyline, yycolumn ); }

//relacioni operatori
\< { return new Yytoken( sym.LESS,yytext(), yyline, yycolumn ); }
\<= { return new Yytoken( sym.LESSEQ,yytext(), yyline, yycolumn ); }
== { return new Yytoken( sym.EQ,yytext(), yyline, yycolumn ); }
\<\> { return new Yytoken( sym.NOTEQ,yytext(), yyline, yycolumn ); }
\> { return new Yytoken( sym.GREATER,yytext(), yyline, yycolumn ); }
\>= { return new Yytoken( sym.GREATEREQ,yytext(), yyline, yycolumn ); }

// operatori
\* { return new Yytoken( sym.MULTIPLY,yytext(), yyline, yycolumn ); }

//separatori
; { return new Yytoken( sym.SEMICOLON, yytext(), yyline, yycolumn ); }
: { return new Yytoken( sym.COLON, yytext(), yyline, yycolumn ); }
:= { return new Yytoken( sym.ASSIGN, yytext(), yyline, yycolumn ); }

//kljucne reci, po neke konstante i neki identifikatori
{slovo}+ { return getKW(); }

//identifikatori
{slovo}({slovo}|{cifra})* { return new Yytoken(sym.ID, yytext(),yyline, yycolumn ); }

//konstante
//int
(2#[0-1]+)|(3#[0-2]+)|(4#[0-3]+)|(5#[0-4]+)|(6#[0-5]+)|(7#[0-6]+)|(8#[0-7]+)|(9#[0-8]+)|((16)?#[0-9A-F]+)|(11#[0-9A]+)|(12#[0-9A-B]+)|(13#[0-9A-C]+)|(14#[0-9A-D]+)|(15#[0-9A-E]+)|((10#)?[0-9]+) { return new Yytoken( sym.CONST, yytext(), yyline, yycolumn ); }
//real
{cifra}+\.{cifra}*(E[+-]{cifra}+)* { return new Yytoken( sym.CONST, yytext(), yyline, yycolumn ); }

//obrada gresaka
. { if (yytext() != null && yytext().length() > 0) return new Yytoken( sym.LEXERR, yytext(), yyline, yycolumn ); }
