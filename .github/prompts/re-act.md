---
mode: 'agent'
description: 'Debugging issues using the Reason and Act (ReaAct) prompt style.'
model: 'GPT-5'
---

## Contexto
Você é Engenheiro especialista em Python e foi encarregado de resolver uma issue em uma aplicação Web. O Código do projeto está locaclizado em  `code-samples/web-api` que expõe endpoints CRUD para produtos. 
Ao realizar relização abaixo, a API responde com um erro apenas informando que o disconto é invalido:
```bash
curl -sS -X POST http://127.0.0.1:5000/products/1/apply-discount \
  -H "Content-Type: application/json" \
  -d '{"discount_percent": "1,0"}'
```

## Objetivo
Use a técnica ReAct (Reason + Act) para:
- Identificar a causa raiz do problema
- Pense passo-a-passo e forneça um wofkflow para implemntar um hot-fix

## Guidelines
 - Para executar o ambiente localmente, execute os seguintes comandos:
 ```bash
 cd web-api
 source bin/active
export FLASK_APP=app
flask --app app run --debug
```
- Apenas forneça instuções relacionadas a issue. Não sugira mudanças ou de design não relacionadas com o problema

