
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.model;

import java.io.Serializable;
import java.util.ArrayList;

import br.com.beansql.exceptions.TipoDadosBeanSQLException;

public class Tabela implements Serializable{
	
	private String nameOfTabela;
	private ArrayList<Coluna> colunas;
	private boolean jaExistePK;
	
	public Tabela(String name) {
		colunas = new ArrayList<Coluna>();
		nameOfTabela = name;
		jaExistePK = false;
	}	
	
	/**
	 * Method mostra os detalhes da tabela
	 *
	 */
	public void describeTable(){
		//descreve os detalhes das tabelas mostrando na saida como atributos e valores
	}
	
	/**
	 * Method adiciona uma nova coluna na tabela
	 * É verificado se o nome é válido, o tipo e as chaves são válidas
	 * @param Coluna
	 * @throws TipoDadosBeanSQLException 
	 */
	public void addColuna(Coluna coluna) throws TipoDadosBeanSQLException{
		for(Coluna c : colunas){
			if(c.getName().equalsIgnoreCase(coluna.getName())){
				throw new TipoDadosBeanSQLException("Name of attributes \""+coluna.getName()+"\" invalid or already exist!\n");
			}			
		}
		//verifica se já existe uma pk, se sim nenhuma outra coluna pode ter pk
		if(coluna.getTiposDeDados().getPK() != null){
			if(!jaExistePK){
				jaExistePK = true;
			}else{
				throw new TipoDadosBeanSQLException("Name of attributes \""+coluna.getName()+"\" invalid, ambiguity primary key or already exist!\n");
			}
		}
		colunas.add(coluna);
	}
	
	/**
	 * Method altera uma coluna existente
	 * @param Coluna
	 */
	public void alterColuna(Coluna coluna){
		
	}
	
	/**
	 * Method deleta a coluna especificada no parametro
	 * @param Coluna
	 */
	public void dropColuna(Coluna coluna){
		
	}

	
	
	public String getNameOfTabela() {
		return nameOfTabela;
	}
	
	public ArrayList<Coluna> getColuna(){
		return colunas;
	}
	public void setColunas(ArrayList<Coluna> listaColuna){
		colunas = listaColuna;
	}
	/**
	 * Method verifica se o argumento informado 
	 * é um nome de algum atributo da lista de colunas da tabela
	 * pode ser referenciado pela coluna (column) ou o nome da tabela+coluna(table.column)
	 * @param args
	 * @return boolean
	 */
	public boolean containsAtributo(String args){
		for(Coluna c : colunas){
			if(c.getName().equalsIgnoreCase(args)
					|| (getNameOfTabela()+"."+c.getName()).equalsIgnoreCase(args)){
				return true;
			}
		}
		return false;
	}
	
}
