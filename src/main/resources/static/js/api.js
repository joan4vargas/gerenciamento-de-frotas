// ════════════════════════════════════════════════════════════════════
// API.JS - Funções auxiliares para requisições à API REST
// ════════════════════════════════════════════════════════════════════

const API_BASE_URL = 'http://localhost:8080';

// ════════════════════════════════════════════════════════════════════
// 1. FETCH GENÉRICO COM JWT
// ════════════════════════════════════════════════════════════════════

function apiFetch(path, options = {}) {
    const token = localStorage.getItem('token');

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    // Adicionar token JWT se existir
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    return fetch(`${API_BASE_URL}${path}`, {
        ...options,
        headers
    })
    .then(response => {
        // Se 401, token expirou
        if (response.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/index.html';
            throw new Error('Sessão expirada. Faça login novamente.');
        }

        // Se 403, sem permissão
        if (response.status === 403) {
            throw new Error('Acesso negado (403)');
        }

        // Se erro no servidor
        if (response.status >= 500) {
            throw new Error(`Erro no servidor (${response.status})`);
        }

        return response.json().catch(() => ({
            statusCode: response.status,
            message: 'Resposta inválida do servidor'
        }));
    })
    .catch(error => {
        console.error('Erro na requisição:', error);
        mostrarErros({ geral: error.message || 'Erro na requisição' });
        throw error;
    });
}

// ════════════════════════════════════════════════════════════════════
// 2. AUTENTICAÇÃO
// ════════════════════════════════════════════════════════════════════

function fazerLogin(email, senha) {
    return apiFetch('/api/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, senha })
    })
    .then(data => {
        if (data.token) {
            localStorage.setItem('token', data.token);
            window.location.href = '/dashboard.html';
        } else {
            throw new Error(data.mensagem || 'Erro ao fazer login');
        }
    });
}

function fazerLogout() {
    localStorage.removeItem('token');
    window.location.href = '/index.html';
}

function checkAuth() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

// ════════════════════════════════════════════════════════════════════
// 3. TRATAMENTO DE ERROS
// ════════════════════════════════════════════════════════════════════

function mostrarErros(erros) {
    const container = document.getElementById('erroModal');
    if (!container) return;

    if (typeof erros === 'string') {
        container.innerHTML = `<div class="alert alert-danger">${erros}</div>`;
    } else if (erros.message) {
        container.innerHTML = `<div class="alert alert-danger">${erros.message}</div>`;
    } else if (erros.geral) {
        container.innerHTML = `<div class="alert alert-danger">${erros.geral}</div>`;
    } else {
        let html = '<div class="alert alert-danger"><ul>';
        for (const [campo, msg] of Object.entries(erros)) {
            html += `<li><strong>${campo}:</strong> ${msg}</li>`;
        }
        html += '</ul></div>';
        container.innerHTML = html;
    }

    container.style.display = 'block';
}

function limparErros() {
    const container = document.getElementById('erroModal');
    if (container) {
        container.innerHTML = '';
        container.style.display = 'none';
    }
}

// ════════════════════════════════════════════════════════════════════
// 4. VEÍCULOS
// ════════════════════════════════════════════════════════════════════

function listarVeiculos(page = 0, size = 10) {
    return apiFetch(`/api/veiculos?page=${page}&size=${size}`);
}

function criarVeiculo(dados) {
    return apiFetch('/api/veiculos', {
        method: 'POST',
        body: JSON.stringify(dados)
    });
}

function editarVeiculo(id, dados) {
    return apiFetch(`/api/veiculos/${id}`, {
        method: 'PUT',
        body: JSON.stringify(dados)
    });
}

function deletarVeiculo(id) {
    return apiFetch(`/api/veiculos/${id}`, {
        method: 'DELETE'
    });
}

// ════════════════════════════════════════════════════════════════════
// 5. MOTORISTAS
// ════════════════════════════════════════════════════════════════════

function listarMotoristas(page = 0, size = 10) {
    return apiFetch(`/api/motoristas?page=${page}&size=${size}`);
}

function criarMotorista(dados) {
    return apiFetch('/api/motoristas', {
        method: 'POST',
        body: JSON.stringify(dados)
    });
}

function editarMotorista(id, dados) {
    return apiFetch(`/api/motoristas/${id}`, {
        method: 'PUT',
        body: JSON.stringify(dados)
    });
}

function deletarMotorista(id) {
    return apiFetch(`/api/motoristas/${id}`, {
        method: 'DELETE'
    });
}

// ════════════════════════════════════════════════════════════════════
// 6. ROTAS
// ════════════════════════════════════════════════════════════════════

function listarRotas(page = 0, size = 10) {
    return apiFetch(`/api/rotas?page=${page}&size=${size}`);
}

function criarRota(dados) {
    return apiFetch('/api/rotas', {
        method: 'POST',
        body: JSON.stringify(dados)
    });
}

function criarRotaGPS(origem, destino) {
    return apiFetch(`/api/rotas/gps?origem=${encodeURIComponent(origem)}&destino=${encodeURIComponent(destino)}`, {
        method: 'POST'
    });
}

function obterGeometriaRota(origem, destino) {
    return apiFetch(`/api/rotas/gps/geometria?origem=${encodeURIComponent(origem)}&destino=${encodeURIComponent(destino)}`);
}

function editarRota(id, dados) {
    return apiFetch(`/api/rotas/${id}`, {
        method: 'PUT',
        body: JSON.stringify(dados)
    });
}

function deletarRota(id) {
    return apiFetch(`/api/rotas/${id}`, {
        method: 'DELETE'
    });
}

// ════════════════════════════════════════════════════════════════════
// 7. VIAGENS
// ════════════════════════════════════════════════════════════════════

function listarViagens(page = 0, size = 10) {
    return apiFetch(`/api/viagens?page=${page}&size=${size}`);
}

function listarViagensAtivas(page = 0, size = 10) {
    return apiFetch(`/api/viagens/ativas?page=${page}&size=${size}`);
}

function criarViagem(dados, estrategia = 'POR_DISTANCIA') {
    return apiFetch(`/api/viagens?estrategia=${estrategia}`, {
        method: 'POST',
        body: JSON.stringify(dados)
    });
}

function finalizarViagem(id) {
    return apiFetch(`/api/viagens/${id}/finalizar`, {
        method: 'PATCH'
    });
}

function deletarViagem(id) {
    return apiFetch(`/api/viagens/${id}`, {
        method: 'DELETE'
    });
}

// ════════════════════════════════════════════════════════════════════
// 8. NOTIFICAÇÕES
// ════════════════════════════════════════════════════════════════════

function listarNotificacoes(page = 0, size = 10) {
    return apiFetch(`/api/notificacoes?page=${page}&size=${size}`);
}

function marcarNotificacaoLida(id) {
    return apiFetch(`/api/notificacoes/${id}/lida`, {
        method: 'PATCH'
    });
}

// ════════════════════════════════════════════════════════════════════
// 9. RELATÓRIOS
// ════════════════════════════════════════════════════════════════════

function baixarRelatorio(tipo, formato) {
    const token = localStorage.getItem('token');
    const url = `${API_BASE_URL}/api/relatorios/${tipo}/${formato}`;

    fetch(url, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (response.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/index.html';
            return;
        }
        return response.blob();
    })
    .then(blob => {
        const link = document.createElement('a');
        const nomeArquivo = `relatorio-${tipo}-${new Date().getTime()}.${formato === 'pdf' ? 'pdf' : 'xlsx'}`;
        link.href = window.URL.createObjectURL(blob);
        link.download = nomeArquivo;
        link.click();
        window.URL.revokeObjectURL(link.href);
    })
    .catch(error => {
        console.error('Erro ao baixar relatório:', error);
        mostrarErros('Erro ao baixar relatório');
    });
}

// ════════════════════════════════════════════════════════════════════
// 10. UTILITÁRIOS
// ════════════════════════════════════════════════════════════════════

function formatarData(data) {
    if (!data) return '-';
    return new Date(data).toLocaleDateString('pt-BR');
}

function formatarHora(data) {
    if (!data) return '-';
    return new Date(data).toLocaleTimeString('pt-BR');
}

function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
}

function formatarCPF(cpf) {
    if (!cpf) return '';
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

function formatarPlaca(placa) {
    if (!placa) return '';
    return placa.toUpperCase();
}