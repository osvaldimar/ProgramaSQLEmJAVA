
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.log;

import java.io.Serializable;
import java.util.ArrayList;

import br.com.beansql.model.DataBase;
import br.com.beansql.model.Usuario;

public class LogDataBase implements Serializable{

	private DataBase dataBaseUtilizada;
	private Usuario usuario;
	private ArrayList<String> textoDeOperacoes;
	
	
	public LogDataBase() {
		
	}
	
	/**
	 * Method efetua a escrita de novos textos e String de logs de operações
	 * @param textoDeOperacoes
	 */
	public void gerarTextoDeOperacoes(String textoDeOperacoes){
		
	}

}
