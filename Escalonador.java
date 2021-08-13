import java.util.Scanner;
import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Comparator; 
import java.util.PriorityQueue; 
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Arrays;

public class Escalonador {
	static int quantum;
	public static void main (String [] args) throws Exception {
		String path = System.getProperty("user.dir");
		path = path + "\\processos";
		String qpath = path + "\\quantum.txt";
		String ppath = path + "\\prioridades.txt";
		File processosdir = new File (path);
		File quantumf = new File (qpath);
		File prioridades = new File(ppath);
		try {
			Scanner ler = new Scanner(quantumf);
			quantum = ler.nextInt();
			ler.close();
		}
		catch (Exception e) {
			System.out.println("Arquivo não encontrado!");
		}
		Scanner lerp = new Scanner(prioridades);
		List<bcp> tabela = new LinkedList<bcp>();
		int nprocessos = 0;
		File [] files = processosdir.listFiles();
		Arrays.sort(files);


		String qs = ("log" + new DecimalFormat("00").format(quantum) + ".txt");
		FileOutputStream outputfile =new FileOutputStream(System.getProperty("user.dir") + "\\" + qs);
		PrintStream outstream = new PrintStream(outputfile);
		System.setOut(outstream);
		int un = 0;


		for (File file : files) {
			if (file.getName().equals(quantumf.getName()) || file.getName().equals(prioridades.getName()))	un++;

			else {
				try {
					Scanner ler = new Scanner(file);
					String nome = ler.nextLine();
					int tam = 0;
					List<String> instlist = new LinkedList<String>();
					while (ler.hasNextLine()) {
						instlist.add(ler.nextLine());
						tam++;
					}

					tabela.add(new bcp(lerp.nextInt(), nome, instlist));
					System.out.println("Carregando " + tabela.get(nprocessos).nome);
					nprocessos++;
					ler.close();
				}
				catch (Exception e) {
					System.out.println("Arquivo inválido");
				}
			}

		}

		List<bcp> list = new LinkedList<bcp>();    //armazena os processos da tabela antes de executar
		list.addAll(tabela);


		lerp.close(); //termina de ler e atribuir processos

		List<bcp> list0 = new LinkedList<bcp>();
		List<bcp> list1 = new LinkedList<bcp>();
		List<bcp> list2 = new LinkedList<bcp>();
		List<bcp> list3 = new LinkedList<bcp>();
		List<bcp> list4 = new LinkedList<bcp>();     //filas de acordo com a prioridade

		for (bcp processo : tabela) {
			switch (processo.creditos) {
				case 0:
				list0.add(processo);
				break;
				case 1:
				list1.add(processo);
				break;
				case 2:
				list2.add(processo);
				break;
				case 3:
				list3.add(processo);
				break;
				case 4:
				list4.add(processo);
			}			

		} //adiciona os processos as respectivas filas
		LinkedList<bcp> prontos = new LinkedList<bcp>();
		createProntos(prontos,list0, list1, list2, list3, list4);
		List <bcp> bloqueados = new LinkedList <bcp>();
		while (!(bloqueados.isEmpty()) || !(prontos.isEmpty())) { 		//começa a executar
			while (prontos.isEmpty()) checabloqueados(bloqueados, prontos, list0, list1, list2, list3, list4);
			bcp aux = prontos.get(0);
			prontos.remove(aux);
			System.out.println("Executando " + aux.nome);
			aux.troca++;

			removeCList(list0,list1,list2,list3,list4,aux);
			switch (aux.executa(quantum)) {
				case 0:
				addProntos(prontos, aux);
				addCList(list0,list1,list2,list3,list4,aux);
				break;

				case 2:
				bloqueados.add(aux);
				break;

				case 3:
				tabela.remove(aux);
				break;

				case -1:
				Exception ic = new Exception("Comando invalido no processo: " + aux.nome + " linha: " + (aux.pc + 2));
				throw ic;

			}
			checabloqueados(bloqueados, prontos, list0, list1, list2, list3, list4);
			checacreditos(prontos,bloqueados, list0, list1, list2, list3, list4);

		}

		double mediaTrocas = 0;
		double mediaInstrucoes = 0;
		
		for (int n = 0; n < list.size(); n++){
			mediaTrocas = mediaTrocas + list.get(n).troca;
			mediaInstrucoes = mediaInstrucoes + list.get(n).media;
		}
		
		mediaTrocas = mediaTrocas / list.size();
		mediaInstrucoes = mediaInstrucoes / list.size();
		
		DecimalFormat trans = new DecimalFormat("#.#");
		
		System.out.println("MEDIA DE TROCAS: " + trans.format(mediaTrocas));
		System.out.println("MEDIA DE INSTRUCOES: " + trans.format(mediaInstrucoes));
	}
	public static void removeCList (List<bcp> list0, List<bcp> list1, List<bcp> list2, List<bcp> list3, List<bcp> list4, bcp aux) {
		switch (aux.creditos) {
			case 0:
			list0.remove(aux);
			break;

			case 1:
			list1.remove(aux);
			break;

			case 2:
			list2.remove(aux);
			break;

			case 3:
			list3.remove(aux);
			break;

			case 4:
			list4.remove(aux);
		}
	}
	public static void addCList (List<bcp> list0, List<bcp> list1, List<bcp> list2, List<bcp> list3, List<bcp> list4, bcp aux) {
		switch (aux.creditos) {
			case 0:
			list0.add(aux);
			break;

			case 1:
			list1.add(aux);
			break;

			case 2:
			list2.add(aux);
			break;

			case 3:
			list3.add(aux);
			break;

			case 4:
			list4.add(aux);
		}

	}
	public static void createProntos (LinkedList<bcp> prontos, List<bcp> list0, List<bcp> list1, List<bcp> list2, List<bcp> list3, List<bcp> list4) {
		for (int i = 0; i < list4.size(); i++) prontos.add(list4.get(i));
		for (int i = 0; i < list3.size(); i++) prontos.add(list3.get(i));
		for (int i = 0; i < list2.size(); i++) prontos.add(list2.get(i));
		for (int i = 0; i < list1.size(); i++) prontos.add(list1.get(i));
		for (int i = 0; i < list0.size(); i++) prontos.add(list0.get(i));
	}
	public static void addProntos (LinkedList<bcp> prontos, bcp processo) {
		int i = 0;
		while (i < prontos.size() && prontos.get(i).creditos >= processo.creditos) i++;
		if (i >= prontos.size())prontos.addLast(processo);
		else prontos.add(i, processo);

	}
	public static void checacreditos (LinkedList<bcp> prontos, List<bcp> bloqueados, List<bcp> list0, List<bcp> list1, List<bcp> list2, List<bcp> list3, List<bcp> list4) {
		//PriorityQueue<bcp> clone = new PriorityQueue<bcp>(prontos);
		for (bcp processo : prontos) {
			if (processo.creditos != 0) return;
		}
		for (bcp processo : bloqueados) {
			if (processo.creditos != 0) return;
		}
		List<bcp> clone = new LinkedList<bcp>();
		clone.addAll(prontos);
		prontos.clear();
		for (bcp aux : clone) {
			removeCList(list0, list1, list2, list3, list4, aux);
			aux.creditos = aux.prioridade;
			aux.multiq = 1;
            aux.first = true;
			addCList(list0, list1, list2, list3, list4, aux);
		}
		createProntos(prontos, list0, list1, list2, list3, list4);
	}
	public static void checabloqueados (List<bcp> bloqueados, LinkedList<bcp> prontos, List<bcp> list0, List<bcp> list1, List<bcp> list2, List<bcp> list3, List<bcp> list4) {
		List<bcp> remover = new LinkedList<bcp>();
		int i = 0;
		for (bcp processo : bloqueados) {
			processo.espera--;
			if (processo.espera == 0) {
				addCList(list0,list1,list2,list3,list4,processo);
				addProntos(prontos, processo);
				processo.status = 0;
				remover.add(processo);
			}
		}
		for (bcp processo : remover) bloqueados.remove(processo);
	}
}
