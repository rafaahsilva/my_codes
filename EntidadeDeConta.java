package br.com.modapdv.persistence.entities.tenant;

import br.com.modapdv.persistence.entities.BaseEntity;
import br.com.modapdv.persistence.entities.usuario.ContaEntity;
import br.com.modapdv.persistence.listeners.ContaListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@FilterDef(
		name = EntidadeDeConta.TENANT_FILTER_NAME,
		parameters = {@ParamDef(name = EntidadeDeConta.TENANT_PARAMETER_NAME, type = "uuid-char")},
		defaultCondition = EntidadeDeConta.TENANT_COLUMN + " = :" + EntidadeDeConta.TENANT_PARAMETER_NAME
)
@Filter(name = EntidadeDeConta.TENANT_FILTER_NAME)
@EntityListeners(ContaListener.class)
public class EntidadeDeConta extends BaseEntity {

	public static final String TENANT_FILTER_NAME = "contaFilter";
	public static final String TENANT_PARAMETER_NAME = "contaId";
	public static final String TENANT_COLUMN = "conta_id";

	@ManyToOne
	ContaEntity conta;

}
