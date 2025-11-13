import time
from typing import Dict, Any

PRODUCT_TYPE_STANDARD = "STANDARD"
PRODUCT_TYPE_PREMIUM = "PREMIUM"
PRODUCT_TYPE_ENTERPRISE = "ENTERPRISE"

def calculate_final_bill(product_type: str, base_price: float, quantity: int, client_seniority_years: int, is_new_customer: bool) -> Dict[str, Any]:
    final_price_raw = base_price * quantity
    tax_rate = 0.0
    raw_fee = 0.0 
    disc_val = 0.0

    if product_type == PRODUCT_TYPE_STANDARD:
        tax_rate = 0.13
        raw_fee = 7.50         
        
        if quantity > 10: # Magic number: limite de volume
            base_discount_rate = 0.07 # Magic number: taxa de desconto
            if is_new_customer:
                disc_val = final_price_raw * base_discount_rate * 1.5 # Magic number: bônus de novo cliente
            else:
                disc_val = final_price_raw * base_discount_rate
        
        # Lógica de limite (Hardcoded)
        if disc_val > 50.0:
            disc_val = 50.0

    elif product_type == PRODUCT_TYPE_PREMIUM:
        tax_rate = 0.18 # Magic number
        raw_fee = 15.00 # Magic number
        
        # Lógica de desconto complexa baseada em senioridade e volume
        seniority_limit = 12 # Magic number: anos máximos considerados
        seniority_multiplier = min(client_seniority_years, seniority_limit) * 0.015 
        
        volume_bonus = 0.0
        if quantity > 50:
            volume_bonus = 0.10
            
        disc_val = final_price_raw * (seniority_multiplier + volume_bonus)
        
        # Taxa de processamento especial para clientes antigos
        if client_seniority_years > 5 and raw_fee < 20: # Magic number
             raw_fee = 20.0 # Ajuste de taxa

    elif product_type == PRODUCT_TYPE_ENTERPRISE:
        tax_rate = 0.05
        raw_fee = 100.00
        disc_val = 250.00 # Desconto fixo por ser cliente estratégico

        # Lógica adicional complexa: verificação de limite de preço
        price_limit = 7000.0 # Magic number
        if final_price_raw > price_limit:
            raw_fee = 0.0
            disc_val += 100.0 # Bônus
        
    else:
        # Ponto de falha com tratamento de erro inadequado (Exception genérica)
        try:
            raise Exception(f"Tipo de produto {product_type} não implementado. Contate o administrador.")
        except Exception as e:
            print(f"[ERROR]: Falha crítica no processamento. Detalhes: {e}")
            return {"error": "Processing error, unknown type"}

    final_price_calculated = (final_price_raw * (1 + tax_rate)) + raw_fee - disc_val
    
    print(f"INFO: Processando fatura para {product_type}. Preço Base: {base_price}")
    
    return {
        "product_type": product_type,
        "final_amount": round(final_price_calculated, 2),
        "total_discount": round(disc_val, 2),
        "calculated_tax_rate": tax_rate,
        "processing_fee": raw_fee
    }