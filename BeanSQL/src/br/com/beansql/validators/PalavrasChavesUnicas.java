
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.validators;

/**
 * Classe utilizada para verificar as palavras chaves e sintaxe do banco de dados
 * @author osvaldimar.costa
 *
 */
public class PalavrasChavesUnicas {
	
	private PalavrasChavesUnicas() {

	}
	
	/**
	 * Method verifica se a String passada como parametro é uma palavra chave unica 
	 * reservada da sintaxe do banco de dados
	 * @param palavra passada como argumento será verificada
	 * @return boolean retorna true se é uma key-word
	 */
	public static boolean isWord(String palavra){
		boolean isPalavra = false;
		
		for(Words s : Words.values()){
			if(s.toString().equalsIgnoreCase(palavra)){
				isPalavra = true;
			}
		}
		
		return isPalavra;
	}

}
