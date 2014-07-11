package net.danieljurado.dialer.devolveregistro;

import net.danieljurado.dialer.modelo.Ligacao;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Campanha;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.ResultadoLigacao;

interface ModificadorResultadoFilter {

  boolean accept(DaoFactory daoFactory, ResultadoLigacao resultadoLigacao, Ligacao ligacao,
      Cliente cliente, Campanha campanha);

  ResultadoLigacao modifica(DaoFactory daoFactory, ResultadoLigacao resultadoLigacao,
      Ligacao ligacao, Cliente cliente, Campanha campanha);

}
