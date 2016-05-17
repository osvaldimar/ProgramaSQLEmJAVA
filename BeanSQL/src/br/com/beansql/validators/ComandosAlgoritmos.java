package br.com.beansql.validators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import br.com.beansql.exceptions.BeanSQLException;

public class ComandosAlgoritmos {

	public ComandosAlgoritmos() {
	}
	
	public static ResponseCommands selectInnerJoin (String texto) {
		//novo algoritmo para select, necessário vim texto sem ";"
		texto = texto.substring(0, texto.length()-1).trim(); //elimina o ";"
		Criterio criterio = new Criterio();
		
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
				while(!arrayToken[contArrayToken].equalsIgnoreCase("FROM")){//enquanto não for from continua
					//exemplo id, nome , endereco
					atributos += arrayToken[contArrayToken];//id,nome,endereco
					contArrayToken++;		
					//System.out.println("while arrayToken="+arrayToken[contArrayToken]);
				}	
				//add todos atributos para seleção em uma lista
				List<String> listaAtributosSelect;
				if(atributos.equals("*")){
					listaAtributosSelect = null;
				}else{
					listaAtributosSelect = Arrays.asList(atributos.split(","));
				}				
				
				//PARTE 2 ADD TABELA
				List<String> listaTabelasSelect = new ArrayList<String>(); 
				contArrayToken++; //contArrayToken atual é FROM incrementa para coletar tabela			
				listaTabelasSelect.add(arrayToken[contArrayToken]);	
				
				
				//PARTE 3 inner JOINS
				
				contArrayToken++; //contArrayToken atual é nomeTabela incrementa para coletar joins			
				while(contArrayToken < arrayToken.length){ //se contador for igual ao total-1 entao chegou no limite
					//**premissa inner joins tem q estar antes de where e order by					
					
					if(arrayToken[contArrayToken].equalsIgnoreCase("JOIN")){//verifica se token é inner
						contArrayToken++;	//incrementa para verificar se o proximo token é JOIN
						//SE ACHOU JOIN ENTÃO ADD NA LISTA A PROXIMA TABELA
						//ADD TABELA - pode lança exception
						listaTabelasSelect.add(arrayToken[contArrayToken]);	
						contArrayToken++;
						
						if(contArrayToken < arrayToken.length){	//se contArrayToken naum chegou no fim
							if(arrayToken[contArrayToken].equalsIgnoreCase("ON")){
								//clausula on, parecida com a where
								//***implementar para pegar dados do WHERE
								String textoWhere = " ";
								contArrayToken++;	//incrementa para pegar próximo token, pois o ultimo foi o where
								while(contArrayToken < arrayToken.length){	//enquanto não for final de arrayToken continua
									if(arrayToken[contArrayToken].equalsIgnoreCase("ORDER")
											|| arrayToken[contArrayToken].equalsIgnoreCase("WHERE")
											|| arrayToken[contArrayToken].equalsIgnoreCase("JOIN")){	//se não for order, acrescenta no texto
										break;
									}else{		//se nao, se realmente o proximo for order quebra o laço
										textoWhere += arrayToken[contArrayToken] + " "; //separa com espaço exemplo table.id >= 'alguma coisa'
										contArrayToken++;
									}
								}
								if(textoWhere.length() > 1){		//se possui mais de um caractere então
									//esse metodo pode lancar excecoes
									criterio.addCondicao(textoWhere);
								}else{
									return null;
								}
							}else if(arrayToken[contArrayToken].toUpperCase().contains("USING(")){ //using(valor)
								String textoWhere = arrayToken[contArrayToken].substring(6, arrayToken[contArrayToken].length()-1); //elimina 'USING(' e ')' e retorna o valor
								if(textoWhere.length() > 1){		//se possui mais de um caractere se o parenteses using() naum está vazio
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
						break;//caso o arrayToken atual não seja inner join sai do laço
					}
					//contArrayToken++; //incrementa para verificar no while se contador está no limite do arrayToken
				}
				
				//PARTE 4 WHERE - depois de achar os joins verifica se tem critérios	
				if(contArrayToken < arrayToken.length){
					if(arrayToken[contArrayToken].equalsIgnoreCase("WHERE")){	//se não for inner join então pode ser WHERE ou ORDER BY
						//***implementar para pegar dados do WHERE
						String textoWhere = " ";
						contArrayToken++;	//incrementa para pegar próximo token, pois o ultimo foi o where
						while(contArrayToken < arrayToken.length){	//enquanto não for final de arrayToken continua
							if(!arrayToken[contArrayToken].equalsIgnoreCase("ORDER")){	//se não for order, acrescenta no texto
								
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
									textoWhere += arrayToken[contArrayToken] + " "; //separa com espaço exemplo table.id >= 'alguma coisa'
									contArrayToken++;
								}
								
							}else{		//se nao, se realmente o proximo for order quebra o laço e add a clausula where no criterio
								break;
							}
						}
						if(textoWhere.length() > 1){		//se possui mais de um caractere então
							//esse metodo pode lancar excecoes
							criterio.addCondicao(textoWhere);
						}else{
							return null;
						}
					}
				}
				
				//PARTE 4 ORDER BY - depois de where verificar ordenação
				if(contArrayToken < arrayToken.length){
					if(arrayToken[contArrayToken].equalsIgnoreCase("ORDER")){	//se não for WHERE pode ser ORDER BY
						contArrayToken++; //se for order o próximo tem que ser BY
						if(arrayToken[contArrayToken].equalsIgnoreCase("BY")){	//tem que ser BY // lança exception
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
									
									}else if(tokenOrderBy.countTokens() == 1){ //se naum for 2 é um e tem somente o nome do campo
										String campo = tokenOrderBy.nextToken();
										criterio.addOrderBy(campo);
									}else{//se contiver mais de 3 tokens isto é diferente de "campo asc" então retorna null
										return null;
									}
								}
								
							}else{	//se o textoOrderBy estiver vazio retorna null como proposito error, é necessário campos e tipo de ordenação
								return null;
							}
						}
					}
				}
				//ao chegar no final, necessario que todos intens de arrayToken deveram ser utilizados
				if(contArrayToken == arrayToken.length){//chegou ao final do arrayToken
					String[] args = toArray(listaAtributosSelect);
					String[] tables = toArray(listaTabelasSelect);
					return new ResponseCommands(Command.SELECT_FROM_JOIN_WHERE, args , null, tables, criterio, null);
				}else{
					//se existe mais itens no arrayToken e não foram utilizados corretamente retorna null, com proposito de comando invalido
					return null; 
				}
				
			}
		}catch(IndexOutOfBoundsException e){
			//caso retorne erro na manipulação de Index, retorna null com propósito de msg de error padrão 
			return null;
		}catch(BeanSQLException e){
			//caso retorne erro em algum comando BeanSQLException, retorna null com propósito de msg de error padrão
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
	
	public static void toCriterios(String texto){
		
	}
	

}
