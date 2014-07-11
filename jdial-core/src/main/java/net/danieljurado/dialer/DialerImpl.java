package net.danieljurado.dialer;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Provider;

import net.danieljurado.dialer.DialerModule.DialerService;
import net.danieljurado.dialer.DialerModule.Versao;
import net.danieljurado.dialer.configuracoes.Configuracoes;
import net.danieljurado.dialer.estoque.Estoque;
import net.danieljurado.dialer.estoque.EstoqueModule.Agendados;
import net.danieljurado.dialer.estoque.EstoqueModule.Livres;
import net.danieljurado.dialer.gerenciadoragentes.GerenciadorAgentes;
import net.danieljurado.dialer.gerenciadorfatork.GerenciadorFatorK;
import net.danieljurado.dialer.gerenciadorligacoes.GerenciadorLigacoes;
import net.danieljurado.dialer.modelo.Discavel;
import net.danieljurado.dialer.modelo.Ligacao;
import net.danieljurado.dialer.modelo.ModeloModule.DiscavelTsa;
import net.danieljurado.dialer.tratadorespecificocliente.TratadorEspecificoCliente;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jdial.common.Engine;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Campanha;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.Servico;

@DialerService
class DialerImpl implements Service, Runnable {

  private static final Logger logger = LoggerFactory.getLogger(DialerImpl.class);

  private final Configuracoes configuracoes;
  private final GerenciadorAgentes gerenciadorAgentes;
  private final GerenciadorLigacoes gerenciadorLigacoes;
  private final Estoque estoqueLivres;
  private final Estoque estoqueAgendados;
  private final Discavel.Factory discavelFactory;
  private final Engine.Factory engineFactory;
  private final Provider<DaoFactory> daoFactoryProvider;
  private final GerenciadorFatorK gerenciadorFatorK;
  private final TratadorEspecificoCliente tratadorEspecificoCliente;
  private final String versao;

  private Engine engine;

  @Inject
  DialerImpl(Configuracoes configuracoes, Engine.Factory engineFactory,
      @Versao String versao, GerenciadorAgentes gerenciadorAgentes,
      GerenciadorLigacoes gerenciadorLigacoes, @Livres Estoque estoqueLivres,
      @Agendados Estoque estoqueAgendados, @DiscavelTsa Discavel.Factory discavelFactory,
      Provider<DaoFactory> daoFactoryProvider, TratadorEspecificoCliente tratadorEspecificoCliente,
      GerenciadorFatorK gerenciadorFatorK) {
    this.configuracoes = configuracoes;
    this.gerenciadorAgentes = gerenciadorAgentes;
    this.gerenciadorLigacoes = gerenciadorLigacoes;
    this.estoqueLivres = estoqueLivres;
    this.estoqueAgendados = estoqueAgendados;
    this.discavelFactory = discavelFactory;
    this.engineFactory = engineFactory;
    this.daoFactoryProvider = daoFactoryProvider;
    this.gerenciadorFatorK = gerenciadorFatorK;
    this.tratadorEspecificoCliente = tratadorEspecificoCliente;
    this.versao = versao;
    logger.info("Iniciando Dialer {}...", this.versao);

    limpaReservas(configuracoes, daoFactoryProvider, tratadorEspecificoCliente);
  }

  void limpaReservas(Configuracoes configuracoes, Provider<DaoFactory> daoFactoryProvider,
      TratadorEspecificoCliente tratadorEspecificoCliente) {
    DaoFactory daoFactory = daoFactoryProvider.get();
    try {
      Campanha campanha = daoFactory.getCampanhaDao().procura(configuracoes.getNomeCampanha());
      logger.info("Limpando reservas para campanha {}...", campanha.getNome());
      daoFactory.beginTransaction();
      tratadorEspecificoCliente.obtemClienteDao(daoFactory).limpaReservas(campanha,
          configuracoes.getNomeBaseDados(), configuracoes.getNomeBase(),
          configuracoes.getOperador());
      daoFactory.commit();
      logger.info("Limpou reservas para campanha {}", campanha.getNome());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    } finally {
      if (daoFactory.hasTransaction())
        daoFactory.rollback();
      daoFactory.close();
    }
  }

  void rodada(DaoFactory daoFactory, Estoque estoque) {
    Campanha campanha = daoFactory.getCampanhaDao().procura(configuracoes.getNomeCampanha());
    logger.debug("Rodada {} para campanha {}", estoque, campanha.getNome());
    int livres = gerenciadorAgentes.getLivres();

    double fatorK = gerenciadorFatorK.getFatorK();

    int quantidadeLigacoes = gerenciadorLigacoes.getQuantidadeLigacoes();

    int quantidadeLigacoesNaoAtendidas = gerenciadorLigacoes.getQuantidadeLigacoesNaoAtendidas();

    int quantidade = ((int) (livres * fatorK) - quantidadeLigacoesNaoAtendidas);

    logger.info("Rodada {} Livres: {} * fatorK: {} - ligacoes: {} ({} total) = quantidade: {}",
        new Object[] {estoque, livres, fatorK, quantidadeLigacoesNaoAtendidas, quantidadeLigacoes,
            quantidade});

    if (quantidade <= 0)
      return;

    Collection<Cliente> clientesAgendados = estoque.obtemRegistros(quantidade);

    logger.debug("Obtive {} clientes de {}", clientesAgendados.size(), estoque);

    DateTime dataBanco = daoFactory.getDataBanco();
    Servico servico = campanha.getServico();

    for (Cliente cliente : clientesAgendados) {
      Discavel discavel = discavelFactory.create(cliente);
      Ligacao ligacao = new Ligacao.Builder(discavel).setInicio(dataBanco).build();
      DateTime inicio = new DateTime();
      gerenciadorLigacoes.disca(ligacao, servico);
      logger.debug("Discagem demorou {} ms", new Duration(inicio, new DateTime()).getMillis());
    }
  }

  @Override
  public void run() {
    if (!configuracoes.getSistemaAtivo()) {
      logger.warn("Sistema inativo");
      return;
    }
    DaoFactory daoFactory = daoFactoryProvider.get();
    try {
      rodada(daoFactory, estoqueAgendados);
      rodada(daoFactory, estoqueLivres);
    } finally {
      daoFactory.close();
    }

  }

  @Override
  public void start() {
    if (engine != null)
      throw new IllegalStateException();
    DaoFactory daoFactory = daoFactoryProvider.get();
    try {
      engine = engineFactory.create(this, configuracoes.getIntervaloEntreRodadas(), false);
    } finally {
      daoFactory.close();
    }
    logger
        .warn(
            "\n------------------------------------\nIniciado Dialer {}\n------------------------------------",
            versao);
  }

  @Override
  public void stop() {
    logger.debug("Encerrando Dialer {}...", versao);
    if (engine == null)
      throw new IllegalStateException();
    engine.stop();
    engine = null;
    limpaReservas(configuracoes, daoFactoryProvider, tratadorEspecificoCliente);
    logger.info("Encerrado Dialer {}", versao);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
  }
}
