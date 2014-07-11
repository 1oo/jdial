package net.danieljurado.dialer.devolveregistro;

import javax.inject.Inject;

import net.danieljurado.dialer.configuracoes.Configuracoes;
import net.danieljurado.dialer.modelo.Ligacao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Campanha;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.ResultadoLigacao;

class ModificadorResultadoInexistenteFake implements ModificadorResultadoFilter {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final Configuracoes configuracoes;

  @Inject
  ModificadorResultadoInexistenteFake(Configuracoes configuracoes) {
    this.configuracoes = configuracoes;
  }

  @Override
  public boolean accept(DaoFactory daoFactory, ResultadoLigacao resultadoLigacao, Ligacao ligacao,
      Cliente cliente, Campanha campanha) {
    if (configuracoes.isUraReversa())
      return false;
    ResultadoLigacao resultadoLigacaoAtendida =
        daoFactory.getResultadoLigacaoDao().procura(-1, campanha);
    return resultadoLigacao.equals(resultadoLigacaoAtendida) && !ligacao.isAtendida();
  }

  @Override
  public ResultadoLigacao modifica(DaoFactory daoFactory, ResultadoLigacao resultadoLigacao,
      Ligacao ligacao, Cliente cliente, Campanha campanha) {
    logger.info("Alterando resultado por inexistenteFake {}", cliente);
    return daoFactory.getResultadoLigacaoDao().procura(13, campanha);
  }

}
