
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
	 * M�todo main, respons�vel por executar a aplica��o
	 * @param args
	 */
	public static void main(String[] args) {
		MyConsole console = new Terminal();
		CentralDeComandos central = new CentralDeComandos(console);
		//Start central de comandos para executar a aplica��o, metodo start lan�a Exception
		try{
			central.start();
		}catch(Exception e){
			//Este m�todo pode lan�ar Exceptions
			e.printStackTrace();
		}
		
	}

}
