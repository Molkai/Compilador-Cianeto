
package comp;

import java.io.PrintWriter;
import java.util.ArrayList;
import ast.*;
import lexer.Lexer;
import lexer.Token;

public class Compiler {

	// compile must receive an input with an character less than
	// p_input.lenght
	public Program compile(char[] input, PrintWriter outError) {

		ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
		signalError = new ErrorSignaller(outError, compilationErrorList);
		symbolTable = new SymbolTable();
		lexer = new Lexer(input, signalError);
		signalError.setLexer(lexer);

		Program program = null;
		lexer.nextToken();
		program = program(compilationErrorList);
		return program;
	}

	private Program program(ArrayList<CompilationError> compilationErrorList) {
		// Program ::= CianetoClass { CianetoClass }
        ArrayList<MetaobjectAnnotation> metaobjectCallList = new ArrayList<>();
        ArrayList<CianetoClass> CianetoClassList = new ArrayList<>();
        Program program = new Program(CianetoClassList, metaobjectCallList, compilationErrorList);
        boolean thereWasAnError = false;
        while ( lexer.token == Token.CLASS ||
                (lexer.token == Token.ID && lexer.getStringValue().equals("open") ) ||
                lexer.token == Token.ANNOT ) {
            try {
                while ( lexer.token == Token.ANNOT ) {
                    metaobjectAnnotation(metaobjectCallList);
                }
                CianetoClass c = classDec();
                CianetoClassList.add(c);
            }
            catch( CompilerError e) {
                // if there was an exception, there is a compilation error
                thereWasAnError = true;
                while ( lexer.token != Token.CLASS && lexer.token != Token.EOF ) {
                    try {
                        next();
                    }
                    catch ( RuntimeException ee ) {
                        e.printStackTrace();
                        return program;
                    }
                }
            }
            catch ( RuntimeException e ) {
                e.printStackTrace();
                thereWasAnError = true;
            }

        }
        if ( !thereWasAnError && lexer.token != Token.EOF ) {
            try {
                error("End of file expected");
            }
            catch( CompilerError e) {
            }
        }
        return program;
	}

	/**  parses a metaobject annotation as <code>{@literal @}cep(...)</code> in <br>
     * <code>
     * @cep(5, "'class' expected") <br>
     * class Program <br>
     *     func run { } <br>
     * end <br>
     * </code>
     *

	 */
	@SuppressWarnings("incomplete-switch")
	private void metaobjectAnnotation(ArrayList<MetaobjectAnnotation> metaobjectAnnotationList) {
		String name = lexer.getMetaobjectName();
		int lineNumber = lexer.getLineNumber();
		lexer.nextToken();
		ArrayList<Object> metaobjectParamList = new ArrayList<>();
		boolean getNextToken = false;
		if ( lexer.token == Token.LEFTPAR ) {
			// metaobject call with parameters
			lexer.nextToken();
			while ( lexer.token == Token.LITERALINT || lexer.token == Token.LITERALSTRING ||
					lexer.token == Token.ID ) {
				switch ( lexer.token ) {
				case LITERALINT:
					metaobjectParamList.add(lexer.getNumberValue());
					break;
				case LITERALSTRING:
					metaobjectParamList.add(lexer.getLiteralStringValue());
					break;
				case ID:
					metaobjectParamList.add(lexer.getStringValue());
				}
				lexer.nextToken();
				if ( lexer.token == Token.COMMA )
					lexer.nextToken();
				else
					break;
			}
			if ( lexer.token != Token.RIGHTPAR )
				error("')' expected after metaobject call with parameters");
			else {
				getNextToken = true;
			}
		}
		if ( name.equals("nce") ) {
			if ( metaobjectParamList.size() != 0 )
				error("Metaobject 'nce' does not take parameters");
		}
		else if ( name.equals("cep") ) {
			if ( metaobjectParamList.size() != 3 && metaobjectParamList.size() != 4 )
				error("Metaobject 'cep' take three or four parameters");
			if ( !( metaobjectParamList.get(0) instanceof Integer)  ) {
				error("The first parameter of metaobject 'cep' should be an integer number");
			}
			else {
				int ln = (Integer ) metaobjectParamList.get(0);
				metaobjectParamList.set(0, ln + lineNumber);
			}
			if ( !( metaobjectParamList.get(1) instanceof String) ||  !( metaobjectParamList.get(2) instanceof String) )
				error("The second and third parameters of metaobject 'cep' should be literal strings");
			if ( metaobjectParamList.size() >= 4 && !( metaobjectParamList.get(3) instanceof String) )
				error("The fourth parameter of metaobject 'cep' should be a literal string");

		}
		metaobjectAnnotationList.add(new MetaobjectAnnotation(name, metaobjectParamList));
		if ( getNextToken ) lexer.nextToken();
	}

	private CianetoClass classDec() {
        boolean isOpen = false;
        CianetoClass superClass = null;
        if ( lexer.token == Token.ID && lexer.getStringValue().equals("open") ) {
              isOpen = true;
              next();
        }
        if ( lexer.token != Token.CLASS ) error("'class' expected");
        lexer.nextToken();
        if ( lexer.token != Token.ID )
            error("Identifier expected");
        String className = lexer.getStringValue();
        if(symbolTable.getInGlobal(className)!=null)
            error("Class "+className+" already exists.");
        lexer.nextToken();
        if ( lexer.token == Token.EXTENDS ) {
            lexer.nextToken();
            if ( lexer.token != Token.ID )
                error("Identifier expected");
            String superclassName = lexer.getStringValue();
            if(symbolTable.getInGlobal(superclassName)==null)
                error("Class "+superclassName+" does not exist.");
            superClass = (CianetoClass) symbolTable.getInGlobal(superclassName);
            if(!superClass.getOpen())
                error("Class "+superClass.getCname()+" can not be inherited.");
            CianetoClass auxSuperClass = superClass;
            do{
                ArrayList<Member> auxMemberList = auxSuperClass.getMemberList();
                for(int i=0; i<auxMemberList.size(); i++){
                    Member member = auxMemberList.get(i);
                    if(member instanceof Method && ((Method)member).getQualifier().isPublic() && !((Method)member).getQualifier().isFinal()){
                        symbolTable.putInSuperClass(((Method)member).getName(), member);
                    }
                }
                auxSuperClass = auxSuperClass.getSClass();
            }while(auxSuperClass!=null);
            lexer.nextToken();
        }

        ArrayList<Member> m = memberList(isOpen);
        if ( lexer.token != Token.END)
            error("'end' expected");
        lexer.nextToken();

        CianetoClass c = new CianetoClass(className, isOpen, superClass, m);

        symbolTable.putInGlobal(className, c);

        symbolTable.removeClassIdent();

        return c;

    }

	private ArrayList<Member> memberList(boolean isOpen) {
        ArrayList<Member> memberList = new ArrayList<Member>();
        while ( true ) {
            Qualifier q = qualifier();
            if ( lexer.token == Token.VAR ) {
                if(q==null)
                    q = new Qualifier(false, true, false, false);
                memberList.add(fieldDec(q));
            }
            else if ( lexer.token == Token.FUNC ) {
                if(q==null)
                    q = new Qualifier(true, false, false, false);
                memberList.add(methodDec(q, isOpen));
            }
            else {
                break;
            }
        }

        return memberList;
    }

	private void error(String msg) {
		this.signalError.showError(msg);
	}

	private void next() {
		lexer.nextToken();
	}

	private void check(Token shouldBe, String msg) {
		if ( lexer.token != shouldBe ) {
			error(msg);
		}
	}

	private Member methodDec(Qualifier q, boolean isOpen) {
		if(!isOpen && q.isFinal())
            this.error("A method cannot be final in this class.");
        lexer.nextToken();
        VariableList paramList = new VariableList();
        Type type = Type.undefinedType;
        String methodName = null;
		if ( lexer.token == Token.ID ) {
			// unary method
            methodName = lexer.getStringValue();
            if(symbolTable.getInClass(methodName)!=null)
                this.error("Name already in use in this class.");
			lexer.nextToken();

		}
		else if ( lexer.token == Token.IDCOLON ) {
			// keyword method. It has parameters
            methodName = lexer.getStringValue();
            if(symbolTable.getInClass(methodName)!=null)
                this.error("Name already in use in this class.");
            next();
            paramList = formalParamDec();
		}
		else {
			error("An identifier or identifer: was expected after 'func'");
		}
		if ( lexer.token == Token.MINUS_GT ) {
			// method declared a return type
			lexer.nextToken();
			type = type();
		}
        Method m = (Method) symbolTable.getInSuperClass(methodName);
        if(q.isOverride()){
            if(m==null)
                this.error("The method "+methodName+" does not exist in the super class.");
            else if(m.getQualifier().isFinal())
                this.error("The super method is final.");
            else if(!type.getName().equals(m.getReturnType().getName()))
                this.error("Return type is different from the super method return type.");
            else if(paramList.getSize()!=m.getParamList().getSize())
                this.error("Method signature is different from super method signature.");
            int i = 0;
            while(i<paramList.getSize()){
                if(!paramList.get(i).getType().getName().equals(m.getParamList().get(i).getType().getName()))
                    this.error("Method signature is different from super method signature.");
            }
        }
        else if(m!=null)
            this.error("The method "+methodName+" is already defined in a super class.");
		if ( lexer.token != Token.LEFTCURBRACKET ) {
			error("'{' expected");
		}
		next();
        currentMethodType = type;
        hasReturn = false;
		ArrayList<Statement> s = statementList();
        if(currentMethodType!=Type.undefinedType && !hasReturn)
            this.error("Method does not have a return.");
		if ( lexer.token != Token.RIGHTCURBRACKET ) {
			error("'}' expected");
		}
        Method method = new Method(methodName, paramList, type, s, q);
        symbolTable.putInClass(methodName, method);
        symbolTable.removeLocalIdent();
		next();
        return method;
	}

    private VariableList formalParamDec(){
        VariableList variableList = new VariableList();
        Variable v;
        Type t = type();
        if( lexer.token != Token.ID )
            this.error("A variable name was expected");
        if(symbolTable.getInLocal(lexer.getStringValue())!=null)
            this.error("Variable already declared.");
        v = new Variable(lexer.getStringValue(), t);
        symbolTable.putInLocal(v.getName(), v);
        variableList.add(v);
        next();
        while ( lexer.token == Token.COMMA  ) {
            lexer.nextToken();
            t = type();
            if ( lexer.token != Token.ID )
                this.error("A variable name was expected");
            if(symbolTable.getInLocal(lexer.getStringValue())!=null)
                this.error("Variable already declared.");
            v = new Variable(lexer.getStringValue(), t);
            symbolTable.putInLocal(v.getName(), v);
            variableList.add(v);
            next();
        }
        return variableList;
    }

	private ArrayList<Statement> statementList() {
		  // only '}' is necessary in this test
        ArrayList<Statement> statementList = new ArrayList<>();
		while ( lexer.token != Token.RIGHTCURBRACKET && lexer.token != Token.END ) {
			statementList.add(statement());
		}
        return statementList;
	}

	private Statement statement() {
        Statement s;
		boolean checkSemiColon = true;
		switch ( lexer.token ) {
		case IF:
			s = ifStat();
			checkSemiColon = false;
			break;
		case WHILE:
			s = whileStat();
			checkSemiColon = false;
			break;
		case RETURN:
			s = returnStat();
			break;
		case BREAK:
			s = breakStat();
			break;
		case REPEAT:
			s = repeatStat();
			break;
		case VAR:
			s = localDec();
			break;
		case ASSERT:
			s = assertStat();
			break;
		default:
			if ( lexer.token == Token.ID && lexer.getStringValue().equals("Out") ) {
				s = writeStat();
			}
			else {
                isVariable= false;
				Type t1 = expr();
                Type t2 = null;
                if(lexer.token==Token.ASSIGN){
                    if(!isVariable)
                        this.error("Expected variable on left-hand side of assignment.");
                    next();
                    t2 = expr();
                    if(!isTypeCompatible(t1, t2))
                        this.error("The two expressions have uncompatible type.");
                } else if(t1!=Type.undefinedType)
                    this.error("The expression must not have a return.");
                s = new AssignStat(t1, t2);
			}

		}
		if ( checkSemiColon ) {
			check(Token.SEMICOLON, "';' expected");
            next();
		}

        return s;
	}

	private LocalVariableList localDec() {
        LocalVariableList variableList = new LocalVariableList();
		next();
		Type t1 = type();
		check(Token.ID, "A variable name was expected");
        if(symbolTable.getInLocal(lexer.getStringValue())!=null)
            this.error("Name already in use.");
        Variable v = new Variable(lexer.getStringValue(), t1);
        variableList.add(v);
        symbolTable.putInLocal(v.getName(), v);
        next();
		while ( lexer.token == Token.COMMA ) {
			next();
			check(Token.ID, "A variable name was expected");
            if(symbolTable.getInLocal(lexer.getStringValue())!=null)
                this.error("Name already in use.");
            v = new Variable(lexer.getStringValue(), t1);
            variableList.add(v);
            symbolTable.putInLocal(v.getName(), v);
			next();
		}
		if ( lexer.token == Token.ASSIGN ) {
			next();
			// check if there is just one variable
			Type t2 = expr();
            if(!isTypeCompatible(t1, t2))
                this.error("The two expressions have uncompatible type.");
		}

        return variableList;

	}

	private RepeatStat repeatStat() {
		next();
        RepeatStat r = new RepeatStat();
        isInsideLoop = true;
		while ( lexer.token != Token.UNTIL && lexer.token != Token.RIGHTCURBRACKET && lexer.token != Token.END ) {
			r.add(statement());
		}
        isInsideLoop = false;
		check(Token.UNTIL, "'until' was expected");
        next();
        Type t = expr();
        if(t!=Type.booleanType)
            this.error("Condition must be of type boolean.");
        return r;
	}

	private BreakStat breakStat() {
		next();
        if(!isInsideLoop)
            this.error("The break statement must be inside a loop.");
        return new BreakStat();
	}

	private ReturnStat returnStat() {
		next();
		Type t1 = expr();
        if(!isTypeCompatible(t1, currentMethodType))
            this.error("The two expressions have uncompatible type.");
        hasReturn = true;
        return new ReturnStat();
	}

	private WhileStat whileStat() {
        WhileStat w = new WhileStat();
		next();
        if(expr()!=Type.booleanType)
            this.error("Condition must be of type boolean.");
		check(Token.LEFTCURBRACKET, "'{' expected after the 'while' expression");
		next();
        isInsideLoop = true;
		while ( lexer.token != Token.RIGHTCURBRACKET && lexer.token != Token.END ) {
			w.add(statement());
		}
        isInsideLoop = false;
		check(Token.RIGHTCURBRACKET, "'}' was expected");
        next();
        return w;
	}

	private IfStat ifStat() {
        IfStat s = new IfStat();
		next();
        if(expr()!=Type.booleanType)
            this.error("Condition must be of type boolean.");
		check(Token.LEFTCURBRACKET, "'{' expected after the 'if' expression");
		next();
		while ( lexer.token != Token.RIGHTCURBRACKET && lexer.token != Token.END && lexer.token != Token.ELSE ) {
			s.add(statement());
		}
		check(Token.RIGHTCURBRACKET, "'}' was expected");
        next();
		if ( lexer.token == Token.ELSE ) {
			next();
			check(Token.LEFTCURBRACKET, "'{' expected after 'else'");
			next();
			while ( lexer.token != Token.RIGHTCURBRACKET ) {
				s.add(statement());
			}
			check(Token.RIGHTCURBRACKET, "'}' was expected");
            next();
		}

        return s;
	}

	/**

	 */
	private WriteStat writeStat() {
		next();
		check(Token.DOT, "a '.' was expected after 'Out'");
		next();
		check(Token.IDCOLON, "'print:' or 'println:' was expected after 'Out.'");
		String printName = lexer.getStringValue();
        next();
		Type t = expr();
        if(t!=Type.intType && t!=Type.stringType)
            this.error("Expression type must be int or string.");
        return new WriteStat(t);
	}

	private Type expr() {
        Type t1 = simpleExpr();
        if(lexer.token==Token.EQ || lexer.token==Token.GT || lexer.token==Token.GE ||
            lexer.token==Token.LT || lexer.token==Token.LE || lexer.token==Token.NEQ){
            Token op = lexer.token;
            next();
            Type t2 = simpleExpr();
            if(t1==Type.undefinedType || t2==Type.undefinedType)
                this.error("Cant compare "+t1.getName()+" and "+t2.getName()+" types.");
            if(t1==Type.intType && t2==t1)
                return Type.booleanType;
            if(op==Token.EQ || op==Token.NEQ){
                if(t1==Type.booleanType){
                    if(t1!=t2)
                        this.error("Cant compare "+t1.getName()+" and "+t2.getName()+" types.");
                    return Type.booleanType;
                }
                if(t1==Type.stringType){
                    if(t2!=Type.nullType && t1!=t2)
                        this.error("Cant compare "+t1.getName()+" and "+t2.getName()+" types.");
                    return Type.booleanType;
                }
                if(t1==Type.nullType){
                    if(t2!=Type.stringType && (t2==Type.intType || t2==Type.booleanType))
                        this.error("Cant compare "+t1.getName()+" and "+t2.getName()+" types.");
                    return Type.booleanType;
                }
                else{
                    if(t2==Type.nullType)
                        return Type.booleanType;
                    CianetoClass c = (CianetoClass) symbolTable.getInGlobal(t1.getName());
                    do{
                        if(c.getName().equals(t2.getName()))
                            return Type.booleanType;
                        c = c.getSClass();
                    }while(c!=null);
                    c = (CianetoClass) symbolTable.getInGlobal(t2.getName());
                    do{
                        if(c.getName().equals(t1.getName()))
                            return Type.booleanType;
                        c = c.getSClass();
                    }while(c!=null);
                    this.error("Cant compare "+t1.getName()+" and "+t2.getName()+" types.");
                }
            }
            this.error("Cant compare "+t1.getName()+" and "+t2.getName()+" types using this operator.");
        }
        return t1;
	}

    private Type simpleExpr(){
        boolean isConcat = false;
        Type t1 = sumSubExpr();
        while(lexer.token==Token.CONCAT){
            isConcat = true;
            next();
            Type t2 = sumSubExpr();
            if((t1!=Type.stringType || t1!=Type.intType) && (t2!=Type.stringType || t2!=Type.intType))
                this.error("Cant concat "+t1.getName()+" and "+t2.getName()+" types.");
        }
        if(isConcat)
            return Type.stringType;
        return t1;
    }

    private Type sumSubExpr(){
        Type t1 = term();
        while(lexer.token==Token.PLUS || lexer.token==Token.MINUS || lexer.token==Token.OR){
            Token op = lexer.token;
            next();
            Type t2 = term();
            if(op==Token.OR){
                if(t1!=Type.booleanType || t1!=t2)
                    this.error("Cant use or on "+t1.getName()+" and "+t2.getName()+" types.");
            }
            else{
                if(t1!=Type.intType || t1!=t2)
                    this.error("Cant use + or - on "+t1.getName()+" and "+t2.getName()+" types.");
            }
            t1 = t2;
        }
        return t1;
    }

    private Type term(){
        Type t1 = signalFactor();
        while(lexer.token==Token.MULT || lexer.token==Token.DIV || lexer.token==Token.AND){
            Token op = lexer.token;
            next();
            Type t2 = signalFactor();
            if(op==Token.AND){
                if(t1!=Type.booleanType || t1!=t2)
                    this.error("Cant use and on "+t1.getName()+" and "+t2.getName()+" types.");
            }
            else{
                if(t1!=Type.intType || t1!=t2)
                    this.error("Cant use * or / on "+t1.getName()+" and "+t2.getName()+" types.");
            }
            t1 = t2;
        }
        return t1;
    }

    private Type signalFactor(){
        if(lexer.token==Token.PLUS || lexer.token==Token.MINUS)
            next();
        return factor();
    }

    private Type factor(){
        Type t = null;
        if(lexer.token==Token.LITERALINT || lexer.token==Token.LITERALSTRING ||
            lexer.token==Token.TRUE || lexer.token==Token.FALSE){
            if(lexer.token==Token.LITERALINT)
                t = Type.intType;
            else if(lexer.token==Token.LITERALSTRING)
                t = Type.stringType;
            else
                t = Type.booleanType;
            next();
        }
        else if(lexer.token==Token.LEFTPAR){
            next();
            t = expr();
            if(lexer.token!=Token.RIGHTPAR)
                this.error("')' was expected");
            next();
        }
        else if(lexer.token==Token.NOT){
            next();
            t = factor();
            if(t!=Type.booleanType)
                this.error("Cant use ! on "+t.getName()+" type.");
        }
        else if(lexer.token==Token.SUPER || lexer.token==Token.SELF){
            t = primaryExpr();
        }
        else if(lexer.token==Token.ID){
            if(lexer.getStringValue().equals("In"))
                t = readExpr();
            else{
                Object o = symbolTable.get(lexer.getStringValue());
                if(o==null)
                    this.error("The identifier "+lexer.getStringValue()+" does not exist.");
                if(o instanceof CianetoClass){
                    t = new Type(((CianetoClass) o).getName());
                    next();
                    if(lexer.token!=Token.DOT)
                        this.error("'.' expected");
                    next();
                    if(lexer.token!=Token.NEW)
                        this.error("'new' expected");
                    next();
                }
                else{
                    t = ((Variable) o).getType();
                    next();
                    if(lexer.token==Token.DOT){
                        next();
                        t = primaryExpr();
                    }
                    else
                        isVariable = true;
                }
            }
        }
        else if(lexer.token!=Token.NULL)
            this.error("Expression expected");
        else{
            t = Type.nullType;
            next();
        }
        return t;
    }

    private Type readExpr(){
        Type t = null;
        next();
        if(lexer.token!=Token.DOT)
            this.error("'.' was expected");
        next();
        if ( lexer.token == Token.ID &&
            (lexer.getStringValue().equals("readInt") ||  lexer.getStringValue().equals("readString"))){
            if(lexer.getStringValue().equals("readInt"))
                t = Type.intType;
            else
                t = Type.stringType;
            next();
        }
        else{
            this.error("'readInt' or 'readString' expected");
        }
        return t;
    }

    private Type primaryExpr(){
        Type t;
        String methodName = null;
        ArrayList<Type> exprList = new ArrayList<>();
        if(lexer.token==Token.SUPER){
            next();
            if(lexer.token!=Token.DOT)
                this.error("'.' was expected");
            next();
            if(lexer.token==Token.IDCOLON){
                methodName = lexer.getStringValue();
                next();
                exprList = expressionList();
            }
            else if(lexer.token!=Token.ID)
                this.error("Identifer was expected");
            else{
                methodName = lexer.getStringValue();
                next();
            }
            Method m = (Method) symbolTable.getInSuperClass(methodName);
            if(m==null)
                this.error("Method "+methodName+" does not exist in super class.");
            if(exprList.size()!=m.getParamList().getSize())
                this.error("Method signature is different from super method signature.");
            int i = 0;
            while(i<exprList.size()){
                if(!exprList.get(i).getName().equals(m.getParamList().get(i).getType().getName()))
                    this.error("Expression type is different from method signature.");
            }
            t = m.getReturnType();
        }
        else if(lexer.token==Token.SELF){
            boolean isMethod = false;
            Method m = null;
            Variable v = null;
            next();
            if(lexer.token==Token.DOT){
                next();
                if(lexer.token==Token.IDCOLON){
                    methodName = lexer.getStringValue();
                    next();
                    exprList = expressionList();
                    m = (Method) symbolTable.getInClass(methodName);
                    isMethod = true;
                }
                else if(lexer.token==Token.ID){
                    String idName = lexer.getStringValue();
                    //methodName = lexer.getStringValue();
                    Object o = symbolTable.getInClass(idName);
                    next();
                    if(o==null)
                        this.error("Id "+idName+" does not exist in class.");
                    if(o instanceof Method){
                        isMethod = true;
                        m = (Method) o;
                    }
                    else if(o instanceof Variable){
                        v = (Variable) o;
                        if(lexer.token==Token.DOT){
                            isMethod = true;
                            if(v.getType()==Type.intType || v.getType()==Type.booleanType
                                || v.getType()==Type.stringType)
                                this.error("Nonono");
                            next();
                            if(lexer.token==Token.IDCOLON){
                                methodName = lexer.getStringValue();
                                next();
                                exprList = expressionList();
                            }
                            else if(lexer.token!=Token.ID)
                                this.error("Identifer was expected");
                            else{
                                methodName = lexer.getStringValue();
                                next();
                            }
                            m = null;
                            CianetoClass c = (CianetoClass) symbolTable.getInGlobal(v.getType().getName());
                            while(c!=null){
                                ArrayList<Member> memberList = c.getMemberList();
                                int i = 0;
                                while(i < memberList.size()){
                                    if(memberList.get(i) instanceof Method)
                                        if(((Method) memberList.get(i)).getName().equals(methodName)){
                                            m = (Method) memberList.get(i);
                                            break;
                                        }
                                    i++;
                                }
                                if(i<memberList.size())
                                    break;
                                c = c.getSClass();
                            }
                        }
                    }
                    else
                        this.error("Something.");
                }
            }
            if(isMethod){
                if(m==null)
                    this.error("Method "+methodName+" does not exist in super class.");
                if(exprList.size()!=m.getParamList().getSize())
                    this.error("Method signature is different from super method signature.");
                int i = 0;
                while(i<exprList.size()){
                    if(!exprList.get(i).getName().equals(m.getParamList().get(i).getType().getName()))
                        this.error("Expression type is different from method signature.");
                }
                t = m.getReturnType();
            }
            else
                t = v.getType();
        }

        else{
            if(lexer.token==Token.IDCOLON){
                methodName = lexer.getStringValue();
                next();
                exprList = expressionList();
            }
            else if(lexer.token!=Token.ID)
                this.error("Identifer was expected");
            else{
                methodName = lexer.getStringValue();
                next();
            }
            Method m = (Method) symbolTable.getInClass(methodName);
            if(m==null)
                this.error("Method "+methodName+" does not exist in any class.");
            if(exprList.size()!=m.getParamList().getSize())
                this.error("Method signature is different from super method signature.");
            int i = 0;
            while(i<exprList.size()){
                if(!exprList.get(i).getName().equals(m.getParamList().get(i).getType().getName()))
                    this.error("Expression type is different from method signature.");
            }
            t = m.getReturnType();
        }

        return t;
    }

    private ArrayList<Type> expressionList(){
        ArrayList<Type> exprList = new ArrayList<>();
        exprList.add(expr());
        while(lexer.token==Token.COMMA){
            next();
            exprList.add(expr());
        }
        return exprList;
    }

    private Member fieldDec(Qualifier q) {
        lexer.nextToken();
        if(q!=null && !q.isPrivate())
            this.error("Variable must be private.");
        VariableList variableList = new VariableList();
        Type t = type();
        if ( lexer.token != Token.ID ) {
            this.error("A variable name was expected");
        }
        else {
            while ( lexer.token == Token.ID  ) {
                if(symbolTable.getInClass(lexer.getStringValue())!=null)
                    this.error("Name already in use.");
                Variable v = new Variable(lexer.getStringValue(), t);
                variableList.add(v);
                symbolTable.putInClass(v.getName(), v);
                lexer.nextToken();
                if ( lexer.token == Token.COMMA ) {
                    lexer.nextToken();
                }
                else {
                    break;
                }
            }
        }

        if(lexer.token==Token.SEMICOLON)
            next();

        return variableList;

    }

	private Type type() {
        Type t = null;
        if ( lexer.token == Token.INT || lexer.token == Token.BOOLEAN || lexer.token == Token.STRING ) {
            if(lexer.token == Token.BOOLEAN)
                t = Type.booleanType;
            else if(lexer.token == Token.INT)
                t = Type.intType;
            else
                t = Type.stringType;
            next();
        }
        else if ( lexer.token == Token.ID ) {
            if(symbolTable.getInGlobal(lexer.getStringValue())==null)
                error("Class "+lexer.getStringValue()+" does not exist");
            t = new Type(lexer.getStringValue());
            next();
        }
        else {
            this.error("A type was expected");
        }
        return t;

    }


    private Qualifier qualifier() {
        if ( lexer.token == Token.PRIVATE ) {
            next();
            return new Qualifier(false, true, false, false);
        }
        else if ( lexer.token == Token.PUBLIC ) {
            next();
            return new Qualifier(true, false, false, false);
        }
        else if ( lexer.token == Token.OVERRIDE ) {
            next();
            if ( lexer.token == Token.PUBLIC ) {
                next();
            }
            return new Qualifier(true, false, false, true);
        }
        else if ( lexer.token == Token.FINAL ) {
            next();
            if ( lexer.token == Token.PUBLIC ) {
                next();
                return new Qualifier(true, false, true, false);
            }
            else if ( lexer.token == Token.OVERRIDE ) {
                next();
                if ( lexer.token == Token.PUBLIC ) {
                    next();
                }
                return new Qualifier(true, false, true, true);
            }
        }
        return null;
    }
	/**
	 * change this method to 'private'.
	 * uncomment it
	 * implement the methods it calls
	 */
	public AssertStat assertStat() {

		lexer.nextToken();
		int lineNumber = lexer.getLineNumber();
		if(expr()!=Type.booleanType)
            this.error("Expression must be of type boolean.");
		if ( lexer.token != Token.COMMA ) {
			this.error("',' expected after the expression of the 'assert' statement");
		}
		lexer.nextToken();
		if ( lexer.token != Token.LITERALSTRING ) {
			this.error("A literal string expected after the ',' of the 'assert' statement");
		}
		String message = lexer.getLiteralStringValue();
		lexer.nextToken();
		/*if ( lexer.token == Token.SEMICOLON )
			lexer.nextToken();*/

		return new AssertStat(message);
	}




	private LiteralInt literalInt() {

		LiteralInt e = null;

		// the number value is stored in lexer.getToken().value as an object of
		// Integer.
		// Method intValue returns that value as an value of type int.
		int value = lexer.getNumberValue();
		lexer.nextToken();
		return new LiteralInt(value);
	}

	private static boolean startExpr(Token token) {

		return token == Token.FALSE || token == Token.TRUE
				|| token == Token.NOT || token == Token.SELF
				|| token == Token.LITERALINT || token == Token.SUPER
				|| token == Token.LEFTPAR || token == Token.NULL
				|| token == Token.ID || token == Token.LITERALSTRING;

	}

    private boolean isTypeCompatible(Type t1, Type t2){
        if(t1==Type.undefinedType || t2==Type.undefinedType)
            return false;
        if(t1.getName().equals(t2.getName()))
            return true;

        if(t1!=Type.booleanType && t2!=Type.booleanType && t1!=Type.intType && t2!=Type.intType){
            if(t1==Type.nullType && t2==Type.nullType)
                return false;
            if(t1==Type.nullType || t2==Type.nullType)
                return true;
            if(t1==Type.stringType || t2==Type.stringType)
                return false;
            CianetoClass c = (CianetoClass) symbolTable.getInGlobal(t1.getName());
            do{
                if(c.getName().equals(t2.getName()))
                    return true;
                c = c.getSClass();
            }while(c!=null);
            c = (CianetoClass) symbolTable.getInGlobal(t2.getName());
            do{
                if(c.getName().equals(t1.getName()))
                    return true;
                c = c.getSClass();
            }while(c!=null);
        }
        return false;
    }

	private SymbolTable		symbolTable;
	private Lexer			lexer;
	private ErrorSignaller	signalError;
    private boolean         isInsideLoop;
    private Type            currentMethodType;
    private boolean         hasReturn;
    private boolean         isVariable;

}
