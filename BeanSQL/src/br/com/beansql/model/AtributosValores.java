package br.com.beansql.model;

import java.util.ArrayList;

public class AtributosValores {

	private String nomeAtributo;
	private String tabelaOrigem;
	private ArrayList<String> valores;
	
	public AtributosValores(){
		this.nomeAtributo = "";
		this.tabelaOrigem = "";
		this.valores = new ArrayList<String>();
	}
	
	public AtributosValores(String nome, String tabelaOrigem, ArrayList<String> valores) {
		this.nomeAtributo = nome;
		this.tabelaOrigem = tabelaOrigem;
		this.valores = new ArrayList<String>();
		this.valores = valores;		
	}	
	
	public String getNomeAtributo() {
		return nomeAtributo;
	}

	public void setNomeAtributo(String nomeAtributo) {
		this.nomeAtributo = nomeAtributo;
	}
	
	public String getTabelaOrigem(){
		return tabelaOrigem;
	}
	
	public void setTabelaOrigem(String tabelaOrigem){
		this.tabelaOrigem = tabelaOrigem;
	}
	
	//getters and setters
	public ArrayList<String> getValores() {
		return valores;
	}

	public void setValores(ArrayList<String> valores) {
		this.valores = valores;
	}	

}
