package br.com.beansql.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.beansql.exceptions.BeanSQLException;
import br.com.beansql.exceptions.TipoDadosBeanSQLException;

public class Criterio {

	private ArrayList<Condicao> condicoes;				//utiliza um objeto capaz de armazenar uma condicao com campo e valor a ser comparado
	private ArrayList<Operador> operadorUniao; 			//utilizados os operadores AND e OR
	private ArrayList<String> listaCamposOrdenar; 
	private ArrayList<Operador> listaTipoOrdem;
	
	public Criterio() {
		condicoes = new ArrayList<Condicao>();
		operadorUniao = new ArrayList<Operador>();
		listaCamposOrdenar = new ArrayList<String>();
		listaTipoOrdem = new ArrayList<Operador>();
		listaTipoOrdem.add(Operador.ASC);	//por padr�o � ordem asc
	}

	/**
	 * M�todo verifica e separa o comando ON utilizado em joins das tabelas
	 * recebe charSequence como parametro com a compara��o condi��o e valor e armazena no objeto Criterio
	 * @param texto informado com charSequence dos comandos On e Where, uma condi��o por vez para adicionar nas listas
	 * 		exemplo: atributo1 == valor, o m�todo � capaz de separar os valores e os operadores, cada condi��o por vez
	 * @throws BeanSQLException 
	 */
	public void addCondicao(String textoComando) throws BeanSQLException{
		
		String[] arrayFiltroComando = new String[2]; //array do filtro do comando separando pelos operadores e condi��es 
		Condicao condicao = new Condicao();
		
		try{
			if(textoComando.contains("<=")){
				arrayFiltroComando = textoComando.split("<=");
				condicao.setOperador(Operador.MENOR_IGUAL);
				
			}else if(textoComando.contains(">=")){
				arrayFiltroComando = textoComando.split(">=");
				condicao.setOperador(Operador.MAIOR_IGUAL);
				
			}else if(textoComando.contains("=")){
				arrayFiltroComando = textoComando.split("=");
				condicao.setOperador(Operador.IGUAL);
				
			}else if(textoComando.contains("<")){
				arrayFiltroComando = textoComando.split("<");
				condicao.setOperador(Operador.MENOR);
				
			}else if(textoComando.contains(">")){
				arrayFiltroComando = textoComando.split(">");
				condicao.setOperador(Operador.MAIOR);
				
			}else if(textoComando.toUpperCase().contains("LIKE")){
				arrayFiltroComando = textoComando.toUpperCase().split("LIKE");
				condicao.setOperador(Operador.LIKE);
				
				arrayFiltroComando[0] = arrayFiltroComando[0].trim(); //remove espa�amentos entre os lados, valor a ser comparado pode ser palavras compostas
				arrayFiltroComando[1] = arrayFiltroComando[1].trim(); //remove espa�amentos entre os lados, valor a ser comparado pode ser palavras compostas
				//se for string remove apostofro no come�o e no fim da string ' ', se n�o conter lan�a exception
				if(arrayFiltroComando[1].substring(0, 1).equals("'")
						&& arrayFiltroComando[1].substring(arrayFiltroComando[1].length()-1, arrayFiltroComando[1].length()).equals("'") ){
					//se contiver apostofro remove ignorando o primeiro e o ultimo caractere
					arrayFiltroComando[1] = arrayFiltroComando[1].substring(1, arrayFiltroComando[1].length()-1);
				}else{
					throw new BeanSQLException("in args of conditions");
				}
				
				//premissa arrayFiltroComando.lenth tem q ser igual a 2 se n�o retorna error
				if(arrayFiltroComando.length != 2){
					throw new BeanSQLException("in args of conditions");
				}				
				condicao.setCampoComparar(arrayFiltroComando[0]);		
				condicao.setCampoOuValorComparado(arrayFiltroComando[1]);
				condicoes.add(condicao);	//add a condicao na lista de condicoes da classe Criterio
				//premissa ap�s as condi�oes serem adicionadas seta AND como padr�o 
				//para comparar com a proxima condia��o seja qual for ON WHERE
				operadorUniao.add(Operador.AND); 
				return; //se for like finaliza metodo
			
			}else{	//se n�o achou nenhuma 
				throw new BeanSQLException("in args of conditions");
			}
			
			//premissa arrayFiltroComando.lenth tem q ser igual a 2 se n�o retorna error
			if(arrayFiltroComando.length != 2){
				throw new BeanSQLException("in args of conditions");
			}			
			//ap�s fazer o split dos atributos a ser comparado, operadores de condi��es e valores
			//tenta adicinar nas listas, pode lan�ar exceptions na manipula��o do array
			//caso seja generico os valores integer ou double
			condicao.setCampoComparar(arrayFiltroComando[0].trim());		//remove espa�amentos entre os lados, valor a ser comparado pode ser palavras compostas
			condicao.setCampoOuValorComparado(arrayFiltroComando[1].trim());//remove espa�amentos entre os lados, valor a ser comparado pode ser palavras compostas
			condicoes.add(condicao);	//add a condicao na lista de condicoes da classe Criterio
			//premissa ap�s as condi�oes serem adicionadas seta AND como padr�o 
			//para comparar com a proxima condia��o seja qual for ON WHERE
			operadorUniao.add(Operador.AND);
			
		}catch(ArrayIndexOutOfBoundsException e){
			//System.out.println("invalido filtro de on e where dentro do array!!\n"+e);
			throw new BeanSQLException("in args of conditions");
		}
	}
	
	public void addOperadorUniao(Operador operador){
		//premissa sobrescreve o ultimo operador, pois a cada condi��o adicionada � setado AND como default
		operadorUniao.remove(operadorUniao.size()-1);
		operadorUniao.add(operador);
	}
	
	//operadores e condi��es
	public static boolean verificarRegistroAtendeCriterios(Criterio criterio) throws TipoDadosBeanSQLException{
		boolean isValidRegistroByCriterio = true;
		boolean condicaoORAnterioValida = false;
		
		for(int i = 0; i < criterio.getOperadorUniao().size(); i++){//size de operadorUniao � sempre um a mais devido ao default AND
			//on + AND + on + AND + where + OR + where
			String campoComparar = criterio.getCondicoes().get(i).getCampoComparar();
			String campoOuValor = criterio.getCondicoes().get(i).getCampoOuValorComparado();
			Operador operador = criterio.getCondicoes().get(i).getOperador();
			Operador andOrAtual = criterio.getOperadorUniao().get(i);
			
			//este metodo pode lancar uma exception NumberFormatException
			if(compararCamposValores(campoComparar, operador, campoOuValor)){
				//verifica se existe mais condicoes e operador AND e OR
				
				//if(i == criterio.getOperadorUniao().size()-1){//se estiver no ultimo loop e ate o momento tudo true retorna true
					//isValidRegistroByCriterio = true;
				//}
				//premissa se validou um OR antes o proximo, somente o proximo se for OR ou AND eh valido tambem
				if(andOrAtual == Operador.OR){
					condicaoORAnterioValida = true;
				}else{	//se algum momento andOrAtual for AND entaum quebra o OR anterior, pois os proximos tem q coincidir com este AND atual
					condicaoORAnterioValida = false;
				}
				
			}else if(andOrAtual == Operador.AND && condicaoORAnterioValida == false){	//mesmo se naum validou o proximo eh OR se sim tem mais uma chance
				//se o proximo eh AND entaum sem chance retorna false
				return false;
			}else if(andOrAtual == Operador.AND){	//se o proximo for AND com certeza a proxima tem q valer independente do OR ultimo
				condicaoORAnterioValida = false;
			}
		}
		
		return isValidRegistroByCriterio;
	}
	
	/**
	 * M�todo realiza a compara��o do valor como atributo e o valor para ser comparado de acordo com operador
	 * Necess�rio an�lise dos atributos e valores e validar nas tabelas e tuplas antes de comparar neste m�todo
	 * @param campoComparar
	 * @param operador
	 * @param campoOuValor
	 * @return true retorna verdadeiro caso os valores informados satisfazem a condi��o do operador
	 * @throws TipoDadosBeanSQLException 
	 */
	public static boolean compararCamposValores(String campoComparar, Operador operador, String campoOuValor) 
			throws TipoDadosBeanSQLException{
		
		try{
			if(operador.equals(Operador.MENOR_IGUAL)){//ent�o � opera��o de <=
				double a = Double.parseDouble(campoComparar);
				double b = Double.parseDouble(campoOuValor);
				if(a <= b){ //se realmente os valores forem inteiros ou double e a <= b ent�o return true
					return true;
				}
			}else if(operador.equals(Operador.MAIOR_IGUAL)){//ent�o � opera��o de >=
				double a = Double.parseDouble(campoComparar);
				double b = Double.parseDouble(campoOuValor);
				if(a >= b){ //se realmente os valores forem inteiros ou double e a >= b ent�o return true
					return true;
				}
			}else if(operador.equals(Operador.IGUAL)){//ent�o � opera��o de ==
				try{
					double a = Double.parseDouble(campoComparar);
					double b = Double.parseDouble(campoOuValor);
					if(a == b){ //se realmente os valores forem inteiros ou double e a == b ent�o return true
						return true;
					}
				}catch(NumberFormatException e){	//pode ser que o operador igual est� sendo utilizado para comparar com string completa (sem %) ou campo do tipo Date
					if(campoOuValor.contains("%")){
						throw new TipoDadosBeanSQLException(); //se contiver simbolo "%" especial para like retorna exception
					}else{
						//como naum est� utilizando parse est� sendo comparado valores String
						if(campoOuValor.substring(0, 1).equals("'")
								&& campoOuValor.substring(campoOuValor.length()-1, campoOuValor.length()).equals("'") ){
							if(campoComparar.toLowerCase().equalsIgnoreCase(
									campoOuValor.toLowerCase().substring(1, campoOuValor.length()-1))){ //elimina tamb�m apostofro caso naum mencionado compara��o � falsa
								return true;
							}
						}else{	//se naum validar os apostofro ' ' entaum n�o foi colocado string ou date, entaum retorna error devido ao formato de string invalido
							throw new TipoDadosBeanSQLException();
						}
					}
				}
			}else if(operador.equals(Operador.MENOR)){//ent�o � opera��o de <
				double a = Double.parseDouble(campoComparar);
				double b = Double.parseDouble(campoOuValor);
				if(a < b){ //se realmente os valores forem inteiros ou double e a < b ent�o return true
					return true;
				}
			}else if(operador.equals(Operador.MAIOR)){//ent�o � opera��o de >
				double a = Double.parseDouble(campoComparar);
				double b = Double.parseDouble(campoOuValor);
				if(a > b){ //se realmente os valores forem inteiros ou double e a > b ent�o return true
					return true;
				}
			}else if(operador.equals(Operador.LIKE)){//ent�o � like				
				String regex = campoOuValor.toLowerCase();	//regex recebe o valor exemplo: %aa%bb%cc%% e padr�o � = .*aa.*bb.*cc.*
				regex = regex.replace("%", ".*");	//tudo que for porcentagem replace com .* aceita qualquer caractere zero ou mais vezes
				Pattern padrao = Pattern.compile(regex);
				Matcher pesquisa = padrao.matcher(campoComparar.toLowerCase()); //verifica se o atributo com valor informado possui o padr�o			
				if(pesquisa.matches()){	//se achou o padr�o retorna true
					return true;
				}				
			}
		}catch(NumberFormatException e){
			throw new TipoDadosBeanSQLException();
		}
		return false;
	}

	public void addOrderBy(String campo) throws TipoDadosBeanSQLException{
		listaCamposOrdenar.add(campo.toLowerCase());
		if(listaTipoOrdem.size() < listaCamposOrdenar.size()){					//se for menor add tamb�m uma ordem
			listaTipoOrdem.add(listaTipoOrdem.get(listaTipoOrdem.size()-1));	//se naum foi mencionado o tipo de ordem, add a ultima informada
		}
	}
	
	public void addOrderBy(String campo, String ordem) throws TipoDadosBeanSQLException{
		listaCamposOrdenar.add(campo.toLowerCase());
		
		if(ordem.toLowerCase().equalsIgnoreCase(Operador.ASC.toString())){
			if(listaTipoOrdem.size() < listaCamposOrdenar.size()){					//se for menor add tamb�m uma ordem
				listaTipoOrdem.add(Operador.ASC);	
			}else{	//se a lista conter o mesmo tamanho passa por cima da antiga o operador de ordem
				listaTipoOrdem.remove(listaTipoOrdem.size()-1);
				listaTipoOrdem.add(Operador.ASC);
			}
		}else if(ordem.toLowerCase().equalsIgnoreCase(Operador.DESC.toString())){
			if(listaTipoOrdem.size() < listaCamposOrdenar.size()){					//se for menor add tamb�m uma ordem
				listaTipoOrdem.add(Operador.DESC);	
			}else{	//se a lista conter o mesmo tamanho passa por cima da antiga o operador de ordem
				listaTipoOrdem.remove(listaTipoOrdem.size()-1);
				listaTipoOrdem.add(Operador.DESC);
			}
		}else{
			throw new TipoDadosBeanSQLException();
		}
		
	}
	
	public ArrayList<Condicao> getCondicoes() {
		return condicoes;
	}

	public void setCondicoes(ArrayList<Condicao> condicoes) {
		this.condicoes = condicoes;
	}

	public ArrayList<Operador> getOperadorUniao() {
		return operadorUniao;
	}

	public void setOperadorUniao(ArrayList<Operador> operadorUniao) {
		this.operadorUniao = operadorUniao;
	}

	public ArrayList<String> getListaCamposOrdenar() {
		return listaCamposOrdenar;
	}

	public void setListaCamposOrdenar(ArrayList<String> listaCamposOrdenar) {
		this.listaCamposOrdenar = listaCamposOrdenar;
	}

	public ArrayList<Operador> getListaTipoOrdem() {
		return listaTipoOrdem;
	}

	public void setListaTipoOrdem(ArrayList<Operador> listaTipoOrdem) {
		this.listaTipoOrdem = listaTipoOrdem;
	}

	
	
}
