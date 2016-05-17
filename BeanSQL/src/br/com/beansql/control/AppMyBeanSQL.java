
/**
 * @author OSVALDIMAR COSTA
 * 
 */

package br.com.beansql.control;

import br.com.beansql.graphics.Terminal;

public class AppMyBeanSQL {
	
	public AppMyBeanSQL() {		
	}
	/**
	 * Método main, responsável por executar a aplicação
	 * @param args
	 */
	public static void main(String[] args) {
		MyConsole console = new Terminal();
		CentralDeComandos central = new CentralDeComandos(console);
		//Start central de comandos para executar a aplicação, metodo start lança Exception
		try{
			central.start();
		}catch(Exception e){
			//Este método pode lançar Exceptions
			e.printStackTrace();
		}
		
	}

}
