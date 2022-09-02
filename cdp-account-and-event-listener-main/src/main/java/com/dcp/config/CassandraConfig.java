package com.dcp.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;

@Configuration
public class CassandraConfig extends AbstractCassandraConfiguration {

    @Value("${spring.data.cassandra.username}")
    private String username;

    @Value("${spring.data.cassandra.password}")
    private String password;

    @NotNull
    @Bean
    public CassandraMappingContext cassandraMapping(){
        return new BasicCassandraMappingContext();
    }
    @NotNull
    @Bean
    public MappingCassandraConverter mappingCassandraConverter(){
        return new MappingCassandraConverter(cassandraMapping());
    }
//    @NotNull
//    @Bean
//    public MappingCassandraConverter mappingCassandraConverter1(){
//        return new MappingCassandraConverter();
//    }

    @NotNull
    @Override
    protected String getKeyspaceName() {
        return "mammad";
    }
    @NotNull
    @Bean
    @Override
    public CqlSessionFactoryBean cassandraSession() {
        CqlSessionFactoryBean cassandraSession = super.cassandraSession();//super session should be called only once
        cassandraSession.setUsername(username);
        cassandraSession.setPassword(password);
        return cassandraSession;
    }
//    @Bean
//    public CassandraClusterFactoryBean cluster() {
//        CassandraClusterFactoryBean cluster =
//                new CassandraClusterFactoryBean();
//        cluster.setContactPoints("127.0.0.1");
//        cluster.setPort(9142);
//        return cluster;
//    }

}