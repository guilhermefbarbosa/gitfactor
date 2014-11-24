gitFactor - Instruções de Uso
=========

##### Requisitos:

* JDK 7
* MySQL 5
* Maven 3

##### Getting started

Fazer o clone do projeto pelo git:

```
git clone https://github.com/guilhermefbarbosa/gitfactor.git
```

Chamar o comando do Maven para gerar o jar e copiar para uma pasta de execução do programa, por exemplo: /var/tmp/gitfactor

```
mvn clean install
```

Utilizar o comando do maven para copiar as dependências do projeto para o diretório /var/tmp/gitfactor/libs:

```
mvn -DoutputDirectory=/var/tmp/gitfactor/libs dependency:copy-dependencies
```

##### Conexão de Banco de Dados

Criar um schema no banco de dados MySQL e executrar o arquivo schema.sql para criar as tabelas necessárias:

```
https://github.com/guilhermefbarbosa/gitfactor/blob/master/gitfactor/src/main/resources/schema.sql
```

Criar um arquivo chamado env.properties no diretório especificado para o jar, com os dados de conexão do MySQL:

```
mysql.url=jdbc:mysql://localhost:3306/gitfactor
mysql.username=root
mysql.password=123456
```

O diretório /var/tmp/git é o diretório temporário que será utilizado pela ferramenta para clonar os repositórios.

##### Para executar a ferramenta, utilize o seguinte comando:

```bash
java -Dproperties.dir=/var/tmp/gitfactor -Xmx6144m -Xms6144m -jar gitfactor.jar /var/tmp/git
```

##### Exemplo de URL para buscar repositórios:

```
// query REST que busca os repositórios com mais de 1000 estrelas e tamanho menor que 1000 kbytes
https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000&sort=stars&order=desc&per_page=100
```
