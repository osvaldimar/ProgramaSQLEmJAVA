
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.graphics;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

import br.com.beansql.control.MyConsole;
import br.com.beansql.log.LogDataBase;
import br.com.beansql.model.Data;
import br.com.beansql.model.DataBase;
import br.com.beansql.model.Tupla;
import br.com.beansql.model.Usuario;

public class Terminal implements MyConsole{

	private File fileData;			//nome da pasta raiz do banco "data"
	private File fileDataBase;		//nome do arquivo principal do banco "databases.db"
	private Scanner scanner;
	private String url = "data\\";
	//private String url = "src\\data\\";
	
	public Terminal() {
		//fileData = new File("src\\data");
		fileData = new File("data");
		fileDataBase = new File(url + "databases.db");
		scanner = new Scanner(System.in);
		
	}
	
	/**
	 * Método responsável por gerar a saída de dados, efetuando print na tela
	 */
	@Override
	public void printOnConsole(String saida) {		
		System.out.print(saida);
	}
	
	/**
	 * Método efetua a leitura de comandos na tela e devolve uma String para cada leitura de linha
	 * @return entrada String - retorna a linha de comando lida no console
	 */
	@Override
	public String readerConsole() {		
		//Console console = System.console();		
		//String entrada = console.readLine();	//string para entrada de dadosde comandos
		
		//Scanner scanner = new Scanner(System.in);
		//String entrada = scanner.nextLine();
		
		//InputStreamReader isr = new InputStreamReader(System.in);
	    //BufferedReader buffer = new BufferedReader(isr);
	    //Scanner scan1 = new Scanner(buffer);
	    //String entrada = scan1.nextLine();
	    
		//BufferedReader in = new BufferedReader(new InputStreamReader( System.in ) );   
		//String entrada = "";
		//try {
			//entrada = in.readLine();
		//} catch (IOException e) {
			//e.printStackTrace();			
		//}   
	    
		String entrada = scanner.nextLine();
		return entrada;
	}
	
	/**
	 * Método efetua a leitura de comandos na tela, porém em forma de password sem mostrar caractere em tela
	 * @return entrada String - retorna a linha de comando lida no console
	 */
	@Override
	public String readerConsolePassword(){		
		Console console = System.console();//console somente funciona quando systema fornece um console ex: prompt
		String entrada = "";
		if(console != null){
			char[] c = console.readPassword();		//string para entrada de dados como password		
			for(int i = 0; i < c.length; i++){
				entrada += c[i];
			}
		}else{
			System.out.println("Error in console for reader password!");
		}
		//String entrada = scanner.nextLine();
		return entrada;
	}
	
	/**
	 * Método realiza a leitura de uma lista de tuplas no arquivo 'database.table.db' e devole a lista de tuplas
	 * @param path String - endereço ou nome do arquivo que será lido os registros
	 * @return tuplas ArrayList<Tupla> - retorna um ArrayList de tuplas (registros)
	 */
	@Override
	public ArrayList<Tupla> readerListTuplas(String path){		
		File file = new File(url+path+".db");
		ArrayList<Tupla> tuplas = new ArrayList<Tupla>();
		
		if(file.exists()){						//existe o arquivo databases.db?
			try{
				FileInputStream fi = new FileInputStream(file);
				ObjectInputStream oi = new ObjectInputStream(fi);
				tuplas = (ArrayList<Tupla>) oi.readObject();
				oi.close();
				fi.close();
				return tuplas;
			}catch(Exception e){
				System.out.println("Exception in method readerListTuplas");
			}			
		}
		return null;
	}

	@Override
	public void writeDB(Data data) {					//persiste banco de dados no Data
		gravarData(fileDataBase, data);		
	}
	
	@Override
	public void writeDatabase(DataBase database) {					//persiste database em um arquivo separado		
		try{
			FileOutputStream fo = new FileOutputStream(url+database.getNameDataBase()+".db");
			ObjectOutputStream oo = new ObjectOutputStream(fo);			
			oo.writeObject(database);
			oo.close();
			fo.close();
		}catch(Exception e){
			System.out.println("Error to write object DataBase!!");
			e.printStackTrace();
		}
	}
	
	@Override
	public DataBase readerDatabase(String name) {					//persiste banco de dados no Data
		File file = new File(url+name+".db");
		DataBase database = null;
		if(file.exists()){						//existe o arquivo databases.db?
			try{
				//System.out.println("use = "+url+name+".db");
				FileInputStream fi = new FileInputStream(file);
				ObjectInputStream oi = new ObjectInputStream(fi);
				database = (DataBase) oi.readObject();
				oi.close();
				fi.close();
				return database;
			}catch(Exception e){
				LogDataBase log = new LogDataBase();	//ainda falta implantar os logs
				database = new DataBase(name, log);
				writeDatabase(database);
				return database;				
			}			
		}else{	//se eu to lendo o arquivo file.db, concerteza ele esta apontado no arquivo principal databases.db, 
				//então por algum motivo o arquivo foi deletado ou não criado, neste caso cria-se um novo arquivo file.db com o nome da DataBase
			try{
				if(file.createNewFile()){
					LogDataBase log = new LogDataBase();	//ainda falta implantar os logs
					database = new DataBase(name, log);
					writeDatabase(database);
					return database;
				}
			}catch(IOException e){
				System.out.println("Error to create new file " + url +name+".db");
			}
			
		}
		return database;
	}
	
	//@SuppressWarnings("unchecked")
	@Override
	public void writeTuplas(Tupla tupla) {				//persiste tuplas em cada arquivo "nomeDataBase.nomeTabela.tbl"
		File file = new File(url+tupla.getPath()+".db"); //txt para testar
		if(file.exists()){
			try{				
				FileInputStream fi = new FileInputStream(file);
				ObjectInputStream oi = new ObjectInputStream(fi);
				ArrayList<Tupla> tuplas = (ArrayList<Tupla>) oi.readObject();
				oi.close();
				fi.close();
				
				tuplas.add(tupla);
				
				FileOutputStream fo = new FileOutputStream(file);
				ObjectOutputStream oo = new ObjectOutputStream(fo);		
				oo.writeObject(tuplas);
				oo.close();
				fo.close();
			}catch(Exception e){
				System.out.println("Error to write object tupla in "+url+tupla.getPath()+".db");
				e.printStackTrace();
			}
		}else{
			try{
				if(file.createNewFile()){
					ArrayList<Tupla> tuplas = new ArrayList<Tupla>();
					tuplas.add(tupla);
					
					FileOutputStream fo = new FileOutputStream(file, true);
					ObjectOutputStream oo = new ObjectOutputStream(fo);			
					oo.writeObject(tuplas);
					oo.close();
					fo.close();
				}
			}catch(IOException e){
				System.out.println("Error to write object tupla in "+url+tupla.getPath()+".db");
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void writeListaDeTuplas(ArrayList<Tupla> listaTuplas, String path) {				//persiste tuplas em cada arquivo "nomeDataBase.nomeTabela.tbl"
		File file = new File(url+ path +".db"); //txt para testar
		if(file.exists()){
			try{				
				FileInputStream fi = new FileInputStream(file);
				ObjectInputStream oi = new ObjectInputStream(fi);
				ArrayList<Tupla> tuplas = (ArrayList<Tupla>) oi.readObject();
				oi.close();
				fi.close();
				
				tuplas = listaTuplas;
				
				FileOutputStream fo = new FileOutputStream(file);
				ObjectOutputStream oo = new ObjectOutputStream(fo);		
				oo.writeObject(tuplas);
				oo.close();
				fo.close();
			}catch(Exception e){
				System.out.println("Error to write object tupla in "+url+path+".db");
				e.printStackTrace();
			}
		}else{
			try{
				if(file.createNewFile()){
					//escreve a propria lista de tuplas	
					FileOutputStream fo = new FileOutputStream(file, true);
					ObjectOutputStream oo = new ObjectOutputStream(fo);			
					oo.writeObject(listaTuplas);
					oo.close();
					fo.close();
				}
			}catch(IOException e){
				System.out.println("Error to write object tupla in "+url+path+".db");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void writeTuplasUpdate(Tupla tupla) {				//persiste tuplas em cada arquivo "nomeDataBase.nomeTabela.tbl"
				
	}

	@Override
	public Data loadDataPrincipal() {
		Data data = new Data(new Usuario("root","root"));	//cria new Data com usuario default
		
		if(fileData.isDirectory()){								//existe o diretorio?
			System.out.println("Load Data successful!!");
			if(fileDataBase.exists()){						//existe o arquivo databases.db?				
				//carregar arquivos
				try{
					FileInputStream fi = new FileInputStream(fileDataBase);
					ObjectInputStream oi = new ObjectInputStream(fi);
					data = (Data) oi.readObject();
					oi.close();
					fi.close();
				}catch(Exception e){
					System.out.println("Error to read object Data, try write new object Data");
					
					//cria new arquivo com nova Data
					gravarData(fileDataBase, data);			//cria new arquivo com nova Data
					System.out.println("New object Data write successful!!");
					
				}
				
			} else		//se nao cria um novo arquivo databases.db
				try {
					
					if(fileDataBase.createNewFile()){						
						gravarData(fileDataBase, data);			//cria new arquivo com nova Data
						System.out.println("New file databases.db created successful!!");						
					}					
					
				} catch (IOException e) {
					System.out.println("Error to create new file databases.db");
					e.printStackTrace();
				}
		}else{			//se nao cria um novo diretorio data
			System.out.println("Data no exist");
			if(fileData.mkdir()){
				System.out.println("New Data was created successful!!");
				try {
					
					if(fileDataBase.createNewFile()){		//criar um novo arquivo databases.db?
						gravarData(fileDataBase, data);			//cria new arquivo com nova Data
						System.out.println("New file databases.db created successful!!");	
					}
					
				} catch (IOException e) {
					System.out.println("Error to create new file databases.db");
					e.printStackTrace();
				}
			}else{		//se não erro ao criar diretorio data
				System.out.println("Error to create new Data");
			}
		}		
		
		return data;
	}
	
	/**
	 * Method grava o objeto Data no arquivo databases.db, caso não exista é criado um objeto default, caso exista é sobrescrito
	 * @param fileDataBase
	 * @param data
	 */
	private void gravarData(File fileDataBase, Data data){
		//cria new arquivo com nova Data e carrega arquivos
		try{
			FileOutputStream fo = new FileOutputStream(fileDataBase);
			ObjectOutputStream oo = new ObjectOutputStream(fo);			
			oo.writeObject(data);
			oo.close();
			fo.close();
		}catch(Exception e){
			System.out.println("Error to write object Data");
			e.printStackTrace();
		}
	}
	@Override
	public void dropTable(String path){
		File file = new File(url+path+".db");
		file.delete();		
	}
	@Override
	public void dropDataBase(String path){
		File file = new File(url+path+".db");
		file.delete();
		//premissa necessário deletar todas tabelas da database responsabilidade do método q chama
	}
}
