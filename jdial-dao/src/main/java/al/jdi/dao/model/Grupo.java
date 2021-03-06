package al.jdi.dao.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import al.jdi.dao.beans.Dao.CampoBusca;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"codigo"})}, indexes = {@Index(
    name = "IX_grupo_codigo", columnList = "codigo")})
public class Grupo implements DaoObject {
  @Id
  @GeneratedValue
  @Column(name = "idGrupo")
  private Long id;

  @Embedded
  private CriacaoModificacao criacaoModificacao = new CriacaoModificacao();

  @CampoBusca
  @Column(nullable = false)
  private String codigo;

  private String descricao;

  private boolean visivelOperador = true;

  private boolean semAgentes = false;

  public Grupo() {}

  private Grupo(Long id, CriacaoModificacao criacaoModificacao, String codigo, String descricao,
      boolean visivelOperador, boolean semAgentes) {
    this.id = id;
    this.criacaoModificacao = criacaoModificacao;
    this.codigo = codigo;
    this.descricao = descricao;
    this.visivelOperador = visivelOperador;
    this.semAgentes = semAgentes;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Grupo other = (Grupo) obj;
    return new EqualsBuilder().append(id, other.id).isEquals();
  }

  public String getCodigo() {
    return codigo;
  }

  @Override
  public CriacaoModificacao getCriacaoModificacao() {
    return criacaoModificacao;
  }

  public String getDescricao() {
    return descricao;
  }

  public Long getId() {
    return id;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(id).toHashCode();
  }

  public boolean isSemAgentes() {
    return semAgentes;
  }

  public boolean isVisivelOperador() {
    return visivelOperador;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setSemAgentes(boolean semAgentes) {
    this.semAgentes = semAgentes;
  }

  public void setVisivelOperador(boolean visivelOperador) {
    this.visivelOperador = visivelOperador;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", id)
        .append(codigo).toString();
  }

  public Grupo clone() {
    return new Grupo(id, criacaoModificacao, codigo, descricao, visivelOperador, semAgentes);
  }
  
}
