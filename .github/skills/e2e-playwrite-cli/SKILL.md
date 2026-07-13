---
name: 'e2e-playwrite-cli'
description: 'Manually loaded agentic skill to plan, explore, implement, and validate Java Playwright E2E tests using the isolated playwright-cli Docker container and Node scripts instead of host dependencies.'
trigger: 'manual'
model: 'Gemini 3.5 Flash'
inputs:
  - route: 'The frontend URL path to inspect and test (e.g., "/")'
---

# Skill: e2e-playwrite-cli

Você é um agente especialista em Engenharia de Software e QA encarregado de planejar, explorar, implementar e validar testes E2E (End-to-End) usando **Playwright Java** para o módulo `@observabily/frontend`. 

Este skill deve ser executado **manualmente** mediante demanda do usuário e recebe como entrada uma **rota/feature** específica para testar. Ao contrário da versão `e2e-playwrite-mcp`, esta skill utiliza diretamente a **Playwright CLI / Node running inside an isolated playwright-cli container** to explore the pages.

---

## Fluxo de Execução da Skill

### Passo 1: Verificação de Dependências (Playwright CLI Container)
Antes de começar, verifique se o container isolado do Playwright está ativo e pronto:
1. Certifique-se de que a stack de observabilidade e o container `playwright-cli` estão rodando:
   ```bash
   cd observabily
   docker compose up -d playwright-cli
   ```
2. Verifique se o pacote `playwright` está instalado globalmente no container:
   ```bash
   docker compose exec playwright-cli npm install -g playwright@1.44.0
   ```
3. Valide se a CLI responde corretamente de dentro do container:
   ```bash
   docker compose exec playwright-cli npx --yes playwright@1.44.0 --version
   ```

---

### Passo 2: Inicialização do Ambiente & Health Check
Garanta que a aplicação web-api e frontend estejam respondendo:
1. Verifique o status dos containers:
   ```bash
   docker compose ps
   ```
2. Valide se o frontend está ativo localmente no host:
   - Frontend: `http://localhost:8081`

---

### Passo 3: Exploração Interativa via Container Playwright
Em vez de utilizar as ferramentas do servidor MCP, você deve explorar a página alvo (`http://frontend:8081{{route}}`) usando um script Node.js temporário rodando dentro do container `playwright-cli`:
1. Crie um script temporário na pasta scratch do workspace (ex: `scratch/explore.js`):
   ```javascript
   const { chromium } = require('playwright');
   
   (async () => {
     // Lançar browser headless
     const browser = await chromium.launch({ headless: true });
     const page = await browser.newPage();
     
     // ATENÇÃO: Como o script roda na rede do Docker, usamos o hostname interno 'frontend'
     console.log('Navigating to http://frontend:8081{{route}}...');
     await page.goto('http://frontend:8081{{route}}');
     
     // Tirar um print da tela para inspeção visual
     await page.screenshot({ path: 'scratch/explore_screenshot.png' });
     console.log('Screenshot saved to scratch/explore_screenshot.png');
     
     // Extrair e exibir o conteúdo DOM relevante
     const bodyHTML = await page.evaluate(() => document.body.innerHTML);
     console.log('--- DOM BODY CONTENT ---');
     console.log(bodyHTML);
     console.log('------------------------');
     
     await browser.close();
   })();
   ```
2. Execute o script dentro do container usando `NODE_PATH` para carregar a biblioteca global:
   ```bash
   docker compose exec playwright-cli sh -c "NODE_PATH=\$(npm root -g) node scratch/explore.js"
   ```
3. Inspecione o HTML retornado no terminal e os arquivos de screenshot na pasta `scratch` do host para mapear os seletores CSS (`data-testid`, IDs, classes) dos formulários e botões da página.

---

### Passo 4: Planejamento de Casos de Teste
Com base na estrutura de elementos obtida pela exploração do container:
1. Identifique o **Happy Path** (fluxo principal).
2. Mapeie **Edge Cases** (casos de erro, validações).
3. Liste os seletores exatos (`data-testid='...'`) a serem usados nas asserções.

---

### Passo 5: Implementação em Java (JUnit 5 + Playwright)
Implemente ou atualize a classe de teste correspondente no diretório `observabily/frontend/src/test/java/com/example/demo/`:
- Utilize JUnit 5 (`org.junit.jupiter.api.*`).
- Instancie a biblioteca Playwright Java (`com.microsoft.playwright.*`).
- **URL no código Java:** Utilize o endereço local no host: `http://localhost:8081/`

---

### Passo 6: Execução & Validação da Suíte
Valide os testes usando o container oficial do Playwright Java para rodar a suíte via Gradle wrapper:
1. Execute a partir do diretório `observabily/frontend`:
   ```bash
   docker run --rm --network="host" -v $(pwd):/app -w /app mcr.microsoft.com/playwright/java:v1.44.0-jammy ./gradlew test --tests "com.example.demo.<TestClassName>"
   ```
2. Se houver falhas, depure re-executando o script Node.js de exploração no container `playwright-cli` para validar alterações no DOM até obter sucesso.
