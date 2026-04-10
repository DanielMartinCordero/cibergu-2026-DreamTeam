const API_BASE = '/auth';
const PROFILE_BASE = '/profile';

// DOM Elements
const views = {
    login: document.getElementById('login-view'),
    register: document.getElementById('register-view'),
    dashboard: document.getElementById('dashboard-view')
};

// State Management
let token = localStorage.getItem('jwt');

// Initialize
window.addEventListener('load', () => {
    if (token) {
        showView('dashboard');
        loadProfile();
    }
});

// Helper: Show View
function showView(viewName) {
    Object.keys(views).forEach(v => views[v].classList.add('hidden'));
    views[viewName].classList.remove('hidden');
}

// Notification System
function notify(message, type = 'success') {
    const n = document.getElementById('notification');
    n.textContent = message;
    n.className = `show ${type}`;
    setTimeout(() => n.classList.remove('show'), 3000);
}

// View Switches
document.getElementById('go-to-register').addEventListener('click', (e) => {
    e.preventDefault();
    showView('register');
});

document.getElementById('go-to-login').addEventListener('click', (e) => {
    e.preventDefault();
    showView('login');
});

// Register Logic
document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('reg-username').value;
    const password = document.getElementById('reg-password').value;
    const btn = document.getElementById('register-btn');

    btn.disabled = true;
    btn.textContent = 'Procesando...';

    try {
        const response = await fetch(`${API_BASE}/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await response.text();

        if (response.ok) {
            notify('¡Registro exitoso! Iniciando sesión...', 'success');
            // Auto-login after register for better UX
            autoLogin(username, password);
        } else {
            notify(data || 'Error en el registro', 'error');
        }
    } catch (err) {
        notify('Servidor no disponible', 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Finalizar Registro';
    }
});

// Login Logic
document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    handleLogin(username, password);
});

async function handleLogin(username, password) {
    const btn = document.getElementById('login-btn');
    btn.disabled = true;
    btn.textContent = 'Verificando...';

    try {
        const response = await fetch(`${API_BASE}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await response.text();

        if (response.ok) {
            token = data;
            localStorage.setItem('jwt', token);
            notify('Bienvenido de nuevo', 'success');
            showView('dashboard');
            loadProfile();
        } else {
            notify(data || 'Credenciales inválidas', 'error');
        }
    } catch (err) {
        notify('Error de conexión', 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Acceder al Sistema';
    }
}

async function autoLogin(username, password) {
    await handleLogin(username, password);
}

// Load Profile Logic
async function loadProfile() {
    const nameEl = document.getElementById('profile-name');
    const avatarEl = document.getElementById('avatar-initials');

    try {
        const response = await fetch(PROFILE_BASE, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const data = await response.text();
            // Data looks like "Perfil del usuario: username"
            const username = data.split(': ')[1];
            nameEl.textContent = username;
            avatarEl.textContent = username.charAt(0).toUpperCase();
        } else {
            logout();
        }
    } catch (err) {
        notify('Error al cargar perfil', 'error');
    }
}

// Logout Logic
document.getElementById('logout-btn').addEventListener('click', logout);

function logout() {
    token = null;
    localStorage.removeItem('jwt');
    showView('login');
    notify('Sesión cerrada correctamente');
}
