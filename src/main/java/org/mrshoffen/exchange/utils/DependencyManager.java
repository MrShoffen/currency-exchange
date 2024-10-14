
package org.mrshoffen.exchange.utils;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.validator.HibernateValidator;
import org.mapstruct.factory.Mappers;
import org.mrshoffen.exchange.dao.CurrencyDao;
import org.mrshoffen.exchange.dao.CurrencyDaoImpl;
import org.mrshoffen.exchange.dao.ExchangeRateDao;
import org.mrshoffen.exchange.dao.ExchangeRateDaoImpl;
import org.mrshoffen.exchange.mapper.CurrencyMapper;
import org.mrshoffen.exchange.mapper.ExchangeMapper;
import org.mrshoffen.exchange.mapper.ExchangeRateMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DependencyManager extends AbstractModule {

    private static final Injector INSTANCE = Guice.createInjector(new DependencyManager());

    public static Injector getInjector() {
        return INSTANCE;
    }

    @Override
    protected void configure() {

        bind(CurrencyDao.class).to(CurrencyDaoImpl.class);
        bind(ExchangeRateDao.class).to(ExchangeRateDaoImpl.class);

        bind(Validator.class).toInstance(hibernateValidatorInstance());

        bind(CurrencyMapper.class).toInstance(Mappers.getMapper(CurrencyMapper.class));
        bind(ExchangeRateMapper.class).toInstance(Mappers.getMapper(ExchangeRateMapper.class));
        bind(ExchangeMapper.class).toInstance(Mappers.getMapper(ExchangeMapper.class));
    }

    private Validator hibernateValidatorInstance() {
        return Validation.byProvider(HibernateValidator.class)
                .configure()
                .buildValidatorFactory().getValidator();
    }

}
