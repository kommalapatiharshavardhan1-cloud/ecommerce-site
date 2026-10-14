const API_BASE = '';
const TOKEN_KEY = 'ecommerce_token';
const USER_KEY = 'ecommerce_user';

function getToken() { return localStorage.getItem(TOKEN_KEY); }
function setSession(token, user) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}
function clearSession() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
}
function getUser() {
  const u = localStorage.getItem(USER_KEY);
  try { return u ? JSON.parse(u) : null; } catch { return null; }
}
function isAdmin() {
  const u = getUser();
  return !!u && u.role === 'ADMIN';
}

async function request(path, options = {}) {
  const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
  const t = getToken();
  if (t) headers['Authorization'] = 'Bearer ' + t;
  const res = await fetch(API_BASE + path, { ...options, headers });
  let data = null;
  const text = await res.text();
  if (text) {
    try { data = JSON.parse(text); } catch { data = text; }
  }
  if (!res.ok) {
    const msg = (data && (data.message || data.error)) || res.statusText || 'Request failed';
    throw new Error(msg);
  }
  return data;
}

const api = {
  get: (p) => request(p),
  post: (p, b) => request(p, { method: 'POST', body: JSON.stringify(b) }),
  put: (p, b) => request(p, { method: 'PUT', body: JSON.stringify(b) }),
  del: (p) => request(p, { method: 'DELETE' }),
};

function showToast(msg, type = 'success') {
  let c = document.querySelector('.toast-container');
  if (!c) {
    c = document.createElement('div');
    c.className = 'toast-container';
    document.body.appendChild(c);
  }
  const t = document.createElement('div');
  t.className = `alert alert-${type === 'success' ? 'success' : 'danger'} shadow`;
  t.textContent = msg;
  c.appendChild(t);
  setTimeout(() => t.remove(), 3000);
}

function updateNavUser() {
  const u = getUser();
  const loginLink = document.getElementById('nav-login');
  const logoutBtn = document.getElementById('nav-logout');
  const userSpan = document.getElementById('nav-user');
  const cartLink = document.getElementById('nav-cart');
  const adminLink = document.getElementById('nav-admin');
  if (u) {
    if (loginLink) loginLink.classList.add('d-none');
    if (logoutBtn) logoutBtn.classList.remove('d-none');
    if (userSpan) { userSpan.textContent = u.fullName; userSpan.classList.remove('d-none'); }
    if (cartLink) cartLink.classList.remove('d-none');
    if (adminLink && u.role === 'ADMIN') adminLink.classList.remove('d-none');
  } else {
    if (loginLink) loginLink.classList.remove('d-none');
    if (logoutBtn) logoutBtn.classList.add('d-none');
    if (userSpan) userSpan.classList.add('d-none');
    if (cartLink) cartLink.classList.add('d-none');
    if (adminLink) adminLink.classList.add('d-none');
  }
  const lo = document.getElementById('nav-logout');
  if (lo) lo.onclick = () => { clearSession(); updateNavUser(); window.location.href = '/index.html'; };
}

function requireAuth() {
  if (!getToken()) {
    window.location.href = '/login.html';
    return false;
  }
  return true;
}

function money(n) {
  var value = Number(n);
  if (isNaN(value)) value = 0;
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(value);
}

document.addEventListener('DOMContentLoaded', updateNavUser);
