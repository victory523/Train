CREATE USER '${exporter_username}'@'localhost' IDENTIFIED BY '${exporter_password}' WITH MAX_USER_CONNECTIONS 3;

GRANT PROCESS, REPLICATION CLIENT, SELECT ON *.* TO '${exporter_username}'@'localhost';