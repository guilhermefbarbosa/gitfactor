gitFactor - Instruções de Uso
=========

##### Requisitos:

[] JDK 7
[] MySQL 5

##### Para executar a ferramenta, utilize o seguinte comando:

java -Xmx6144m -Xms6144m -jar gitfactor.jar /var/tmp/git

##### O diretório /var/tmp/git é o diretório temporário que será utilizado pela ferramenta para clonar os repositórios.

Para execução da ferramenta será necessário a conexão com um banco de dados MySQL utilizando o arquivo schema.sql como o script inicial do banco de dados.
