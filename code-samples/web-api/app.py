from flask import Flask, request, jsonify
from typing import Any, Dict

from db import products_db
from business import calculate_discounted_price
from business import calculate_unit_price

app = Flask(__name__)

# -------------------------
# Helpers
# -------------------------

def _validate_new_product(data: Dict[str, Any]) -> Dict[str, Any]:
    if 'id' not in data or 'name' not in data or 'price' not in data:
        raise ValueError("Campos obrigatórios: id, name, price")
    if not isinstance(data['price'], (int, float)) or data['price'] < 0:
        raise ValueError("Preço inválido")
    return {
        'id': str(data['id']),
        'name': data['name'],
        'price': float(data['price']),
        'tags': data.get('tags', [])
    }

# -------------------------
# Endpoints
# -------------------------

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'ok'}), 200

@app.route('/products', methods=['POST'])
def create_product():
    """Cria um novo produto (WRITE)."""
    data = request.get_json() or {}
    try:
        product = _validate_new_product(data)
    except ValueError as e:
        return jsonify({'error': str(e)}), 400

    product_id = product['id']
    if product_id in products_db:
        return jsonify({'error': 'Produto já existe'}), 409

    products_db[product_id] = product
    return jsonify(product), 201

@app.route('/products', methods=['GET'])
def list_products():
    """Lista todos os produtos (READ)."""
    return jsonify(list(products_db.values())), 200

@app.route('/products/<product_id>', methods=['GET'])
def get_product(product_id: str):
    """Recupera um único produto (READ)."""
    product = products_db.get(product_id)
    if not product:
        return jsonify({'error': 'Produto não encontrado'}), 404
    return jsonify(product), 200

@app.route('/products/<product_id>/apply-discount', methods=['POST'])
def apply_discount(product_id: str):
    """Aplica um desconto ao produto (BUSINESS LOGIC + BUG proposital)."""
    data = request.get_json() or {}
    discount_percent = data.get('discount_percent', 0)

    product = products_db.get(product_id)
    if not product:
        return jsonify({'error': 'Produto não encontrado'}), 404

    # BUG de tipagem: tratamos 'product' (dict) como objeto com atributo 'price'.
    # O correto seria product['price'].
    try:
        # Linha problemática proposital original (para demonstração):
        # new_price = calculate_discounted_price(product.price, discount_percent)

        # Correção mínima usada em runtime normal: acessar como dicionário
        if not isinstance(discount_percent, (int, float)):
            # tentar converter strings que venham do JSON
            try:
                discount_percent = float(discount_percent)
            except Exception:
                return jsonify({'error': 'discount_percent inválido'}), 400

        new_price = calculate_discounted_price(product['price'], discount_percent)
    except Exception as e:
        return jsonify({'error': 'Falha ao aplicar desconto', 'details': str(e)}), 500

    product['price'] = new_price
    return jsonify(product), 200


@app.route('/products/<product_id>/unit-price', methods=['GET'])
def unit_price(product_id: str):
    """Calcula o custo unitário usando campos 'cost' e 'quantity' (simula erros sutis).

    Exemplo de falhas:
    - cost com vírgula ou símbolo -> precisa ser limpo
    - quantity em string contendo caracteres inválidos -> ValueError
    - quantity <= 0 -> ZeroDivisionError
    """
    product = products_db.get(product_id)
    if not product:
        return jsonify({'error': 'Produto não encontrado'}), 404

    try:
        unit = calculate_unit_price(product.get('cost', ''), product.get('quantity', 0))
    except ZeroDivisionError as zde:
        return jsonify({'error': 'Quantidade inválida para cálculo unitário', 'details': str(zde)}), 400
    except ValueError as ve:
        return jsonify({'error': 'Formato de número inválido', 'details': str(ve)}), 400
    except Exception as e:
        return jsonify({'error': 'Falha ao calcular preço unitário', 'details': str(e)}), 500

    return jsonify({'product_id': product_id, 'unit_price': unit}), 200

if __name__ == '__main__':
    # Execução direta para testes locais
    app.run(debug=True)
