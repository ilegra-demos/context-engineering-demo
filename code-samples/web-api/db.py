from typing import Dict, Any

# Banco de dados em memória simulando persistência.
# Chave = id do produto, Valor = dados do produto.
products_db: Dict[str, Dict[str, Any]] = {}

# Dados de exemplo para facilitar testes manuais.
products_db['1'] = {
    'id': '1',
    'name': 'Teclado Mecânico',
    # price armazenado como número
    'price': 350.00,
    # quantity e cost simulam dados vindos de fontes externas (strings com formatos variados)
    # Exemplos de valores que podem causar erros sutis:
    # - quantidade em string
    # - cost com vírgula ou símbolo
    'quantity': '10',
    'cost': '150,50',
    'tags': ['hardware', 'periféricos']
}
products_db['2'] = {
    'id': '2',
    'name': 'Mouse Gamer',
    'price': 199.90,
    'quantity': 5,
    # cost com símbolo de moeda (a conversão deve lidar com isso ou falhar)
    'cost': '$80.00',
    'tags': ['hardware', 'periféricos']
}
