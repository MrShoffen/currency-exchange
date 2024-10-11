
package org.mrshoffen.exchange.utils;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.validator.HibernateValidator;
import org.mrshoffen.exchange.dao.CurrencyDao;
import org.mrshoffen.exchange.dao.CurrencyDaoImpl;
import org.mrshoffen.exchange.service.CurrencyService;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DependencyManager extends AbstractModule {

    private static final Injector INSTANCE = Guice.createInjector(new DependencyManager());

    public static Injector getInjector() {
        return INSTANCE;
    }

    @Override
    protected void configure() {
//        bind(CurrencyDao.class).to(CurrencyDaoImpl.class);
//        bind(ExchangeRateDao.class).to(ExchangeRateDaoImpl.class);
        bind(CurrencyDao.class).to(CurrencyDaoImpl.class);
        bind(Validator.class).toInstance(configureValidator());


    }

    private Validator configureValidator() {
        return Validation.byProvider(HibernateValidator.class)
                .configure()
                .buildValidatorFactory().getValidator();
    }

}
