// API.JS - Funcoes compartilhadas por todas as paginas


const API_BASE_URL = 'http://localhost:8080';


// AUTENTICACAO


function getToken() {
    return localStorage.getItem('token') || '';
}

function getUsuario() {
    const userData = localStorage.getItem('userData');
    return userData ? JSON.parse(userData) : { nome: 'Usuario' };
}

function checkAuth() {
    const token = getToken();
    if (!token) {
        window.location.href = '/index.html';
        return false;
    }
    return true;
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userData');
    window.location.href = '/index.html';
}


// FETCH GENERICO COM JWT
// Adiciona /api automaticamente se o path nao comecar com /api


function apiFetch(path, options = {}) {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    // Normalizar path: garantir que comece com /api
    let finalPath = path;
    if (!finalPath.startsWith('/api')) {
        if (!finalPath.startsWith('/')) finalPath = '/' + finalPath;
        finalPath = '/api' + finalPath;
    }

    return fetch(`${API_BASE_URL}${finalPath}`, {
        ...options,
        headers
    })
    .then(async response => {
        if (response.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/index.html';
            throw new Error('Sessao expirada');
        }

        if (response.status === 204) return {};

        const data = await response.json().catch(() => ({}));

        // Se erro (400, 403, 409, 500), lancar com mensagem
        if (!response.ok) {
            const erro = new Error(data.mensagem || data.erro || data.message || `Erro ${response.status}`);
            erro.campos = data.campos || data.errors || null;
            erro.status = response.status;
            throw erro;
        }

        return data;
    });
}


// TRATAMENTO DE ERROS


function mostrarErros(erros) {
    const container = document.getElementById('erroModal') || document.getElementById('erroGeral');
    if (!container) {
        alert(typeof erros === 'string' ? erros : (erros.message || erros.geral || 'Erro'));
        return;
    }

    let msg;
    if (typeof erros === 'string') msg = erros;
    else if (erros.message) msg = erros.message;
    else if (erros.geral) msg = erros.geral;
    else if (erros.mensagem) msg = erros.mensagem;
    else {
        msg = Object.entries(erros).map(([k, v]) => `${k}: ${v}`).join('<br>');
    }

    container.innerHTML = `<div class="alert alert-danger">${msg}</div>`;
    container.style.display = 'block';
}

function mostrarAlertaErro(msg) {
    const container = document.getElementById('erroModal') || document.getElementById('erroGeral');
    if (container) {
        container.innerHTML = `<div class="alert alert-danger">${msg}</div>`;
        container.style.display = 'block';
    } else {
        alert(msg);
    }
}

function limparErros() {
    const container = document.getElementById('erroModal') || document.getElementById('erroGeral');
    if (container) {
        container.innerHTML = '';
        container.style.display = 'none';
    }
    // Remover marcacoes de campos invalidos
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    document.querySelectorAll('.invalid-feedback').forEach(el => el.remove());
}


// RELATORIOS


function baixarRelatorio(tipo, formato) {
    const token = getToken();
    const url = `${API_BASE_URL}/api/relatorios/${tipo}/${formato}`;

    fetch(url, { headers: { 'Authorization': `Bearer ${token}` } })
    .then(response => {
        if (!response.ok) throw new Error(`Erro ${response.status}`);
        return response.blob();
    })
    .then(blob => {
        const link = document.createElement('a');
        const ext = formato === 'pdf' ? 'pdf' : 'xlsx';
        link.href = window.URL.createObjectURL(blob);
        link.download = `relatorio-${tipo}-${Date.now()}.${ext}`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(link.href);
    })
    .catch(error => alert('Erro ao baixar: ' + error.message));
}


// UTILITARIOS


function formatarData(data) {
    if (!data) return '-';
    return new Date(data).toLocaleDateString('pt-BR');
}

function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valor || 0);
}

function formatarCPF(cpf) {
    if (!cpf) return '';
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}