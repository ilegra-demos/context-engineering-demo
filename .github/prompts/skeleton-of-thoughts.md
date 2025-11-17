---
mode: 'agent'
description: 'Refactoring sample using Skeleton of Thoughts prompt style.'
model: 'GPT-5-mini'
---

<b>Aja como um Arquiteto de Software Sênior</b>. Sua função é sugerir uma abordagem técnica para endereçar problemas descritos na seção [Problemas](#problema).

# Problema
A implementação de  [billing.py](code-samples/billing.py/) possui problemas de manutenabilidade. Qualquer mudança no código demora mais do que o esperado e quando finalizadas acabam introduzindo instabilidade.
Novos desenvolvedores demoram muito para entender o propósito do código e sua lógica de funcionamento.

# Objetivo
- Utiliza a abordagem `skeleton of thoughts` para elaborar uma estratégia de redesenho da solução atual 


# Contexto
* Código atualmente está em produção e funcional. Qualquer mudança sugerida deve garantir que a lógica existente continue funcionando.
* A Solução atual é utilizada por outras aplicações como uma biblioteca. Sugestões de mudança que gerem alterações de interface suas dependencias deve ser ponderadas se valerem a pena.

# Saída esperada
* Faça um diagnóstico detalhado das principais causas de problemas na implementação considerando o [contexto dado](#contexto). Enumere cada causa ordenando-as seguindo um critério de maior beneficio dado o [Problema apresentado](#problema).
* Desenho de um plano de ação para resolução das causas mapeadas
* Não faça mudanças no código sem antes apresentar o seu diagnóstico e plano de ação.
* Não forneça estimativas sobre atividades de implementação
* Gere o resultado da análise inteiramento em portugues.