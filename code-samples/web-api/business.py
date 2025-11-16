from typing import Union

Number = Union[int, float]

def calculate_discounted_price(price: Number, discount_percent: Number) -> float:
    """Calcula o preço após aplicar um percentual de desconto.

    Regra de negócio:
    - Desconto não pode ser negativo.
    - Desconto máximo de 90% para evitar preço quase zero.
    - Retorno com duas casas decimais.
    """
    if not isinstance(price, (int, float)):
        raise TypeError("price deve ser numérico")
    if not isinstance(discount_percent, (int, float)):
        raise TypeError("discount_percent deve ser numérico")
    if discount_percent < 0:
        raise ValueError("Desconto não pode ser negativo")
    if discount_percent > 90:
        raise ValueError("Desconto acima do limite permitido (90%)")
    return round(float(price) * (1 - float(discount_percent) / 100), 2)


def _clean_number(value: str) -> float:
    """Tenta normalizar strings numéricas recebidas de fontes externas.

    Exemplos que devem ser tratados:
    - '150,50' -> 150.50
    - '$80.00' -> 80.00
    - '10' -> 10.0

    Se a string contiver caracteres inválidos, a função lança ValueError.
    """
    if isinstance(value, (int, float)):
        return float(value)
    if not isinstance(value, str):
        raise TypeError("Valor deve ser string ou número")

    # Remover espaços e símbolos comuns
    v = value.strip()
    v = v.replace('$', '')
    # substituir vírgula por ponto para decimais europeus
    v = v.replace(',', '.')

    # permitir apenas dígitos, ponto e sinais
    allowed = set('0123456789.-')
    if not all(ch in allowed for ch in v):
        raise ValueError(f"Formato numérico inválido: {value}")

    try:
        return float(v)
    except Exception as e:
        raise ValueError(f"Falha ao converter '{value}' para número: {e}")


def calculate_unit_price(cost: str, quantity) -> float:
    """Calcula o preço unitário a partir do custo total e da quantidade.

    Possíveis erros simulados:
    - cost: string com formato inválido -> ValueError
    - quantity: zero ou negativo -> ZeroDivisionError ou ValueError
    """
    c = _clean_number(cost)

    # permitir que quantity venha como string ou número
    if isinstance(quantity, str):
        try:
            q = int(quantity)
        except Exception:
            # simular erro de conversão mais sutil (string com caracteres inválidos)
            raise ValueError(f"Quantidade inválida: {quantity}")
    elif isinstance(quantity, (int, float)):
        q = int(quantity)
    else:
        raise TypeError("quantity deve ser int/float/str")

    if q <= 0:
        # divisão por zero ou valores negativos devem ser capturados na lógica de negócio
        raise ZeroDivisionError("Quantidade deve ser maior que zero")

    return round(c / q, 2)
