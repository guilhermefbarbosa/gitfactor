gitFactor - Instruções de Uso
=========

##### Requisitos Mínimos:

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

Criar um schema no banco de dados MySQL e executar o arquivo schema.sql para criar as tabelas necessárias:

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

Após início da execução do programa, é solicitado uma url para selecionar os repositórios.
Veja a documentação da api REST do GitHub para maiores informações: https://developer.github.com/v3/search/#search-repositories

##### Exemplo de URL para buscar repositórios:

```
// query REST que busca os repositórios com mais de 1000 estrelas e tamanho menor que 1000 kbytes
https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000&sort=stars&order=desc&per_page=100
```

##### Trecho de Resultado da Execução:

```
2014-11-24 18:12:02 INFO  ClassPathXmlApplicationContext:513 - Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@6155b814: startup date [Mon Nov 24 18:12:02 BRST 2014]; root of context hierarchy
2014-11-24 18:12:02 INFO  XmlBeanDefinitionReader:316 - Loading XML bean definitions from class path resource [applicationContext.xml]
2014-11-24 18:12:03 INFO  PropertySourcesPlaceholderConfigurer:172 - Loading properties file from URL [file:/var/tmp/gitfactor/env.properties]
2014-11-24 18:12:04 INFO  LocalContainerEntityManagerFactoryBean:332 - Building JPA container EntityManagerFactory for persistence unit 'gitfactorUnit'
2014-11-24 18:12:04 WARN  HibernatePersistence:58 - HHH015016: Encountered a deprecated javax.persistence.spi.PersistenceProvider [org.hibernate.ejb.HibernatePersistence]; use [org.hibernate.jpa.HibernatePersistenceProvider] instead.
2014-11-24 18:12:04 INFO  LogHelper:46 - HHH000204: Processing PersistenceUnitInfo [
	name: gitfactorUnit
	...]
2014-11-24 18:12:04 INFO  Version:54 - HHH000412: Hibernate Core {4.3.5.Final}
2014-11-24 18:12:04 INFO  Environment:239 - HHH000206: hibernate.properties not found
2014-11-24 18:12:04 INFO  Environment:346 - HHH000021: Bytecode provider name : javassist
2014-11-24 18:12:04 INFO  Version:66 - HCANN000001: Hibernate Commons Annotations {4.0.4.Final}
2014-11-24 18:12:05 INFO  Dialect:145 - HHH000400: Using dialect: org.hibernate.dialect.MySQLDialect
2014-11-24 18:12:05 INFO  ASTQueryTranslatorFactory:47 - HHH000397: Using ASTQueryTranslatorFactory
2014-11-24 18:12:05 WARN  SettingsFactory:367 - Unrecognized value for "hibernate.hbm2ddl.auto": none
Type the url to search git repositories in GitHub:
https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000&sort=stars&order=desc&per_page=100
2014-11-24 18:12:17 INFO  GitfactorMain:41 - Query String: https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000&sort=stars&order=desc&per_page=100
2014-11-24 18:12:51 INFO  GitHubAnalyser:615 - Searching git repositories...
2014-11-24 18:12:51 INFO  GitHubAnalyser:617 - Found 11 repositories.
2014-11-24 18:12:51 INFO  GitHubAnalyser:97 - Total repositories: 11
2014-11-24 18:12:51 INFO  GitHubAnalyser:296 - Repository: FizzBuzzEnterpriseEdition - stars: 2724
2014-11-24 18:12:51 INFO  GitRepositoryUtils:26 - Cloning from https://github.com/EnterpriseQualityCoding/FizzBuzzEnterpriseEdition.git to /var/tmp/git/FizzBuzzEnterpriseEdition
2014-11-24 18:13:01 INFO  GitHubAnalyser:744 - Checkout master branch OK.
2014-11-24 18:13:01 INFO  GitHubAnalyser:304 - Getting commit logs for repository FizzBuzzEnterpriseEdition.
2014-11-24 18:13:45 INFO  GitHubAnalyser:710 - Ref branch checkout: refs/heads/49799b8e1d4c2364ff4f589042f3837b6475eaa7
2014-11-24 18:13:45 INFO  GitHubAnalyser:710 - Ref branch checkout: refs/heads/3c477e2cd1412cc56f0e16cec92077e2f42787a1
2014-11-24 18:13:45 INFO  GitHubAnalyser:649 - Tempo total buildModelStructure(): 0.144 [s].
2014-11-24 18:13:45 INFO  GitHubAnalyser:651 - Comparing models to get refactorings.
2014-11-24 18:13:45 INFO  GitHubAnalyser:667 - 2 refactorings encontrados.
2014-11-24 18:13:45 INFO  GitHubAnalyser:669 - Extract Operation	private appendObject(builder StringBuilder, value Object) : void extracted from private pushMethod(joinPoint JoinPoint) : void in class hugo.weaving.internal.Hugo
2014-11-24 18:13:45 INFO  GitHubAnalyser:669 - Extract Operation	private appendObject(builder StringBuilder, value Object) : void extracted from private popMethod(joinPoint JoinPoint, result Object, lengthMillis long) : void in class hugo.weaving.internal.Hugo
2014-11-24 18:13:45 INFO  GitHubAnalyser:655 - Tempo total analyseModelRefactorings(): 0.048 [s].
2014-11-24 18:13:45 INFO  GitHubAnalyser:173 - [hugo] Commit 49799b8e1d4c2364ff4f589042f3837b6475eaa7 analysed.
2014-11-24 18:13:45 INFO  GitHubAnalyser:192 - 2 commits analysed.
2014-11-24 18:13:45 INFO  GitHubAnalyser:250 - Updating parent information for commit 3c477e2cd1412cc56f0e16cec92077e2f42787a1.
2014-11-24 18:13:45 INFO  GitHubAnalyser:646 - Building model structure.
2014-11-24 18:13:45 INFO  GitHubAnalyser:744 - Checkout master branch OK.
2014-11-24 18:13:45 INFO  GitHubAnalyser:723 - Deleted branchs: [refs/heads/3c477e2cd1412cc56f0e16cec92077e2f42787a1]
2014-11-24 18:13:45 INFO  GitHubAnalyser:710 - Ref branch checkout: refs/heads/3c477e2cd1412cc56f0e16cec92077e2f42787a1
2014-11-24 18:13:46 INFO  GitHubAnalyser:710 - Ref branch checkout: refs/heads/0a0d0ba9f3405077d5387617a4ddb24bc9971213
2014-11-24 18:13:46 INFO  GitHubAnalyser:649 - Tempo total buildModelStructure(): 0.147 [s].
2014-11-24 18:13:46 INFO  GitHubAnalyser:651 - Comparing models to get refactorings.
2014-11-24 18:13:46 INFO  GitHubAnalyser:667 - 1 refactorings encontrados.
2014-11-24 18:13:46 INFO  GitHubAnalyser:669 - Extract Operation	private isMainThread() : boolean extracted from private pushMethod(joinPoint JoinPoint) : void in class hugo.weaving.internal.Hugo
2014-11-24 18:13:46 INFO  GitHubAnalyser:655 - Tempo total analyseModelRefactorings(): 0.011 [s].
```
