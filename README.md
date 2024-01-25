# FIAP-GRUPO-44
NetFlips
## Introdução:

O presente projeto foi implementado visando entrega de um sistema de exibição de videos utilizando tecnologias Spring Boot, Spring WebFlux, MongoDB além da utilização de testes unitáriss, mocks e de integração

![img_2.png](img_2.png)

<h1 align="center">
  Desenvolvimento das APIs
</h1>

## Tecnologias

- [Spring Boot](https://spring.io/projects/spring-boot):Modulo derivado do Spring Framework que facilita desenvolvimento de aplicações java implementando injeção e inversão de dependencias
- [Sprig Webflux](https://docs.spring.io/spring-framework/reference/web/webflux.html): Permite trabalhar com programação reativa em aplicações Java com Spring.
- [MongoDB](https://www.mongodb.com/pt-br): Banco de dados orientado a documentos livre, de código aberto e multiplataforma
- [JUnit](https://junit.org/junit5/): O JUnit é um framework open-source, que se assemelha ao raio de testes software java
- [Mockito](https://site.mockito.org/): Framework de teste de código aberto
- [Postman](https://learning.postman.com/docs/developer/postman-api/intro-api/): Ferramenta destinada a desenvolvedores que possibilita testar chamadas API e gerar documentação de forma iterativa.Foi usado neste projeto para gerar collections e realizar teste de chamadas aos endpoints;
- [Tortoise](https://tortoisegit.org/docs/tortoisegit/): Ferramenta gerencial que facilita manipulação de projetos em GIT. Foi usado neste projeto para resolução de conflitos.
- [Sourcetree](https://confluence.atlassian.com/get-started-with-sourcetree): Assim como o Tortoise é uma ferramenta gerencial para facilitar o desenvolvimento de projetos em Git, no entanto possui uma interface mais receptivel e navegabilidade facilitada.Foi usado neste projeto paa navegação e criação de ramos.
## Práticas adotadas


- Uso de DTOs para a API
- Injeção de Dependências


## Como Executar

### Localmente
- Clonar repositório git
- Construir o projeto:
```
./mvnw clean package
```
- Executar:


A API poderá ser acessada em [localhost:8080](http://localhost:8080)

O Swagger poderá ser visualizado em [localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)


<h1 align="center">
  API Usuario
</h1>

<p align="center">
 https://gitlab.com/mattec1/grupo-44-netflips-videos-fiap
</p>

API para gerenciar usuarios.Deve ser executado em primeira instancia antes do cadastro de videos e exibição.

## API Endpoints

Para fazer as requisições HTTP abaixo, foi utilizada a ferramenta [http](https://web.postman.co/workspaces):

Lista de usuarios apresenta fluxo reativo com respostas http de elementos usuários os quais possuem listas de historico de exibição e demais informações

LISTAR USUARIO:

- GET /usuario
```
http GET http://localhost:8080/usuario

HTTP/1.1 200 OK
Content-Type: application/json
[
	{
		"headers": {},
		"body": {
			"content": [
				{
					"codigo": "65b18f7dcd84b0450c9c8e61",
					"nome": "Marcos Pereira",
					"email": "pereira.marcos@example.com",
					"cpf": "792.130.600-41",
					"telefone": "(35)99949-1526",
					"dataNascimento": "1974-02-27",
					"historicoExibicao": []
				},
				{
					"codigo": "65afb552d39a0455941d790c",
					"nome": "Joao Rocha",
					"email": "joao.rocha@example.com",
					"cpf": "175.475.496-16",
					"telefone": "(35)98765-1778",
					"dataNascimento": "1971-06-15",
					"historicoExibicao": [
						{
							"codigo": "65b051af2051060580aa4477",
							"dataVisualizacao": "2024-01-23T12:34:56",
							"pontuacao": 4.5,
							"visualizado": true,
							"recomenda": true
						}
					]
				},
				{
					"codigo": "65afb1b8530647553bbbd0c7",
					"nome": "Herick Raposo",
					"email": "herickraposo@example.com",
					"cpf": "030.601.856-03",
					"telefone": "(35)98765-1778",
					"dataNascimento": "1997-10-21",
					"historicoExibicao": [
						{
							"codigo": "65b03eae07c56d07afe465c4",
							"dataVisualizacao": "2024-01-23T12:34:56",
							"pontuacao": 4.5,
							"recomenda": false
						},
						{
							"codigo": "65b040295d1f7c43501cd112",
							"dataVisualizacao": "2024-01-23T12:34:56",
							"pontuacao": 4.5,
							"recomenda": false
						},
						{
							"codigo": "65b041909ba13f2a9aff692c",
							"dataVisualizacao": "2024-01-23T12:34:56",
							"pontuacao": 4.5,
							"recomenda": false
						},
						{
							"codigo": "65b045ac79ff0a5273575e94",
							"dataVisualizacao": "2024-01-23T12:34:56",
							"pontuacao": 4.5,
							"recomenda": true
						}
					]
				}
			],
			"pageable": {
				"pageNumber": 0,
				"pageSize": 10,
				"sort": {
					"empty": false,
					"sorted": true,
					"unsorted": false
				},
				"offset": 0,
				"paged": true,
				"unpaged": false
			},
			"last": true,
			"totalPages": 1,
			"totalElements": 3,
			"size": 10,
			"number": 0,
			"sort": {
				"empty": false,
				"sorted": true,
				"unsorted": false
			},
			"numberOfElements": 3,
			"first": true,
			"empty": false
		},
		"statusCode": "OK",
		"statusCodeValue": 200
	}
]

```
CADASTRO DE USUARIO
- POST /usuario
```
http POST http://localhost:8080/usuario

HTTP/1.1 200 OK
Content-Type: application/json

Request body:

{
  "nome": "Marcos Pereira",
  "email": "pereira.marcos@example.com",
  "cpf": "792.130.600-41",
  "telefone": "(35)99949-1526",
  "dataNascimento": "1974-02-27"
}

Response:
{
    "codigo": "65b18f7dcd84b0450c9c8e61",
    "nome": "Marcos Pereira",
    "email": "pereira.marcos@example.com",
    "cpf": "792.130.600-41",
    "telefone": "(35)99949-1526",
    "dataNascimento": "1974-02-27",
    "historicoExibicao": []
}

```

BUSCA DE USUARIO POR CODIGO
- GET /usuario/{codigo}
```
GET http://localhost:8080/pessoas/65afb552d39a0455941d790c
HTTP/1.1 200 OK
Content-Type: application/json

{
    {
    "codigo": "65afb552d39a0455941d790c",
    "nome": "Joao Rocha",
    "email": "joao.rocha@example.com",
    "cpf": "175.475.496-16",
    "telefone": "(35)98765-1778",
    "dataNascimento": "1971-06-15",
    "historicoExibicao": [
        {
            "codigo": "65b051af2051060580aa4477",
            "dataVisualizacao": "2024-01-23T12:34:56",
            "pontuacao": 4.5,
            "visualizado": true,
            "recomenda": true
        }
    ]
}
}
```
Atualizando usuario:
- PUT /usuario/{codigo}
```
http://localhost:8080/usuario/657ed5327ecc4c4548497a10
HTTP/1.1 200 OK
Content-Type: application/json
transfer-encoding: chunked
{
  "nome": "Herick",
  "email": "raposo@example.com",
  "cpf": "123.456.789-09",
  "telefone": "(35)98765-4321",
  "dataNascimento": "1990-01-01"
}

```

Deletando usuario:
- DELETE /usuario/{codigo}
```
DELETE http://localhost:8080/usuario/657ed5327ecc4c4548497a10
HTTP/1.1 204 No Content
Content-Length: 142
Content-Type: application/json

```

<h1 align="center">
  API Video
</h1>

<p align="center">
 https://gitlab.com/mattec1/grupo-44-netflips-videos-fiap
</p>

API para gerenciamento de Videos. Após manipulçao de usuarios deve-se inserir os videos e manipula-los posteriormente conforme necessidade.

## API Endpoints


Lista de Videos

- GET /video

```
http GET http://localhost:8080/video
[
    {
        "headers": {},
        "body": {
            "content": [
                {
                    "codigo": "65aff31d12e1b2616604a6c4",
                    "titulo": "High School Musical",
                    "url": "https://www.exemplo.com/highschoolmusical",
                    "dataPublicacao": "2024-01-23T12:34:56",
                    "nomesCategorias": [
                        "Filme",
                        "Musical",
                        "Teem"
                    ]
                },
                {
                    "codigo": "65aff35812e1b2616604a6c5",
                    "titulo": "Panico",
                    "url": "https://www.exemplo.com/panico",
                    "dataPublicacao": "2024-01-23T12:34:56",
                    "nomesCategorias": [
                        "Filme",
                        "Terror"
                    ]
                },
                {
                    "codigo": "65b197624d9eda1d250e273c",
                    "titulo": "Homem Aranha no Aranhaverso",
                    "url": "https://www.exemplo.com/homem_aranha_aranhaverso",
                    "dataPublicacao": "2024-01-22T20:34:56",
                    "nomesCategorias": [
                        "Filme",
                        "Aventura",
                        "Fantasia",
                        "Teem"
                    ]
                },
                {
                    "codigo": "65b1966e4d9eda1d250e273b",
                    "titulo": "O Massacre da Serra Elétrica",
                    "url": "https://www.exemplo.com/massacre",
                    "dataPublicacao": "2024-01-22T18:34:56",
                    "nomesCategorias": [
                        "Filme",
                        "Terror"
                    ]
                }
            ],
            "pageable": {
                "pageNumber": 0,
                "pageSize": 10,
                "sort": {
                    "empty": false,
                    "sorted": true,
                    "unsorted": false
                },
                "offset": 0,
                "paged": true,
                "unpaged": false
            },
            "last": true,
            "totalPages": 1,
            "totalElements": 4,
            "size": 10,
            "number": 0,
            "sort": {
                "empty": false,
                "sorted": true,
                "unsorted": false
            },
            "numberOfElements": 4,
            "first": true,
            "empty": false
        },
        "statusCode": "OK",
        "statusCodeValue": 200
    }
]
```

Possiveis filtros;
- - **pagina**:Parametro não obrigatório que define o numero da pagina que o usuário deseja acessar
- - **tamanho**: Parametro não obrigatório que define a quantidade de itens que serão retornados pela listagem
- - **Titulo**: Parametro não obrigatório que filtra a lista pela String do tituloe;
- - **Data publicação**:Parametro não obrigatório que filtra por localdatetime da data de publicação;
- - **Categoria**:Parametro não obrigatório que filtra a lista com base no nome da categoria filtrada

* GET /video/{codigo}
```
http://localhost:8080/video/65aff31d12e1b2616604a6c4
 {
    "codigo": "65aff31d12e1b2616604a6c4",
    "titulo": "High School Musical",
    "url": "https://www.exemplo.com/highschoolmusical",
    "dataPublicacao": "2024-01-23T12:34:56",
    "nomesCategorias": [
        "Filme",
        "Musical",
        "Teem"
    ]
}
```

CADASTRO DE VIDEOS
* POST /eletrodomesticos

```
http POST http://localhost:8080/video
HTTP/1.1 201 CREATED
Content-Type: application/json

{
  "titulo": "O chamado 3",
  "url": "https://www.exemplo.com/ochamado_3",
  "dataPublicacao": "2023-12-31T20:35:56",
  "categorias": [1,7,8]
}
```
ATUALIZAÇÃO DE VIDEO:

- PUT /video/{codgio}
```
http://localhost:8080/video/65b199f64d9eda1d250e273f
HTTP/1.1 200 OK
Content-Length: 129
Content-Type: application/json

{
  "titulo": "O chamado",
  "url": "https://www.exemplo.com/ochamado",
  "dataPublicacao": "2023-12-31T20:35:56",
  "categorias": [1,7,8]
}
```
DELEÇÃO DE VIDEO
- DELETE /video/{codigo}
```
DELETE http://localhost:8080/video/65b199f64d9eda1d250e273f
HTTP/1.1 204 No Content
Content-Length: 142
Content-Type: application/json

```

**CATEGORIAS**
1. Filme;
2. Serie;
3. Documentario;
4. Comedia;
5. Ação;
6. Aventura;
7. Suspense;
8. Terror;
9. Fantasia;
10. Ficção Cientifica;
11. Musical;
12. Historico;
13. Anime;
14. Dorama;
15. Teen;
16. Sitcom;

<h1 align="center">
  API EXIBIÇÃO
</h1>

<p align="center">
 https://gitlab.com/mattec1/grupo-44-netflips-videos-fiap
</p>

API para gerenciamento de Exibições. Após manipulçao de usuarios e videos deve-se inserir os visualizações de videos que serão vinculadas ao historico do usuario.


## API Endpoints

LISTAR EXIBIÇÃO:

- GET /exibicao

```
GET http://localhost:8080/exibicao

RESPONSE
[
    {
        "headers": {},
        "body": {
            "content": [
                {
                    "codigo": "65b1ae094d9eda1d250e2740",
                    "dataVisualizacao": "2024-01-23T21:00:56",
                    "pontuacao": 5.0,
                    "visualizado": true,
                    "recomenda": true,
                    "usuario": {
                        "codigo": "65afb1b8530647553bbbd0c7",
                        "nome": "Herick Raposo",
                        "email": "herickraposo@example.com",
                        "cpf": "030.601.856-03",
                        "telefone": "(35)98765-1778",
                        "dataNascimento": "1997-10-21",
                        "historicoExibicao": null
                    },
                    "video": {
                        "codigo": "65aff35812e1b2616604a6c5",
                        "titulo": "Panico",
                        "url": "https://www.exemplo.com/panico",
                        "dataPublicacao": "2024-01-23T12:34:56",
                        "nomesCategorias": [
                            "Filme",
                            "Terror"
                        ]
                    }
                },
                {
                    "codigo": "65b1ae794d9eda1d250e2741",
                    "dataVisualizacao": "2024-01-23T18:00:56",
                    "pontuacao": 5.0,
                    "visualizado": true,
                    "recomenda": true,
                    "usuario": {
                        "codigo": "65afb1b8530647553bbbd0c7",
                        "nome": "Herick Raposo",
                        "email": "herickraposo@example.com",
                        "cpf": "030.601.856-03",
                        "telefone": "(35)98765-1778",
                        "dataNascimento": "1997-10-21",
                        "historicoExibicao": null
                    },
                    "video": {
                        "codigo": "65b1966e4d9eda1d250e273b",
                        "titulo": "O Massacre da Serra Elétrica",
                        "url": "https://www.exemplo.com/massacre",
                        "dataPublicacao": "2024-01-22T18:34:56",
                        "nomesCategorias": [
                            "Filme",
                            "Terror"
                        ]
                    }
                },
                {
                    "codigo": "65b1aea44d9eda1d250e2742",
                    "dataVisualizacao": "2024-01-23T18:00:56",
                    "pontuacao": 5.0,
                    "visualizado": true,
                    "recomenda": false,
                    "usuario": {
                        "codigo": "65afb1b8530647553bbbd0c7",
                        "nome": "Herick Raposo",
                        "email": "herickraposo@example.com",
                        "cpf": "030.601.856-03",
                        "telefone": "(35)98765-1778",
                        "dataNascimento": "1997-10-21",
                        "historicoExibicao": null
                    },
                    "video": {
                        "codigo": "65b197624d9eda1d250e273c",
                        "titulo": "Homem Aranha no Aranhaverso",
                        "url": "https://www.exemplo.com/homem_aranha_aranhaverso",
                        "dataPublicacao": "2024-01-22T20:34:56",
                        "nomesCategorias": [
                            "Filme",
                            "Aventura",
                            "Fantasia",
                            "Teem"
                        ]
                    }
                },
                {
                    "codigo": "65b051af2051060580aa4477",
                    "dataVisualizacao": "2024-01-23T12:34:56",
                    "pontuacao": 4.5,
                    "visualizado": true,
                    "recomenda": true,
                    "usuario": {
                        "codigo": "65afb552d39a0455941d790c",
                        "nome": "Joao Rocha",
                        "email": "joao.rocha@example.com",
                        "cpf": "175.475.496-16",
                        "telefone": "(35)98765-1778",
                        "dataNascimento": "1971-06-15",
                        "historicoExibicao": null
                    },
                    "video": {
                        "codigo": "65aff31d12e1b2616604a6c4",
                        "titulo": "High School Musical",
                        "url": "https://www.exemplo.com/highschoolmusical",
                        "dataPublicacao": "2024-01-23T12:34:56",
                        "nomesCategorias": [
                            "Filme",
                            "Musical",
                            "Teem"
                        ]
                    }
                }
            ],
            "pageable": {
                "pageNumber": 0,
                "pageSize": 10,
                "sort": {
                    "empty": false,
                    "sorted": true,
                    "unsorted": false
                },
                "offset": 0,
                "paged": true,
                "unpaged": false
            },
            "last": true,
            "totalPages": 1,
            "totalElements": 4,
            "size": 10,
            "number": 0,
            "sort": {
                "empty": false,
                "sorted": true,
                "unsorted": false
            },
            "numberOfElements": 4,
            "first": true,
            "empty": false
        },
        "statusCode": "OK",
        "statusCodeValue": 200
    }
]

```
BUSCAR POR CODIGO:

- GET /exibicao/{codio} 

```
GET http://localhost:8080/exibicao/65b051af2051060580aa4477

RESPONSE:
{
    "codigo": "65b051af2051060580aa4477",
    "dataVisualizacao": "2024-01-23T12:34:56",
    "pontuacao": 4.5,
    "visualizado": true,
    "recomenda": true,
    "usuario": {
        "codigo": "65afb552d39a0455941d790c",
        "nome": "Joao Rocha",
        "email": "joao.rocha@example.com",
        "cpf": "175.475.496-16",
        "telefone": "(35)98765-1778",
        "dataNascimento": "1971-06-15",
        "historicoExibicao": null
    },
    "video": {
        "codigo": "65aff31d12e1b2616604a6c4",
        "titulo": "High School Musical",
        "url": "https://www.exemplo.com/highschoolmusical",
        "dataPublicacao": "2024-01-23T12:34:56",
        "nomesCategorias": [
            "Filme",
            "Musical",
            "Teem"
        ]
    }
}
```
INSERIR EXIBIÇÃO:

- POST /exibicao
```
POST http://localhost:8080/exibicao

HTTP/1.1 201 CREATED
Content-Type: application/json

REQUEST BODY

{
  "dataVisualizacao": "2024-01-23T18:00:56",
  "pontuacao": 5,
  "visualizado": true,
  "recomenda": false,
  "usuario": {
    "codigo": "65afb1b8530647553bbbd0c7"
  },
  "video": {
    "codigo": "65b197624d9eda1d250e273c"
  }
}


RESPONSE

{
    "codigo": "65b1aea44d9eda1d250e2742",
    "dataVisualizacao": "2024-01-23T18:00:56",
    "pontuacao": 5.0,
    "visualizado": true,
    "recomenda": false,
    "usuario": {
        "codigo": "65afb1b8530647553bbbd0c7",
        "nome": "Herick Raposo",
        "email": "herickraposo@example.com",
        "cpf": "030.601.856-03",
        "telefone": "(35)98765-1778",
        "dataNascimento": "1997-10-21",
        "historicoExibicao": null
    },
    "video": {
        "codigo": "65b197624d9eda1d250e273c",
        "titulo": "Homem Aranha no Aranhaverso",
        "url": "https://www.exemplo.com/homem_aranha_aranhaverso",
        "dataPublicacao": "2024-01-22T20:34:56",
        "nomesCategorias": [
            "Filme",
            "Aventura",
            "Fantasia",
            "Teem"
        ]
    }
}


```
ATUALIZA EXIBIÇÃO:

- PUT /exibicao/{codigo}

```
PUT http://localhost:8080/exibicao/65b051af2051060580aa4477

REQUEST BODY
{
    "dataVisualizacao": "2024-01-23T12:34:56",
    "pontuacao": 4.5,
    "visualizado": true,
    "recomenda": true
}


RESPOSE

{
    "codigo": "65b051af2051060580aa4477",
    "dataVisualizacao": "2024-01-23T12:34:56",
    "pontuacao": 4.5,
    "visualizado": true,
    "recomenda": true,
    "usuario": {
        "codigo": "65afb552d39a0455941d790c",
        "nome": "Joao Rocha",
        "email": "joao.rocha@example.com",
        "cpf": "175.475.496-16",
        "telefone": "(35)98765-1778",
        "dataNascimento": "1971-06-15",
        "historicoExibicao": null
    },
    "video": {
        "codigo": "65aff31d12e1b2616604a6c4",
        "titulo": "High School Musical",
        "url": "https://www.exemplo.com/highschoolmusical",
        "dataPublicacao": "2024-01-23T12:34:56",
        "nomesCategorias": [
            "Filme",
            "Musical",
            "Teem"
        ]
    }
}

```
ATUALIZA EXIBIÇÃO:

- DELETE /exibicao/{codigo}
```

DELETE:http://localhost:8080/exibicao/65b051af2051060580aa4477

```