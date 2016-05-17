
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.beansql.validators.TiposDeDados;


public class Coluna implements Serializable {
	
	private String nameOfAtributo;
	private ConjuntoTiposDeDados conjuntoTiposDeDados;
	
	public Coluna(String name, ConjuntoTiposDeDados conjuntoTiposDeDados){
		this.conjuntoTiposDeDados = conjuntoTiposDeDados;
		this.nameOfAtributo = name;
	}
	//metodo paralelo, futuramente implementar o tamanho do varchar
	public boolean isValidItemInseridoNaColuna(TiposDeDados tipo, String item){
		if(tipo.equals(TiposDeDados.INTEGER)){
			int i = Integer.parseInt(item);
			//lança exception NumberFormatException // tratamento de erros
			return true;
		}else if(tipo.equals(TiposDeDados.DOUBLE)){
			double i = Double.parseDouble(item);
			//lança exception NumberFormatException // tratamento de erros
			return true;		
		}else if(tipo.equals(TiposDeDados.VARCHAR)){
			if(item.substring(0,1).equals("'") && item.substring(item.length()-1, item.length()).equals("'")){	//se a string informada estiver dentro do apostofro ' '				
				//verifica se o tamanho do varchar é igual ou menor ao tamanho definido no conjunto de tipos da coluna atual
				if(item.length()-2 <= conjuntoTiposDeDados.getTamanhoVarchar()){
					return true;
				}				
			}
		}else if(tipo.equals(TiposDeDados.CHAR)){
			if(item.substring(0,1).equals("'") && item.substring(item.length()-1, item.length()).equals("'")){	//se a string informada estiver dentro do apostofro ' '				
				//verifica se o tamanho do varchar é igual ou menor ao tamanho definido no conjunto de tipos da coluna atual
				if(item.length()-2 == 1){
					return true;
				}		
			}
		}else if(tipo.equals(TiposDeDados.DATE)){
			if(item.substring(0,1).equals("'") && item.substring(item.length()-1, item.length()).equals("'")){	//se a string informada estiver dentro do apostofro ' '				
				//verifica se o tamanho do varchar é igual ou menor ao tamanho definido no conjunto de tipos da coluna atual
				//falta enteder esse padra de data dd/mm/aaaa
				String itemData = item.substring(1,item.length()-1); //string para armazenar a data sem ' '				
				Pattern padrao = Pattern.compile(
						"^((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$"); 	// yyyy-mm-dd
				
						//"^((0[1-9]|[12]\\d)\\/(0[1-9]|1[0-2])|30\\/(0[13-9]|1[0-2])|31" +	
						//"\\/(0[13578]|1[02]))" +
						//"\\/\\d{4}$");													// dd/mm/yyyy

				Matcher pesquisa = padrao.matcher(itemData);
				if(pesquisa.matches()){
					return true;
				}
			}
		}
		return false;
	}
	
	//Getters and Setters
	
	public String getName() {
		return nameOfAtributo;
	}

	public void setName(String name) {
		this.nameOfAtributo = name;
	}

	public ConjuntoTiposDeDados getTiposDeDados() {
		return conjuntoTiposDeDados;
	}

	public void setTiposDeDados(ConjuntoTiposDeDados conjuntoTiposDeDados) {
		this.conjuntoTiposDeDados = conjuntoTiposDeDados;
	}
	
	
	
}
