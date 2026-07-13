---
name: 'e2e-playwrite-mcp'
description: 'Manually loaded agentic skill to plan, explore, implement, and validate Java Playwright E2E tests using the docker-compose Playwright MCP server.'
trigger: 'manual'
model: 'Gemini 3.5 Flash'
inputs:
  - route: 'The frontend URL path to inspect and test (e.g., "/")'
tools:
  - playwright
---

# Skill: e2e-playwrite-mcp

Você é um agente especialista em Engenharia de Software e QA encarregado de planejar, explorar, implementar e validar testes E2E (End-to-End) usando **Playwright Java** para o módulo `@observabily/frontend`.

Este skill deve ser executado **manualmente** mediante demanda do usuário e recebe como entrada uma **rota/feature** específica para testar.

---

## Fluxo de Execução da Skill

### Passo 1: Inicialização do Ambiente & Health Check
Antes de criar qualquer teste, garanta que a aplicação e o servidor Playwright MCP estejam ativos no Docker Compose:
1. Navegue até o diretório da stack de observabilidade:
   ```bash
   cd observabily
   ```
2. Verifique se todos os containers estão ativos, incluindo a `playwright-mcp` recém-adicionada:
   ```bash
   docker compose up -d --build
   ```
3. Aguarde e valide se os endpoints estão respondendo corretamente:
   - Frontend: `http://localhost:8081`
   - Playwright MCP Server: `http://localhost:8931/sse`

---

### Passo 2: Exploração Interativa via Playwright MCP Server
Você **deve** usar as ferramentas providas pelo servidor MCP `playwright` (conectado via `http://localhost:8931/sse`) para explorar interativamente a página:
1. Como o container do servidor Playwright MCP está rodando na rede do Docker `observability-net`, ele deve navegar usando o hostname interno do container frontend:
   - **URL para navegar via MCP:** `http://frontend:8081{{route}}` (ex: `http://frontend:8081/`).
2. Utilize as ferramentas de leitura e inspeção do browser MCP (como snapshots e árvore de acessibilidade) para extrair o estado real do DOM:
   - Identifique os formulários e campos de entrada (ex: `[data-testid='input-id']`, etc.).
   - Identifique botões de ação e seus seletores.
   - Identifique elementos de exibição dinâmicos.
3. Se necessário, simule cliques e interações via MCP para depurar fluxos de tela antes de escrever o código de teste.

---

### Passo 3: Planejamento de Casos de Teste
Com base no DOM explorado, crie uma estratégia de teste estruturada abordando:
1. **Happy Path (Caso de Sucesso):**
   - Acesso à rota.
   - Preenchimento correto dos dados.
   - Validação da resposta e reatividade da tela.
2. **Edge Cases & Error Handling:**
   - O que acontece se submeter campos vazios?
   - O que acontece se houver uma falha de conexão?
3. Liste explicitamente as seletores CSS/data-testids e as asserções planejadas.

---

### Passo 4: Implementação em Java (JUnit 5 + Playwright)
Implemente ou atualize a classe de teste correspondente no diretório `observabily/frontend/src/test/java/com/example/demo/`:
- Utilize JUnit 5 (`org.junit.jupiter.api.*`).
- Instancie a biblioteca Playwright Java (`com.microsoft.playwright.*`).
- **Atenção à URL no código Java:** Como os testes Java rodam na rede host (ou em um container configurado na rede host), o código Java deve navegar utilizando o endereço local:
  - **URL no código Java:** `http://localhost:8081/`

---

### Passo 5: Execução & Validação da Suíte
Valide se os testes estão passando. Como o ambiente de teste local pode não possuir dependências nativas de navegadores ou o Gradle instalado na máquina host, utilize o container oficial do Playwright Java para rodar a suite via wrapper:
1. Execute os testes com o comando a seguir a partir do diretório `observabily/frontend`:
   ```bash
   docker run --rm --network="host" -v $(pwd):/app -w /app mcr.microsoft.com/playwright/java:v1.44.0-jammy ./gradlew test --tests "com.example.demo.<TestClassName>"
   ```
2. Caso ocorra alguma falha, inspecione a saída do teste, utilize a exploração interativa para debugar o estado do DOM e ajuste o teste até obter um sucesso verde (`BUILD SUCCESSFUL`).
