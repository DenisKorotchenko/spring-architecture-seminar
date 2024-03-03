package ru.dksu.configuration;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.dksu.entity.UserEntity;

@Configuration
public class HibernateConfiguration {
    @Bean
    public SessionFactory sessionFactory() {
        return new org.hibernate.cfg.Configuration()
                .addAnnotatedClass(UserEntity.class)
                .buildSessionFactory();
    }
//    @Bean
//    public DataSource dataSource() {
//        Hibernate
//    }
}