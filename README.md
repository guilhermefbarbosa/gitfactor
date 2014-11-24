gitFactor - Instruções de Uso
=========

##### Requisitos:

* JDK 7
* MySQL 5

##### Para executar a ferramenta, utilize o seguinte comando:

```bash
java -Xmx6144m -Xms6144m -jar gitfactor.jar /var/tmp/git
```

##### O diretório /var/tmp/git é o diretório temporário que será utilizado pela ferramenta para clonar os repositórios.

Para execução da ferramenta será necessário a conexão com um banco de dados MySQL utilizando o arquivo schema.sql como o script inicial do banco de dados.

##### Exemplo de URL para buscar repositórios:

```
// query REST que busca os repositórios com mais de 1000 estrelas e tamanho menor que 1000 kbytes
https://api.github.com/search/repositories?q=language:java stars:>1000 size:<1000&sort=stars&order=desc&per_page=100
```
