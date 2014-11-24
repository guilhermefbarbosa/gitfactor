gitFactor - Instruções de Uso
=========

##### Requisitos:

* JDK 7
* MySQL 5

##### Para executar a ferramenta, utilize o seguinte comando:

```bash
java -Dproperties.dir=/var/tmp/gitfactor -Xmx6144m -Xms6144m -jar gitfactor.jar /var/tmp/git
```

##### Conexão de Banco de Dados

Criar um arquivo chamado env.properties no diretório especificado em properties.dir no comando Java acima, com os dados de conexão do MySQL:

```
mysql.url=jdbc:mysql://localhost:3306/gitfactor
mysql.username=root
mysql.password=123456
```

##### O diretório /var/tmp/git é o diretório temporário que será utilizado pela ferramenta para clonar os repositórios.

Para execução da ferramenta será necessário a conexão com um banco de dados MySQL utilizando o arquivo schema.sql como o script inicial do banco de dados.

##### Exemplo de URL para buscar repositórios:

```
// query REST que busca os repositórios com mais de 1000 estrelas e tamanho menor que 1000 kbytes
https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000&sort=stars&order=desc&per_page=100
```
