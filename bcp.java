import java.util.List;
import java.util.LinkedList;

public class bcp {
	String nome;    //nome do processo
	int pc;         //contador de programa
	int status;    //0 = pronto, 1 = executando, 2 = bloqueado, 3 = acabou
	int prioridade;
	int creditos;
	int multiq;
	boolean first;
	int espera;
	int x; 
	int y;    //registradores
	List<String> insts;  //referencia ao segmento de texto do programa
	int troca;
	double nQuanta;
	double nInstrucoes;
	double media;

	public bcp (int prioridade, String nome, List<String> insts) {
		this.pc = 0;
		this.status = 0;              //0 = pronto, 1 = executando, 2 = bloqueado
		this.creditos = prioridade;
		this.prioridade = prioridade;
		this.nome = nome;
		this.insts = insts;
		this.multiq = 1;
		this.espera = 0;
		this.first = true;
		this.troca = 0;
		this.nQuanta = 0;
		this.nInstrucoes = 0;
		this.media = 0;
	}
	public int executa (int quantum) {   //retorna status do programa, 0 = não acabou, 1 = executando, 2 = bloqueado, 3 = acabou
		int i = 0;
		int nq = multiq*quantum;
		status = 1;
		
		nQuanta = nQuanta + multiq;
		
		while (i < nq) {
			nInstrucoes++;
			String inst = insts.get(pc).toUpperCase();    //um dos arquivos possuia diferenciação nos capitais das letras: "COm
			if (inst.contains("E/S")) {
				System.out.println("E/S iniciada em " + nome); 
				System.out.println("Interrompendo " + nome + " após " + (i+1) + " instruções");
				pc++;
				status = 2;
				espera = 3;
				setcredito(-2);
				return 2;
			}
			else if (inst.contains("SAIDA")) {
				status = 3;
				System.out.println(nome + " Terminado. " + "X=" + x + ". Y=" + y);
				media = nInstrucoes / nQuanta;
				//System.out.println("media= " + media);
				return 3;
			}
			else {
				if(inst.contains("COM")) pc++;
				else if(inst.contains("=")) {
					String [] sub = inst.split("=");
					if (sub[0].contains("X")) x = Integer.parseInt(sub[1]);
					else if (sub[0].contains("Y")) y = Integer.parseInt(sub[1]);
					pc++;
				}
				else return -1;     //COMANDO INVALIDO
			}
			i++;
		}
		setcredito(-2);
		status = 0;
		System.out.println("Interrompendo " + nome + " após " + nq + " instruções");
		return 0;

	}
	public void setx (int x) {
		this.x = x;
	}
	public void sety (int y) {
		this.y = y;
	}

	public void setcredito (int creditos) {
		int ant = this.creditos;
		this.creditos = this.creditos + creditos;
		if (this.creditos < 0) this.creditos = 0;
		if (ant == this.creditos) return;
		if (prioridade == 0) this.multiq = 1;
		if (status == 2) this.multiq = this.multiq + 1;
		if (first == false && prioridade != 0 && status != 2) this.multiq = this.multiq + 1;
		if (first == true && prioridade != 0 && status != 2) this.multiq = 2;

		first = false;
	}

	public void setpc (int pc) {
		this.pc = pc;
	}
}