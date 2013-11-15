# Poseidon Server

Poseidon Server is java application that can run and 
evaluate custom monitoring scripts agains Oracle, Mysql and 
PostgreSQL databases. The repository database can 
be managed using Poseidon UI application. 

## Requirements 

* Linux x86_64
* JRE 1.6/7

## Setup

### Setup repository

Install and setup Poseidon UI application, which will setup repository
database. 

### Download and extract the software

### Setup database user to access repository database. 

	grant select,insert,update,delete on <db_name>.* 
	to <app_user>@'%' identified by '***';

### Create new server in Poseidon UI

Create new server record using Poseidon UI. Server ID will be needed
in the configuration file below

### Create configuration file

In the ./conf subdirectory, copy provided poseidon.ini.example file to 
poseidon.ini and modify it with repository database information and
server ID from above. 

### Startup/Shutdown

Startup/shutdown server using ./bin/poseidon_server script

	./bin/poseidon_server start|stop
