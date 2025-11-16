# Web API de Exemplo (Flask)

Esta API REST em Flask demonstra operações simples de criação, listagem e recuperação de produtos em um "banco" em memória, além de uma operação de negócio para aplicar desconto.

## Pré-requisitos

- Python 3.10+
- Ambiente virtual recomendado

## Instalação

```bash
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

## Executando a aplicação

```bash
flask --app app run --debug
```

A aplicação iniciará (por padrão) em: `http://127.0.0.1:5000`.

## Endpoints

### Health Check
`GET /health`

Resposta:
```json
{"status": "ok"}
```

### Criar Produto
`POST /products`

Body JSON exemplo:
```json
{
  "id": "3",
  "name": "Headset USB",
  "price": 250.0,
  "tags": ["audio"]
}
```

Possíveis respostas:
- `201 Created` com o produto criado
- `400 Bad Request` campos inválidos
- `409 Conflict` se o ID já existir

### Listar Produtos
`GET /products`

Retorna lista de produtos:
```json
[
  {"id": "1", "name": "Teclado Mecânico", "price": 350.0, "tags": ["hardware", "periféricos"]},
  {"id": "2", "name": "Mouse Gamer", "price": 199.9, "tags": ["hardware", "periféricos"]}
]
```

### Recuperar Produto por ID
`GET /products/<id>`

Retorna o produto ou `404` se não existir.

### Aplicar Desconto em Produto
`POST /products/<id>/apply-discount`

Body JSON exemplo:
```json
{
  "discount_percent": 15
}
```

Respostas esperadas:
- `200 OK` produto atualizado (se sucesso)
- `404 Not Found` se o produto não existir
- `500 Internal Server Error` se a lógica de desconto falhar

## Lógica de Negócio
A lógica de cálculo de desconto (arquivo `business.py`) valida limites (0% a 90%) e retorna o novo preço com arredondamento.

## Observações
- Os dados são mantidos em memória (arquivo `db.py`). Ao reiniciar o servidor, qualquer novo produto criado se perde.
- Para testes rápidos você pode usar `curl` ou ferramentas como Postman / Insomnia.

## Exemplos usando curl

Criar produto:
```bash
# Health check
curl -sS http://127.0.0.1:5000/health

# Criar produto (POST /products)
curl -sS -X POST http://127.0.0.1:5000/products \
  -H "Content-Type: application/json" \
  -d '{"id":"3","name":"Headset USB","price":250.0,"tags":["audio"]}'

# Listar produtos (GET /products)
curl -sS http://127.0.0.1:5000/products

# Recuperar produto por ID (GET /products/<id>)
curl -sS http://127.0.0.1:5000/products/1

# Aplicar desconto (POST /products/<id>/apply-discount)
curl -sS -X POST http://127.0.0.1:5000/products/1/apply-discount \
  -H "Content-Type: application/json" \
  -d '{"discount_percent": 10}'

# Chamada para produto inexistente (exemplo de erro)
curl -sS -X POST http://127.0.0.1:5000/products/999/apply-discount \
  -H "Content-Type: application/json" \
  -d '{"discount_percent": 10}'
```

Listar produtos:
```bash
curl http://127.0.0.1:5000/products
```

Aplicar desconto:
```bash
curl -X POST http://127.0.0.1:5000/products/1/apply-discount \
  -H "Content-Type: application/json" \
  -d '{"discount_percent": 10}'
```

Recuperar produto:
```bash
curl http://127.0.0.1:5000/products/1
```