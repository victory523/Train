CREATE USER '${exporter_username}' IDENTIFIED BY '${exporter_password}' WITH MAX_USER_CONNECTIONS 3;

GRANT USAGE, PROCESS, REPLICATION CLIENT, SELECT ON *.* TO '${exporter_username}';
