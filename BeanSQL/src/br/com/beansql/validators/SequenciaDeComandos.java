
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.validators;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.beansql.control.ALIAS;
import br.com.beansql.exceptions.BeanSQLException;
import br.com.beansql.exceptions.TipoDadosBeanSQLException;
import br.com.beansql.model.ConjuntoTiposDeDados;

public class SequenciaDeComandos {

	private SequenciaDeComandos() {
		
	}
	/**
	 * Method verifica se o argumento informado � valido com uma express�o regular
	 * pelo padr�o [a-zA-Z]\w+
	 * @param String args
	 * @return boolean
	 */
	private static boolean isValidArgs(String args){
		//vlida��o com express�o regular - regex
		//[a-zA-Z]\w+ 1� letra entre a-z e A-Z depois Qualquer letra, d�gito ou underscore ( _ )
		
		Pattern padrao = Pattern.compile("[a-zA-Z]\\w*");
		Matcher pesquisa = padrao.matcher(args);
		if(pesquisa.matches()){
			return true;
		}else{
			//System.out.println("nao foi valido o args SequenciaDeComandos");
			return false;
		}
	}
	
	/**
	 * Method verifica se os valores s�o v�lidos dentro dos parenteses
	 * @param valores, nameOfTabela
	 * @return ResponseCommands
	 * @throws TipoDadosBeanSQLException, IndexOutOfBoundsException 
	 */
	private static ResponseCommands verifyValoresEmParenteses(String valores, String[] nameOfTabela) throws TipoDadosBeanSQLException, IndexOutOfBoundsException{
		ResponseCommands response = null;
		//remove espa�os com trim
		String semEspacos = valores.substring(0, valores.length()-1).trim(); //temos certeza que o ultimo � ";"
		valores = semEspacos + ";";
		if(valores.substring(0,1).equals("(") && valores.substring(valores.length()-2, valores.length()).equals(");")){	
			String nova = valores.substring(1, valores.length()-2);	//nova String recebe os valores sem parenteses
			String[] arrayValores = nova.split(",");		//separa em tokens pela virgula	
			
			for(int i = 0; i < arrayValores.length; i++){
				arrayValores[i] = arrayValores[i].trim();//tirando os espa�oes da esquerda e da direita
			}
			return response = new ResponseCommands(Command.INSERT_INTO_VALUES, arrayValores, null, nameOfTabela, null, null);			
		}		
		return response;
	}
			
	/**
	 * Method verifica se os atributos s�o v�lidos dentro dos parenteses
	 * @param atributos, nameOfTabela
	 * @return ResponseCommands
	 * @throws TipoDadosBeanSQLException, IndexOutOfBoundsException 
	 */
	private static ResponseCommands verifyAtributosEmParenteses(String atributos, String[] nameOfTabela) throws TipoDadosBeanSQLException, IndexOutOfBoundsException{
		ResponseCommands response = null;
		//remove espa�os com trim
		String semEspacos = atributos.substring(0, atributos.length()-1).trim(); //temos certeza que o ultimo � ";"
		atributos = semEspacos + ";";
		if(atributos.substring(0,1).equals("(") && atributos.subSequence(atributos.length()-2, atributos.length()).equals(");")){		
			String nova = atributos.substring(1, atributos.length()-2);	//nova String recebe os atributos sem parenteses
			String[] arrayVirgula = nova.split(",");		//separa em tokens pela virgula
			String[] argumentos = new String[arrayVirgula.length]; //argumentos com valores dos nomes dos atributos com tamanho pelas quantidades de ","
			ConjuntoTiposDeDados[] arrayConjunto = new ConjuntoTiposDeDados[arrayVirgula.length]; //conjunto dos tipos de dados para cada argumento
			
			StringTokenizer token = null;			
			for(int i = 0; i < arrayVirgula.length; i++){
				token = new StringTokenizer(arrayVirgula[i]);			//novo token criado para armazenar cada linha entre as virgulas // elimina espa�os
				String[] arrayToken = new String[token.countTokens()];	//novo array de Strings para receber os tokens de cada linha para melhor acesso
				
				for(int j = 0; j < arrayToken.length; j++){
					arrayToken[j] = token.nextToken();		//array armazena uma linha de dados entre a virgula ex: "id integer not null" ,					
				}
				
				//utilizar Try para IndexOutOfBoundsException, pois manipula array sem valida��o de indices
				try{
					if(!PalavrasChavesUnicas.isWord(arrayToken[0])){
						if(isValidArgs(arrayToken[0])){							
							//arrayToken[0] � um atributo v�lido para argumento, todos arrayToken[0] � o nome do atributo da tabela
							argumentos[i] = arrayToken[0]; //argumentos na posi��o atual de i pelas virgulas recebe o atributo arrayToken[0]
														
							//validar ConjuntoTiposDeDados
							ConjuntoTiposDeDados conj = new ConjuntoTiposDeDados();							
							conj.setTipo(arrayToken[1]);	//adiciona o tipo referente ao atributo, � obrigat�rio ter um tipo, se n�o lan�a Exception												 
							//System.out.println("Atributos v�lidos entre ( ) e ; atributo="+arrayToken[0]+" tipo="+arrayToken[1]);
							
							for(int k = 2; k < arrayToken.length; k++){	//k come�a com 2 pois 0 j� � o nome do pr�prio atributo, obs arrayToken[1] � o tipo varchar, integer etc
								try{
									conj.setOthersNullUniquePK(arrayToken[k]);//tenta adicionar os tipos no conjunto se for v�lidos e se tiver mais extras
								}catch(TipoDadosBeanSQLException e){
									//caso o tipo de dados informado n�o exista, tenta pegar a pr�xima string e verifica se existe espa�amento
									//exemplo: comando composto > not_null tamb�m pode ser informado como not null ou primary_key como primary key
									if(k+1 <= arrayToken.length-1){	//posso ir at� a ultima posic��o do arrayToken para verificar se o pr�ximo comando � composto										
										conj.setOthersNullUniquePK(arrayToken[k] + " " + arrayToken[k+1]); // se n�o achar vai retornar novamente outro throw										
										k++;	//caso seja composto e n�o lan�ar exception, incrementa k++;
									}else{
										//se for o ultimo comando inv�lido, n�o � composto
										throw new TipoDadosBeanSQLException();
									}
								}								
							}
							
							arrayConjunto[i] = conj;	//adiciona no indice atual o cojunto de tipos de dados depois que setar o tipo e others extras
						}else{
							//System.out.println("nome do atributo n�o � valido ou erro syntax="+arrayToken[0]);
							String erro = "invalid arguments format near '" + arrayToken[0] + "'" ;
							return new ResponseCommands(Command.ERROR_IN, erro);
						}
					}else{
						//System.out.println("nome do atributo n�o � valido key word="+arrayToken[0]);
						String[] args = {arrayToken[0]};
						return new ResponseCommands(Command.KEY_WORD_RESERVED, args, null, null, null, null);
					}
				}catch(IndexOutOfBoundsException in){
					//pode lan�ar uma exece��o de manipula��o de arrays
					//System.out.println("lan�ou IndexOutOfBoundsException");
					return null;
				}catch(TipoDadosBeanSQLException tipoEx){
					//pode lan�ar um CommandSQL
					//System.out.println("lan�ou CommandTipoDadosBeanSQLException");
					return null;
				}
				if(i == arrayVirgula.length-1){ //se for a ultimo loop de i, retorna com sucesso todos os argumentos, conjunto de tipos de dados e o Command valido					
					return response = new ResponseCommands(Command.CREATE_TABLE, argumentos, arrayConjunto, nameOfTabela, null, null);
				}
			}			
		}
		return response;
	}
	
	/**
	 * Methdo realiza uma busca de palavras chaves separando cada sequencia de comandos e verificando possiveis erros de sintaxe do banco e retorando o Command encontrado
	 * @param sequencia
	 * @return Command
	 * @throws BeanSQLException 
	 */
	public static ResponseCommands sortSequenciaDeComandos(String sequencia) throws BeanSQLException {		
		//ResponseCommands response = null;
		StringTokenizer token = new StringTokenizer(sequencia);
		String[] arrayToken = new String[token.countTokens()];	
		
		for(int i = 0; i < arrayToken.length; i++){
			arrayToken[i] = token.nextToken();			
		}
					
		//SHOWS
		try{			
			if(arrayToken.length >= 2 && arrayToken.length <= 3){
				if(arrayToken[0].equalsIgnoreCase("SHOW")){
					try{
						if(arrayToken[1].equalsIgnoreCase("DATABASES;")){
							return new ResponseCommands(Command.SHOW_DATABASES, null, null, null, null, null);
						}else if(arrayToken[1].equalsIgnoreCase("DATABASES") && arrayToken[2].equalsIgnoreCase(";")){
							return new ResponseCommands(Command.SHOW_DATABASES, null, null, null, null, null);
						}
						
						if(arrayToken[1].equalsIgnoreCase("TABLES;")){
							return new ResponseCommands(Command.SHOW_TABLES, null, null, null, null, null);
						}else if(arrayToken[1].equalsIgnoreCase("TABLES") && arrayToken[2].equalsIgnoreCase(";")){
							return new ResponseCommands(Command.SHOW_TABLES, null, null, null, null, null);
						}
					}catch(IndexOutOfBoundsException index){
						//pode lan�ar uma exception out bounds in arrayToken[2]...
					}
				}
			}
			//EXIT AND QUIT
			if(arrayToken.length == 1 ){
				if(arrayToken[0].equalsIgnoreCase("EXIT;") || arrayToken[0].equalsIgnoreCase("QUIT;") ){
					return new ResponseCommands(Command.EXIT, null);
				}
			}
			if(arrayToken.length == 2){
				if((arrayToken[0].equalsIgnoreCase("EXIT") && arrayToken[1].equalsIgnoreCase(";"))
						|| (arrayToken[0].equalsIgnoreCase("QUIT") && arrayToken[1].equalsIgnoreCase(";"))){
					return new ResponseCommands(Command.EXIT, null);
				}
			}
			
			//USE
			if(arrayToken.length >= 2 && arrayToken.length <= 3){
				if(arrayToken[0].equalsIgnoreCase("USE")){
					try{
						if(arrayToken[1].substring(arrayToken[1].length()-1,arrayToken[1].length()).equals(";")){
							String[] argumentos = {arrayToken[1].substring(0,arrayToken[1].length()-1)};
							return new ResponseCommands(Command.USE, argumentos, null, null, null, null);
						}else if(arrayToken[2].equalsIgnoreCase(";")){
							String[] argumentos = {arrayToken[1]};
							return new ResponseCommands(Command.USE, argumentos, null, null, null, null);
						}
					}catch(IndexOutOfBoundsException e){
						//metodo pode lan�ar uma exce��o no substring
					}	
				}
			}
			//DESCRIBE
			if(arrayToken.length >= 2 && arrayToken.length <= 3){
				if(arrayToken[0].equalsIgnoreCase("DESCRIBE")){
					try{
						if(arrayToken[1].substring(arrayToken[1].length()-1,arrayToken[1].length()).equals(";")){
							String[] nameOfTable = {arrayToken[1].substring(0,arrayToken[1].length()-1)};
							return new ResponseCommands(Command.DESCRIBE, null, null, nameOfTable, null, null);
						}else if(arrayToken[2].equalsIgnoreCase(";")){
							String[] nameOfTable = {arrayToken[1]};
							return new ResponseCommands(Command.DESCRIBE, null, null, nameOfTable, null, null);
						}
					}catch(IndexOutOfBoundsException e){
						//metodo pode lan�ar uma exce��o no substring
					}	
				}
			}
			
			//CREATE DATABASE
			if(arrayToken.length >= 3 && arrayToken.length <= 4){
				if(arrayToken[0].equalsIgnoreCase("CREATE")){
					if(arrayToken[1].equalsIgnoreCase("DATABASE")){						
						try{
							if(arrayToken[2].substring(arrayToken[2].length()-1,arrayToken[2].length()).equals(";")){
								if(!PalavrasChavesUnicas.isWord(arrayToken[2].substring(0,arrayToken[2].length()-1))){									
									if(isValidArgs(arrayToken[2].substring(0,arrayToken[2].length()-1))){
										String[] argumentos = {arrayToken[2].substring(0,arrayToken[2].length()-1)};
										return new ResponseCommands(Command.CREATE_DATABASE, argumentos, null, null, null, null);
									}
								}else{	//palavra chave do banco
									String[] argumentos = {arrayToken[2].substring(0,arrayToken[2].length()-1)};
									return new ResponseCommands(Command.KEY_WORD_RESERVED, argumentos, null, null, null, null);
								}
							}else if(!PalavrasChavesUnicas.isWord(arrayToken[2])){
								if(isValidArgs(arrayToken[2])){
									String[] argumentos = {arrayToken[2]};
									return new ResponseCommands(Command.CREATE_DATABASE, argumentos, null, null, null, null);
								}
							}else{		//palavra chave do banco
								String[] argumentos = {arrayToken[2]};
								return new ResponseCommands(Command.KEY_WORD_RESERVED, argumentos, null, null, null, null);
							}
						}catch(IndexOutOfBoundsException e){
							//metodo pode lan�ar uma exce��o no substring
						}		
					}
				}
			}
			//CREATE TABLE
			if(arrayToken.length >= 4){
				//exemplo: insert into "tbl" ("atb" int);
				if(arrayToken[0].equalsIgnoreCase("CREATE")){					
					if(arrayToken[1].equalsIgnoreCase("TABLE")){
						try{
							for(int i = 0; i < arrayToken[2].length(); i++ ){//1 0 / 2 1 / 3 2 / 4 3 / 5 4 / 6
								if(arrayToken[2].substring(arrayToken[2].length()-i-1, arrayToken[2].length()-i).equals("(")){									
									//se ap�s o nome da tabela vir o "(" ent�o eliminamos os parenteses para add o nome da tabela
									arrayToken[3] = arrayToken[2].substring(arrayToken[2].length()-i-1, arrayToken[2].length())+" "+arrayToken[3];	//acrescento o parenteses q estava no nome da tabela direto nos atributos seguintes
									arrayToken[2] = arrayToken[2].substring(0, arrayToken[2].length()-i-1);									
								}								
							}							
							if(!PalavrasChavesUnicas.isWord(arrayToken[2])){
								if(isValidArgs(arrayToken[2])){
									String atributos = "";
									for(int i = 3; i < arrayToken.length; i++){
										atributos += arrayToken[i]+(i == arrayToken.length-1 ? "" : " "); //retorna todas as String do array na variavel atributos
									}
									//verifica se os atributos dentro dos parenteses s�o v�lidos e retorna ResponseCommands
									try{
										//methodo pode lan�ar CommandBeanSQLException
										String[] nameOfTabela = {arrayToken[2]};	//nome da tabela
										ResponseCommands response = verifyAtributosEmParenteses(atributos, nameOfTabela);
										if(response != null){ //se for null, n�o � valido o comando
											//System.out.println("retornou response com sucesso depois de verify atributos in parenteses");
											return response;
										}
									}catch(TipoDadosBeanSQLException tipoEx){
										//metodo pode lan�ar exception, n�o h� o q fazer retorna command invalid
										return new ResponseCommands(Command.INVALID_COMMAND, null, null, null, null, null);
									}catch(ArrayIndexOutOfBoundsException arrayEx){
										//metodo pode lan�ar exception, n�o h� o q fazer retorna command invalid
										return new ResponseCommands(Command.INVALID_COMMAND, null, null, null, null, null);
									}
								}else{
									
								}
							}else{		//palavra chave do banco
								String[] argumentos = {arrayToken[2]};
								return new ResponseCommands(Command.KEY_WORD_RESERVED, argumentos, null, null, null, null);
							}
						}catch(IndexOutOfBoundsException e){//caso retorne exception na manipula��o de string e arrays
							return new ResponseCommands(Command.INVALID_COMMAND, null, null, null, null, null);
						}
					}					
				}
			}
			//INSERT INTO
			if(arrayToken.length >= 4){
				//exemplo: insert into "tbl" ("atb" int);
				if(arrayToken[0].equalsIgnoreCase("INSERT")){
					if(arrayToken[1].equalsIgnoreCase("INTO")){
						String[] nameOfTabela = {arrayToken[2]};	//nome da tabela
						try{
							if(arrayToken[3].equalsIgnoreCase("VALUES")){
								String valores = ""; //retorna todas as String do array na variavel atributos
								for(int i = 4; i < arrayToken.length; i++){
									valores += arrayToken[i]+(i == arrayToken.length-1 ? "" : " "); 
								}
								ResponseCommands response = verifyValoresEmParenteses(valores, nameOfTabela);
								if(response != null){ //se for null, n�o � valido o comando
									return response;
								}
							}else if(arrayToken[3].substring(0, 7).equalsIgnoreCase("VALUES(")){
								//retorna todas as String do array na variavel atributos com os parenteses ignorando o values							
								String valores = arrayToken[3].substring(6, arrayToken[3].length());								
								for(int i = 4; i < arrayToken.length; i++){
									valores += arrayToken[i]+(i == arrayToken.length-1 ? "" : " "); 
								}								
								ResponseCommands response = verifyValoresEmParenteses(valores, nameOfTabela);
								if(response != null){ //se for null, n�o � valido o comando
									return response;
								}
							}
						}catch(StringIndexOutOfBoundsException e){
							//metodo pode lan�ar exceptions na manipula��o de substring							
						}catch(ArrayIndexOutOfBoundsException e){
							//metodo pode lan�ar exceptions na manipula��o de arrays							
						}
					}
				}
			}
			//SELECTS
			if(arrayToken.length >= 4){
				if(arrayToken[0].equalsIgnoreCase("SELECT")){
					ResponseCommands response = selectJoinWhereOrder(sequencia); //novo metodo
					if(response != null){
						return response;
					}
				}
			}		
			//DROPS
			if(arrayToken.length >= 2 && arrayToken.length <= 4){
				if(arrayToken[0].equalsIgnoreCase("DROP")){
					if(arrayToken[1].equalsIgnoreCase("TABLE")){
						try{
							if(arrayToken[2].substring(arrayToken[2].length()-1,arrayToken[2].length()).equals(";")){
								String[] nameOfTable = {arrayToken[2].substring(0,arrayToken[2].length()-1)};
								return new ResponseCommands(Command.DROP_TABLE, null, null, nameOfTable, null, null);
							}else if(arrayToken[3].equalsIgnoreCase(";")){
								String[] nameOfTable = {arrayToken[2]};
								return new ResponseCommands(Command.DROP_TABLE, null, null, nameOfTable, null, null);
							}
						}catch(IndexOutOfBoundsException e){
							//metodo pode lan�ar uma exce��o no substring
						}
					}else if(arrayToken[1].equalsIgnoreCase("DATABASE")){
						try{
							if(arrayToken[2].substring(arrayToken[2].length()-1,arrayToken[2].length()).equals(";")){
								String nameOfDataBase = arrayToken[2].substring(0,arrayToken[2].length()-1);
								return new ResponseCommands(Command.DROP_DATABASE, null, null, null, null, nameOfDataBase);
							}else if(arrayToken[3].equalsIgnoreCase(";")){
								String nameOfDataBase = arrayToken[2];
								return new ResponseCommands(Command.DROP_DATABASE, null, null, null, null, nameOfDataBase);
							}
						}catch(IndexOutOfBoundsException e){
							//metodo pode lan�ar uma exce��o no substring
						}
					}
				}
			}
			//DELETE
			if(arrayToken.length >= 3){
				if(arrayToken[0].equalsIgnoreCase("DELETE")){
					if(arrayToken[1].equalsIgnoreCase("FROM")){
						String textoComando = "";
						for(int i = 2; i < arrayToken.length; i++){
							textoComando += arrayToken[i]+(i == arrayToken.length-1 ? "" : " "); //se for o ultimo token entaum n�o concaterna o espa�o
						}
						//sabemos que o ultimo caractere sempre ser� ";" entaum eliminamos-o
						textoComando = textoComando.substring(0, textoComando.length()-1);
						String[] tokenDelete = textoComando.split(" ");
						String[] nameOfTable = { tokenDelete[0] };		//add tabela
						Criterio criterio = new Criterio();
						//at� o momento n�o lan�a exceptions
						int contArrayToken = 1;
						
						if(contArrayToken < tokenDelete.length){
							if(tokenDelete[contArrayToken].equalsIgnoreCase("WHERE")){	//se n�o for inner join ent�o pode ser WHERE ou ORDER BY
								//***implementar para pegar dados do WHERE
								String textoWhere = " ";
								contArrayToken++;	//incrementa para pegar pr�ximo token, pois o ultimo foi o where
								while(contArrayToken < tokenDelete.length){	//enquanto n�o for final de arrayToken continua
										
									//verificar AND e OR
									if(tokenDelete[contArrayToken].equalsIgnoreCase("AND")){
										criterio.addCondicao(textoWhere);
										criterio.addOperadorUniao(Operador.AND);
										textoWhere = "";	//zera variavel para armazenar mais condiceos pois existe AND
										contArrayToken++;
									}else if(tokenDelete[contArrayToken].equalsIgnoreCase("OR")){
										criterio.addCondicao(textoWhere);
										criterio.addOperadorUniao(Operador.OR);
										textoWhere = "";	//zera variavel para armazenar mais condiceos pois existe OR
										contArrayToken++;
									}else{	//se naum for nem order nem and nem or continua add os condicoes na variavel								
										textoWhere += tokenDelete[contArrayToken] + " "; //separa com espa�o exemplo table.id >= 'alguma coisa'
										contArrayToken++;
									}
										
								}
								if(textoWhere.length() > 1){	//se possui mais de um caractere ent�o
									//esse metodo pode lancar excecoes
									try{
										criterio.addCondicao(textoWhere);
									}catch(IndexOutOfBoundsException e){
										//caso retorne erro na manipula��o de Index, retorna null com prop�sito de msg de error padr�o
										String erro = "Error in where clause '" + textoWhere.trim() + "'";
										return new ResponseCommands(Command.ERROR_IN, erro);
									}catch(BeanSQLException e){
										String erro = "Error in where clause '" + textoWhere.trim() + "'";
										return new ResponseCommands(Command.ERROR_IN, erro);
									}
								}else{
									String erro = "Error in where clause '" + textoWhere.trim() + "'";
									return new ResponseCommands(Command.ERROR_IN, erro); //n�o foi add nada no where
								}
							}
						}
						
						return new ResponseCommands(Command.DELETE_FROM_WHERE, null, null, nameOfTable, criterio, null);
					}
				}
			}
			
			//UPDATE - update table1 set campo=valor, campo2 where campo = vaor;
			if(arrayToken.length >= 4){
				if(arrayToken[0].equalsIgnoreCase("UPDATE")){
					String[] nameOfTable = { arrayToken[1] };
					String atributos = "";
					Criterio criterio = new Criterio();
					
					if(arrayToken[2].equalsIgnoreCase("SET")){
						
						String textoComando = "";
						for(int i = 3; i < arrayToken.length; i++){
							textoComando += arrayToken[i]+" ";
						}
						//sabemos que o ultimo caractere sempre ser� ";" entaum eliminamos-o
						textoComando = textoComando.trim();
						textoComando = textoComando.substring(0, textoComando.length()-1);
						String[] arrayWhere = textoComando.split(" ");
												
						int contArrayToken = 0;
						for(int i = 0; i < arrayWhere.length; i++){//for para pegar os campos e valores
							if(!arrayWhere[i].equalsIgnoreCase("WHERE")){//se for where sai do loop dos campos
								atributos += arrayWhere[i]+" ";
								contArrayToken++;	//quando for where naum incrementa pois contador atual � where
							}else{
								break;
							}
						}
						//com where ou sem where
						String[] arrayAtributos = atributos.split(",");//split dos campos pela virgula
						String[] arraySetCampos = new String[arrayAtributos.length];//array para quantidade de sets 'campo1=values' isto � um set
						int contSet = 0;
						
						for(String s : arrayAtributos){
							String temp = s.trim();//.replace(" ", "");	//remove todos espa�os na string
							if(temp.contains("=")){	//se contiver operador de atribui��o ok
								arraySetCampos[contSet++] = temp; 	//add a nova string no set de campos sem espa�os
							}else{	//esse set de campos n�o � valido retorna erro com o campo invalido
								String erro = "Error in set clause '" + s.trim() + "'";
								return new ResponseCommands(Command.ERROR_IN, erro);
							}
						}
						
						//clausula where
						if(contArrayToken < arrayWhere.length){
							if(arrayWhere[contArrayToken].equalsIgnoreCase("WHERE")){	//se n�o for inner join ent�o pode ser WHERE ou ORDER BY
								//***implementar para pegar dados do WHERE
								String textoWhere = " ";
								contArrayToken++;	//incrementa para pegar pr�ximo token, pois o ultimo foi o where
								while(contArrayToken < arrayWhere.length){	//enquanto n�o for final de arrayToken continua
										
									//verificar AND e OR
									if(arrayWhere[contArrayToken].equalsIgnoreCase("AND")){
										criterio.addCondicao(textoWhere);
										criterio.addOperadorUniao(Operador.AND);
										textoWhere = "";	//zera variavel para armazenar mais condiceos pois existe AND
										contArrayToken++;
									}else if(arrayWhere[contArrayToken].equalsIgnoreCase("OR")){
										criterio.addCondicao(textoWhere);
										criterio.addOperadorUniao(Operador.OR);
										textoWhere = "";	//zera variavel para armazenar mais condiceos pois existe OR
										contArrayToken++;
									}else{	//se naum for nem order nem and nem or continua add os condicoes na variavel								
										textoWhere += arrayWhere[contArrayToken] + " "; //separa com espa�o exemplo table.id >= 'alguma coisa'
										contArrayToken++;
									}
										
								}
								if(textoWhere.length() > 1){	//se possui mais de um caractere ent�o
									//esse metodo pode lancar excecoes
									try{
										criterio.addCondicao(textoWhere);
									}catch(IndexOutOfBoundsException e){
										//caso retorne erro na manipula��o de Index, retorna null com prop�sito de msg de error padr�o
										String erro = "Error in where clause '" + textoWhere.trim() + "'";
										return new ResponseCommands(Command.ERROR_IN, erro);
									}catch(BeanSQLException e){
										String erro = "Error in where clause '" + textoWhere.trim() + "'";
										return new ResponseCommands(Command.ERROR_IN, erro);
									}
								}else{
									String erro = "Error in where clause '" + textoWhere.trim() + "'";
									return new ResponseCommands(Command.ERROR_IN, erro); //naum foi add nada depois do where
								}
							}else{
								String erro = "Error in where clause";
								return new ResponseCommands(Command.ERROR_IN, erro); //naum foi add nada depois do where//se naum for where e o ultimo n�o � ';' tem comandos incorretos sem where 
							}
						}
						
						return new ResponseCommands(Command.UPDATE_SET_WHERE, arraySetCampos, null, nameOfTable, criterio, null);
					}
				}
			}			
			
		}catch(Exception e){
			e.printStackTrace();
			throw new BeanSQLException("Error occurred in BeanSQL");
		}
		
		return new ResponseCommands(Command.INVALID_COMMAND, null, null, null, null, null);
	}
	
	//algoritmos
	
	public static ResponseCommands selectJoinWhereOrder (String texto) {
		//novo algoritmo para select, necess�rio vim texto sem ";"
		texto = texto.substring(0, texto.length()-1).trim(); //elimina o ";"
		Criterio criterio = new Criterio();
		ALIAS alias = null;
		
		//select id, nome from pessoa inner join endereco
		//texto = "select id, salario     from pessoa    inner join endereco";
		
		StringTokenizer token = new StringTokenizer(texto);
		String[] arrayToken = new String[token.countTokens()];			
		for(int i = 0; i < arrayToken.length; i++){
			arrayToken[i] = token.nextToken();
		}
		try{
			if(arrayToken[0].equalsIgnoreCase("SELECT")){
				//split para coletar os atributos separados por virgulas
				String atributos = "";
				int contArrayToken = 1;
				
				//parte 1 select atributos para mostrar
				while(!arrayToken[contArrayToken].equalsIgnoreCase("FROM")){//enquanto n�o for from continua
					//exemplo id, nome , endereco
					atributos += arrayToken[contArrayToken] + " ";//id ,nome ,endereco 
					contArrayToken++;
					//System.out.println("while arrayToken="+arrayToken[contArrayToken]);
				}	
				//add todos atributos para sele��o em uma lista
				List<String> listaAtributosSelect = null;
				if(atributos.trim().equals("*") || atributos.trim().equalsIgnoreCase("ALL")){ //trim para remover espa�amentos
					listaAtributosSelect = null;
				}else{
					//listaAtributosSelect = Arrays.asList(atributos.split(","));
					//separa por virgulas
					listaAtributosSelect = new ArrayList<String>();
					String[] arrayAtributosAlias = atributos.split(",");
					//verifica se a cada virgula � somente um valor token ou tres com alias
					for(String a : arrayAtributosAlias){
						StringTokenizer tok = new StringTokenizer(a);
						if(tok.countTokens() == 1){
							listaAtributosSelect.add(a.trim());	//trim para remover espa�amentos
						//}else if(tok.countTokens() == 3){
						}else if(tok.countTokens() >= 3){
							if(alias == null){
								alias = new ALIAS();
							}
							//pega todas as referencias dos elementos do tok
							String original =  tok.nextToken();	//valor � um nome original do campo
							String as = tok.nextToken();		//keyword 'AS'
							String apelido = "";
							while(tok.hasMoreTokens()){
								apelido += tok.nextToken() + " ";	//apelido
							}
							
							if(as.equalsIgnoreCase("AS")){ 	//confirma se o 2 token � a key word 'AS'
								apelido = apelido.trim();
								//valida apelido antes
								if(!PalavrasChavesUnicas.isWord(apelido)){	//valida o apelido
									//esquema para nome composto do apelido no campo
									if(apelido.substring(0,1).equals("\"") && apelido.substring(apelido.length()-1, apelido.length()).equals("\"")){
										// se for nome composto n�o valida em isValidArgs
										apelido = apelido.substring(1, apelido.length()-1); //remove as aspas ""
										try{
											alias.addCampoAS(apelido, original); //pode lan�ar exception de alias repetidos
										}catch(BeanSQLException e){
											return new ResponseCommands(Command.ERROR_IN, e.getMessage());
										}
										
										listaAtributosSelect.add(apelido);		//lista de atributos add o apelido do campo
										
									}else if(isValidArgs(apelido)){
										
										try{
											alias.addCampoAS(apelido, original); //pode lan�ar exception de alias repetidos
										}catch(BeanSQLException e){
											return new ResponseCommands(Command.ERROR_IN, e.getMessage());
										}
										
										listaAtributosSelect.add(apelido);		//lista de atributos add o apelido do campo
										
									}else{								
										String erro =  "invalid alias format near '" + apelido + "'" ;
										return new ResponseCommands(Command.ERROR_IN, erro);
									}
								}else{
									String[] argumentos = {arrayToken[2]};
									return new ResponseCommands(Command.KEY_WORD_RESERVED, argumentos, null, null, null, null);
								}
								
							}else{	//caso seja alguma outra coisa retorna error
								return null;
							}
						}else{
							//se n�o for 1 (campo) ou 3 (campo as apelido) retorna error
							return null;
						}
					}
					
				}				
				
				//PARTE 2 ADD TABELA
				List<String> listaTabelasSelect = new ArrayList<String>(); 
				contArrayToken++; //contArrayToken atual � FROM incrementa para coletar tabela		
				
				//listaTabelasSelect.add(arrayToken[contArrayToken]);
				//antes de adicionar verifica se n�o contem apelidos alias
				String original = arrayToken[contArrayToken];	//recupera o nome da tabela original
				String apelido = "";
				if(contArrayToken + 1 < arrayToken.length){
					if(arrayToken[contArrayToken + 1].equalsIgnoreCase("AS")){ //verifica se o proximo � 'AS'
						//se tiver as add o proximo token como apelido, se de alguma forma naum for, ser� lan�ado exception mais pra frente
						apelido = arrayToken[contArrayToken + 2]; //recupera nome do apelido
						//valida apelido antes
						if(alias == null){
							alias = new ALIAS();
						}
						if(!PalavrasChavesUnicas.isWord(apelido)){	//valida o apelido
							if(isValidArgs(apelido)){
								
								try{
									alias.addTabelaAS(apelido, original); //pode lan�ar exception de alias repetidos
								}catch(BeanSQLException e){
									return new ResponseCommands(Command.ERROR_IN, e.getMessage());
								}										
								listaTabelasSelect.add(apelido.toLowerCase());		//lista de tabelas add o apelido da tabela
								contArrayToken += 3;
							}else{								
								String erro =  "invalid alias format near '" + apelido + "'" ;
								return new ResponseCommands(Command.ERROR_IN, erro);
							}
						}else{
							String[] argumentos = {arrayToken[2]};
							return new ResponseCommands(Command.KEY_WORD_RESERVED, argumentos, null, null, null, null);
						}
						
					}else{
						listaTabelasSelect.add( original.toLowerCase() );		//se naum tiver o comando 'AS' entaum continua normal
						contArrayToken++;
					}
				}else{ //se n�o o contador est� no ultimo token e add a tabela
					listaTabelasSelect.add( original.toLowerCase() );		//se naum tiver o comando 'AS' entaum continua normal
					contArrayToken++;
				}
				
				
				//PARTE 3 inner JOINS
				
				//contArrayToken++; //contArrayToken atual � nomeTabela incrementa para coletar joins			
				while(contArrayToken < arrayToken.length){ //se contador for igual ao total-1 entao chegou no limite
					//**premissa inner joins tem q estar antes de where e order by					
					
					if(arrayToken[contArrayToken].equalsIgnoreCase("JOIN")){//verifica se token � inner
						contArrayToken++;	//incrementa para verificar se o proximo token � JOIN
						//SE ACHOU JOIN ENT�O ADD NA LISTA A PROXIMA TABELA
						//ADD TABELA - pode lan�a exception
						
						//antes de adicionar verifica se n�o contem apelidos alias
						original = arrayToken[contArrayToken];	//recupera o nome da tabela original
						apelido = "";
						if(contArrayToken + 1 < arrayToken.length){
							if(arrayToken[contArrayToken + 1].equalsIgnoreCase("AS")){ //verifica se o proximo � 'AS'
								//se tiver as add o proximo token como apelido, se de alguma forma naum for, ser� lan�ado exception mais pra frente
								apelido = arrayToken[contArrayToken + 2]; //recupera nome do apelido
								//valida apelido antes
								if(alias == null){
									alias = new ALIAS();
								}
								if(!PalavrasChavesUnicas.isWord(apelido)){	//valida o apelido
									if(isValidArgs(apelido)){
										
										try{
											alias.addTabelaAS(apelido, original); //pode lan�ar exception de alias repetidos
										}catch(BeanSQLException e){
											return new ResponseCommands(Command.ERROR_IN, e.getMessage());
										}										
										listaTabelasSelect.add(apelido.toLowerCase());		//lista de tabelas add o apelido da tabela
										contArrayToken += 3;
									}else{								
										String erro =  "invalid alias format near '" + apelido + "'" ;
										return new ResponseCommands(Command.ERROR_IN, erro);
									}
								}else{
									String[] argumentos = {arrayToken[2]};
									return new ResponseCommands(Command.KEY_WORD_RESERVED, argumentos, null, null, null, null);
								}
								
							}else{
								listaTabelasSelect.add( original.toLowerCase() );		//se naum tiver o comando 'AS' entaum continua normal
								contArrayToken++;
							}
						}else{ //se n�o o contador est� no ultimo token e add a tabela
							listaTabelasSelect.add( original.toLowerCase() );		//se naum tiver o comando 'AS' entaum continua normal
							contArrayToken++;
						}				
						
						
						
						if(contArrayToken < arrayToken.length){	//se contArrayToken naum chegou no fim
							if(arrayToken[contArrayToken].equalsIgnoreCase("ON")){
								//clausula on, parecida com a where
								//***implementar para pegar dados do WHERE
								String textoWhere = " ";
								contArrayToken++;	//incrementa para pegar pr�ximo token, pois o ultimo foi o where
								while(contArrayToken < arrayToken.length){	//enquanto n�o for final de arrayToken continua
									if(arrayToken[contArrayToken].equalsIgnoreCase("ORDER")
											|| arrayToken[contArrayToken].equalsIgnoreCase("WHERE")
											|| arrayToken[contArrayToken].equalsIgnoreCase("JOIN")){	//se n�o for order, acrescenta no texto
										break;
									}else{		//se nao, se realmente o proximo for order quebra o la�o
										textoWhere += arrayToken[contArrayToken] + " "; //separa com espa�o exemplo table.id >= 'alguma coisa'
										contArrayToken++;
									}
								}
								if(textoWhere.length() > 1){		//se possui mais de um caractere ent�o
									//esse metodo pode lancar excecoes
									criterio.addCondicao(textoWhere);
								}else{
									return null;
								}
							}else if(arrayToken[contArrayToken].toUpperCase().contains("USING(")){ //using(valor)
								String textoWhere = arrayToken[contArrayToken].substring(6, arrayToken[contArrayToken].length()-1); //elimina 'USING(' e ')' e retorna o valor
								if(textoWhere.length() > 1){		//se possui mais de um caractere se o parenteses using() naum est� vazio
									//esse metodo pode lancar excecoes
									String tabela1 = listaTabelasSelect.get(listaTabelasSelect.size()-2) + "."; //pega a tabela anterior da ultima
									String tabela2 = listaTabelasSelect.get(listaTabelasSelect.size()-1) + "."; //pega ultima tabela
									criterio.addCondicao(tabela1 + textoWhere + "=" + tabela2 + textoWhere); //exemplo: tabela1.id=tabela2.id
									contArrayToken++; //proximo token a ser analizado no where principal do join
								}else{
									return null;
								}
							}
						}
						
					}else{	
						//return null; //CASO TENHA MAIS COMANDOS RETORNA NULL COMO ERRO, mas aki ainda pode ter where ou order by
						break;//caso o arrayToken atual n�o seja inner join sai do la�o
					}
					//contArrayToken++; //incrementa para verificar no while se contador est� no limite do arrayToken
				}
				
				//PARTE 4 WHERE - depois de achar os joins verifica se tem crit�rios	
				if(contArrayToken < arrayToken.length){
					if(arrayToken[contArrayToken].equalsIgnoreCase("WHERE")){	//se n�o for inner join ent�o pode ser WHERE ou ORDER BY
						//***implementar para pegar dados do WHERE
						String textoWhere = " ";
						contArrayToken++;	//incrementa para pegar pr�ximo token, pois o ultimo foi o where
						while(contArrayToken < arrayToken.length){	//enquanto n�o for final de arrayToken continua
							if(!arrayToken[contArrayToken].equalsIgnoreCase("ORDER")){	//se n�o for order, acrescenta no texto
								
								//verificar AND e OR
								if(arrayToken[contArrayToken].equalsIgnoreCase("AND")){
									criterio.addCondicao(textoWhere);
									criterio.addOperadorUniao(Operador.AND);
									textoWhere = "";	//zera variavel para armazenar mais condiceos pois existe AND
									contArrayToken++;
								}else if(arrayToken[contArrayToken].equalsIgnoreCase("OR")){
									criterio.addCondicao(textoWhere);
									criterio.addOperadorUniao(Operador.OR);
									textoWhere = "";	//zera variavel para armazenar mais condiceos pois existe OR
									contArrayToken++;
								}else{	//se naum for nem order nem and nem or continua add os condicoes na variavel								
									textoWhere += arrayToken[contArrayToken] + " "; //separa com espa�o exemplo table.id >= 'alguma coisa'
									contArrayToken++;
								}
								
							}else{		//se nao, se realmente o proximo for order quebra o la�o e add a clausula where no criterio
								break;
							}
						}
						if(textoWhere.length() > 1){		//se possui mais de um caractere ent�o
							//esse metodo pode lancar excecoes
							criterio.addCondicao(textoWhere);
						}else{
							return null;
						}
					}
				}
				
				//PARTE 4 ORDER BY - depois de where verificar ordena��o
				if(contArrayToken < arrayToken.length){
					if(arrayToken[contArrayToken].equalsIgnoreCase("ORDER")){	//se n�o for WHERE pode ser ORDER BY
						contArrayToken++; //se for order o pr�ximo tem que ser BY
						if(arrayToken[contArrayToken].equalsIgnoreCase("BY")){	//tem que ser BY // lan�a exception
							//***implementar para pegar dados do ORDER BY
							contArrayToken++;		
							String textoOrderBy = "";
							while(contArrayToken < arrayToken.length){
								textoOrderBy += arrayToken[contArrayToken++] + " ";
							}
							
							if(!textoOrderBy.isEmpty()){
								
								String[] arrayOrderBy = textoOrderBy.split(",");
								for(String s : arrayOrderBy){
									StringTokenizer tokenOrderBy = new StringTokenizer(s);
									
									if(tokenOrderBy.countTokens() == 2){	//token do order tem q ter 2 strings campo e valor(desc ou asc)
										String campo = tokenOrderBy.nextToken();
										String ordem = tokenOrderBy.nextToken();
										criterio.addOrderBy(campo, ordem);
									
									}else if(tokenOrderBy.countTokens() == 1){ //se naum for 2 � um e tem somente o nome do campo
										String campo = tokenOrderBy.nextToken();
										criterio.addOrderBy(campo);
									}else{//se contiver mais de 3 tokens isto � diferente de "campo asc" ent�o retorna null
										return null;
									}
								}
								
							}else{	//se o textoOrderBy estiver vazio retorna null como proposito error, � necess�rio campos e tipo de ordena��o
								return null;
							}
						}
					}
				}
				//ao chegar no final, necessario que todos intens de arrayToken deveram ser utilizados
				if(contArrayToken == arrayToken.length){//chegou ao final do arrayToken
					String[] args = toArray(listaAtributosSelect);
					String[] tables = toArray(listaTabelasSelect);
					return new ResponseCommands(Command.SELECT_FROM_JOIN_WHERE, args , null, tables, criterio, null, alias);
				}else{
					//se existe mais itens no arrayToken e n�o foram utilizados corretamente retorna null, com proposito de comando invalido
					return null; 
				}
				
			}
		}catch(IndexOutOfBoundsException e){
			//caso retorne erro na manipula��o de Index, retorna null com prop�sito de msg de error padr�o 
			return null;
		}catch(BeanSQLException e){
			//caso retorne erro em algum comando BeanSQLException, retorna null com prop�sito de msg de error padr�o
			return null;
		}
		return null;
	}
	
	public static String[] toArray(Collection<String> c){
		if(c == null){
			return null;
		}else{
			String[] array = new String[c.size()];
			int i = 0;
			Iterator<String> it = c.iterator();
			while(it.hasNext()){
				array[i++] = (String) it.next();
			}
			return array;
		}
	}
	
	/**
	 * M�todo realiza o filtro do nome do campo, se for absoluto retorna somente campo
	 * @param nomeAbsoltuoCampo String - recebe uma String com nome absoluto do campo ou n�o
	 * @return somenteCampo String - retorna somente o nome do campo sem a tabela
	 */
	public static String getSomenteNomeCampoSemNomeAbsoluto(String nomeAbsoltuoCampo){
		String somenteCampo = "";
		if(nomeAbsoltuoCampo.contains(".")){
			for(int i = 0; i < nomeAbsoltuoCampo.length()-1; i++){
				if(nomeAbsoltuoCampo.substring(i, i+1).equalsIgnoreCase(".")){	//se achar o ponto
					//add todos os caracteres da string ap�s o ponto
					somenteCampo = nomeAbsoltuoCampo.substring(i+1, nomeAbsoltuoCampo.length());
				}
			}
		}else{
			somenteCampo = nomeAbsoltuoCampo;
		}
		return somenteCampo;
	}
	
	public static String eliminaApostofro(String valor){
		String s = valor;
		if(valor.substring(0, 1).equals("'") && valor.substring(valor.length()-1, valor.length()).equals("'")){
			s = valor.substring(1, valor.length()-1);
		}
		return s;
	}
	
}
