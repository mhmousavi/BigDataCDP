-- docker-compose exec postgres psql -U metrics
drop table active_sessions;
create table active_sessions (ts timestamp, active_sessions int);
