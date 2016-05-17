
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.model;

import java.io.Serializable;
import java.util.ArrayList;

import br.com.beansql.control.TabelaDecorator;
import br.com.beansql.exceptions.DataBaseBeanSQLException;
import br.com.beansql.log.LogDataBase;

public class Data implements Serializable{

	private DataBase currentDataBase;
	private Usuario currentUsuario;	
	private ArrayList<String> nameOfAllDataBases;
	private ArrayList<Usuario> allUsuarios;
	private ArrayList<LogDataBase> allLogsDataBases;
	
	public Data(Usuario usuario) {
		nameOfAllDataBases = new ArrayList<String>();
		allUsuarios = new ArrayList<Usuario>();
		allLogsDataBases = new ArrayList<LogDataBase>();
		//construtor recebe um usuario default
		//contrutor cria um minimundo "database" default com o nome "BeanSql"		
		currentUsuario = usuario;			//seta usuario current
		allUsuarios.add(usuario);			//add um novo usuario
		nameOfAllDataBases.add("BeanSql"); 	//primeiro minimundo criado pelo usuario default
	}
	
	/**
	 * Method mostra todas as base de dados cadastradas no banco
	 */
	public ArrayList<String> showDataBases(){
		if(nameOfAllDataBases.isEmpty()){
			return null;
		}
		return this.nameOfAllDataBases;
	}
	
	/**
	 * Method adiciona uma nova base de dados no banco e verifica se o nome é válido
	 * @param nameOfdatabase String com o nome da nova DataBase
	 * @throws DataBaseBeanSQLException 
	 */
	public void addDataBase(String nameOfdatabase) throws DataBaseBeanSQLException{
		for(String name : getNameOfAllDataBases()){
			if(name.equalsIgnoreCase(nameOfdatabase)){//se o nome já existir na base dade dados retorna Exception
				throw new DataBaseBeanSQLException("This name or arguments \""+ nameOfdatabase +"\" no is valid or already exist!\n");
			}
		}
		nameOfAllDataBases.add(nameOfdatabase); // caso não exista o nome e for válido add DataBase ao banco
	}
	
	/**
	 * Method deleta a base de dados informada como parametro
	 * @param database
	 */
	public void dropDataBase(DataBase database){
		//deleta a base de dados
	}

	/**
	 * Method set a database utilizada como default para operaçoes atuais
	 * @param database
	 */
	public boolean useDataBase(String nameOfDatabase){
		for(String s : nameOfAllDataBases){
			if(s.equalsIgnoreCase(nameOfDatabase)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method gera um novo log ou atualiza no banco o log com atualizações e e novas operações realizadas
	 * @param log
	 */
	public void gerarLogDeOperacoes(LogDataBase log){
		//adiciona novo log ou atualiza no banco
	}
	
	public void setCurrentDataBase(DataBase database) {
		this.currentDataBase = database;
	}
	
	public DataBase getCurrentDataBase() {
		return currentDataBase;
	}

	public Usuario getCurrentUsuario() {
		return currentUsuario;
	}

	public ArrayList<Usuario> getAllUsuarios() {
		return allUsuarios;
	}

	public ArrayList<String> getNameOfAllDataBases() {
		return nameOfAllDataBases;
	}	
	
}
