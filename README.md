# 🚛 Sistema de Gerenciamento de Frotas

Sistema completo para gerenciamento de veículos, motoristas e rotas desenvolvido com **Spring Boot + Bootstrap 5 + Leaflet**.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura do Sistema](#arquitetura-do-sistema)
- [Padrões de Projeto GoF](#padrões-de-projeto-gof)
- [Funcionalidades](#funcionalidades)
- [Regras de Negócio](#regras-de-negócio)
- [Fluxo de Eventos](#fluxo-de-eventos)
- [Estrutura de Pacotes](#estrutura-de-pacotes)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Níveis de Acesso](#níveis-de-acesso)
- [Integrantes](#integrantes)

---

## 📖 Sobre o Projeto

Sistema web para controle de frotas que permite o gerenciamento completo de veículos (caminhões, furgões e reboques), motoristas, rotas e viagens. O sistema conta com integração GPS real via OpenRouteService e Nominatim, geração de relatórios em PDF e Excel, autenticação JWT com dois níveis de usuário e implementação de cinco padrões de projeto GoF.

---

## 🛠️ Tecnologias Utilizadas

### Backend
| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.2.5 | Framework principal |
| Spring Security | 6.2.4 | Autenticação e autorização |
| Spring Data JPA | 3.2.5 | Persistência de dados |
| Hibernate | 6.4.4 | ORM |
| PostgreSQL | 42.6.2 | Banco de dados |
| JWT (jjwt) | 0.11.5 | Tokens de autenticação |
| iText PDF | 5.5.13.3 | Geração de relatórios PDF |
| Apache POI | 5.2.5 | Geração de relatórios Excel |
| OpenRouteService API | — | Cálculo de rotas reais |
| Nominatim (OpenStreetMap) | — | Geocodificação de endereços |

### Frontend
| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Bootstrap | 5.3.0 | Framework CSS |
| Bootstrap Icons | 1.10.5 | Ícones |
| Leaflet.js | 1.9.4 | Mapas interativos |
| CARTO Basemaps | — | Tiles de mapa alternativos |
| JavaScript (ES6+) | — | Lógica do frontend |

---

## 🏗️ Arquitetura do Sistema

O sistema segue arquitetura em camadas com separação clara de responsabilidades:

```
┌─────────────────────────────────────────┐
│              FRONTEND                   │
│   Bootstrap 5 + Leaflet + JavaScript    │
└──────────────────┬──────────────────────┘
                   │ HTTP/REST + JWT
┌──────────────────▼──────────────────────┐
│            CONTROLLER LAYER             │
│   REST Controllers + GlobalExceptionHandler │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│             SERVICE LAYER               │
│   Business Logic + GoF Patterns         │
│   (Factory, Observer, Facade, Strategy) │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│           REPOSITORY LAYER              │
│        Spring Data JPA Repositories     │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│              DATABASE                   │
│           PostgreSQL                    │
└─────────────────────────────────────────┘
```

### Diagrama de Entidades

```
Veiculo (abstract)
├── Caminhao (tipoCaminhao, marca, numeroEixos)
├── Furgao (marca, quantidadeAssentos)
└── Reboque (tipoReboque, numeroEixos)

Motorista ──────────┐
                    ├──► Viagem ◄──── Rota
Veiculo ────────────┘
                         │
                         ▼
                    Notificacao ──► Usuario
```

### Herança JPA (JOINED)

```sql
veiculos          -- tabela base
├── caminhoes     -- JOIN por id
├── furgoes       -- JOIN por id
└── reboques      -- JOIN por id
```

---

## 🎨 Padrões de Projeto GoF

### 1. Factory Pattern — Criação de Veículos
**Localização:** `pattern/factory/VeiculoFactory.java`

**Justificativa:** Centraliza a lógica de criação das três subclasses de veículo (Caminhão, Furgão, Reboque). Sem o Factory, a lógica de instanciação ficaria espalhada nos controllers ou services, violando o princípio da responsabilidade única.

```java
// Uso no VeiculoService
Veiculo veiculo = veiculoFactory.criar(dto); // Factory decide qual subclasse criar
```

**Fluxo:**
```
VeiculoRequestDTO (tipo: "CAMINHAO")
        │
        ▼
VeiculoFactory.criar()
        │
        ├── "CAMINHAO" → new Caminhao()
        ├── "FURGAO"   → new Furgao()
        └── "REBOQUE"  → new Reboque()
```

---

### 2. Adapter Pattern — Integração GPS (Desafio Obrigatório)
**Localização:** `pattern/adapter/`

**Justificativa:** O desafio obrigatório do tema exige integração com múltiplos serviços de GPS. O Adapter permite trocar o provedor GPS sem alterar o código cliente, seguindo o princípio aberto/fechado.

**Interface comum:**
```java
public interface GpsService {
    double calcularDistancia(String origem, String destino);
    int calcularTempoEstimado(String origem, String destino);
    String obterRota(String origem, String destino);
}
```

**Implementações:**
- `LeafletGpsAdapter` — usa OpenRouteService API para distâncias reais
- `CartoGpsAdapter` — usa Nominatim + Haversine com tiles CARTO

**Troca de provedor sem alterar código:**
```java
// RotaService usa @Qualifier("leaflet") ou @Qualifier("carto")
@Qualifier("leaflet") GpsService gpsService
```

---

### 3. Observer Pattern — Notificações de Viagem
**Localização:** `pattern/observer/`

**Justificativa:** Quando uma viagem é criada ou finalizada, múltiplos subsistemas precisam ser notificados (notificações para usuários, logs, atualizações de status). O Observer desacopla o publicador dos assinantes.

**Fluxo:**
```
ViagemFacade.iniciarViagem()
        │
        ▼
ViagemEventPublisher.publicarViagemCriada()
        │
        └──► NotificacaoObserver.onViagemCriada()
                    │
                    ▼
             Salva notificação para todos os usuários
```

**Evento disparado automaticamente:**
- Viagem criada → notificação: "Nova viagem: Veículo X, Motorista Y, Rota Z"
- Viagem finalizada → notificação: "Veículo X e Motorista Y liberados"

---

### 4. Facade Pattern — Orquestração de Viagens
**Localização:** `pattern/facade/ViagemFacade.java`

**Justificativa:** O processo de iniciar uma viagem envolve múltiplos passos: validar disponibilidade, calcular custo, atualizar status de veículo e motorista, salvar viagem e disparar eventos. O Facade simplifica essa complexidade em uma única chamada.

**Sem Facade:** o controller precisaria conhecer e coordenar 5 repositories e 2 patterns.

**Com Facade:**
```java
// ViagemController chama apenas:
viagemFacade.iniciarViagem(veiculoId, motoristaId, rotaId, pesoCarga, valorCarga, estrategia);
```

**Internamente o Facade:**
1. Busca e valida veículo, motorista e rota
2. Aplica regras de negócio
3. Calcula custo via Strategy
4. Calcula previsão de chegada
5. Atualiza status do veículo e motorista
6. Salva a viagem
7. Dispara evento Observer

---

### 5. Strategy Pattern — Cálculo de Custo (Extra)
**Localização:** `pattern/strategy/`

**Justificativa:** Diferentes tipos de carga exigem diferentes formas de calcular o custo. O Strategy permite adicionar novos algoritmos de cálculo sem modificar o código existente.

| Estratégia | Fórmula | Uso |
|------------|---------|-----|
| `CustoPorDistancia` | distância × R$ 4,50/km | Cargas leves |
| `CustoPorPeso` | distância × peso × R$ 0,02/kg·km | Cargas pesadas |

**Seleção em tempo de execução:**
```
POST /api/viagens?estrategia=POR_DISTANCIA
POST /api/viagens?estrategia=POR_PESO
```

---

## ✅ Funcionalidades

### Veículos
- Cadastro de Caminhões, Furgões e Reboques
- Campos específicos por tipo (eixos, marca, capacidade)
- Controle de status: DISPONIVEL, EM_VIAGEM, MANUTENCAO, INATIVO
- Validação de placa e chassi únicos
- Paginação e ordenação

### Motoristas
- Cadastro completo com CPF, CNH e categoria
- Controle de status: DISPONIVEL, EM_VIAGEM, AFASTADO, INATIVO
- Validação de CPF e CNH únicos

### Rotas
- Cadastro manual com distância e tempo
- Criação automática via GPS (OpenRouteService)
- Consulta de rota com mapa interativo Leaflet/CARTO
- Distância real calculada pela malha rodoviária

### Viagens
- Criação com validação de disponibilidade
- Seleção de estratégia de cálculo de custo
- Finalização com liberação automática de veículo e motorista
- Filtros: todas, ativas, finalizadas
- Evento automático ao criar/finalizar

### Dashboard
- Cards com totais em tempo real
- Mapa interativo com marcadores de viagens ativas
- Troca de provedor de mapas (Leaflet ↔ CARTO)
- Lista de viagens ativas com botão de finalização

### Relatórios
- PDF: Viagens, Veículos, Motoristas (com totalizadores)
- Excel: Viagens (com aba de resumo), Veículos, Motoristas
- Formatação com cores e cabeçalhos destacados

---

## ⚠️ Regras de Negócio

### Regra 1 — Disponibilidade de Veículo
Um veículo só pode iniciar uma viagem se seu status for `DISPONIVEL`.
```
Status EM_VIAGEM, MANUTENCAO ou INATIVO → BusinessException
```

### Regra 2 — Disponibilidade de Motorista
Um motorista só pode iniciar uma viagem se seu status for `DISPONIVEL`.
```
Status EM_VIAGEM, AFASTADO ou INATIVO → BusinessException
```

### Regra 3 — Capacidade de Carga
O peso da carga não pode exceder a capacidade máxima do veículo.
```
pesoCarga > veiculo.capacidadeKg → BusinessException
Ex: "Peso da carga (15000 kg) excede a capacidade do veículo (10000 kg)"
```

### Regra 4 — Unicidade de Placa e Chassi
Não é possível cadastrar dois veículos com a mesma placa ou chassi.

### Regra 5 — Unicidade de CPF e CNH
Não é possível cadastrar dois motoristas com o mesmo CPF ou CNH.

---

## 🔔 Fluxo de Eventos

O sistema implementa arquitetura orientada a eventos internos:

```
┌─────────────┐     cria viagem      ┌─────────────────┐
│  Controller │ ──────────────────► │  ViagemFacade   │
└─────────────┘                      └────────┬────────┘
                                              │ publica evento
                                              ▼
                                    ┌─────────────────────┐
                                    │ ViagemEventPublisher │
                                    └──────────┬──────────┘
                                               │ notifica observers
                                               ▼
                                    ┌─────────────────────┐
                                    │  NotificacaoObserver │
                                    └──────────┬──────────┘
                                               │ salva no banco
                                               ▼
                                    ┌─────────────────────┐
                                    │  tabela notificacoes │
                                    │  (para cada usuário) │
                                    └─────────────────────┘
```

**Eventos implementados:**
- `onViagemCriada` — dispara ao iniciar viagem
- `onViagemFinalizada` — dispara ao finalizar viagem

**Ações automáticas:**
- Criação de notificação para todos os usuários cadastrados
- Atualização de status do veículo e motorista
- Cálculo e registro do custo da viagem

---

## 📁 Estrutura de Pacotes

```
gerenciamento.frotas.TrabFinal/
│
├── config/
│   ├── SecurityConfig.java       # Spring Security + CORS
│   ├── JwtUtil.java              # Geração e validação de tokens
│   └── JwtAuthFilter.java        # Filtro de autenticação
│
├── controller/
│   ├── AuthController.java       # POST /api/auth/login
│   ├── VeiculoController.java    # CRUD /api/veiculos
│   ├── MotoristaController.java  # CRUD /api/motoristas
│   ├── RotaController.java       # CRUD /api/rotas + GPS
│   ├── ViagemController.java     # /api/viagens
│   ├── NotificacaoController.java
│   ├── UsuarioController.java
│   └── RelatorioController.java  # PDF e Excel
│
├── dto/
│   ├── veiculo/                  # VeiculoRequestDTO, VeiculoResponseDTO
│   ├── motorista/                # MotoristaRequestDTO, MotoristaResponseDTO
│   ├── rota/                     # RotaRequestDTO, RotaResponseDTO
│   ├── viagem/                   # ViagemRequestDTO, ViagemResponseDTO
│   ├── notificacao/              # NotificacaoResponseDTO
│   └── usuario/                  # UsuarioRequestDTO, LoginRequestDTO...
│
├── exception/
│   ├── ResourceNotFoundException.java
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
│
├── model/
│   └── entity/
│       ├── veiculo/
│       │   ├── Veiculo.java      # Entidade abstrata base
│       │   ├── Caminhao.java
│       │   ├── Furgao.java
│       │   ├── Reboque.java
│       │   └── StatusVeiculo.java
│       ├── Motorista.java
│       ├── Rota.java
│       ├── Viagem.java
│       ├── Notificacao.java
│       ├── Usuario.java
│       └── StatusMotorista.java
│
├── pattern/
│   ├── factory/
│   │   └── VeiculoFactory.java   # GoF: Factory
│   ├── adapter/
│   │   ├── GpsService.java       # Interface Adapter
│   │   ├── LeafletGpsAdapter.java # Provedor Leaflet/ORS
│   │   └── CartoGpsAdapter.java  # Provedor CARTO
│   ├── observer/
│   │   ├── ViagemObserver.java   # Interface Observer
│   │   ├── NotificacaoObserver.java
│   │   └── ViagemEventPublisher.java
│   ├── facade/
│   │   └── ViagemFacade.java     # GoF: Facade
│   └── strategy/
│       ├── CustoStrategy.java    # Interface Strategy
│       ├── CustoPorDistancia.java
│       └── CustoPorPeso.java
│
├── repository/                   # Spring Data JPA Repositories
├── service/                      # Lógica de negócio
└── TrabFinalApplication.java
```

---

## 🚀 Como Executar

### Pré-requisitos
- Java 21 (Temurin recomendado)
- Maven 3.8+
- IntelliJ IDEA (recomendado)
- Acesso ao banco PostgreSQL

### Configuração

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/gerenciamento-de-frotas.git
cd gerenciamento-de-frotas
```

2. Configure o `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://SEU_HOST:5432/SEU_BANCO
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
ors.api.key=SUA_CHAVE_OPENROUTESERVICE
```

3. Execute no IntelliJ ou via Maven:
```bash
mvn spring-boot:run
```

4. Acesse: **http://localhost:8080**

### Primeiro Acesso

1. Acesse `http://localhost:8080/index.html`
2. Clique em **"Criar conta demo"**
3. Use as credenciais: `admin@frotas.com` / `123456`

---

## 🔌 Endpoints da API

### Autenticação
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/login` | Login e geração de token JWT | Público |
| POST | `/api/usuarios` | Criar novo usuário | Público |

### Veículos
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/veiculos` | Listar (paginado) | JWT |
| GET | `/api/veiculos/{id}` | Buscar por ID | JWT |
| POST | `/api/veiculos` | Criar veículo | JWT |
| PUT | `/api/veiculos/{id}` | Atualizar veículo | JWT |
| PATCH | `/api/veiculos/{id}/status` | Atualizar status | JWT |
| DELETE | `/api/veiculos/{id}` | Excluir | ADMIN |

### Motoristas
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/motoristas` | Listar (paginado) | JWT |
| GET | `/api/motoristas/{id}` | Buscar por ID | JWT |
| POST | `/api/motoristas` | Criar motorista | JWT |
| PUT | `/api/motoristas/{id}` | Atualizar | JWT |
| PATCH | `/api/motoristas/{id}/status` | Atualizar status | JWT |
| DELETE | `/api/motoristas/{id}` | Excluir | ADMIN |

### Rotas
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/rotas` | Listar (paginado) | JWT |
| POST | `/api/rotas` | Criar rota manual | JWT |
| POST | `/api/rotas/gps` | Criar via GPS | JWT |
| GET | `/api/rotas/gps/consultar` | Consultar GPS | JWT |
| PUT | `/api/rotas/{id}` | Atualizar | JWT |
| DELETE | `/api/rotas/{id}` | Excluir | ADMIN |

### Viagens
| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| GET | `/api/viagens` | Listar todas (paginado) | JWT |
| GET | `/api/viagens/ativas` | Listar em andamento | JWT |
| GET | `/api/viagens/finalizadas` | Listar finalizadas | JWT |
| POST | `/api/viagens?estrategia=X` | Iniciar viagem | JWT |
| PATCH | `/api/viagens/{id}/finalizar` | Finalizar viagem | JWT |

### Relatórios
| Método | Endpoint | Formato | Auth |
|--------|----------|---------|------|
| GET | `/api/relatorios/viagens/pdf` | PDF | JWT |
| GET | `/api/relatorios/viagens/excel` | XLSX | JWT |
| GET | `/api/relatorios/veiculos/pdf` | PDF | JWT |
| GET | `/api/relatorios/veiculos/excel` | XLSX | JWT |
| GET | `/api/relatorios/motoristas/pdf` | PDF | JWT |
| GET | `/api/relatorios/motoristas/excel` | XLSX | JWT |

---

## 🔐 Níveis de Acesso

| Funcionalidade | ROLE_USER | ROLE_ADMIN |
|----------------|-----------|------------|
| Visualizar veículos, motoristas, rotas | ✅ | ✅ |
| Criar e editar registros | ✅ | ✅ |
| Iniciar e finalizar viagens | ✅ | ✅ |
| Gerar relatórios | ✅ | ✅ |
| Ver notificações | ✅ | ✅ |
| Excluir qualquer registro | ❌ | ✅ |
| Listar todos os usuários | ❌ | ✅ |

**Autenticação:** Bearer Token JWT no header `Authorization`

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Token expira em: **24 horas**

---

## 👥 Integrantes

| Nome | Responsabilidade |
|------|-----------------|
| Integrante 1 | Backend — Entidades JPA, Repositories, Services |
| Integrante 2 | Padrões GoF — Factory, Adapter GPS, Observer |
| Integrante 3 | Padrões GoF — Facade, Strategy, Segurança JWT |
| Integrante 4 | Frontend — Dashboard, Veículos, Motoristas |
| Integrante 5 | Frontend — Rotas, Viagens, Relatórios PDF/Excel |

---

## 📊 Banco de Dados

**Tabelas geradas automaticamente pelo Hibernate:**

```
usuarios          -- autenticação e autorização
motoristas        -- cadastro de motoristas
veiculos          -- tabela base (herança JOINED)
caminhoes         -- subtipo de veículo
furgoes           -- subtipo de veículo
reboques          -- subtipo de veículo
rotas             -- origens e destinos
viagens           -- registro de viagens
notificacoes      -- sistema de notificações
```

---

## 📝 Licença

Projeto desenvolvido para fins acadêmicos — Trabalho Final de Frameworks para Desenvolvimento de Software.

---

*Sistema de Controle de Frotas — 2026*
