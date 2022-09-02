package com.dcp.iam.app;

import com.dcp.iam.domain.Company;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface CompaniesRepository extends CassandraRepository<Company, String> {
}
