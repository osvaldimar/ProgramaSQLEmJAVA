package br.com.beansql.control;

import java.util.HashMap;

import br.com.beansql.exceptions.BeanSQLException;

/**Classe responsável por dar nomes e apelidos as colunas e tabelas
 * o banco add esta classe como um objeto temporario que tem referencias 
 * de apelidos para tabelas e campos
 * ao fim do select o banco remove esta classe que contem as referencias dos apelidos
 */	
public class ALIAS {
	
	private HashMap<String, String> mapaCamposAS = new HashMap<String, String>();
	private HashMap<String, String> mapaTabelasAS = new HashMap<String, String>();
		
	public ALIAS() {
		mapaCamposAS = new HashMap<String, String>();
		mapaTabelasAS = new HashMap<String, String>();
	}
	
	//diferente do mysql, mysql deixa ter dois apelidos para atributos diferentes porem pode dar erros em consulta dos apelidos
	public void addCampoAS(String campoAS, String campoOriginal) throws BeanSQLException{
		//não pode conter apelidos repetidos
		if(mapaCamposAS.containsKey(campoAS.toLowerCase())){			
			throw new BeanSQLException("Column '" + campoAS + "' is ambiguous");
		}else{
			mapaCamposAS.put(campoAS.toLowerCase(), campoOriginal);
		}
	}
	
	public void addTabelaAS(String tabelaAS, String tabelaOriginal) throws BeanSQLException{
		//não pode conter apelidos repetidos
		if(mapaTabelasAS.containsKey(tabelaAS.toLowerCase())){			
			throw new BeanSQLException("Column '" + tabelaAS + "' is ambiguous");
		}else{
			mapaTabelasAS.put(tabelaAS.toLowerCase(), tabelaOriginal);
		}
	}
	//getters and setters
	
	public HashMap<String, String> getMapCamposAS() {
		return mapaCamposAS;
	}

	public void setMapCamposAS(HashMap<String, String> mapCamposAS) {
		this.mapaCamposAS = mapCamposAS;
	}

	public HashMap<String, String> getMapaTabelasAS() {
		return mapaTabelasAS;
	}

	public void setMapaTabelasAS(HashMap<String, String> mapaTabelasAS) {
		this.mapaTabelasAS = mapaTabelasAS;
	}
	
	
	

}
