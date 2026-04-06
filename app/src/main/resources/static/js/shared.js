/* shared.js — include in every page before page-specific script */

/* ── helpers ── */
function getToken()  { return localStorage.getItem('token'); }
function getRole()   { return localStorage.getItem('cms_role'); }
function setRole(r)  { localStorage.setItem('cms_role', r); }
function logout()    { localStorage.clear(); location.href = '/'; }

/* ── API wrappers ── */
async function api(method, path, body) {
  const opts = {
    method,
    headers: { 'Content-Type': 'application/json' },
  };
  const token = getToken();
  if (token) opts.headers['Authorization'] = 'Bearer ' + token;
  if (body)  opts.body = JSON.stringify(body);
  const r = await fetch(path, opts);
  const data = r.ok ? await r.json() : await r.json().catch(() => ({}));
  return { ok: r.ok, status: r.status, data };
}

/* ── Navbar ── */
function renderNavbar(role) {
  const el = document.getElementById('navbar');
  if (!el) return;

  let links = '';
  let right = '';
  const tok = getToken();

  if (role === 'admin') {
    links = `<a href="/adminDashboard/${tok}">Home</a>`;
    right = `<button class="btn-logout" onclick="logout()">Logout</button>`;
  } else if (role === 'doctor') {
    links = `<a href="/doctorDashboard/${tok}">Home</a>`;
    right = `<button class="btn-logout" onclick="logout()">Logout</button>`;
  } else if (role === 'patient') {
    links = `<a href="/pages/loggedPatientDashboard.html">Home</a>
             <a href="/pages/patientAppointments.html">Appointments</a>`;
    right = `<button class="btn-logout" onclick="logout()">Logout</button>`;
  } else {
    /* public pages */
    right = `<a href="#" onclick="openModal('loginModal')" style="color:#94a3b8;text-decoration:none;font-weight:600">Login</a>
             <a href="#" onclick="openModal('signupModal')" style="color:#94a3b8;text-decoration:none;font-weight:600;margin-left:8px">Sign Up</a>`;
  }

  el.innerHTML = `
    <nav class="navbar">
      <a class="navbar-brand" href="/">
        <img src="/assets/images/logo/logo.png" alt="logo">Hospital CMS
      </a>
      <div class="navbar-links">${links}${right}</div>
    </nav>`;
}

/* ── Footer ── */
function renderFooter() {
  const el = document.getElementById('footer');
  if (!el) return;
  el.innerHTML = `
    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <img src="/assets/images/logo/logo.png" alt="logo">
          <p>&copy; 2025 Hospital CMS. All rights reserved.</p>
        </div>
        <div class="footer-cols">
          <div class="footer-col">
            <h4>Company</h4>
            <a href="#">About</a><a href="#">Careers</a><a href="#">Press</a>
          </div>
          <div class="footer-col">
            <h4>Support</h4>
            <a href="#">Account</a><a href="#">Help Center</a><a href="#">Contact Us</a>
          </div>
          <div class="footer-col">
            <h4>Legals</h4>
            <a href="#">Terms &amp; Conditions</a>
            <a href="#">Privacy Policy</a>
            <a href="#">Licensing</a>
          </div>
        </div>
      </div>
    </footer>`;
}

/* ── Modal helpers ── */
function openModal(id)  { const m = document.getElementById(id); if (m) m.classList.add('open'); }
function closeModal(id) { const m = document.getElementById(id); if (m) m.classList.remove('open'); }

/* close on backdrop click */
document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay')) {
    e.target.classList.remove('open');
  }
});
