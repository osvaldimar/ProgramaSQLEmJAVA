package br.com.beansql.control;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import br.com.beansql.model.AtributosValores;
import br.com.beansql.model.Tabela;
import br.com.beansql.validators.ResponseCommands;

public class JoinTable {

	private Hashtable<Tabela, Integer> tabelasJoin;
	
	public JoinTable() {
		tabelasJoin = new Hashtable<Tabela, Integer>();
	}
	
	public void addTabelaJoin(Tabela tabelaJoin, int totalValores){
		tabelasJoin.put(tabelaJoin, totalValores);
	}
	
	public int getTotalValorParaJoinTodasTabelas(){
		int result = 1;
		for(Integer i : tabelasJoin.values()){
			result *= i;	//multiplica todos valores de tabelasJoin e retorna o maximo para mixar fazer joins em todas tabelas
		}
		return result;
	}
	
	public Hashtable<Tabela, Integer> getTabelasJoin(){
		return tabelasJoin;
	}
	
	public AtributosValores[] calcularJoinTables(AtributosValores[] arrayAV, ResponseCommands response){
		
		AtributosValores[] resultAV = new AtributosValores[arrayAV.length]; //novo array de AtributosValores para mixar e fazer os joins nas tabelas
		
		int pularEm = 1;	
		for(int j = 0; j < response.getNameOfTabela().length; j++){
			for(Tabela t : tabelasJoin.keySet()){
				if(t.getNameOfTabela().equalsIgnoreCase(response.getNameOfTabela()[j])){
					for(int i = 0; i < arrayAV.length; i++){
						if(arrayAV[i].getTabelaOrigem().equalsIgnoreCase(t.getNameOfTabela())){
							
							int totalJoins = getTotalValorParaJoinTodasTabelas(); //pega o total para mixar entre as tabelas do join
							int contTotalTudo = 0;
							resultAV[i] = new AtributosValores();
							resultAV[i].setNomeAtributo(arrayAV[i].getNomeAtributo());
							resultAV[i].setTabelaOrigem(arrayAV[i].getTabelaOrigem());
							
							while(contTotalTudo < totalJoins){
								int contPegarValor = 0;
								while(contPegarValor < arrayAV[i].getValores().size()){
									int contAteTotal = 0;
									while(contAteTotal < pularEm){
										resultAV[i].getValores().add(arrayAV[i].getValores().get(contPegarValor));
										contAteTotal++;
										contTotalTudo++;
									}
									contPegarValor++;	//indice incrementa de acordo com pularEm 
								}
							}
						}
					}
					pularEm = pularEm*getTabelasJoin().get(t); //este procedimento (pega valor total das tabelas anterior do mix) faz com q a cada tabela do join multiplica o valor das tabelas anteriores para aplicar:
				}
			}
		}
		return resultAV;
	}
	
}
