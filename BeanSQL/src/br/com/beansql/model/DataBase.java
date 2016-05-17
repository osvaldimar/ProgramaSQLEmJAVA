
/**
 * @author OSVALDIMAR
 * 
 */

package br.com.beansql.model;

import java.io.Serializable;
import java.security.AllPermission;
import java.util.ArrayList;

import br.com.beansql.control.ALIAS;
import br.com.beansql.exceptions.AliasBeanSQLException;
import br.com.beansql.exceptions.DataBaseBeanSQLException;
import br.com.beansql.exceptions.DataBaseBeanSQLException;
import br.com.beansql.exceptions.TableBeanSQLException;
import br.com.beansql.exceptions.TipoDadosBeanSQLException;
import br.com.beansql.log.LogDataBase;
import br.com.beansql.validators.SequenciaDeComandos;

public class DataBase implements Serializable {

	private String nameDataBase;
	private LogDataBase log;
	private ArrayList<Tabela> tabelas;
	private ALIAS alias;
	
	public DataBase(String name, LogDataBase log) {
		this.nameDataBase = name;
		this.log = log;
		this.tabelas = new ArrayList<Tabela>();
		this.alias = null;
	}
	
	/**
	 * 1 Válida nomes das tabelas original pelo 'value do hashmap', valida se o apelido da tabela 'key do hashmap' é um nome válido e sem duplicatas
	 * 2 Válida nomes dos campos original pelo 'value do hashmap', valida se o apelido do campo 'key do hashmap' é um nome válido e sem duplicatas
	 * 
	 * @param alias
	 * @throws AliasBeanSQLException 
	 */
	public void setAlias(ALIAS alias, String[] nameOfTable) throws AliasBeanSQLException{
		this.alias = alias;
		//apelido das tabelas
		if(!alias.getMapaTabelasAS().isEmpty()){
			for(String key : alias.getMapaTabelasAS().keySet()){
				
				//verifica se o valor do hashmap (nome da tabela orginal) contem na database
				try {
					if( containsNameOfTable(alias.getMapaTabelasAS().get(key)) ){	//este método lança exception caso tabela original naum exista
						
						//verifica se a propria key (apelido) é valido ou se já existe também
						try{
							if( containsNameOfTable(key) ){	//se naum tiver a tabela lança exception					
								//se o nome da tabela for valido add como ficticio ou fantasia nas tabelas da database como temporário
								//se o apelido já for um nome da tabela da database retorna error												
								throw new AliasBeanSQLException("Table alias '" + alias.getMapaTabelasAS().get(key) + "' already exist!\n");					
							}	
						}catch(DataBaseBeanSQLException e){	
							//este processo lança exception caso o apelido da tabela não contem na base de dados, é experado que isto ocorra sempre
						}
						
					}
					
				} catch (DataBaseBeanSQLException e) {
					throw new AliasBeanSQLException(e.getMessage());	//lança exception caso tabela não exista Table doesn't exist				
				}
				
			}
			//se foram validados e naum ocorreu erros é add no banco as tabelas alias
			for(String key : alias.getMapaTabelasAS().keySet()){
				Tabela tabela = new Tabela(key);	//key é o nome da tabela como apelido
				try {
					 //método seta as todas as colunas da tabela original pelo value do hashmap
					tabela.setColunas(getTabelaPeloNome( alias.getMapaTabelasAS().get(key) ).getColuna() ); //retorna as colunas da tabela original e seta na tabela apelido
				} catch (DataBaseBeanSQLException e) {
					// Este processo não lança exception, pois já foi validado os nomes das tabelas
					e.printStackTrace();
				}
				tabelas.add(tabela);
			}
		}
		
		//apelido dos campos
		if(!alias.getMapaTabelasAS().isEmpty()){
			//valida nome das tabelas
			String[] tabelasEscolhidas = SequenciaDeComandos.toArray( alias.getMapaTabelasAS().values());	//retorna todas as tabelas originais escolhidas do 'value do hashmap' como um array de String
			for(String nomeDaTabela : tabelasEscolhidas){
				try {
					containsNameOfTable(nomeDaTabela);	//lança exception caso nome da tabela não exista, pode chegar aki se somente tiver campos com apelidos, necessário verificar tabela original
				} catch (DataBaseBeanSQLException e) {
					throw new AliasBeanSQLException(e.getMessage());	//retorna o erro com a informação de tabela original inexistente
				} 
			}
			
			//valida campos originais pelo 'value do hashmap'
			for(String key : alias.getMapCamposAS().keySet()){	//for para pegar as chaves que são apelidos dos campos
				//passa os nomes das tabelas originais e um campo original por vez do for para validar os originais (campo e tabela), valida duplicatas de campo e campo que não existe
				try {
					//lança exception caso nomes incorretos dos originais, é verificado agora pelos nomes das tabelas originais
					validarDuplicatasNomeCampoTabela( SequenciaDeComandos.toArray(alias.getMapaTabelasAS().values()), alias.getMapCamposAS().get(key) );
				} catch (DataBaseBeanSQLException e) {
					
						//caso retorne exception pode ser que o nome da tabela e o campo é referenciado por apelido ex: tableApelido.campoOriginal
					try {
						//lança exception caso nomes incorretos dos originais, porém é verificado agora pelos nomes das tabelas apelidos
						validarDuplicatasNomeCampoTabela( SequenciaDeComandos.toArray(alias.getMapaTabelasAS().keySet()), alias.getMapCamposAS().get(key) );
					
					} catch (DataBaseBeanSQLException e1) {
						try{
							//caso ainda não foi validado é possivel que o value deste apelido está em uma tabela que não foi declarada um apelido, verificamos em todas escolhidas informadas
							validarDuplicatasNomeCampoTabela( nameOfTable, alias.getMapCamposAS().get(key) );
							
						}catch(DataBaseBeanSQLException e2){
							throw new AliasBeanSQLException(e1.getMessage());	//retorna o erro com a informação de campo original inexistente
						}
					}
				} 
				
			}
			
			//valida os apelidos dos campos, se chegou aki sabemos que os nomes originais dos campos(nome absoluto) não contem duplicados e podemos referenciar para achar em qualquer tabela da database
			//obs é possivel que as tabelas escolhidas tenham duplicatas, mas o método principal de quem chama trata duplicatas de tabelas
			for(String key : alias.getMapCamposAS().keySet()){
				//valida se todas os apelidos dos campos 'key do hashmap' são validos e se não existe o campo na tabela
				try{
					//valida os apelidos dos campos, necessário que os apelidos sejam unicos
					validarDuplicatasNomeCampoTabela( SequenciaDeComandos.toArray( alias.getMapaTabelasAS().values() ), alias.getMapCamposAS().get(key)); //lança exception caso nomes incorretos dos originais
				}catch(DataBaseBeanSQLException e){
					//é necessário que caia no catch pois sabemos que os apelidos não estão duplicados e não existe na tabela
					
					if(e.getMessage().contains("ambiguous")){ 
						throw new AliasBeanSQLException(e.getMessage()); //se for erro de ambiguidade lança exception
					}else{
						//caso somente não exista o apelido, é valido
					}
					
				}
			}
			
			//se chegou aqui com sucesso, nomes tabelas originais validos, nomes campos originais validos e apelidos validos
			
		}
		
	}
	
	public void removeAlias(ALIAS alias) {
		this.alias = null;
		if(!alias.getMapaTabelasAS().isEmpty()){
			for(String key : alias.getMapaTabelasAS().keySet()){	//os nomes das tabelas Alias são as proprias keys do hashmap
				try{
					Tabela tabela = getTabelaPeloNome( key ); //método retorna o objeto Tabela da lista de tabelas pelo nome informado que é o proprio apelido
					tabelas.remove(tabela);	//remove a tabela apelido da listas de tabelas da database
				}catch(DataBaseBeanSQLException e){
					//esta exception não é lançada uma vez q todos os apelidos são válidos e estão na database
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Method retorna lista de String com as tabelas correspondente ao banco de dados - DataBase
	 * @return ArrayList<String>
	 */
	public ArrayList<String> showTables(){
		ArrayList<String> nameOfAllTabelas = new ArrayList<String>();
		if(tabelas.isEmpty()){
			return null;		//se tabela estiver vazia retorna uma lista null
		}else{
			for(Tabela t : tabelas){
				nameOfAllTabelas.add(t.getNameOfTabela());
			}
			return nameOfAllTabelas;
		}		
	}
	
	/**
	 * Method adiciona uma nova tabela ao banco, é necessário checar, é responsabilidade de quem chama o metodo
	 * @param tabela
	 * @throws TableBeanSQLException 
	 */
	public void addTabela(Tabela tabela) throws TableBeanSQLException{
		for(Tabela t : tabelas){
			if(t.getNameOfTabela().equalsIgnoreCase(tabela.getNameOfTabela())){
				throw new TableBeanSQLException("Name of table \""+tabela.getNameOfTabela()+"\" invalid or already exist!\n");
			}
		}
		tabelas.add(tabela);
	}
	/**
	 * Metodo verifica se o nome da tabela informado como String contém
	 * na lista de tabelas do DataBase
	 * @return boolean retorna true se a database conter o nome da tabela
	 * @throws DataBaseBeanSQLException 
	 */
	public boolean containsNameOfTable(String nomeTabela) throws DataBaseBeanSQLException{
		for(Tabela t : tabelas){	//se conter o nome da tabela na lista de tabelas do database
			if(t.getNameOfTabela().equalsIgnoreCase(nomeTabela)){
				return true;
			}
		}
		//caso não acha o nome da tabela retorna exception com a descrição de tabela não existe
		throw new DataBaseBeanSQLException("Table '"+ nomeTabela + "' doesn't exist\n");
	}
	
	//retornar nome completo do campo com a tabela exemplo: entrada: campo1 ou tabela.campo1 > saida: tabela.campo1
	//este método também verifica se os campos passados não contem duplicatas
	public String[] getCamposNomeAbsoluto(String[] nomeTabelasUsadas, String[] campos) throws DataBaseBeanSQLException{
		String[] camposNomeAbsoluto = new String[campos.length];
		for(int i = 0; i < campos.length; i++){
			//esse método lança exception caso nome do campo incorreto, não existe ou duplicado entre as tabelas
			validarDuplicatasNomeCampoTabela(nomeTabelasUsadas, campos[i]);
			
			//se foi validado acima, verifica qual é a tabela do campo e transforma em campo com nome absoluto
			for(String s : nomeTabelasUsadas){
				for(Tabela t : getTabelas()){	 //contador de todas tabelas da database
					if(t.getNameOfTabela().equalsIgnoreCase(s)){
						if(t.containsAtributo(campos[i])){	//sabemos que naum contem duplicatas já foi analizado metodo anterior
							//se contem na tabela o nome do campo, entaum colocamos o nome da tabela + campo e valida ex: table.campo1
							String campoAbsoluto = t.getNameOfTabela() + "." + campos[i];
							if(t.containsAtributo(campoAbsoluto)){ //valida se o nome absoluto contem na tabela e se não está: table.table.campo1
								camposNomeAbsoluto[i] = campoAbsoluto;
							}else{ //se não conter o nome "campos[i]" já estava como absoluto
								camposNomeAbsoluto[i] = campos[i];
							}
						}
					}
				}
			}				
		}
		return camposNomeAbsoluto;
	}
	//validação
	public void validarDuplicatasNomeCampoTabela(String[] nomeTabelasUsadas, String nomeCampo) throws DataBaseBeanSQLException{
		boolean achouAtributo = false;
		for(Tabela t : getTabelas()){	 //contador de todas tabelas da database				
			for(String s : nomeTabelasUsadas){ //contador das tabelas do response
				if(t.getNameOfTabela().equalsIgnoreCase(s)){
					if(t.containsAtributo(nomeCampo)){
						//se achouAtributo é false seta true
						if(!achouAtributo){
							achouAtributo = true;
						}else{ //se achouAtributo já é true e achou outro contém duplicata nas tabelas
							throw new DataBaseBeanSQLException("Column '" + nomeCampo + "' is ambiguous, verify syntax version to your BeanSQL!\n");
						}
					}
				}
			}
		}
		if(!achouAtributo){	//se nao achar o atributo retorna error
			throw new DataBaseBeanSQLException("Unknown column '"+ nomeCampo + "' in 'field list' or doesn't exist\n");
		}
	}
	
	/**
	 * Método recebe uma String com o nome da tabela e retorna um objeto Tabela correspondente ao nome informado
	 * @param nomeDaTabela String - recebe uma String com o nome da tabela desejada
	 * @return Tabela tabela - retorna a tabela pelo nome informado
	 * @throws DataBaseBeanSQLException - Este método lança uma Exception informando que a Tabela não existe caso nome incorreto
	 */
	public Tabela getTabelaPeloNome(String nomeDaTabela) throws DataBaseBeanSQLException{
		for(Tabela t : tabelas){
			if(t.getNameOfTabela().equalsIgnoreCase(nomeDaTabela)){
				return t;
			}
		}
		throw new DataBaseBeanSQLException("Table '" + nomeDaTabela + "' doesn't exist!\n"); //se não achar o nome da tabela, ela não existe
	}
	
	//Getters and Setters

	public String getNameDataBase() {
		return nameDataBase;
	}

	public void setNameDataBase(String nameDataBase) {
		this.nameDataBase = nameDataBase;
	}

	public LogDataBase getLog() {
		return log;
	}

	public void setLog(LogDataBase log) {
		this.log = log;
	}

	public ArrayList<Tabela> getTabelas() {
		return tabelas;
	}

	
}
