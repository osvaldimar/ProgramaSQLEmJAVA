
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.control;

import java.util.ArrayList;

import br.com.beansql.model.Data;
import br.com.beansql.model.DataBase;
import br.com.beansql.model.Tupla;

public interface MyConsole {
	
	public void printOnConsole(String saida);
	
	public String readerConsole();
	
	public String readerConsolePassword();	
	
	public ArrayList<Tupla> readerListTuplas(String path);
	
	public void writeDB(Data data);
	
	public void writeDatabase(DataBase database);
	
	public DataBase readerDatabase(String name);
	
	public void writeTuplas(Tupla tupla);
	
	public void writeTuplasUpdate(Tupla tupla);
	
	public void writeListaDeTuplas(ArrayList<Tupla> listaTuplas, String path);
		
	public Data loadDataPrincipal();
	
	public void dropTable(String path);
	
	public void dropDataBase(String path);

}
