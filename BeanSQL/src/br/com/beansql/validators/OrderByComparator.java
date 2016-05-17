package br.com.beansql.validators;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import br.com.beansql.exceptions.BeanSQLException;
import br.com.beansql.model.AtributosValores;

public class OrderByComparator implements Comparator<HashMap<String, String>> {

	private static String[] campos;
	private static Operador[] tipoOrdem;
	
	public OrderByComparator() {	
	}

	@Override
	public int compare(HashMap<String, String> registro1, HashMap<String, String> registro2) {
		
		int num = 0;
		for(int i = 0; i < campos.length; i++){
			//campos pode vir com o nome do atributo de duas formas: campo1 ou table1.campo1
			if(tipoOrdem[i] == Operador.ASC){
				num = registro1.get( campos[i].toLowerCase() ).compareTo(registro2.get( campos[i].toLowerCase() )); //case sensitive faz diferença em keys do hashmap
			}else if(tipoOrdem[i] == Operador.DESC){
				num = -registro1.get(campos[i]).compareTo(registro2.get(campos[i])); //colocando sinal de "-" retorna o numero ao contrario se positivo negativo e vice versa
			}			
			if(num == 0){	//então é igual verifica mais condições do order by por outros campos
				continue;
			}else{
				break;	//finaliza loop e retorna o num
			}
		}
		
		return num;
	}
	
	public static AtributosValores[] sortOrderBy(AtributosValores[] arrayAV, String[] campos, Operador[] tipoOrdem){
		
		OrderByComparator.campos = campos;
		OrderByComparator.tipoOrdem = tipoOrdem;
		
		ArrayList< HashMap<String, String> > listaRegistros = new ArrayList< HashMap<String, String> >();	//lista dos registros
		
		for(int i = 0; i < arrayAV[0].getValores().size(); i++){		//for para contagem dos valores em uma coluna de valores	
			
			HashMap<String, String> mapaRegistro = new HashMap<String, String>();
			
			//premissa os nomes das chaves (colunas ou campos) tem que está com nome absoluto, pode haver campos de tabelas com nomes iguais
			for(int j = 0; j < arrayAV.length; j++){	//for para contagem dos atributos
				String key = arrayAV[j].getTabelaOrigem() + "." + arrayAV[j].getNomeAtributo(); //nome absoluto do campo ex: table.campo1
				String value = arrayAV[j].getValores().get(i);
				mapaRegistro.put(key, value);		//add no mapa o campo e o valor				
			}
			listaRegistros.add(mapaRegistro);
		}
		
		//ordena de acordo com a comparação do objeto  OrderByComparator que terá campos e tipo de ordem para ordenar
		Collections.sort(listaRegistros, new OrderByComparator());
		
		//recupera os valores da lista de registros(HashMap) para um array de Atributos Valores
		for(int i = 0; i < arrayAV.length; i++){		//for para contagem do tamnho de keys do hashmap - tamanho de colunas	
			
			//premissa para recuperar os valores de um campo, os nomes do campo tem que está com nome absoluto
			AtributosValores av = new AtributosValores();		//novo AtributosValores
			av.setNomeAtributo(arrayAV[i].getNomeAtributo());
			av.setTabelaOrigem(arrayAV[i].getTabelaOrigem());
			for(int j = 0; j < listaRegistros.size(); j++){	//for para contagem dos atributos
				String valor = listaRegistros.get(j).get(arrayAV[i].getTabelaOrigem() + "." + arrayAV[i].getNomeAtributo());//nome campo absoluto
				av.getValores().add(valor);			
			}
			arrayAV[i] = av;		//seta no arrayAV o novo AtributosValores com uma nova lista ordenada
		}
				
		return arrayAV;
	}

}
