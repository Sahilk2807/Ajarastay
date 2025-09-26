const yearEl = document.getElementById('year');
if (yearEl) yearEl.textContent = new Date().getFullYear();

const tabLogin = document.getElementById('tab-login');
const tabSignup = document.getElementById('tab-signup');
const loginForm = document.getElementById('login-form');
const signupForm = document.getElementById('signup-form');

if (tabLogin && tabSignup && loginForm && signupForm) {
  tabLogin.addEventListener('click', () => {
    tabLogin.classList.add('bg-indigo-600', 'text-white');
    tabSignup.classList.remove('bg-indigo-600', 'text-white');
    signupForm.classList.add('hidden');
    loginForm.classList.remove('hidden');
  });
  tabSignup.addEventListener('click', () => {
    tabSignup.classList.add('bg-indigo-600', 'text-white');
    tabLogin.classList.remove('bg-indigo-600', 'text-white');
    loginForm.classList.add('hidden');
    signupForm.classList.remove('hidden');
  });
}

if (signupForm) {
  signupForm.addEventListener('submit', (e) => {
    const passInput = signupForm.querySelector('input[name="password"]');
    if (!passInput) return;
    const pass = passInput.value;
    if (pass.length < 6) {
      e.preventDefault();
      alert('Password must be at least 6 characters');
    }
  });
}

const yearEl = document.getElementById('year');
if (yearEl) yearEl.textContent = new Date().getFullYear();

const tabLogin = document.getElementById('tab-login');
const tabSignup = document.getElementById('tab-signup');
const loginForm = document.getElementById('login-form');
const signupForm = document.getElementById('signup-form');

if (tabLogin && tabSignup && loginForm && signupForm) {
  tabLogin.addEventListener('click', () => {
    tabLogin.classList.add('bg-indigo-600','text-white');
    tabSignup.classList.remove('bg-indigo-600','text-white');
    signupForm.classList.add('hidden');
    loginForm.classList.remove('hidden');
  });
  tabSignup.addEventListener('click', () => {
    tabSignup.classList.add('bg-indigo-600','text-white');
    tabLogin.classList.remove('bg-indigo-600','text-white');
    loginForm.classList.add('hidden');
    signupForm.classList.remove('hidden');
  });
}

if (signupForm) {
  signupForm.addEventListener('submit', (e) => {
    const pass = signupForm.querySelector('input[name="password"]').value;
    if (pass.length < 6) {
      e.preventDefault();
      alert('Password must be at least 6 characters');
    }
  });
}
