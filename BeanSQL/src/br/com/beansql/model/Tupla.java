
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.model;

import java.io.Serializable;
import java.util.HashMap;

public class Tupla implements Serializable{

	private String path;		//endereço, caminho do arquivo "nomeDataBase.nomeTabela.tbl" para armazenar
	private String[] tuplas;	//Array de cada registro de uma linha(tupla) para armazenar no arquivo
	private HashMap<String, String> mapaTuplas;
	public Tupla(){
		
	}
	public Tupla(String path, String[] tuplas) {
		this.path = path;
		this.tuplas = tuplas;		
	}
	public Tupla(String path, HashMap<String, String> mapaTuplas) {
		this.path = path;
		this.mapaTuplas = mapaTuplas;	
	}
	
	//getters and setters	
	public void setPath(String path){
		this.path = path;
	}
	
	public void setTuplas(String[] tuplas){
		this.tuplas = tuplas;
	}
	public String getPath() {
		return path;
	}
	public String[] getTuplas() {
		return tuplas;
	}
	
	public HashMap<String, String> getMapaTuplas() {
		return mapaTuplas;
	}
	
	

}
