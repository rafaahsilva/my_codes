package br.com.modapdv.persistence.aspects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.com.modapdv.persistence.entities.tenant.EntidadeDeConta;
import br.com.modapdv.usecase.security.UsuarioPrincipal;

@Aspect
@AllArgsConstructor
@Component
public class ContaFilterAspect {

    @PersistenceContext
    private final EntityManager entityManager;

    @Before("execution(* br.com.modapdv.persistence.repositories.tenant.RepositorioDeConta+.*(..))")
    public void beforeFindOfTenantableRepository() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth == null) {
			entityManager
				.unwrap(Session.class)
				.disableFilter(EntidadeDeConta.TENANT_FILTER_NAME);
		} else {
			var principal = (UsuarioPrincipal) auth.getPrincipal();
			entityManager
				.unwrap(Session.class)
				.enableFilter(EntidadeDeConta.TENANT_FILTER_NAME)
				.setParameter(
					EntidadeDeConta.TENANT_PARAMETER_NAME, principal.getContaId()
				);
		};
    }

}
