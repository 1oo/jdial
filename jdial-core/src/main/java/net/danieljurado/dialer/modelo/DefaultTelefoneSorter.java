package net.danieljurado.dialer.modelo;

import static ch.lambdaj.Lambda.on;
import static org.apache.commons.collections.ComparatorUtils.chainedComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import net.danieljurado.dialer.configuracoes.Configuracoes;
import al.jdi.dao.model.Telefone;
import ch.lambdaj.Lambda;
import ch.lambdaj.function.compare.ArgumentComparator;

class DefaultTelefoneSorter implements TelefoneSorter {

  private final Configuracoes configuracoes;

  @Inject
  DefaultTelefoneSorter(Configuracoes configuracoes) {
    this.configuracoes = configuracoes;
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public List<Telefone> sort(List<Telefone> telefones) {
    if (configuracoes.isPriorizaCelular()) {
      Collections.sort(telefones);
      return telefones;
    }

    ArgumentComparator byPrioridade = new ArgumentComparator(on(Telefone.class).getPrioridade());
    ArgumentComparator byId = new ArgumentComparator(on(Telefone.class).getId());
    Comparator orderBy = chainedComparator(byPrioridade, byId);
    return Lambda.sort(telefones, on(Telefone.class), orderBy);
  }
}