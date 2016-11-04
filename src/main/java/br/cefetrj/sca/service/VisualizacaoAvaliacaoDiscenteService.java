package br.cefetrj.sca.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.cefetrj.sca.dominio.PeriodoLetivo;
import br.cefetrj.sca.dominio.Professor;
import br.cefetrj.sca.dominio.Turma;
import br.cefetrj.sca.dominio.avaliacaoturma.Alternativa;
import br.cefetrj.sca.dominio.avaliacaoturma.AvaliacaoTurma;
import br.cefetrj.sca.dominio.repositories.AvaliacaoTurmaRepositorio;
import br.cefetrj.sca.dominio.repositories.TurmaRepositorio;

@Service
public class VisualizacaoAvaliacaoDiscenteService {

	@Autowired
	private TurmaRepositorio repositorio;
	@Autowired
	private AvaliacaoTurmaRepositorio repositorio2;

	private String[] PerguntasAvaliacao = { "Primeira", "Segunda", "Terceira", "Quarta", "Quinta", "Sexta", "Setima",
			"Oitava" };

	public List<Turma> listarTurmaLecionadas(Professor p) {
		List<Turma> t = repositorio.findTurmasLecionadasPorProfessor(p.getMatricula());
		List<Turma> turmasAvaliadas = new ArrayList<>();

		for (Turma turma : t) {
			List<AvaliacaoTurma> a = repositorio2.findAvaliacoesTurmaLista(turma.getId());
			if (a.size() != 0) {
				turmasAvaliadas.add(turma);
			}
		}
		return turmasAvaliadas;
	}

	public List<AvaliacaoTurma> selecionarTurma(Turma t) {
		List<AvaliacaoTurma> tAvaliadas = repositorio2.findAvaliacoesTurmaLista(t.getId());
		System.out.println("service--" + tAvaliadas.size());
		return tAvaliadas;
	}

	public void conversaoRespospa(List<AvaliacaoTurma> avaliacaoTurma) throws IOException {	
		
		String arquivoCSV = "C:/Users/Joao/git/sca/src/main/webapp/data.csv";
		BufferedWriter strW = new BufferedWriter(new FileWriter(arquivoCSV));
		strW.write(
				"Pergunta,Insuficiente(s) ou Ruim(ns),Suficiente(s) ou Regular(es),Bom(ns) ou Boa(s),Otimo(s) ou Otima(s)\n");
		List<Alternativa> resp = new ArrayList<>();
		for (int i = 0; i < avaliacaoTurma.size(); i++) {
			AvaliacaoTurma a = avaliacaoTurma.get(i);
			resp.addAll(a.getRespostas());
		}

		System.out.println("Service" + resp.size());

		int[] valorRespostas = new int[4];
		int posicao = 0;
		int posicaoPerg = 0;
		for (int i = 0; i < resp.size(); i++) {
			int valorResp = 1;
			if (resp.get(posicao).getDescritor().equals("Insuficiente(s) ou Ruim(ns)")) {
				valorRespostas[3] += valorResp;
			}
			if (resp.get(posicao).getDescritor().equals("Suficiente(s) ou Regular(es)")) {
				valorRespostas[2] += valorResp;
			}
			if (resp.get(posicao).getDescritor().equals("Bom(ns) ou Boa(s)")) {
				valorRespostas[1] += valorResp;
			}
			if (resp.get(posicao).getDescritor().equals("Ótimo(s) ou Ótima(s)")) {
				valorRespostas[0] += valorResp;
			}
			posicao += 8;
			if (posicao >= resp.size()) {
				strW.write(PerguntasAvaliacao[posicaoPerg] + "," + valorRespostas[3] + "," + valorRespostas[2] + ","
						+ valorRespostas[1] + "," + valorRespostas[0] + "\n");
				posicaoPerg += 1;
				posicao = posicaoPerg;
				valorRespostas = new int[4];
			}
		}

		strW.close();

	}
}
