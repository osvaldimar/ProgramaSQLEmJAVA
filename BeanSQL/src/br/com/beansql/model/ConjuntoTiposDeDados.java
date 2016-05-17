package br.com.beansql.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.beansql.exceptions.TipoDadosBeanSQLException;
import br.com.beansql.validators.TiposDeDados;

public class ConjuntoTiposDeDados implements Serializable{

	private TiposDeDados tipo;
	private TiposDeDados isNull = TiposDeDados.NULL;//inicializa com null, aceita null por padrão
	private TiposDeDados isUnique;
	private TiposDeDados PK;
	private int tamanhoVarchar;
	
	public ConjuntoTiposDeDados(){ //construtor public
	}
	
	public void setOthersNullUniquePK(String others) throws TipoDadosBeanSQLException{
		if(others.equalsIgnoreCase("NULL")){	//se outros for null
			this.isNull = TiposDeDados.NULL;
		}else if(others.equalsIgnoreCase("NOT_NULL")){//se outros for not_null
			this.isNull = TiposDeDados.NOT_NULL;
		}else if(others.equalsIgnoreCase("NOT NULL")){//se outros for not null nome composto
			this.isNull = TiposDeDados.NOT_NULL;
		}else if(others.equalsIgnoreCase("UNIQUE")){  //se outros for unique
			this.isUnique = TiposDeDados.UNIQUE;
		}else if(others.equalsIgnoreCase("PRIMARY_KEY")){  //se outros for primary_key
			this.PK = TiposDeDados.PRIMARY_KEY;
		}else if(others.equalsIgnoreCase("PRIMARY KEY")){  //se outros for primary key nome composto
			this.PK = TiposDeDados.PRIMARY_KEY;
		}else{
			throw new TipoDadosBeanSQLException("Type no is valid!");
		}
	}
	
	public void setTipo(String tipo) throws TipoDadosBeanSQLException{		
		
		Pattern padrao = Pattern.compile("(v|V)(a|A)(r|R)(c|C)(h|H)(a|A)(r|R)" +
				"\\(\\d{1,6}\\)");
		Matcher pesquisa = padrao.matcher(tipo);
		if(pesquisa.matches()){	//se o varchar estiver no padrao ex: varchar(40)
			this.tipo = TiposDeDados.VARCHAR;
			try{
				String s = tipo.substring(8,tipo.length()-1);//pega o inteiro depois do parenteses e antes de fechar o parenteses
				this.tamanhoVarchar = Integer.parseInt(s);
			}catch(Exception e){
				throw new TipoDadosBeanSQLException("Type no is valid!");
			}
		}else if(tipo.equalsIgnoreCase("CHAR")){
			this.tipo = TiposDeDados.CHAR;
		//esee else informa que o varchar padrão é 40
		//}else if(tipo.equalsIgnoreCase("VARCHAR")){
			//this.tipo = TiposDeDados.VARCHAR;
			//this.tamanhoVarchar = 40;
		}else if(tipo.equalsIgnoreCase("INTEGER")){
			this.tipo = TiposDeDados.INTEGER;
		}else if(tipo.equalsIgnoreCase("DOUBLE")){
			this.tipo = TiposDeDados.DOUBLE;
		}else if(tipo.equalsIgnoreCase("DATE")){
			this.tipo = TiposDeDados.DATE;
		}else{
			throw new TipoDadosBeanSQLException("Type no is valid!");
		}
	}

	public void setIsNull(TiposDeDados isNull) {
		this.isNull = isNull;
	}

	public void setIsUnique(TiposDeDados isUnique) {
		this.isUnique = isUnique;
	}

	public void setPK(TiposDeDados pK) {
		PK = pK;
	}

	public TiposDeDados getTipo() {
		return tipo;
	}

	public TiposDeDados getIsNull() {
		return isNull;
	}

	public TiposDeDados getIsUnique() {
		return isUnique;
	}

	public TiposDeDados getPK() {
		return PK;
	}

	public int getTamanhoVarchar() {
		return tamanhoVarchar;
	}
	
	
	

}
