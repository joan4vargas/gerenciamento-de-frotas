const API = 'http://localhost:8080/api';

function getToken() { return localStorage.getItem('token'); }
function getUsuario() { return JSON.parse(localStorage.getItem('usuario') || '{}'); }
function headers() {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + getToken()
    };
}
function logout() { localStorage.clear(); window.location.href = '/index.html'; }
function checkAuth() { if (!getToken()) window.location.href = '/index.html'; }

async function apiFetch(path, options = {}) {
    const res = await fetch(API + path, { ...options, headers: headers() });
    if (res.status === 401) { logout(); return; }
    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        const error = new Error(err.erro || 'Erro na requisição');
        error.campos = err.campos || null;
        throw error;
    }
    if (res.status === 204) return null;
    return res.json();
}

// Mostra erros inline nos campos do formulário
function mostrarErros(campos) {
    // Limpa erros anteriores
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    document.querySelectorAll('.invalid-feedback').forEach(el => el.remove());

    if (!campos) return;
    Object.entries(campos).forEach(([campo, msg]) => {
        const input = document.getElementById(campo);
        if (input) {
            input.classList.add('is-invalid');
            const div = document.createElement('div');
            div.className = 'invalid-feedback';
            div.textContent = msg;
            input.parentNode.appendChild(div);
        }
    });
}

// Limpa todos os erros do formulário
function limparErros() {
    document.querySelectorAll('.is-invalid').forEach(el => el.classList.remove('is-invalid'));
    document.querySelectorAll('.invalid-feedback').forEach(el => el.remove());
}

// Mostra alerta de erro geral no modal
function mostrarAlertaErro(mensagem, containerId = 'erroModal') {
    const el = document.getElementById(containerId);
    if (el) {
        el.innerHTML = `
            <div class="alert alert-danger alert-dismissible py-2 mb-2">
                <i class="bi bi-exclamation-triangle me-2"></i>${mensagem}
                <button type="button" class="btn-close py-2" onclick="this.parentNode.remove()"></button>
            </div>`;
    }
}