package net.danieljurado.dialer.devolveregistro;

import net.danieljurado.dialer.modelo.Ligacao;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.ResultadoLigacao;

interface ProcessoDevolucao extends Comparable<ProcessoDevolucao> {

  int getOrdem();

  boolean accept(Ligacao ligacao, Cliente cliente, ResultadoLigacao resultadoLigacao,
      DaoFactory daoFactory);

  boolean executa(Ligacao ligacao, Cliente cliente, ResultadoLigacao resultadoLigacao,
      DaoFactory daoFactory);
}
